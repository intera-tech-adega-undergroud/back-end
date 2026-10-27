package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.dto.FiadoDTO;
import com.intera.adegaunderground.dto.PagamentoFiadoDTO;
import com.intera.adegaunderground.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.intera.adegaunderground.dto.CreditPageDTO;
import com.intera.adegaunderground.service.CursorPaginationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fiados")
public class FiadoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private CursorPaginationService pagination;

    @GetMapping("/paginados")
    public ResponseEntity<CreditPageDTO> listCreditPage(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "Todos") String status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim
    ) {
        return ResponseEntity.ok(
                pagination.findCredit(
                        cursor,
                        limit,
                        status,
                        dataInicio,
                        dataFim
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<FiadoDTO>> listarFiados(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim
    ) {
        if (dataInicio == null || dataFim == null) {
            return ResponseEntity.ok(eventoService.buscarTodosClientesFiado());
        }

        return ResponseEntity.ok(eventoService.buscarFiadosPorPeriodo(dataInicio, dataFim));
    }

    @PostMapping("/pagamento")
    public ResponseEntity<Void> pagarFiado(@RequestBody PagamentoFiadoDTO dto) {
        eventoService.registrarPagamentoFiado(dto);
        return ResponseEntity.ok().build();
    }
}