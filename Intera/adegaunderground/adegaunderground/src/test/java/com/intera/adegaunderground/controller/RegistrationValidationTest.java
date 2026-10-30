package com.intera.adegaunderground.controller;

import com.intera.adegaunderground.config.GlobalExceptionHandler;
import com.intera.adegaunderground.entity.Cliente;
import com.intera.adegaunderground.entity.Categoria;
import com.intera.adegaunderground.entity.CategoriaBebida;
import com.intera.adegaunderground.entity.Produto;
import com.intera.adegaunderground.entity.UnidadeBebida;
import com.intera.adegaunderground.repository.CategoriaRepository;
import com.intera.adegaunderground.repository.ClienteRepository;
import com.intera.adegaunderground.repository.ProdutoRepository;
import com.intera.adegaunderground.repository.UnidadeBebidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RegistrationValidationTest {
    private MockMvc mvc;
    private ClienteRepository clients;
    private ProdutoRepository products;
    private UnidadeBebidaRepository bottles;
    private CategoriaRepository categories;

    @BeforeEach
    void setUp() {
        clients = mock(ClienteRepository.class);
        products = mock(ProdutoRepository.class);
        bottles = mock(UnidadeBebidaRepository.class);
        categories = mock(CategoriaRepository.class);
        ClienteController clientController = new ClienteController();
        ProdutoController productController = new ProdutoController();
        UnidadeBebidaController bottleController = new UnidadeBebidaController();
        ReflectionTestUtils.setField(clientController, "clienteRepository", clients);
        ReflectionTestUtils.setField(productController, "produtoRepository", products);
        ReflectionTestUtils.setField(productController, "categoriaRepository", categories);
        ReflectionTestUtils.setField(bottleController, "unidadeBebidaRepository", bottles);
        ReflectionTestUtils.setField(bottleController, "produtoRepository", products);
        when(clients.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        when(products.save(any(Produto.class))).thenAnswer(i -> i.getArgument(0));
        when(bottles.save(any(UnidadeBebida.class))).thenAnswer(i -> i.getArgument(0));
        mvc = MockMvcBuilders.standaloneSetup(clientController, productController, bottleController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void emptyProductAndBottleAreRejectedBeforePersistence() throws Exception {
        for (String path : new String[]{"/produtos", "/unidadesBebidas"}) {
            mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(products, bottles);
    }

    @Test
    void clientDefaultsApplyBeforeValidation() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"Teste","telefone":"11999999999"}
                """)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldoDevedor").value(0))
                .andExpect(jsonPath("$.compraFiado").value(false));
    }

    @Test
    void generatedClientIdCannotBeProvidedOnCreate() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("""
                {"idCliente":1,"nome":"Teste","telefone":"11999999999","saldoDevedor":0,"compraFiado":false}
                """)).andExpect(status().isBadRequest());
        verifyNoInteractions(clients);
    }

    @Test
    void unknownCategoryIsRejectedInsteadOfCreatingUnusableProduct() throws Exception {
        mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"Teste","preco":10,"embalagem":"LATA","volumeMl":350,
                 "qtdMinima":1,"qtdUnidade":-1,"categoria":"BEBIDA_UNIDADE"}
                """)).andExpect(status().isBadRequest());
        verify(products, never()).save(any());
    }

    @Test
    void negativeBottleVolumeIsRejected() throws Exception {
        mvc.perform(post("/unidadesBebidas").contentType(MediaType.APPLICATION_JSON).content("""
                {"mlInicial":750,"mlAtual":-1,"status":"ABERTO","produto":{"idProduto":1}}
                """)).andExpect(status().isBadRequest());
        verifyNoInteractions(bottles);
    }

    @Test
    void validProductCanStartWithNegativeStock() throws Exception {
        Categoria category = new Categoria();
        category.setIdCategoria(1);
        category.setCategoria(CategoriaBebida.BEBIDA_UNIDADE);
        when(categories.findByCategoria(CategoriaBebida.BEBIDA_UNIDADE)).thenReturn(category);
        mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"Teste","preco":10,"embalagem":"LATA","volumeMl":350,
                 "qtdMinima":1,"qtdUnidade":-1,"categoria":"BEBIDA_UNIDADE"}
                """)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.qtdUnidade").value(-1));
    }

    @Test
    void validBottleReferencesExistingProduct() throws Exception {
        Produto product = new Produto();
        product.setIdProduto(1);
        when(products.findById(1)).thenReturn(Optional.of(product));
        mvc.perform(post("/unidadesBebidas").contentType(MediaType.APPLICATION_JSON).content("""
                {"mlInicial":750,"mlAtual":500,"status":"ABERTO","produto":{"idProduto":1}}
                """)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.produto.idProduto").value(1));
    }

    @Test
    void bottleCannotReferenceMissingProduct() throws Exception {
        when(products.findById(999)).thenReturn(Optional.empty());
        mvc.perform(post("/unidadesBebidas").contentType(MediaType.APPLICATION_JSON).content("""
                {"mlInicial":750,"mlAtual":500,"status":"ABERTO","produto":{"idProduto":999}}
                """)).andExpect(status().isNotFound());
        verifyNoInteractions(bottles);
    }
}
