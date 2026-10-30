package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.StatusBebida;
import com.intera.adegaunderground.entity.UnidadeBebida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UnidadeBebidaRepository extends JpaRepository<UnidadeBebida, Integer> {
    List<UnidadeBebida> findByStatus(StatusBebida status);

    Optional<UnidadeBebida>
    findFirstByProduto_IdProdutoAndStatusAndMlAtualGreaterThanOrderByIdUnidadeBebidaAsc(
            Integer idProduto, StatusBebida status, Integer mlAtual);

    Optional<UnidadeBebida>
    findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(
            Integer idProduto,
            StatusBebida status
    );
}
