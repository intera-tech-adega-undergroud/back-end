package projeto.intera;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.intera.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
