package com.intera.adegaunderground.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.intera.adegaunderground.entity.Funcionario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Essa é a "assinatura" da sua adega. Em produção, isso ficaria escondido.
    private String secret = "adega_underground_secreta_123";

    public String gerarToken(Funcionario funcionario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Aqui montamos o "Crachá Digital"
            return JWT.create()
                    .withIssuer("AdegaUnderground") // Quem emitiu
                    .withSubject(funcionario.getEmail()) // De quem é o crachá
                    .withClaim("nivel", funcionario.getNivelAcesso()) // Guardamos o nível (ex: GERENTE)
                    .withExpiresAt(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"))) // Expira em 2 horas
                    .sign(algorithm); // Assina e tranca

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o token JWT", e);
        }
    }
}