package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.VendaDTO;
import com.intera.adegaunderground.entity.*;
import com.intera.adegaunderground.observer.EstoqueNotifier;
import com.intera.adegaunderground.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaleConsistencyTest {
    private EventoService service;
    private EventoRepository events;
    private ProdutoRepository products;
    private ItemEventoRepository items;
    private ComboRepository combos;
    private UnidadeBebidaRepository bottles;
    private FormaPagamentoRepository paymentMethods;
    private EventoPagamentoRepository payments;

    @BeforeEach
    void setUp() {
        service = new EventoService(mock(EstoqueNotifier.class));
        events = mock(EventoRepository.class);
        products = mock(ProdutoRepository.class);
        items = mock(ItemEventoRepository.class);
        combos = mock(ComboRepository.class);
        bottles = mock(UnidadeBebidaRepository.class);
        FuncionarioRepository employees = mock(FuncionarioRepository.class);
        ReflectionTestUtils.setField(service, "eventoRepository", events);
        ReflectionTestUtils.setField(service, "produtoRepository", products);
        ReflectionTestUtils.setField(service, "itemEventoRepository", items);
        ReflectionTestUtils.setField(service, "comboRepository", combos);
        ReflectionTestUtils.setField(service, "unidadeBebidaRepository", bottles);
        ReflectionTestUtils.setField(service, "funcionarioRepository", employees);
        paymentMethods = mock(FormaPagamentoRepository.class);
        payments = mock(EventoPagamentoRepository.class);
        ReflectionTestUtils.setField(service, "formaPagamentoRepository", paymentMethods);
        ReflectionTestUtils.setField(service, "entityManager", mock(jakarta.persistence.EntityManager.class));
        ReflectionTestUtils.setField(service, "movimentacaoEstoqueService", mock(MovimentacaoEstoqueService.class));
        ReflectionTestUtils.setField(service, "eventoPagamentoRepository", payments);
        when(paymentMethods.findByCodigoAndAtivoTrue(any())).thenReturn(Optional.empty());
        when(employees.findById(1)).thenReturn(Optional.of(new Funcionario()));
        when(events.save(any(Evento.class))).thenAnswer(i -> {
            Evento event = i.getArgument(0);
            event.setIdEvento(1);
            return event;
        });
        when(bottles.save(any(UnidadeBebida.class))).thenAnswer(i -> i.getArgument(0));
    }

    private VendaDTO sale(String entries) {
        return JsonMapper.builder().build().readValue("""
                {"idFuncionario":1,"formaPagamento":"PIX","pago":true,"itens":%s}
                """.formatted(entries), VendaDTO.class);
    }

    private Produto product(CategoriaBebida type) {
        Categoria category = new Categoria();
        category.setCategoria(type);
        Produto product = new Produto();
        product.setIdProduto(1);
        product.setNome("Teste");
        product.setCategoria(category);
        product.setPreco(BigDecimal.TEN);
        product.setQtdUnidade(1);
        product.setQtdMinimo(0);
        product.setVolumeMl(50);
        when(products.findById(1)).thenReturn(Optional.of(product));
        return product;
    }

    @Test
    void rejectsEmptySaleBeforeSavingEvent() {
        assertThrows(IllegalArgumentException.class, () -> service.salvarVenda(sale("[]")));
        verifyNoInteractions(events, products, items);
    }

    @Test
    void repeatedProductIsAggregatedWithoutDoubleWritingCompositeKey() {
        Produto product = product(CategoriaBebida.BEBIDA_UNIDADE);
        service.salvarVenda(sale("""
                [{"idProduto":1,"quantidade":2},{"idProduto":1,"quantidade":3}]
                """));
        assertEquals(-4, product.getQtdUnidade());
        ArgumentCaptor<ItemEvento> captor = ArgumentCaptor.forClass(ItemEvento.class);
        verify(items).save(captor.capture());
        assertEquals(5, captor.getValue().getQuantidade());
    }

    @Test
    void rejectsComboWithoutComponents() {
        product(CategoriaBebida.COMBO);
        assertThrows(IllegalArgumentException.class, () ->
                service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]")));
        verifyNoInteractions(items);
    }

    @Test
    void missingCategoryGivesActionableValidationError() {
        Produto product = product(CategoriaBebida.BEBIDA_UNIDADE);
        product.setCategoria(null);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]")));
        assertTrue(error.getMessage().contains("categoria"));
    }

    @Test
    void zeroAndNegativeQuantitiesAreRejectedBeforeStockChanges() {
        for (int quantity : List.of(0, -1)) {
            assertThrows(IllegalArgumentException.class, () -> service.salvarVenda(
                    sale("[{\"idProduto\":1,\"quantidade\":" + quantity + "}]")));
        }
        verifyNoInteractions(products, items, events);
    }

    @Test
    void exhaustedBottleIsNotRefilledBySale() {
        product(CategoriaBebida.BEBIDA_FRACIONADA);
        UnidadeBebida exhausted = new UnidadeBebida();
        exhausted.setIdUnidadeBebida(1);
        exhausted.setStatus(StatusBebida.FECHADO);
        exhausted.setMlInicial(750);
        exhausted.setMlAtual(0);
        when(bottles.findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(1, StatusBebida.ABERTO))
                .thenReturn(Optional.empty());
        when(bottles.findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(1, StatusBebida.FECHADO))
                .thenReturn(Optional.of(exhausted));
        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]"));
        assertEquals(0, exhausted.getMlAtual());
        assertEquals(StatusBebida.FECHADO, exhausted.getStatus());
    }

    @Test
    void openingAvailableBottleAlsoDecrementsZeroStock() {
        Produto product = product(CategoriaBebida.BEBIDA_FRACIONADA);
        product.setQtdUnidade(0);
        UnidadeBebida bottle = new UnidadeBebida();
        bottle.setMlInicial(50);
        bottle.setMlAtual(50);
        bottle.setStatus(StatusBebida.FECHADO);
        when(bottles.findFirstByProduto_IdProdutoAndStatusAndMlAtualGreaterThanOrderByIdUnidadeBebidaAsc(
                1, StatusBebida.FECHADO, 0)).thenReturn(Optional.of(bottle));
        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]"));
        assertEquals(-1, product.getQtdUnidade());
        assertEquals(0, bottle.getMlAtual());
    }
}
