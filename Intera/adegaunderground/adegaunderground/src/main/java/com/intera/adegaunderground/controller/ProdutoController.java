package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.dto.EntradaEstoqueDTO;
import com.intera.adegaunderground.dto.ProdutoRequestDTO;
import com.intera.adegaunderground.entity.Categoria;
import com.intera.adegaunderground.entity.CategoriaBebida;
import com.intera.adegaunderground.entity.Funcionario;
import com.intera.adegaunderground.entity.Produto;
import com.intera.adegaunderground.repository.FuncionarioRepository;
import com.intera.adegaunderground.repository.CategoriaRepository;
import com.intera.adegaunderground.repository.ProdutoRepository;
import com.intera.adegaunderground.dto.CursorPage;
import com.intera.adegaunderground.dto.StockProductDTO;
import com.intera.adegaunderground.service.CursorPaginationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private CursorPaginationService pagination;

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

    @GetMapping("/paginados")
    public ResponseEntity<CursorPage<StockProductDTO>> listStockPage(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "") String busca
    ) {
        return ResponseEntity.ok(
                pagination.findStock(cursor, limit, busca)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody ProdutoRequestDTO produto) {

        return produtoRepository.findById(id)
                .map(produtoExistente -> {
                    Categoria categoria = findCategory(produto);
                    produtoExistente.setNome(produto.nome());
                    if (produtoExistente.getPreco() == null
                            || produtoExistente.getPreco().compareTo(produto.preco()) != 0) {
                        produtoExistente.setPrecoAlteradoPor(funcionarioLogadoId());
                    }
                    produtoExistente.setPreco(produto.preco());
                    produtoExistente.setQtdMinimo(produto.qtdMinima());
                    produtoExistente.setVolumeMl(produto.volumeMl());
                    produtoExistente.setEmbalagem(produto.embalagem());
                    produtoExistente.setCategoria(categoria);

                    Produto atualizado = produtoRepository.save(produtoExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ProdutoRequestDTO produto) {

        Categoria categoria = findCategory(produto);

        Produto novoProduto = new Produto();

        novoProduto.setAtivo(true);
        novoProduto.setEmbalagem(produto.embalagem());
        novoProduto.setCategoria(categoria);
        novoProduto.setPreco(produto.preco());
        novoProduto.setNome(produto.nome());
        novoProduto.setQtdUnidade(produto.qtdUnidade());
        novoProduto.setQtdMinimo(produto.qtdMinima());
        novoProduto.setVolumeMl(produto.volumeMl());

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
                    if (produto.getQtdUnidade() == null) {
                        throw new IllegalArgumentException("Produto sem quantidade de estoque cadastrada.");
                    }

                    produto.setQtdUnidade(
                            Math.addExact(produto.getQtdUnidade(), entrada.getQuantidade())
                    );

                    Produto produtoAtualizado = produtoRepository.save(produto);

                    return ResponseEntity.ok(produtoAtualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Categoria findCategory(ProdutoRequestDTO produto) {
        Categoria categoria = categoriaRepository.findByCategoria(produto.categoria());
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não cadastrada. Consulte GET /categorias.");
        }
        if ((produto.categoria() == CategoriaBebida.BEBIDA_FRACIONADA
                || produto.categoria() == CategoriaBebida.DRINK)
                && produto.volumeMl() <= 0) {
            throw new IllegalArgumentException("Produtos fracionados e drinks precisam de volume maior que zero.");
        }
        return categoria;
    }

    /** Id do funcionário autenticado (o JWT guarda o e-mail), ou null se não houver. */
    private Integer funcionarioLogadoId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        return funcionarioRepository.findByEmail(auth.getName())
                .map(Funcionario::getIdFuncionario)
                .orElse(null);
    }
}
