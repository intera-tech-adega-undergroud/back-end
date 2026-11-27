package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.VendaDTO;
import com.intera.adegaunderground.entity.*;
import com.intera.adegaunderground.observer.EstoqueNotifier;
import com.intera.adegaunderground.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** Controle de estoque e de ml: saldos, livros de movimentação e ordem das travas. */
class StockControlTest {
    private EventoService service;
    private ProdutoRepository products;
    private UnidadeBebidaRepository bottles;
    private EntityManager em;
    private MovimentacaoEstoqueService ledger;

    @BeforeEach
    void setUp() {
        service = new EventoService(mock(EstoqueNotifier.class));
        EventoRepository events = mock(EventoRepository.class);
        products = mock(ProdutoRepository.class);
        bottles = mock(UnidadeBebidaRepository.class);
        FuncionarioRepository employees = mock(FuncionarioRepository.class);
        em = mock(EntityManager.class);
        ledger = mock(MovimentacaoEstoqueService.class);
        ReflectionTestUtils.setField(service, "eventoRepository", events);
        ReflectionTestUtils.setField(service, "produtoRepository", products);
        ReflectionTestUtils.setField(service, "itemEventoRepository", mock(ItemEventoRepository.class));
        ReflectionTestUtils.setField(service, "comboRepository", mock(ComboRepository.class));
        ReflectionTestUtils.setField(service, "unidadeBebidaRepository", bottles);
        ReflectionTestUtils.setField(service, "funcionarioRepository", employees);
        ReflectionTestUtils.setField(service, "formaPagamentoRepository", mock(FormaPagamentoRepository.class));
        ReflectionTestUtils.setField(service, "eventoPagamentoRepository", mock(EventoPagamentoRepository.class));
        ReflectionTestUtils.setField(service, "entityManager", em);
        ReflectionTestUtils.setField(service, "movimentacaoEstoqueService", ledger);
        Funcionario employee = new Funcionario();
        employee.setIdFuncionario(1);
        when(employees.findById(1)).thenReturn(Optional.of(employee));
        when(events.save(any(Evento.class))).thenAnswer(i -> {
            Evento e = i.getArgument(0);
            e.setIdEvento(77);
            return e;
        });
        when(bottles.save(any(UnidadeBebida.class))).thenAnswer(i -> i.getArgument(0));
        when(bottles.findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(anyInt(), eq(StatusBebida.ABERTO)))
                .thenReturn(Optional.empty());
        when(bottles.findFirstByProduto_IdProdutoAndStatusAndMlAtualGreaterThanOrderByIdUnidadeBebidaAsc(
                anyInt(), eq(StatusBebida.FECHADO), eq(0))).thenReturn(Optional.empty());
    }

    private VendaDTO sale(String items) {
        return JsonMapper.builder().build().readValue("""
                {"idFuncionario":1,"formaPagamento":"PIX","pago":true,"itens":%s}
                """.formatted(items), VendaDTO.class);
    }

    private Produto product(int id, CategoriaBebida type, int stock, int volumeMl) {
        Categoria category = new Categoria();
        category.setCategoria(type);
        Produto p = new Produto();
        p.setIdProduto(id);
        p.setNome("Produto " + id);
        p.setCategoria(category);
        p.setPreco(BigDecimal.TEN);
        p.setQtdUnidade(stock);
        p.setQtdMinimo(0);
        p.setVolumeMl(volumeMl);
        when(products.findById(id)).thenReturn(Optional.of(p));
        return p;
    }

    private UnidadeBebida bottle(int id, StatusBebida status, int initial, int current) {
        UnidadeBebida u = new UnidadeBebida();
        u.setIdUnidadeBebida(id);
        u.setStatus(status);
        u.setMlInicial(initial);
        u.setMlAtual(current);
        return u;
    }

