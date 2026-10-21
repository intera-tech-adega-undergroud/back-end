package com.intera.adegaunderground.config;

import com.intera.adegaunderground.entity.Funcionario;
import com.intera.adegaunderground.entity.Permissao;
import com.intera.adegaunderground.entity.PermissaoCodigo;
import com.intera.adegaunderground.repository.FuncionarioRepository;
import com.intera.adegaunderground.repository.PermissaoRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissaoInitializer implements ApplicationRunner {

    private final PermissaoRepository permissaoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public PermissaoInitializer(
            PermissaoRepository permissaoRepository,
            FuncionarioRepository funcionarioRepository
    ) {
        this.permissaoRepository = permissaoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Set<String> existentes = permissaoRepository.findAll().stream()
                .map(Permissao::getPermissao)
                .collect(Collectors.toSet());

        for (String codigo : PermissaoCodigo.TODAS) {
            if (!existentes.contains(codigo)) {
                Permissao nova = new Permissao();
                nova.setPermissao(codigo);
                permissaoRepository.save(nova);
            }
        }

        // Contas criadas antes do controle de permissões mantêm o acesso total.
        List<Permissao> conhecidas = permissaoRepository.findByPermissaoIn(PermissaoCodigo.TODAS);
        for (Funcionario legado : funcionarioRepository.findSemPermissoesConhecidas(PermissaoCodigo.TODAS)) {
            legado.getPermissoes().addAll(conhecidas);
        }
    }
}
