package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.entity.Cliente;
import com.intera.adegaunderground.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @GetMapping("/ordeneddivida")
    public ResponseEntity<List<Cliente>> ordenarPorDivida() {
        return ResponseEntity.ok(clienteRepository.findAll(
                Sort.by(Sort.Direction.DESC, "saldoDevedor").and(Sort.by("idCliente"))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listaPorId(@PathVariable Integer id) {
        return clienteRepository.findById(id)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<?> listaPorNome(@PathVariable String nome) {
        return clienteRepository.findByNomeIgnoreCase(nome)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody Cliente cliente) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    clienteExistente.setNome(cliente.getNome());
                    clienteExistente.setEmail(cliente.getEmail());
                    clienteExistente.setTelefone(cliente.getTelefone());

                    Cliente atualizado = clienteRepository.save(clienteExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody Cliente cliente) {
        if (cliente.getIdCliente() != null) {
            throw new IllegalArgumentException("Não envie idCliente no cadastro; o banco gera esse campo.");
        }

        if (cliente.getCompraFiado() == null) {
            cliente.setCompraFiado(false);
        }

        if (cliente.getSaldoDevedor() == null) {
            cliente.setSaldoDevedor(BigDecimal.ZERO);
        }

        Cliente novoCliente = clienteRepository.save(cliente);

        return ResponseEntity.status(201).body(novoCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}