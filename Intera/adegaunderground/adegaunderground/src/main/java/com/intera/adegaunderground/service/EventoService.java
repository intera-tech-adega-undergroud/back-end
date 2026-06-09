package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.*;
import com.intera.adegaunderground.entity.*;
import com.intera.adegaunderground.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

            Double saldoDevedor = cliente.getSaldoDevedor() != null
                    ? cliente.getSaldoDevedor()
                    : 0.0;

            String status = saldoDevedor <= 0 ? "Pago" : "Em Aberto";

            lista.add(new FiadoDTO(
                    cliente.getIdCliente(),
                    cliente.getNome(),
                    saldoDevedor,
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
        return buscarTodosClientesFiado();
    }

    public List<FiadoDTO> buscarTodosFiados() {
        return buscarTodosClientesFiado();
    }

    public void registrarPagamentoFiado(PagamentoFiadoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Double saldoAtual = cliente.getSaldoDevedor() != null
                ? cliente.getSaldoDevedor()
                : 0.0;

        Double valorPagamento = dto.getValorPagamento();

        if (valorPagamento == null || valorPagamento <= 0) {
            throw new RuntimeException("Valor de pagamento inválido");
        }

        if (valorPagamento > saldoAtual) {
            throw new RuntimeException("Valor de pagamento maior que a dívida");
        }

        Double novoSaldo = saldoAtual - valorPagamento;

        cliente.setSaldoDevedor(novoSaldo);

        if (novoSaldo <= 0) {
            cliente.setSaldoDevedor(0.0);
        }

        clienteRepository.save(cliente);

        System.out.println("Pagamento fiado registrado");
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Valor pago: " + valorPagamento);
        System.out.println("Forma de pagamento: " + dto.getFormaPagamento());
    }

    private Double calcularTotal(VendaDTO dto){

        double total = 0;

        for(ItemVendaDTO itemDTO : dto.getItens()){

            Produto produto =
                    produtoRepository.findById(
                            itemDTO.getIdProduto()
                    ).orElseThrow();

            total += produto.getPreco() *
                    itemDTO.getQuantidade();
        }

        return total;
    }

    @Transactional
    public Evento salvarVenda(VendaDTO dto) {

        Evento evento = new Evento();
        evento.setTipo(TipoEvento.VENDA);
        evento.setFormaPagamento(dto.getFormaPagamento());
        evento.setPago(dto.getPago());
        evento.setDataHoraEvento(LocalDateTime.now());

        Funcionario funcionario = funcionarioRepository.findById(dto.getIdFuncionario()).orElseThrow();
        evento.setFuncionario(funcionario);

        if (dto.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(dto.getIdCliente()).orElseThrow();
            evento.setCliente(cliente);

            if (!dto.getPago()) {
                cliente.setSaldoDevedor(cliente.getSaldoDevedor() + calcularTotal(dto));
                clienteRepository.save(cliente);
            }
        }

        Evento eventoSalvo = eventoRepository.save(evento);

        for (ItemVendaDTO itemDTO : dto.getItens()) {

            Produto produto =
                    produtoRepository
                            .findById(itemDTO.getIdProduto())
                            .orElseThrow();

            String categoria =
                    produto.getCategoria()
                            .getCategoria()
                            .toString();

            if (categoria.equals("COMBO")) {

                List<Combo> itensCombo =
                        comboRepository.findByProdutoPai(produto);

                for (Combo combo : itensCombo) {

                    Produto produtoCombo =
                            combo.getProdutoFilho();

                    Integer quantidadeFinal =
                            combo.getQuantidade()
                                    * itemDTO.getQuantidade();

                    processarBaixaEstoque(
                            produtoCombo,
                            quantidadeFinal
                    );
                }

            } else {

                processarBaixaEstoque(
                        produto,
                        itemDTO.getQuantidade()
                );
            }

            ItemEvento item = new ItemEvento();

            item.setEvento(eventoSalvo);

            item.setProduto(produto);

            item.setQuantidade(
                    itemDTO.getQuantidade()
            );

            itemEventoRepository.save(item);
        }

        return eventoSalvo;
    }

    private void consumirMlUnidadeBebida(
            Produto produtoDose,
            Produto produtoControle,
            Integer quantidadeDosesVendidas
    ) {

        int mlTotalNecessario =
                (produtoDose.getVolumeMl() != null
                        ? produtoDose.getVolumeMl()
                        : 50)
                        * quantidadeDosesVendidas;

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
                                .findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(
                                        produtoControle.getIdProduto(),
                                        StatusBebida.FECHADO
                                )
                                .orElse(null);

                if (unidade != null) {

                    System.out.println(
                            "[DEBUG] Garrafa fechada encontrada. Abrindo..."
                    );

                    unidade.setStatus(
                            StatusBebida.ABERTO
                    );

                    if (
                            unidade.getMlAtual() == null
                                    || unidade.getMlAtual() <= 0
                    ) {

                        unidade.setMlAtual(
                                unidade.getMlInicial()
                        );
                    }

                    unidadeBebidaRepository.save(
                            unidade
                    );

                    int estoqueAnterior =
                            produtoControle.getQtdUnidade();

                    if (
                            produtoControle.getQtdUnidade() > 0
                    ) {

                        produtoControle.setQtdUnidade(
                                produtoControle.getQtdUnidade() - 1
                        );

                        produtoRepository.save(
                                produtoControle
                        );
                    }

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
                            produtoControle.getQtdUnidade() - 1
                    );

                    produtoRepository.save(
                            produtoControle
                    );

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

        String categoria =
                produto.getCategoria()
                        .getCategoria()
                        .toString();

        boolean exigeFracionamento =
                categoria.equals("BEBIDA_FRACIONADA")
                        || categoria.equals("DRINK");

        if (!exigeFracionamento) {

            produto.setQtdUnidade(
                    produto.getQtdUnidade() - quantidade
            );

            produtoRepository.save(produto);

            return;
        }

        Produto produtoControle =
                obterProdutoControleEstoque(produto);

        consumirMlUnidadeBebida(
                produto,
                produtoControle,
                quantidade
        );
    }
}