package com.intera.adegaunderground.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.intera.adegaunderground.entity.PermissaoCodigo.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

        @Value("${app.security.swagger-public:false}")
        private boolean swaggerPublic;

        @Value("${app.security.cors.allowed-origins:http://localhost:5173}")
        private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            HttpMethod.OPTIONS,
                            "/**"
                    ).permitAll();

                    auth.requestMatchers(
                            HttpMethod.POST,
                            "/funcionarios/login",
                            "/clientes"
                    ).permitAll();

                    if (swaggerPublic) {
                        auth.requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll();
                    } else {
                        auth.requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).authenticated();
                    }

                    auth.requestMatchers(HttpMethod.GET, "/funcionarios", "/funcionarios/**").hasAuthority(USUARIOS_VER);
                    auth.requestMatchers(HttpMethod.POST, "/funcionarios").hasAuthority(USUARIOS_CRIAR);
                    auth.requestMatchers(HttpMethod.PUT, "/funcionarios/**").hasAuthority(USUARIOS_EDITAR);

                    auth.requestMatchers(HttpMethod.GET, "/produtos", "/produtos/**").hasAuthority(PRODUTOS_VER);
                    auth.requestMatchers(HttpMethod.POST, "/produtos").hasAuthority(PRODUTOS_CRIAR);
                    auth.requestMatchers(HttpMethod.PUT, "/produtos/**").hasAuthority(PRODUTOS_CRIAR);
                    auth.requestMatchers(HttpMethod.DELETE, "/produtos/**").hasAuthority(PRODUTOS_EXCLUIR);
                    auth.requestMatchers(HttpMethod.PATCH, "/produtos/*/entrada").hasAuthority(ESTOQUE_ENTRADA);
                    auth.requestMatchers(HttpMethod.POST, "/notas-fiscais/**").hasAuthority(ESTOQUE_ENTRADA);

                    auth.requestMatchers(HttpMethod.GET, "/unidadesBebidas", "/unidadesBebidas/**").hasAuthority(ESTOQUE_VER);
                    auth.requestMatchers(HttpMethod.POST, "/unidadesBebidas").hasAuthority(ESTOQUE_AJUSTE);
                    auth.requestMatchers(HttpMethod.PUT, "/unidadesBebidas/**").hasAuthority(ESTOQUE_AJUSTE);
                    auth.requestMatchers(HttpMethod.DELETE, "/unidadesBebidas/**").hasAuthority(ESTOQUE_PERDAS);

                    auth.requestMatchers(HttpMethod.POST, "/eventos/venda").hasAuthority(VENDAS_REGISTRAR);
                    auth.requestMatchers(HttpMethod.GET, "/eventos").hasAuthority(VENDAS_HISTORICO);

                    auth.requestMatchers(
                            "/fiados/**",
                            "/clientes/**",
                            "/funcionarios/**",
                            "/notas-fiscais/**"
                    ).authenticated();

                    auth.anyRequest().authenticated();
                })

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        List<String> parsedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .collect(Collectors.toList());

        configuration.setAllowedOrigins(parsedOrigins);

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setExposedHeaders(
                Arrays.asList("Authorization")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}