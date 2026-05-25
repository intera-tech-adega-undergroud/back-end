package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.dto.VendaBigNumberDTO;
import com.intera.adegaunderground.entity.Evento;
import com.intera.adegaunderground.repository.EventoRepository;
import com.intera.adegaunderground.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        return ResponseEntity.status(200).body(eventoRepository.findAll());
    }

    @GetMapping("/vendas-mes")
    public ResponseEntity<?> buscarVendasMes() {

        try {

            VendaBigNumberDTO dto = eventoService.buscarVendasMes();

            if (dto == null) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dto);

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

                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dto);

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

                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(lista);

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

                return ResponseEntity
                        .noContent()
                        .build();
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
}
