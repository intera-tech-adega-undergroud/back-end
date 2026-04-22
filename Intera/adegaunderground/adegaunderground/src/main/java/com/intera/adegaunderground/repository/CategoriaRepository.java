package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
