package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "permissao_funcionario")
@Schema(description = "Representa uma lista das permissões de cada usuario")
public class PermissaoFuncionario {

    @Id
    @NotNull(message = "Funcionário é obrigatório")
    @ManyToOne
    @JoinColumn(name = "tbl_funcionario_id_funcionario", nullable = false)
    @Schema(description = "Funcionário associado")
    private Funcionario funcionario;

    @Id
    @NotNull(message = "Permissão é obrigatória")
    @ManyToOne
    @JoinColumn(name = "dmsao_permissao_id_permissao", nullable = false)
    @Schema(description = "Permissão associada")
    private Permissao permissao;

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Permissao getPermissao() {
        return permissao;
    }

    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }
}
