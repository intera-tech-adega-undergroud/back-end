package com.intera.adegaunderground.entity;

import java.util.List;

public final class PermissaoCodigo {

    public static final String USUARIOS_VER = "usuarios_ver";
    public static final String USUARIOS_CRIAR = "usuarios_criar";
    public static final String USUARIOS_EDITAR = "usuarios_editar";
    public static final String USUARIOS_DESATIVAR = "usuarios_desativar";
    public static final String USUARIOS_RESET = "usuarios_reset";

    public static final String PRODUTOS_VER = "produtos_ver";
    public static final String PRODUTOS_CRIAR = "produtos_criar";
    public static final String PRODUTOS_EXCLUIR = "produtos_excluir";

    public static final String ESTOQUE_VER = "estoque_ver";
    public static final String ESTOQUE_ENTRADA = "estoque_entrada";
    public static final String ESTOQUE_AJUSTE = "estoque_ajuste";
    public static final String ESTOQUE_PERDAS = "estoque_perdas";
    public static final String ESTOQUE_HISTORICO = "estoque_historico";

    public static final String VENDAS_REGISTRAR = "vendas_registrar";
    public static final String VENDAS_DESCONTO = "vendas_desconto";
    public static final String VENDAS_CANCELAR = "vendas_cancelar";
    public static final String VENDAS_HISTORICO = "vendas_historico";

    public static final List<String> TODAS = List.of(
            USUARIOS_VER, USUARIOS_CRIAR, USUARIOS_EDITAR, USUARIOS_DESATIVAR, USUARIOS_RESET,
            PRODUTOS_VER, PRODUTOS_CRIAR, PRODUTOS_EXCLUIR,
            ESTOQUE_VER, ESTOQUE_ENTRADA, ESTOQUE_AJUSTE, ESTOQUE_PERDAS, ESTOQUE_HISTORICO,
            VENDAS_REGISTRAR, VENDAS_DESCONTO, VENDAS_CANCELAR, VENDAS_HISTORICO
    );

    private PermissaoCodigo() {
    }
}
