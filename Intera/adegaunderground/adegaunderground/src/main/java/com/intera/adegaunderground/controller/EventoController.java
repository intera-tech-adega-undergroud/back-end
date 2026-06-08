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

@RestController
@RequestMapping("/eventos")
@CrossOrigin(origins = "*")
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

        try {

            VendaBigNumberDTO dto =
                    eventoService.buscarVendasMes();

            if (dto == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(dto);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar vendas do mês");
        }
    }

    @GetMapping("/faturamento-dia")
    public ResponseEntity<?> buscarFaturamentoDia() {

        try {

            VendaBigNumberDTO dto =
                    eventoService.buscarFaturamentoDia();

            if (dto == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(dto);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar faturamento do dia");
        }
    }

    @GetMapping("/ranking-funcionarios")
    public ResponseEntity<?> buscarRankFuncionarios() {

        try {

            var lista =
                    eventoService.buscarRankFuncionarios();

            if (lista == null || lista.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(lista);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ranking de funcionários");
        }
    }

    @GetMapping("/grafico-dashboard")
    public ResponseEntity<?> buscarGraficoDash(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim
    ) {

        try {

            var lista =
                    eventoService.buscarGraficoDash(
                            dataInicio,
                            dataFim
                    );

            if (lista.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(lista);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body("Erro interno do servidor");
        }
    }

    @GetMapping("/fiados")
    public ResponseEntity<List<FiadoDTO>> listarFiados(

            @RequestParam(required = false)
            LocalDate dataInicio,

            @RequestParam(required = false)
            LocalDate dataFim

    ) {

        if (dataInicio != null && dataFim != null) {

            return ResponseEntity.ok(
                    eventoService.buscarFiadosPorPeriodo(
                            dataInicio,
                            dataFim
                    )
            );
        }

        return ResponseEntity.ok(
                eventoService.buscarFiados()
        );
    }

    @PostMapping("/venda")
    public ResponseEntity<?> registrarVenda(
            @RequestBody VendaDTO dto
    ) {

        try {

            Evento evento =
                    eventoService.salvarVenda(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(evento);

        } catch (Exception e){

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}