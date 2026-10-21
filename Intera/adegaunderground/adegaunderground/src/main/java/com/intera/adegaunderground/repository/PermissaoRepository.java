package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PermissaoRepository extends JpaRepository<Permissao, Integer> {

    List<Permissao> findByPermissaoIn(Collection<String> permissoes);
}
