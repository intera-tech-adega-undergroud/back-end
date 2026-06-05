package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByNomeIgnoreCase(String nome);

    List<Cliente> findByCompraFiadoTrue();
}