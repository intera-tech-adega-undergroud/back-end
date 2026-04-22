package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}
