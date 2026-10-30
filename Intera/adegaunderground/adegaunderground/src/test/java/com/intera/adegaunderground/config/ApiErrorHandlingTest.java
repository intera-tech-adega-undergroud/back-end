package com.intera.adegaunderground.config;

import com.intera.adegaunderground.controller.ClienteController;
import com.intera.adegaunderground.controller.EventoController;
import com.intera.adegaunderground.controller.NotaFiscalController;
import com.intera.adegaunderground.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApiErrorHandlingTest {
    private MockMvc mvc;
    private ClienteRepository clients;

    @BeforeEach
    void setUp() {
        clients = mock(ClienteRepository.class);
        ClienteController controller = new ClienteController();
        ReflectionTestUtils.setField(controller, "clienteRepository", clients);
        mvc = MockMvcBuilders.standaloneSetup(controller, new EventoController(), new NotaFiscalController())
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").isNotEmpty());
        verifyNoInteractions(clients);
    }

    @Test
    void missingBodyAndDatesReturnBadRequest() throws Exception {
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/eventos/grafico-dashboard")).andExpect(status().isBadRequest());
    }

    @Test
    void unsupportedMethodAndMediaTypePreserveHttpStatus() throws Exception {
        mvc.perform(patch("/clientes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/clientes").contentType(MediaType.TEXT_PLAIN).content("{}"))
                .andExpect(status().isUnsupportedMediaType());
        mvc.perform(post("/notas-fiscais/ocr").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void databaseConflictDoesNotBecomeInternalError() throws Exception {
        when(clients.save(any())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("SQL details"));
        mvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"Teste","telefone":"11999999999","saldoDevedor":0,"compraFiado":false}
                """)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("SQL"))));
    }

    @Test
    void unexpectedFailureRemainsInternalError() throws Exception {
        when(clients.findAll()).thenThrow(new IllegalStateException("private details"));
        mvc.perform(get("/clientes")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.errorId").isNotEmpty());
    }

}
