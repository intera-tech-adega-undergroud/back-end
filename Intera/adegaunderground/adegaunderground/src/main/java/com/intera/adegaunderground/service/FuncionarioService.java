package com.intera.adegaunderground.service;

import com.intera.adegaunderground.dto.FuncionarioCadastroDTO;
import com.intera.adegaunderground.entity.Funcionario;
import com.intera.adegaunderground.entity.Permissao;
import com.intera.adegaunderground.entity.PermissaoCodigo;
import com.intera.adegaunderground.repository.FuncionarioRepository;
import com.intera.adegaunderground.repository.PermissaoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final PermissaoRepository permissaoRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            PermissaoRepository permissaoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.permissaoRepository = permissaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Funcionario cadastrar(FuncionarioCadastroDTO dto, String emailCriador) {
        Set<String> solicitadas = dto.permissoes().stream()
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String codigo : solicitadas) {
            if (!PermissaoCodigo.TODAS.contains(codigo)) {
                throw new IllegalArgumentException("Permissão inválida: " + codigo);
            }
        }

        Funcionario criador = funcionarioRepository.findByEmail(emailCriador)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado"));

        // Impede que o usuário conceda mais acesso do que ele mesmo possui.
        Set<String> doCriador = criador.getPermissoes().stream()
                .map(Permissao::getPermissao)
                .collect(Collectors.toSet());
        if (!doCriador.containsAll(solicitadas)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode conceder permissões que não possui"
            );
        }

        String email = dto.email().trim().toLowerCase(Locale.ROOT);
        String cpf = dto.cpf().replaceAll("\\D", "");
        String celular = dto.celular().replaceAll("\\D", "");

        if (funcionarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        if (funcionarioRepository.existsByCpf(cpf)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        Funcionario novo = new Funcionario();
        novo.setNomeUsuario(dto.nome().trim() + " " + dto.sobrenome().trim());
        novo.setEmail(email);
        novo.setCpf(cpf);
        novo.setCelular(celular);
        novo.setSenhaCripto(passwordEncoder.encode(dto.senha()));
        novo.setPermissoes(new HashSet<>(permissaoRepository.findByPermissaoIn(solicitadas)));

        try {
            return funcionarioRepository.saveAndFlush(novo);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ou CPF já cadastrado");
        }
    }

    public Optional<Funcionario> autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return Optional.empty();
        }

        return funcionarioRepository.findByEmail(email.trim())
                .filter(f -> !Boolean.FALSE.equals(f.getAtivo()))
                .filter(f -> senhaConfere(senha, f.getSenhaCripto()));
    }

    public List<String> nomesDasPermissoes(Funcionario funcionario) {
        return funcionario.getPermissoes().stream()
                .map(Permissao::getPermissao)
                .sorted()
                .toList();
    }

    // Aceita senhas legadas em texto puro até que sejam redefinidas.
    private boolean senhaConfere(String informada, String armazenada) {
        if (armazenada == null) {
            return false;
        }
        if (armazenada.startsWith("$2")) {
            return passwordEncoder.matches(informada, armazenada);
        }
        return MessageDigest.isEqual(
                informada.getBytes(StandardCharsets.UTF_8),
                armazenada.getBytes(StandardCharsets.UTF_8)
        );
    }
}
