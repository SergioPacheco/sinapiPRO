package br.edu.ifrn.sinapiPRO.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.model.HistoricoSenha;
import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.HistoricoSenhaRepository;
import br.edu.ifrn.sinapiPRO.repository.UsuariosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.SenhaObrigatoriaUsuarioException;

@Service
public class UsuarioService {

	@Autowired
	private UsuariosRepository usuariosRepository;

	@Autowired
	private HistoricoSenhaRepository historicoSenhaRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public void salvar(Usuario usuario) {
		 
		Optional<Usuario> usuarioExistente = usuariosRepository.findByEmail(usuario.getEmail());
		
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new JaCadastradoException("E-mail já cadastrado");
		}
		
		if (usuario.isNovo() && StringUtils.isEmpty(usuario.getSenha())) {
			throw new SenhaObrigatoriaUsuarioException("Senha é obrigatória para novo usuário");
		}

		String senhaPlain = usuario.getSenha();
		boolean alterandoSenha = !StringUtils.isEmpty(senhaPlain);
		
		if (usuario.isNovo() || alterandoSenha) {
			if (!usuario.isNovo() && alterandoSenha) {
				validarHistoricoSenha(usuario, senhaPlain);
				salvarHistoricoSenha(usuarioExistente.get());
			}
			usuario.setSenha(this.passwordEncoder.encode(senhaPlain));
		} else {
			usuario.setSenha(usuarioExistente.get().getSenha());
		}
		usuario.setConfirmacaoSenha(usuario.getSenha());
		
		if (!usuario.isNovo() && usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioExistente.get().getAtivo());
		}
		
		usuariosRepository.save(usuario);

		if (usuario.isNovo()) {
			salvarHistoricoSenha(usuario);
		}
	}

	private void validarHistoricoSenha(Usuario usuario, String novaSenhaPlain) {
		List<HistoricoSenha> ultimas = historicoSenhaRepository
				.findTop3ByUsuarioOrderByDataCriacaoDesc(usuario);
		for (HistoricoSenha h : ultimas) {
			if (passwordEncoder.matches(novaSenhaPlain, h.getSenhaHash())) {
				throw new RuntimeException("A nova senha não pode ser igual às 3 últimas senhas utilizadas");
			}
		}
	}

	private void salvarHistoricoSenha(Usuario usuario) {
		HistoricoSenha h = new HistoricoSenha();
		h.setUsuario(usuario);
		h.setSenhaHash(usuario.getSenha());
		h.setDataCriacao(LocalDateTime.now());
		historicoSenhaRepository.save(h);
	}

	@Transactional
	public void alterarStatus(Long[] codigos, StatusUsuario statusUsuario) {
		statusUsuario.executar(codigos, usuariosRepository);
	}
	
	@Transactional
	public void alteraOrcamentoAtual(Usuario usuario, Long codigoOrcamentoAtual) {
		Optional<Usuario> usuarioExistente = usuariosRepository.findByEmail(usuario.getEmail());
		
		if (usuarioExistente.isPresent()) {
			Usuario editaUsuario = usuarioExistente.get();
			editaUsuario.setCodigoOrcamentoAtual(codigoOrcamentoAtual);
			usuariosRepository.save(usuario);
		}
		
	}
	 
	@Transactional 
	public Optional<Usuario> findByNome(String nome) {
        return usuariosRepository.findByNome(nome);
    }
	@Transactional 
	public Optional<Usuario> findByEmail(String email) {
        return usuariosRepository.findByEmail(email);
    }
	
}
