package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    @Query("select distinct f from Funcionario f left join fetch f.permissoes where f.email = :email")
    Optional<Funcionario> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    // Funcionários anteriores ao controle de permissões, que ficariam sem acesso algum.
    @Query("select f from Funcionario f where not exists " +
            "(select p.idPermissao from f.permissoes p where p.permissao in :codigos)")
    List<Funcionario> findSemPermissoesConhecidas(@Param("codigos") Collection<String> codigos);
}