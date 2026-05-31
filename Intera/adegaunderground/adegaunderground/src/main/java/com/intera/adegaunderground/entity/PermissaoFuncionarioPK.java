package com.intera.adegaunderground.entity;

import java.io.Serializable;
import java.util.Objects;

public class PermissaoFuncionarioPK implements Serializable {
    private Integer funcionario;
    private Integer permissao;

    public PermissaoFuncionarioPK() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissaoFuncionarioPK that = (PermissaoFuncionarioPK) o;
        return Objects.equals(funcionario, that.funcionario) && Objects.equals(permissao, that.permissao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcionario, permissao);
    }
}

