package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.config.TokenService;
import com.intera.adegaunderground.entity.Funcionario;
import com.intera.adegaunderground.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private TokenService tokenService;

    // --- ROTA NOVA: PARA CADASTRAR O DONO DO ERP ---
    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody Funcionario novoFuncionario) {
        try {
            repository.save(novoFuncionario);
            return ResponseEntity.status(201).body("Cliente (Admin) cadastrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao cadastrar: E-mail ou CPF já existem.");
        }
    } // Fim do método de cadastrar

    // --- ROTA ANTIGA QUE JÁ ESTAVA AÍ: LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<String> fazerLogin(@RequestBody Funcionario tentativaLogin) {

        Optional<Funcionario> funcionarioEncontrado = repository.findByEmailAndSenhaCripto(
                tentativaLogin.getEmail(),
                tentativaLogin.getSenhaCripto()
        );

        if (funcionarioEncontrado.isPresent()) {
            // GERAMOS O TOKEN JWT!
            String token = tokenService.gerarToken(funcionarioEncontrado.get());

            // Devolvemos o token para o Front-end
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("E-mail ou senha incorretos!");
        }
    } // Fim do método de login
}