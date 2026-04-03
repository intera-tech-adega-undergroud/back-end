package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    // Busca o funcionário pelo e-mail e senha exatos
    Optional<Funcionario> findByEmailAndSenhaCripto(String email, String senhaCripto);
}