package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tbl_funcionario")
@Schema(description = "Representa um funcionário da adega")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do funcionário", example = "1")
    private Integer idFuncionario;

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Schema(description = "Nome de usuário do funcionário", example = "admin")
    private String nomeUsuario;

    @NotBlank(message = "Senha é obrigatório")
    @Schema(description = "Senha criptografada do funcionário", example = "*****", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String senhaCripto;

    @NotBlank(message = "Nível de acesso é obrigatório")
    @Schema(description = "Nível de acesso do funcionário", example = "ADMIN")
    private String nivelAcesso;

    @NotBlank(message = "CPF é obrigatório")
    @Schema(description = "CPF do funcionário", example = "12345678900")
    private String cpf;

    @NotBlank(message = "Celular é obrigatório")
    @Schema(description = "Número de celular do funcionário", example = "11999999999")
    private String celular;

    @NotBlank(message = "Email é obrigatório")
    @Schema(description = "Email do funcionário", example = "admin@adega.com")
    private String email;

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenhaCripto() {
        return senhaCripto;
    }

    public void setSenhaCripto(String senhaCripto) {
        this.senhaCripto = senhaCripto;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}