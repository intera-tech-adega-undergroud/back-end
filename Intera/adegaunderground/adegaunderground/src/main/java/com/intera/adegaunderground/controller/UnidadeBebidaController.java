package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.entity.StatusBebida;
import com.intera.adegaunderground.entity.UnidadeBebida;
import com.intera.adegaunderground.repository.UnidadeBebidaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/UnidadesBebidas")
@CrossOrigin(origins = "*")
public class UnidadeBebidaController {

    @Autowired
    private UnidadeBebidaRepository unidadeBebidaRepository;

    @GetMapping
    public ResponseEntity<List<UnidadeBebida>> listarUnidades() {
        return ResponseEntity.ok(unidadeBebidaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listaPorId(@PathVariable Integer id) {
        return unidadeBebidaRepository.findById(id)
                .map(unidade -> ResponseEntity.ok(unidade))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> listaPorStatus(@PathVariable StatusBebida status) {
        return ResponseEntity.ok(unidadeBebidaRepository.findByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody UnidadeBebida unidade) {
        return unidadeBebidaRepository.findById(id)
                .map(unidadeExistente -> {
                    unidadeExistente.setMlInicial(unidade.getMlInicial());
                    unidadeExistente.setMlAtual(unidade.getMlAtual());
                    unidadeExistente.setStatus(unidade.getStatus());
                    unidadeExistente.setProduto(unidade.getProduto());

                    UnidadeBebida atualizada = unidadeBebidaRepository.save(unidadeExistente);
                    return ResponseEntity.ok(atualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UnidadeBebida unidade) {
        UnidadeBebida nova = unidadeBebidaRepository.save(unidade);
        return ResponseEntity.status(201).body(nova);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        return unidadeBebidaRepository.findById(id)
                .map(unidade -> {
                    unidadeBebidaRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
