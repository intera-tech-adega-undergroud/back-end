package projeto.intera.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import projeto.intera.entity.Usuario;
import projeto.intera.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*") // Essencial para o seu Front-end conseguir conectar!
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @PostMapping
    public Usuario salvar(@RequestBody Usuario usuario) {
        return repository.save(usuario);
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return repository.findAll();

    }

    @PostMapping("/login")
    public ResponseEntity<String> fazerLogin(@RequestBody Usuario tentativaLogin) {

        // Chama o método mágico que criamos no Repository
        Optional<Usuario> usuarioEncontrado = repository.findByEmailAndSenha(
                tentativaLogin.getEmail(),
                tentativaLogin.getSenha()
        );

        if (usuarioEncontrado.isPresent()) {
            // Se achou, retorna status 200 (OK)
            return ResponseEntity.ok("Bem-vindo(a), " + usuarioEncontrado.get().getUsername() + "!");
        } else {
            // Se não achou, retorna status 401 (Unauthorized / Não Autorizado)
            return ResponseEntity.status(401).body("E-mail ou senha incorretos!");
        }
    }

}
