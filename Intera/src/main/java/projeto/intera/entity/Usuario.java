package projeto.intera.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_usuario") // Avisa o Spring que o nome exato da tabela é esse
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // Avisa que a coluna do ID tem esse nome específico
    private Long idUsuario;

    private String username;

    private String cpf;

    private String senha;

    private String email;

    private String celular;

    // --- GETTERS E SETTERS ---

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
}