package com.intera.adegaunderground.config;

import com.intera.adegaunderground.entity.Funcionario;
import com.intera.adegaunderground.repository.FuncionarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void clientsGetProcessesBearerToken() throws Exception {
        TokenService tokens = mock(TokenService.class);
        FuncionarioRepository employees = mock(FuncionarioRepository.class);
        Funcionario employee = new Funcionario();
        employee.setEmail("test@example.com");
        when(tokens.validarToken("valid")).thenReturn(employee.getEmail());
        when(employees.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        JwtFilter filter = new JwtFilter();
        ReflectionTestUtils.setField(filter, "tokenService", tokens);
        ReflectionTestUtils.setField(filter, "funcionarioRepository", employees);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/clientes");
        request.setServletPath("/clientes");
        request.addHeader("Authorization", "Bearer valid");
        filter.doFilter(request, new MockHttpServletResponse(), (req, res) ->
                assertNotNull(SecurityContextHolder.getContext().getAuthentication()));
    }

    @Test
    void clientsPostRemainsPublic() throws Exception {
        JwtFilter filter = new JwtFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/clientes");
        request.setServletPath("/clientes");
        filter.doFilter(request, new MockHttpServletResponse(), (req, res) ->
                assertNull(SecurityContextHolder.getContext().getAuthentication()));
    }
}
