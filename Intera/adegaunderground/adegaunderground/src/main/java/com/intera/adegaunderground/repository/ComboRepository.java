package com.intera.adegaunderground.repository;

import com.intera.adegaunderground.entity.Combo;
import com.intera.adegaunderground.entity.ComboPK; // Certifique-se de importar sua classe PK
import com.intera.adegaunderground.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 1. Ajustado de Long para ComboPK por causa da chave composta @IdClass
public interface ComboRepository extends JpaRepository<Combo, ComboPK> {

    // 2. Ajustado de findByCombo para findByProdutoPai
    List<Combo> findByProdutoPai(Produto produtoPai);
}