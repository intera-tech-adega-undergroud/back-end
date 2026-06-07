package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.dto.EntradaEstoqueDTO;
import com.intera.adegaunderground.dto.ProdutoRequestDTO;
import com.intera.adegaunderground.entity.Categoria;
import com.intera.adegaunderground.entity.Produto;
import com.intera.adegaunderground.repository.CategoriaRepository;
import com.intera.adegaunderground.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        return ResponseEntity.ok(produtoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listaPorId(@PathVariable Integer id) {
        return produtoRepository.findById(id)
                .map(produto -> ResponseEntity.ok(produto))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<?> listaPorNome(@PathVariable String nome) {
        return produtoRepository.findByNomeIgnoreCase(nome)
                .map(produto -> ResponseEntity.ok(produto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody ProdutoRequestDTO produto) {

        Categoria categoria = categoriaRepository.findByCategoria(produto.getCategoria());

        return produtoRepository.findById(id)
                .map(produtoExistente -> {
                    produtoExistente.setNome(produto.getNome());
                    produtoExistente.setPreco(produto.getPreco());
                    produtoExistente.setQtdMinimo(produto.getQtdMinima());
                    produtoExistente.setVolumeMl(produto.getVolumeMl());
                    produtoExistente.setEmbalagem(produto.getEmbalagem());
                    produtoExistente.setCategoria(categoria);

                    Produto atualizado = produtoRepository.save(produtoExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ProdutoRequestDTO produto) {

        Categoria categoria = categoriaRepository.findByCategoria(produto.getCategoria());

        Produto novoProduto = new Produto();

        novoProduto.setAtivo(true);
        novoProduto.setEmbalagem(produto.getEmbalagem());
        novoProduto.setCategoria(categoria);
        novoProduto.setPreco(produto.getPreco());
        novoProduto.setNome(produto.getNome());
        novoProduto.setQtdUnidade(produto.getQtdUnidade());
        novoProduto.setQtdMinimo(produto.getQtdMinima());
        novoProduto.setVolumeMl(produto.getVolumeMl());

        Produto novoProdutoResposta = produtoRepository.save(novoProduto);
        return ResponseEntity.status(201).body(novoProdutoResposta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produtoRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/entrada")
    public ResponseEntity<?> entradaEstoque(
            @PathVariable Integer id,
            @RequestBody @Valid EntradaEstoqueDTO entrada) {

        return produtoRepository.findById(id)
                .map(produto -> {

                    produto.setQtdUnidade(
                            produto.getQtdUnidade() + entrada.getQuantidade()
                    );

                    Produto produtoAtualizado = produtoRepository.save(produto);

                    return ResponseEntity.ok(produtoAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

