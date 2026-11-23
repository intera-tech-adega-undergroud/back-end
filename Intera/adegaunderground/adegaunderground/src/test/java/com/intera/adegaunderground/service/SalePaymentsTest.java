package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.VendaDTO;
import com.intera.adegaunderground.entity.*;
import com.intera.adegaunderground.observer.EstoqueNotifier;
import com.intera.adegaunderground.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Valores gravados na venda, desconto e pagamento dividido. */
class SalePaymentsTest {
    private EventoService service;
    private EventoRepository events;
    private ItemEventoRepository items;
    private EventoPagamentoRepository payments;
    private ClienteRepository clients;
    private Cliente client;

    @BeforeEach
    void setUp() {
        service = new EventoService(mock(EstoqueNotifier.class));
        events = mock(EventoRepository.class);
        ProdutoRepository products = mock(ProdutoRepository.class);
        items = mock(ItemEventoRepository.class);
        payments = mock(EventoPagamentoRepository.class);
        clients = mock(ClienteRepository.class);
        FuncionarioRepository employees = mock(FuncionarioRepository.class);
        FormaPagamentoRepository methods = mock(FormaPagamentoRepository.class);
        ReflectionTestUtils.setField(service, "eventoRepository", events);
        ReflectionTestUtils.setField(service, "produtoRepository", products);
        ReflectionTestUtils.setField(service, "itemEventoRepository", items);
        ReflectionTestUtils.setField(service, "funcionarioRepository", employees);
        ReflectionTestUtils.setField(service, "clienteRepository", clients);
        ReflectionTestUtils.setField(service, "formaPagamentoRepository", methods);
        ReflectionTestUtils.setField(service, "eventoPagamentoRepository", payments);

        when(employees.findById(1)).thenReturn(Optional.of(new Funcionario()));
        when(employees.findById(2)).thenReturn(Optional.of(new Funcionario()));
        client = new Cliente();
        client.setSaldoDevedor(BigDecimal.ZERO);
        when(clients.findById(7)).thenReturn(Optional.of(client));
        when(events.save(any(Evento.class))).thenAnswer(i -> {
            Evento e = i.getArgument(0);
            e.setIdEvento(1);
            return e;
        });
        method(methods, "DINHEIRO", true, false);
        method(methods, "PIX", false, false);
        method(methods, "DEBITO", false, false);
        method(methods, "FIADO", false, true);

        Categoria category = new Categoria();
        category.setCategoria(CategoriaBebida.BEBIDA_UNIDADE);
        Produto product = new Produto();
        product.setIdProduto(1);
        product.setNome("Cerveja");
        product.setCategoria(category);
        product.setPreco(new BigDecimal("50.00"));
        product.setQtdUnidade(100);
        product.setQtdMinimo(0);
        when(products.findById(1)).thenReturn(Optional.of(product));
    }

    private static void method(FormaPagamentoRepository repo, String code, boolean cash, boolean debt) {
        FormaPagamento f = new FormaPagamento();
        f.setCodigo(code);
        f.setDescricao(code);
        f.setEhDinheiro(cash);
        f.setGeraSaldoDevedor(debt);
        when(repo.findByCodigoAndAtivoTrue(code)).thenReturn(Optional.of(f));
    }

    private VendaDTO sale(String extra, boolean paid, String form) {
        return JsonMapper.builder().build().readValue("""
                {"idFuncionario":1,%s"formaPagamento":"%s","pago":%s,
                 "itens":[{"idProduto":1,"quantidade":2}]%s}
                """.formatted(paid ? "" : "\"idCliente\":7,", form, paid, extra), VendaDTO.class);
    }

