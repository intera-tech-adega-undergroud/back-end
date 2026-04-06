package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.StatusBebida;
import com.intera.adegaunderground.entity.UnidadeBebida;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeBebidaRepository extends JpaRepository<UnidadeBebida, Integer> {
    @Nullable Object findByStatus(StatusBebida status);
}
