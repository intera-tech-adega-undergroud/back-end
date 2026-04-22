package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.entity.Combo;
import com.intera.adegaunderground.entity.Evento;
import com.intera.adegaunderground.repository.ComboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/combos")
public class ComboController {

    @Autowired
    private ComboRepository comboRepository;

    @GetMapping
    public ResponseEntity<List<Combo>> listar() {
        return ResponseEntity.status(200).body(comboRepository.findAll());
    }
}
