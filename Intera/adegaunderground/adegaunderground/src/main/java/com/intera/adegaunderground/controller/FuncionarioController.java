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

    // INJETAMOS O SERVIÇO AQUI
    @Autowired
    private TokenService tokenService;

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
    }
}