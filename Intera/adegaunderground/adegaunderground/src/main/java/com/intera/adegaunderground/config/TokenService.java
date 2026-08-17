package com.intera.adegaunderground.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.algorithms.Algorithm;
import com.intera.adegaunderground.entity.Funcionario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final String secret;
    private final String issuer;
    private final long expirationHours;

    public TokenService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.issuer:AdegaUnderground}") String issuer,
            @Value("${app.security.jwt.expiration-hours:2}") long expirationHours
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("O segredo JWT deve ter ao menos 32 caracteres.");
        }
        this.secret = secret;
        this.issuer = issuer;
        this.expirationHours = expirationHours;
    }

    public String gerarToken(Funcionario funcionario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(funcionario.getEmail())
                    .withExpiresAt(LocalDateTime.now().plusHours(expirationHours).toInstant(ZoneOffset.of("-03:00")))
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            logger.error("Falha ao gerar token JWT para o usuário {}", funcionario.getEmail(), e);
            throw new RuntimeException("Erro ao gerar o token JWT", e);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException e) {
            logger.warn("Token JWT inválido ou expirado.");
            return null;
        }
    }
}