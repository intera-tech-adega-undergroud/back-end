package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.EventoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoPagamentoRepository extends JpaRepository<EventoPagamento, Integer> {
    List<EventoPagamento> findByEvento_IdEvento(Integer idEvento);
}
