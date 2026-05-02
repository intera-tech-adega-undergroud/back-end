package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.config.TokenService;
import com.intera.adegaunderground.entity.Funcionario;
import com.intera.adegaunderground.repository.FuncionarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private TokenService tokenService;

    // --- ROTA NOVA: PARA CADASTRAR O DONO DO ERP ---
    @Operation(summary = "Cadastrar um novo funcionário (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar (email ou CPF duplicado)")
    })
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
    @Operation(summary = "Realizar login do funcionário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso (retorna token JWT)"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos")
    })
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