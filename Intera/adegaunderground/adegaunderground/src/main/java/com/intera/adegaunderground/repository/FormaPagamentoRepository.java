package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Integer> {
    Optional<FormaPagamento> findByCodigoAndAtivoTrue(String codigo);
}
