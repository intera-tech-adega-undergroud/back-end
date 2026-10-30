package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.PagamentoFiadoDTO;
import com.intera.adegaunderground.entity.Cliente;
import com.intera.adegaunderground.observer.EstoqueNotifier;
import com.intera.adegaunderground.repository.ClienteRepository;
import com.intera.adegaunderground.repository.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditValidationTest {
    private EventoService service;
    private ClienteRepository clients;
    private EventoRepository events;

    @BeforeEach
    void setUp() {
        service = new EventoService(mock(EstoqueNotifier.class));
        clients = mock(ClienteRepository.class);
        events = mock(EventoRepository.class);
        ReflectionTestUtils.setField(service, "clienteRepository", clients);
        ReflectionTestUtils.setField(service, "eventoRepository", events);
    }

    @Test
    void periodUsesFilteredQueryInsteadOfAllClients() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = start.plusDays(2);
        when(events.buscarTodosClientesFiadoPorPeriodo(start, end)).thenReturn(List.of());
        assertTrue(service.buscarFiadosPorPeriodo(start, end).isEmpty());
        verify(events).buscarTodosClientesFiadoPorPeriodo(start, end);
        verifyNoInteractions(clients);
    }

    @Test
    void invalidPeriodsAreRejected() {
        LocalDate day = LocalDate.of(2026, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> service.buscarFiadosPorPeriodo(day, null));
        assertThrows(IllegalArgumentException.class, () -> service.buscarFiadosPorPeriodo(day, day.minusDays(1)));
    }

    private PagamentoFiadoDTO payment(Integer id, double value) {
        return JsonMapper.builder().build().readValue(
                "{\"idCliente\":" + id + ",\"valorPagamento\":" + value + ",\"formaPagamento\":\"PIX\"}",
                PagamentoFiadoDTO.class);
    }

    @Test
    void missingClientReturnsNotFound() {
        when(clients.findById(999)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.registrarPagamentoFiado(payment(999, 1)));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void invalidPaymentsDoNotChangeDebt() {
        Cliente client = new Cliente();
        client.setSaldoDevedor(BigDecimal.TEN);
        when(clients.findById(1)).thenReturn(Optional.of(client));
        for (double value : List.of(0.0, -1.0, 11.0)) {
            assertThrows(IllegalArgumentException.class, () -> service.registrarPagamentoFiado(payment(1, value)));
        }
        assertEquals(BigDecimal.TEN, client.getSaldoDevedor());
        verify(clients, never()).save(any());
    }

    @Test
    void validPaymentSubtractsExactAmount() {
        Cliente client = new Cliente();
        client.setSaldoDevedor(new BigDecimal("10.10"));
        when(clients.findById(1)).thenReturn(Optional.of(client));
        service.registrarPagamentoFiado(payment(1, 0.1));
        assertEquals(0, new BigDecimal("10.00").compareTo(client.getSaldoDevedor()));
        verify(clients).save(client);
    }
}
