package projeto.intera;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.intera.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Não esqueça deste import!

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Não esqueça deste import!
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Não esqueça deste import!

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring faz a mágica do SQL apenas lendo este nome:
    Optional<Usuario> findByEmailAndSenha(String email, String senha);
}
