package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.FiadoDTO;
import com.intera.adegaunderground.dto.GraficoDashDTO;
import com.intera.adegaunderground.dto.RankFuncionarioDTO;
import com.intera.adegaunderground.dto.VendaBigNumberDTO;
import com.intera.adegaunderground.entity.Cliente;
import com.intera.adegaunderground.entity.Evento;
import com.intera.adegaunderground.repository.ClienteRepository;
import com.intera.adegaunderground.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

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
            Integer dia = ((Number) resultado[0]).intValue();
            Double valorTotalVendas = ((Number) resultado[1]).doubleValue();

            lista.add(new GraficoDashDTO(dia, valorTotalVendas));
        }

        return lista;
    }

    public List<FiadoDTO> buscarFiados() {
        List<Cliente> clientes = clienteRepository.findByCompraFiadoTrue();

        List<FiadoDTO> lista = new ArrayList<>();

        for (Cliente cliente : clientes) {

            Evento ultimoEvento =
                    eventoRepository.findFirstByCliente_IdClienteOrderByDataHoraEventoDesc(
                            cliente.getIdCliente()
                    );

            lista.add(new FiadoDTO(
                    cliente.getIdCliente(),
                    cliente.getNome(),
                    cliente.getSaldoDevedor(),
                    ultimoEvento != null ? ultimoEvento.getDataHoraEvento() : null,
                    cliente.getSaldoDevedor() <= 0
            ));
        }

        return lista;
    }

    public List<FiadoDTO> buscarFiadosPorPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        return buscarFiados();
    }

    public List<FiadoDTO> buscarTodosFiados() {
        return buscarFiados();
    }
}