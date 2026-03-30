package com.intera.adegaunderground.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tbl_funcionario")
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFuncionario;
    @NotBlank(message = "Nome de usuário é obrigatório")
    private String nomeUsuario;
    @NotBlank(message = "Senha é obrigatório")
    private String senhaCripto;
    @NotBlank(message = "Nível de acesso é obrigatório")
    private String nivelAcesso;
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;
    @NotBlank(message = "Celular é obrigatório")
    private String celular;
    @NotBlank(message = "Email é obrigatório")
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
