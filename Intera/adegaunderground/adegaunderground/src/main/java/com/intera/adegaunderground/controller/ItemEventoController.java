package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.entity.ItemEvento;
import com.intera.adegaunderground.repository.ItemEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itens-eventos")
public class ItemEventoController {

    @Autowired
    private ItemEventoRepository itemEventoRepository;

    @GetMapping
    public ResponseEntity<List<ItemEvento>> listar() {
        return ResponseEntity.status(200).body(itemEventoRepository.findAll());
    }
}