    private List<EventoPagamento> savedPayments() {
        ArgumentCaptor<EventoPagamento> captor = ArgumentCaptor.forClass(EventoPagamento.class);
        verify(payments, atLeast(0)).save(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void legacySaleStoresPricesAndTotals() {
        Evento event = service.salvarVenda(sale("", true, "PIX"));

        assertEquals(new BigDecimal("100.00"), event.getSubtotal());
        assertEquals(new BigDecimal("100.00"), event.getTotal());
        assertEquals(BigDecimal.ZERO.setScale(2), event.getDescontoValor());
        assertEquals(StatusVenda.CONCLUIDA, event.getStatus());
        assertFalse(event.getValoresEstimados());
        assertSame(event.getFuncionario(), event.getExecutor());

        ArgumentCaptor<ItemEvento> item = ArgumentCaptor.forClass(ItemEvento.class);
        verify(items).save(item.capture());
        assertEquals(new BigDecimal("50.00"), item.getValue().getPrecoUnitario());
        assertEquals(new BigDecimal("100.00"), item.getValue().getSubtotalItem());

        List<EventoPagamento> saved = savedPayments();
        assertEquals(1, saved.size());
        assertEquals("PIX", saved.get(0).getFormaPagamento().getCodigo());
        assertEquals(new BigDecimal("100.00"), saved.get(0).getValorAplicado());
    }

    @Test
    void percentDiscountReducesTotalAndKeepsSubtotal() {
        Evento event = service.salvarVenda(sale(
                ",\"descontoTipo\":\"PERCENTUAL\",\"descontoValor\":10,\"idFuncionarioDesconto\":2", true, "PIX"));

        assertEquals(new BigDecimal("100.00"), event.getSubtotal());
        assertEquals(new BigDecimal("10.00"), event.getDescontoValor());
        assertEquals(new BigDecimal("10.00"), event.getDescontoPercentual());
        assertEquals(new BigDecimal("90.00"), event.getTotal());
        assertEquals(DescontoTipo.PERCENTUAL, event.getDescontoTipo());
        assertNotNull(event.getFuncionarioDesconto());
    }

    @Test
    void fixedDiscountHigherThanSubtotalIsRejectedBeforeSaving() {
        assertThrows(IllegalArgumentException.class, () -> service.salvarVenda(sale(
                ",\"descontoTipo\":\"VALOR\",\"descontoValor\":100.01", true, "PIX")));
        verifyNoInteractions(events, items, payments);
    }

    @Test
    void discountValueWithoutTypeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.salvarVenda(sale(",\"descontoValor\":5", true, "PIX")));
    }

    @Test
    void splitCashAndPixComputesChange() {
        Evento event = service.salvarVenda(sale("""
                ,"pagamentos":[{"forma":"DINHEIRO","valorRecebido":70},{"forma":"PIX","valorRecebido":40}]
                """, true, "DINHEIRO"));

        assertEquals("DINHEIRO+PIX", event.getFormaPagamento());
        assertTrue(event.getPago());
        List<EventoPagamento> saved = savedPayments();
        assertEquals(2, saved.size());
        assertEquals(new BigDecimal("70.00"), saved.get(0).getValorRecebido());
        assertEquals(new BigDecimal("60.00"), saved.get(0).getValorAplicado());
        assertEquals(new BigDecimal("10.00"), saved.get(0).getTroco());
        assertEquals(new BigDecimal("40.00"), saved.get(1).getValorAplicado());
        assertEquals(BigDecimal.ZERO.setScale(2), saved.get(1).getTroco());
    }

    @Test
    void splitThatDoesNotCoverTotalIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.salvarVenda(sale("""
                ,"pagamentos":[{"forma":"PIX","valorRecebido":40},{"forma":"DEBITO","valorRecebido":30}]
                """, true, "PIX")));
        verifyNoInteractions(events);
    }

    @Test
    void cardOrPixAboveRemainingIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.salvarVenda(sale("""
                ,"pagamentos":[{"forma":"PIX","valorRecebido":120}]
                """, true, "PIX")));
    }

    @Test
    void unknownPaymentMethodIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.salvarVenda(sale("""
                ,"pagamentos":[{"forma":"CHEQUE","valorRecebido":100}]
                """, true, "PIX")));
    }

    @Test
    void splitWithDebtPartRaisesCustomerBalanceByThatPartOnly() {
        Evento event = service.salvarVenda(sale("""
                ,"pagamentos":[{"forma":"PIX","valorRecebido":40},{"forma":"FIADO","valorRecebido":60}]
                """, false, "FIADO"));

        assertFalse(event.getPago());
        assertEquals(new BigDecimal("60.00"), client.getSaldoDevedor());
        verify(clients).save(client);
    }

    @Test
    void paidFlagThatContradictsSplitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.salvarVenda(sale("""
                ,"pagamentos":[{"forma":"PIX","valorRecebido":40},{"forma":"FIADO","valorRecebido":60}]
                """, true, "PIX")));
    }

    @Test
    void legacyDebtSaleChargesDiscountedTotalToCustomer() {
        Evento event = service.salvarVenda(sale(
                ",\"descontoTipo\":\"VALOR\",\"descontoValor\":20", false, "FIADO"));

        assertEquals(new BigDecimal("80.00"), event.getTotal());
        assertEquals(new BigDecimal("80.00"), client.getSaldoDevedor());
        assertEquals("FIADO", savedPayments().get(0).getFormaPagamento().getCodigo());
    }

    @Test
    void inactiveEmployeeCannotLogIn() {
        FuncionarioRepository repo = mock(FuncionarioRepository.class);
        FuncionarioService auth = new FuncionarioService(repo, mock(PermissaoRepository.class), mock(PasswordEncoder.class));
        Funcionario f = new Funcionario();
        f.setSenhaCripto("segredo");
        f.setAtivo(false);
        when(repo.findByEmail("a@a.com")).thenReturn(Optional.of(f));
        assertTrue(auth.autenticar("a@a.com", "segredo").isEmpty());

        f.setAtivo(true);
        assertTrue(auth.autenticar("a@a.com", "segredo").isPresent());
    }
}
