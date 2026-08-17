package com.intera.adegaunderground.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger securityLogger = LoggerFactory.getLogger(JwtFilter.class);

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/clientes",
            "/funcionarios/login"
    );

    @Autowired
    private TokenService tokenService;

    @Value("${app.security.swagger-public:false}")
    private boolean swaggerPublic;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        if (request.getMethod().equals("OPTIONS") || PUBLIC_PATHS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (swaggerPublic && (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {

                String email = tokenService.validarToken(token);

                if (email != null) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.emptyList()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                } else {
                    securityLogger.warn("Tentativa com token inválido na rota {}", path);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                    return;
                }

            } catch (RuntimeException e) {
                securityLogger.warn("Falha de autenticação JWT na rota {}", path, e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falha de autenticação");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}