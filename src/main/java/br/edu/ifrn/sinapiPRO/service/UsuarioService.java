package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.UsuariosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.SenhaObrigatoriaUsuarioException;

@Service
public class UsuarioService {

	@Autowired
	private UsuariosRepository usuariosRepository;

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
		
		if (usuario.isNovo() || !StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
		} else if (StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(usuarioExistente.get().getSenha());
		}
		usuario.setConfirmacaoSenha(usuario.getSenha());
		
		if (!usuario.isNovo() && usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioExistente.get().getAtivo());
		}
		
		usuario.setSenha(this.passwordEncoder.encode("admin")); // erro ao validar
		
		usuariosRepository.save(usuario);
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