    @Test
    void negativeStockIsAllowedAndLedgerKeepsTheBalance() {
        Produto p = product(1, CategoriaBebida.BEBIDA_UNIDADE, 1, 0);

        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":3}]"));

        assertEquals(-2, p.getQtdUnidade());
        verify(ledger).registrarEstoque(p, TipoMovimentoEstoque.VENDA, -3, 1, 77, 1);
    }

    @Test
    void doseConsumesMlFromOpenBottleAndWritesVolumeLedger() {
        Produto dose = product(1, CategoriaBebida.BEBIDA_FRACIONADA, 10, 50);
        UnidadeBebida open = bottle(5, StatusBebida.ABERTO, 750, 300);
        when(bottles.findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(1, StatusBebida.ABERTO))
                .thenReturn(Optional.of(open));

        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":2}]"));

        assertEquals(200, open.getMlAtual());
        assertEquals(StatusBebida.ABERTO, open.getStatus());
        assertEquals(10, dose.getQtdUnidade());
        verify(ledger).registrarVolume(open, TipoMovimentoVolume.VENDA, -100, 300, 1, 77, 1);
    }

    @Test
    void saleLargerThanBottleOpensNextOneAndMarksFirstAsExhausted() {
        Produto dose = product(1, CategoriaBebida.BEBIDA_FRACIONADA, 4, 50);
        UnidadeBebida nearlyEmpty = bottle(5, StatusBebida.ABERTO, 750, 30);
        UnidadeBebida sealed = bottle(6, StatusBebida.FECHADO, 750, 750);
        when(bottles.findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(1, StatusBebida.ABERTO))
                .thenReturn(Optional.of(nearlyEmpty), Optional.empty());
        when(bottles.findFirstByProduto_IdProdutoAndStatusAndMlAtualGreaterThanOrderByIdUnidadeBebidaAsc(
                1, StatusBebida.FECHADO, 0)).thenReturn(Optional.of(sealed));

        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]"));

        assertEquals(0, nearlyEmpty.getMlAtual());
        assertEquals(StatusBebida.ESGOTADA, nearlyEmpty.getStatus());
        assertNotNull(nearlyEmpty.getEsgotadaEm());
        assertEquals(StatusBebida.ABERTO, sealed.getStatus());
        assertEquals(730, sealed.getMlAtual());
        assertNotNull(sealed.getAbertaEm());
        assertEquals(3, dose.getQtdUnidade());
        verify(ledger).registrarVolume(nearlyEmpty, TipoMovimentoVolume.VENDA, -30, 30, 1, 77, 1);
        verify(ledger).registrarVolume(sealed, TipoMovimentoVolume.VENDA, -20, 750, 1, 77, 1);
        verify(ledger).registrarEstoque(dose, TipoMovimentoEstoque.ABERTURA_GARRAFA, -1, 4, 77, 1);
    }

    @Test
    void missingBottleCreatesVirtualOneAndStockMayGoNegative() {
        Produto dose = product(1, CategoriaBebida.BEBIDA_FRACIONADA, 0, 50);

        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]"));

        assertEquals(-1, dose.getQtdUnidade());
        verify(bottles, atLeastOnce()).save(argThat(u -> u.getMlInicial() == 50 && u.getStatus() != null));
        verify(ledger).registrarEstoque(dose, TipoMovimentoEstoque.ABERTURA_GARRAFA, -1, 0, 77, 1);
    }

    @Test
    void childDoseConsumesFromParentBottleStock() {
        Produto parent = product(2, CategoriaBebida.BEBIDA_UNIDADE, 5, 750);
        Produto dose = product(1, CategoriaBebida.BEBIDA_FRACIONADA, 0, 50);
        dose.setProdutoPai(parent);
        UnidadeBebida open = bottle(9, StatusBebida.ABERTO, 750, 100);
        when(bottles.findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(2, StatusBebida.ABERTO))
                .thenReturn(Optional.of(open));

        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]"));

        assertEquals(50, open.getMlAtual());
        assertEquals(5, parent.getQtdUnidade());
    }

    @Test
    void productsAreLockedInAscendingIdOrderBeforeAnyStockChange() {
        Produto a = product(3, CategoriaBebida.BEBIDA_UNIDADE, 10, 0);
        Produto b = product(1, CategoriaBebida.BEBIDA_UNIDADE, 10, 0);

        service.salvarVenda(sale("[{\"idProduto\":3,\"quantidade\":1},{\"idProduto\":1,\"quantidade\":1}]"));

        InOrder order = inOrder(em);
        order.verify(em).refresh(b, LockModeType.PESSIMISTIC_WRITE);
        order.verify(em).refresh(a, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    void parentProductIsLockedTogetherWithTheDose() {
        Produto parent = product(2, CategoriaBebida.BEBIDA_UNIDADE, 5, 750);
        Produto dose = product(1, CategoriaBebida.BEBIDA_FRACIONADA, 0, 50);
        dose.setProdutoPai(parent);

        service.salvarVenda(sale("[{\"idProduto\":1,\"quantidade\":1}]"));

        verify(em).refresh(dose, LockModeType.PESSIMISTIC_WRITE);
        verify(em).refresh(parent, LockModeType.PESSIMISTIC_WRITE);
    }
}
