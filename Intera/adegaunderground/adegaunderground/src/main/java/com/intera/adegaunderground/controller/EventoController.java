package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.dto.FiadoDTO;
import com.intera.adegaunderground.dto.VendaBigNumberDTO;
import com.intera.adegaunderground.dto.VendaDTO;
import com.intera.adegaunderground.entity.Evento;
import com.intera.adegaunderground.repository.EventoRepository;
import com.intera.adegaunderground.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        return ResponseEntity.ok(eventoRepository.findAll());
    }

    @GetMapping("/vendas-mes")
    public ResponseEntity<?> buscarVendasMes() {
        VendaBigNumberDTO dto = eventoService.buscarVendasMes();
        return dto == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/faturamento-dia")
    public ResponseEntity<?> buscarFaturamentoDia() {
        VendaBigNumberDTO dto = eventoService.buscarFaturamentoDia();
        return dto == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/ranking-funcionarios")
    public ResponseEntity<?> buscarRankFuncionarios() {
        var lista = eventoService.buscarRankFuncionarios();
        return lista == null || lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @GetMapping("/grafico-dashboard")
    public ResponseEntity<?> buscarGraficoDash(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim
    ) {

        var lista = eventoService.buscarGraficoDash(dataInicio, dataFim);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @GetMapping("/fiados")
    public ResponseEntity<List<FiadoDTO>> listarFiados(

            @RequestParam(required = false)
            LocalDate dataInicio,

            @RequestParam(required = false)
            LocalDate dataFim

    ) {

        if (dataInicio != null || dataFim != null) {

            return ResponseEntity.ok(
                    eventoService.buscarFiadosPorPeriodo(
                            dataInicio,
                            dataFim
                    )
            );
        }

        return ResponseEntity.ok(
                eventoService.buscarTodosClientesFiado()
        );
    }

    @PostMapping("/venda")
    public ResponseEntity<?> registrarVenda(
            @Valid @RequestBody VendaDTO dto
    ) {
        Evento evento = eventoService.salvarVenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }
}