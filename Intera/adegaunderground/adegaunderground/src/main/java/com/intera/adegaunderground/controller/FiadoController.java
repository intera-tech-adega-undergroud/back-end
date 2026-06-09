package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.dto.FiadoDTO;
import com.intera.adegaunderground.dto.PagamentoFiadoDTO;
import com.intera.adegaunderground.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fiados")
@CrossOrigin(origins = "*")
public class FiadoController {

    @Autowired
    private EventoService eventoService;

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