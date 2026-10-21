package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.config.TokenService;
import com.intera.adegaunderground.dto.FuncionarioCadastroDTO;
import com.intera.adegaunderground.dto.FuncionarioResponseDTO;
import com.intera.adegaunderground.dto.LoginResponseDTO;
import com.intera.adegaunderground.entity.Funcionario;
import com.intera.adegaunderground.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "Cadastrar um novo funcionário com suas permissões")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para cadastrar ou conceder as permissões"),
            @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrados")
    })
    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> cadastrar(
            @Valid @RequestBody FuncionarioCadastroDTO dto,
            Authentication authentication
    ) {
        Funcionario criado = funcionarioService.cadastrar(dto, authentication.getName());

        return ResponseEntity.status(201).body(
                new FuncionarioResponseDTO(
                        criado.getIdFuncionario(),
                        criado.getNomeUsuario(),
                        criado.getEmail(),
                        funcionarioService.nomesDasPermissoes(criado)
                )
        );
    }

    @Operation(summary = "Realizar login do funcionário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso (retorna token JWT)"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> fazerLogin(
            @RequestBody Funcionario tentativaLogin
    ) {
        Optional<Funcionario> funcionarioEncontrado =
                funcionarioService.autenticar(
                        tentativaLogin.getEmail(),
                        tentativaLogin.getSenhaCripto()
                );

        if (funcionarioEncontrado.isPresent()) {
            Funcionario funcionario = funcionarioEncontrado.get();

            return ResponseEntity.ok(
                    new LoginResponseDTO(
                            tokenService.gerarToken(funcionario),
                            funcionario.getIdFuncionario(),
                            funcionario.getNomeUsuario(),
                            "Funcionário",
                            funcionarioService.nomesDasPermissoes(funcionario)
                    )
            );
        }

        return ResponseEntity.status(401).build();
    } // Fim do método de login
}