package com.intera.adegaunderground.service;

import com.intera.adegaunderground.entity.*;
import com.intera.adegaunderground.repository.MovimentoEstoqueRepository;
import com.intera.adegaunderground.repository.MovimentoVolumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Grava os livros de movimentação (estoque em unidades e volume em ml). Não decide nada sobre
 * saldo: o estoque negativo continua permitido e a venda nunca é bloqueada por ele.
 * Deve ser chamado dentro da transação da venda.
 */
@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private MovimentoEstoqueRepository estoqueRepository;

    @Autowired
    private MovimentoVolumeRepository volumeRepository;

    public void registrarEstoque(Produto produto, TipoMovimentoEstoque tipo, int delta, int saldoAnterior,
                                 Integer idEvento, Integer idFuncionario) {
        MovimentoEstoque m = new MovimentoEstoque();
        m.setIdProduto(produto.getIdProduto());
        m.setTipo(tipo);
        m.setQuantidadeDelta(delta);
        m.setSaldoAnterior(saldoAnterior);
        m.setSaldoPosterior(Math.addExact(saldoAnterior, delta));
        m.setIdEvento(idEvento);
        m.setIdFuncionario(idFuncionario);
        estoqueRepository.save(m);
    }

    public void registrarVolume(UnidadeBebida unidade, TipoMovimentoVolume tipo, int mlDelta, int mlAnterior,
                                Integer idProdutoVendido, Integer idEvento, Integer idFuncionario) {
        MovimentoVolume m = new MovimentoVolume();
        m.setIdUnidadeBebida(unidade.getIdUnidadeBebida());
        m.setTipo(tipo);
        m.setMlDelta(mlDelta);
        m.setMlAnterior(mlAnterior);
        m.setMlPosterior(mlAnterior + mlDelta);
        m.setIdProdutoVendido(idProdutoVendido);
        m.setIdEvento(idEvento);
        m.setIdFuncionario(idFuncionario);
        volumeRepository.save(m);
    }
}
