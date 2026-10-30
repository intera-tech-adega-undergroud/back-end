package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.*;
import com.intera.adegaunderground.entity.*;
import com.intera.adegaunderground.observer.EstoqueNotifier;
import com.intera.adegaunderground.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemEventoRepository itemEventoRepository;

    @Autowired
    private UnidadeBebidaRepository unidadeBebidaRepository;

    @Autowired
    private ComboRepository comboRepository;

    private final EstoqueNotifier estoqueNotifier;

    public EventoService(EstoqueNotifier estoqueNotifier) {
        this.estoqueNotifier = estoqueNotifier;
    }

    public VendaBigNumberDTO buscarVendasMes() {
        var resultadoQuery = eventoRepository.buscarVendasMes();

        if (resultadoQuery.isEmpty()) return null;

        Object[] resultado = resultadoQuery.get(0);

        if (resultado[0] == null || resultado[1] == null) return null;

        Double valorTotal = ((Number) resultado[0]).doubleValue();
        Long totalVendas = ((Number) resultado[1]).longValue();

        return new VendaBigNumberDTO(valorTotal, totalVendas);
    }

    public VendaBigNumberDTO buscarFaturamentoDia() {
        var resultadoQuery = eventoRepository.buscarFaturamentoDia();

        if (resultadoQuery.isEmpty()) return null;

        Object[] resultado = resultadoQuery.get(0);

        if (resultado[0] == null || resultado[1] == null) return null;

        Double valorTotal = ((Number) resultado[0]).doubleValue();
        Long totalVendas = ((Number) resultado[1]).longValue();

        return new VendaBigNumberDTO(valorTotal, totalVendas);
    }

    public List<RankFuncionarioDTO> buscarRankFuncionarios() {
        var resultadoQuery = eventoRepository.buscarRankFuncionarios();

        if (resultadoQuery.isEmpty()) return null;

        List<RankFuncionarioDTO> lista = new ArrayList<>();

        for (Object[] resultado : resultadoQuery) {
            Long idFuncionario = ((Number) resultado[0]).longValue();
            String vendedor = resultado[1].toString();
            Long quantidadeVendas = ((Number) resultado[2]).longValue();
            Double valorTotalVendido = ((Number) resultado[3]).doubleValue();

            lista.add(new RankFuncionarioDTO(
                    idFuncionario,
                    vendedor,
                    quantidadeVendas,
                    valorTotalVendido
            ));
        }

        return lista;
    }

    public List<GraficoDashDTO> buscarGraficoDash(
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas são obrigatórias");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data inicial não pode ser maior que data final");
        }

        var resultadoQuery = eventoRepository.buscarGraficoDash(dataInicio, dataFim);

        List<GraficoDashDTO> lista = new ArrayList<>();

        for (Object[] resultado : resultadoQuery) {

            Object valorData = resultado[0];

            LocalDate data;

            if (valorData instanceof LocalDate) {

                data = (LocalDate) valorData;

            } else if (valorData instanceof java.sql.Date) {

                data = ((java.sql.Date) valorData).toLocalDate();

            } else {

                throw new RuntimeException(
                        "Tipo inesperado: " +
                                valorData.getClass()
                );
            }

            Double valorTotalVendas =
                    ((Number) resultado[1]).doubleValue();

            lista.add(
                    new GraficoDashDTO(
                            data,
                            valorTotalVendas
                    )
            );
        }

        return lista;
    }

    public List<FiadoDTO> buscarTodosClientesFiado() {
        List<Cliente> clientes = clienteRepository.findAll();

        List<FiadoDTO> lista = new ArrayList<>();

        for (Cliente cliente : clientes) {

            Evento ultimoEvento =
                    eventoRepository.findFirstByCliente_IdClienteOrderByDataHoraEventoDesc(
                            cliente.getIdCliente()
                    );

            String dataVenda = ultimoEvento != null && ultimoEvento.getDataHoraEvento() != null
                    ? ultimoEvento.getDataHoraEvento().toLocalDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
                    : "-";

            BigDecimal saldoDevedor = cliente.getSaldoDevedor() != null ? cliente.getSaldoDevedor() : BigDecimal.ZERO;
            String status = saldoDevedor.signum() <= 0 ? "Pago" : "Em Aberto";

            lista.add(new FiadoDTO(
                    cliente.getIdCliente(),
                    cliente.getNome(),
                    saldoDevedor.doubleValue(),
                    dataVenda,
                    status,
                    new ArrayList<>()
            ));
        }

        return lista;
    }

    public List<FiadoDTO> buscarFiadosPorPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Informe as duas datas, em ordem válida.");
        }
        return eventoRepository.buscarTodosClientesFiadoPorPeriodo(dataInicio, dataFim).stream()
                .map(row -> new FiadoDTO(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        ((Number) row[2]).doubleValue(),
                        row[3] == null ? "-" : formatCreditDate(row[3]),
                        (String) row[4],
                        List.of()
                )).toList();
    }

    private String formatCreditDate(Object value) {
        LocalDateTime date = value instanceof java.sql.Timestamp timestamp
                ? timestamp.toLocalDateTime() : (LocalDateTime) value;
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public List<FiadoDTO> buscarTodosFiados() {
        return buscarTodosClientesFiado();
    }

    @Transactional
    public void registrarPagamentoFiado(PagamentoFiadoDTO dto) {
        if (dto.idCliente() == null || dto.valorPagamento() == null || dto.valorPagamento().signum() <= 0) {
            throw new IllegalArgumentException("Informe cliente e valor de pagamento maior que zero.");
        }
        Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        BigDecimal saldoAtual = cliente.getSaldoDevedor() != null ? cliente.getSaldoDevedor() : BigDecimal.ZERO;
        BigDecimal valorPagamento = dto.valorPagamento();
        if (valorPagamento.compareTo(saldoAtual) > 0) throw new IllegalArgumentException("Valor de pagamento maior que a dívida");
        BigDecimal novoSaldo = saldoAtual.subtract(valorPagamento);
        cliente.setSaldoDevedor(novoSaldo.signum() <= 0 ? BigDecimal.ZERO : novoSaldo);

        clienteRepository.save(cliente);

        System.out.println("Pagamento fiado registrado");
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Valor pago: " + valorPagamento);
        System.out.println("Forma de pagamento: " + dto.formaPagamento());
    }

    private BigDecimal calcularTotal(VendaDTO dto){

        BigDecimal total = BigDecimal.ZERO;

        for(ItemVendaDTO itemDTO : dto.itens()){

            Produto produto =
                    produtoRepository.findById(
                            itemDTO.idProduto()
                    ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
            validateStockProduct(produto);

            total = total.add(
                    produto.getPreco().multiply(
                            BigDecimal.valueOf(itemDTO.quantidade())
                    )
            );
        }

        return total;
    }

    @Transactional
    public Evento salvarVenda(VendaDTO dto) {
        Map<Integer, Integer> quantities = aggregateItems(dto);

        Evento evento = new Evento();
        evento.setTipo(TipoEvento.VENDA);
        evento.setFormaPagamento(dto.formaPagamento());
        evento.setPago(dto.pago());
        evento.setDataHoraEvento(LocalDateTime.now());

        Funcionario funcionario = funcionarioRepository.findById(dto.idFuncionario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
        evento.setFuncionario(funcionario);

        if (dto.idCliente() != null) {
            Cliente cliente = clienteRepository.findById(dto.idCliente())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
            evento.setCliente(cliente);

            if (!dto.pago()) {
                BigDecimal saldoAtual = cliente.getSaldoDevedor() != null ? cliente.getSaldoDevedor() : BigDecimal.ZERO;
                cliente.setSaldoDevedor(saldoAtual.add(calcularTotal(dto)));
                clienteRepository.save(cliente);
            }
        }

        Evento eventoSalvo = eventoRepository.save(evento);

        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {

            Produto produto =
                    produtoRepository
                            .findById(entry.getKey())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
            validateStockProduct(produto);

            String categoria =
                    produto.getCategoria()
                            .getCategoria()
                            .toString();

            if (categoria.equals("COMBO")) {

                List<Combo> itensCombo =
                        comboRepository.findByProdutoPai(produto);
                if (itensCombo.isEmpty()) {
                    throw new IllegalArgumentException("Combo sem componentes cadastrados: " + produto.getIdProduto());
                }

                for (Combo combo : itensCombo) {

                    Produto produtoCombo =
                            combo.getProdutoFilho();

                    Integer quantidadeFinal =
                            Math.multiplyExact(combo.getQuantidade(), entry.getValue());

                    processarBaixaEstoque(
                            produtoCombo,
                            quantidadeFinal
                    );
                }

            } else {

                processarBaixaEstoque(
                        produto,
                        entry.getValue()
                );
            }

            ItemEvento item = new ItemEvento();

            item.setEvento(eventoSalvo);

            item.setProduto(produto);

            item.setQuantidade(
                    entry.getValue()
            );

            itemEventoRepository.save(item);
        }

        return eventoSalvo;
    }

    private Map<Integer, Integer> aggregateItems(VendaDTO dto) {
        if (dto.idFuncionario() == null || dto.pago() == null
                || dto.formaPagamento() == null || dto.formaPagamento().isBlank()
                || dto.itens() == null || dto.itens().isEmpty()) {
            throw new IllegalArgumentException("Informe funcionário, pagamento e pelo menos um item de venda.");
        }
        if (!dto.pago() && dto.idCliente() == null) {
            throw new IllegalArgumentException("Venda fiado precisa de um cliente.");
        }
        Map<Integer, Integer> quantities = new LinkedHashMap<>();
        for (ItemVendaDTO item : dto.itens()) {
            if (item == null || item.idProduto() == null || item.quantidade() == null || item.quantidade() <= 0) {
                throw new IllegalArgumentException("Cada item precisa de produto e quantidade maior que zero.");
            }
            quantities.merge(item.idProduto(), item.quantidade(), Math::addExact);
        }
        return quantities;
    }

    private void validateStockProduct(Produto produto) {
        if (produto == null || produto.getCategoria() == null || produto.getCategoria().getCategoria() == null) {
            throw new IllegalArgumentException("Produto sem categoria cadastrada.");
        }
        if (produto.getPreco() == null || produto.getQtdUnidade() == null || produto.getQtdMinimo() == null) {
            throw new IllegalArgumentException("Produto com preço ou dados de estoque incompletos: " + produto.getIdProduto());
        }
    }

    private void consumirMlUnidadeBebida(
            Produto produtoDose,
            Produto produtoControle,
            Integer quantidadeDosesVendidas
    ) {

        if (produtoDose.getVolumeMl() == null || produtoDose.getVolumeMl() <= 0
                || produtoControle.getVolumeMl() == null || produtoControle.getVolumeMl() <= 0) {
            throw new IllegalArgumentException("Produto fracionado precisa de volume maior que zero.");
        }
        int mlTotalNecessario = Math.multiplyExact(produtoDose.getVolumeMl(), quantidadeDosesVendidas);

        System.out.println("\n================ [DEBUG INÍCIO VENDA FRACIONADA] ================");
        System.out.println("Produto vendido: " + produtoDose.getNome());
        System.out.println("Produto controle: " + produtoControle.getNome());
        System.out.println("Quantidade vendida: " + quantidadeDosesVendidas);
        System.out.println("Volume por unidade: " + produtoDose.getVolumeMl() + "ml");
        System.out.println("Total necessário: " + mlTotalNecessario + "ml");
        System.out.println("================================================================");

        int rodada = 1;

        while (mlTotalNecessario > 0) {

            System.out.println(
                    "\n-> [RODADA "
                            + rodada
                            + "] Restante: "
                            + mlTotalNecessario
                            + "ml"
            );

            UnidadeBebida unidade =
                    unidadeBebidaRepository
                            .findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(
                                    produtoControle.getIdProduto(),
                                    StatusBebida.ABERTO
                            )
                            .orElse(null);

            if (unidade != null) {

                System.out.println(
                        "[DEBUG] Garrafa aberta encontrada. ID="
                                + unidade.getIdUnidadeBebida()
                                + " mlAtual="
                                + unidade.getMlAtual()
                );
            }

            if (unidade == null) {

                System.out.println(
                        "[DEBUG] Nenhuma garrafa aberta encontrada."
                );

                unidade =
                        unidadeBebidaRepository
                                .findFirstByProduto_IdProdutoAndStatusAndMlAtualGreaterThanOrderByIdUnidadeBebidaAsc(
                                        produtoControle.getIdProduto(),
                                        StatusBebida.FECHADO,
                                        0
                                )
                                .orElse(null);

                if (unidade != null) {

                    System.out.println(
                            "[DEBUG] Garrafa fechada encontrada. Abrindo..."
                    );

                    unidade.setStatus(
                            StatusBebida.ABERTO
                    );

                    unidadeBebidaRepository.save(
                            unidade
                    );

                    int estoqueAnterior =
                            produtoControle.getQtdUnidade();

                    produtoControle.setQtdUnidade(
                            Math.subtractExact(produtoControle.getQtdUnidade(), 1)
                    );

                    produtoRepository.save(
                            produtoControle
                    );

                    verificarEstoque(produtoControle);

                    System.out.println(
                            "[DEBUG] Estoque físico atualizado: "
                                    + estoqueAnterior
                                    + " -> "
                                    + produtoControle.getQtdUnidade()
                    );

                } else {

                    System.out.println(
                            "[DEBUG ALERT] Nenhuma garrafa encontrada. Criando unidade virtual."
                    );

                    UnidadeBebida novaUnidade =
                            new UnidadeBebida();

                    novaUnidade.setProduto(
                            produtoControle
                    );

                    novaUnidade.setStatus(
                            StatusBebida.ABERTO
                    );

                    int volumeGarrafa =
                            produtoControle.getVolumeMl();

                    novaUnidade.setMlInicial(
                            volumeGarrafa
                    );

                    novaUnidade.setMlAtual(
                            volumeGarrafa
                    );

                    unidade =
                            unidadeBebidaRepository.save(
                                    novaUnidade
                            );

                    int estoqueAnterior =
                            produtoControle.getQtdUnidade();

                    produtoControle.setQtdUnidade(
                            Math.subtractExact(produtoControle.getQtdUnidade(), 1)
                    );

                    produtoRepository.save(
                            produtoControle
                    );

                    verificarEstoque(produtoControle);

                    System.out.println(
                            "[DEBUG] Unidade virtual criada. ID="
                                    + unidade.getIdUnidadeBebida()
                    );

                    System.out.println(
                            "[DEBUG] Estoque atualizado: "
                                    + estoqueAnterior
                                    + " -> "
                                    + produtoControle.getQtdUnidade()
                    );
                }
            }

            int mlDisponivel =
                    unidade.getMlAtual();

            System.out.println(
                    "[DEBUG] ml disponível="
                            + mlDisponivel
            );

            if (mlDisponivel >= mlTotalNecessario) {

                int mlAnterior =
                        unidade.getMlAtual();

                unidade.setMlAtual(
                        mlDisponivel - mlTotalNecessario
                );

                System.out.println(
                        "[DEBUG] mlAtual: "
                                + mlAnterior
                                + " -> "
                                + unidade.getMlAtual()
                );

                mlTotalNecessario = 0;

            } else {

                unidade.setMlAtual(0);

                mlTotalNecessario -= mlDisponivel;

                System.out.println(
                        "[DEBUG] Garrafa esgotada. Restante="
                                + mlTotalNecessario
                );
            }

            if (unidade.getMlAtual() <= 0) {

                unidade.setMlAtual(0);

                unidade.setStatus(
                        StatusBebida.FECHADO
                );

                System.out.println(
                        "[DEBUG] Garrafa fechada por esgotamento."
                );
            }

            unidadeBebidaRepository.save(
                    unidade
            );

            rodada++;
        }

        System.out.println(
                "\n================ [DEBUG FIM VENDA FRACIONADA] ================\n"
        );
    }

    private Produto obterProdutoControleEstoque(
            Produto produto
    ) {

        if (produto.getProdutoPai() != null) {
            return produto.getProdutoPai();
        }

        return produto;
    }

    private void processarBaixaEstoque(
            Produto produto,
            Integer quantidade
    ) {
        validateStockProduct(produto);

        String categoria =
                produto.getCategoria()
                        .getCategoria()
                        .toString();

        boolean exigeFracionamento =
                categoria.equals("BEBIDA_FRACIONADA")
                        || categoria.equals("DRINK");

        if (!exigeFracionamento) {

            produto.setQtdUnidade(
                    Math.subtractExact(produto.getQtdUnidade(), quantidade)
            );

            produtoRepository.save(produto);

            verificarEstoque(produto);

            return;
        }

        Produto produtoControle =
                obterProdutoControleEstoque(produto);
        validateStockProduct(produtoControle);

        consumirMlUnidadeBebida(
                produto,
                produtoControle,
                quantidade
        );
    }

    private void verificarEstoque(
            Produto produto
    ) {

        if (
                produto.getQtdUnidade()
                        <=
                        produto.getQtdMinimo()
        ) {

            estoqueNotifier.notificarObservers(
                    produto
            );
        }
    }
}