package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.StatusBebida;
import com.intera.adegaunderground.entity.UnidadeBebida;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.List;

public interface UnidadeBebidaRepository extends JpaRepository<UnidadeBebida, Integer> {
    List<UnidadeBebida> findByStatus(StatusBebida status);

    // Leituras com trava (SELECT ... FOR UPDATE): leem o dado mais recente já confirmado e impedem
    // que duas vendas simultâneas consumam o mesmo ml.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UnidadeBebida>
    findFirstByProduto_IdProdutoAndStatusAndMlAtualGreaterThanOrderByIdUnidadeBebidaAsc(
            Integer idProduto, StatusBebida status, Integer mlAtual);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UnidadeBebida>
    findFirstByProduto_IdProdutoAndStatusOrderByIdUnidadeBebidaAsc(
            Integer idProduto,
            StatusBebida status
    );
}
