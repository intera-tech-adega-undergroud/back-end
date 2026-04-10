package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    Optional<Object> findByNomeIgnoreCase(String nome);
}
