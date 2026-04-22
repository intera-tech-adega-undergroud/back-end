package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.entity.Evento;
import com.intera.adegaunderground.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        return ResponseEntity.status(200).body(eventoRepository.findAll());
    }
}
