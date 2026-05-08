package br.edu.ifrn.sinapiPRO.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.edu.ifrn.sinapiPRO.model.HistoricoSenha;
import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.HistoricoSenhaRepository;
import br.edu.ifrn.sinapiPRO.repository.UsuariosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.UsuarioFilter;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.SenhaObrigatoriaUsuarioException;
import br.edu.ifrn.sinapiPRO.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class UsuarioService extends AbstractFilterableUniqueFieldCrudService<Usuario, UsuarioFilter, UsuariosRepository, String> {

	private final UsuariosRepository usuariosRepository;
	private final HistoricoSenhaRepository historicoSenhaRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(
			UsuariosRepository usuariosRepository,
			HistoricoSenhaRepository historicoSenhaRepository,
			PasswordEncoder passwordEncoder) {
		super(
				usuariosRepository,
				Usuario::getCodigo,
				Usuario::getEmail,
				usuariosRepository::findByEmail,
				"E-mail já cadastrado",
				"Impossível apagar o usuário.",
				"Usuário não encontrado.");
		this.usuariosRepository = usuariosRepository;
		this.historicoSenhaRepository = historicoSenhaRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public Usuario salvar(Usuario usuario) {
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
			usuario.setSenha(passwordEncoder.encode(senhaPlain));
		} else {
			usuario.setSenha(usuarioExistente.get().getSenha());
		}
		usuario.setConfirmacaoSenha(usuario.getSenha());

		if (!usuario.isNovo() && usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioExistente.get().getAtivo());
		}

		Usuario usuarioSalvo = usuariosRepository.save(usuario);

		if (usuario.isNovo()) {
			salvarHistoricoSenha(usuarioSalvo);
		}

		return usuarioSalvo;
	}

	private void validarHistoricoSenha(Usuario usuario, String novaSenhaPlain) {
		List<HistoricoSenha> ultimas = historicoSenhaRepository.findTop3ByUsuarioOrderByDataCriacaoDesc(usuario);
		for (HistoricoSenha historicoSenha : ultimas) {
			if (passwordEncoder.matches(novaSenhaPlain, historicoSenha.getSenhaHash())) {
				throw new RuntimeException("A nova senha não pode ser igual às 3 últimas senhas utilizadas");
			}
		}
	}

	private void salvarHistoricoSenha(Usuario usuario) {
		HistoricoSenha historicoSenha = new HistoricoSenha();
		historicoSenha.setUsuario(usuario);
		historicoSenha.setSenhaHash(usuario.getSenha());
		historicoSenha.setDataCriacao(LocalDateTime.now());
		historicoSenhaRepository.save(historicoSenha);
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
			usuariosRepository.save(editaUsuario);
		}
	}

	@Transactional(readOnly = true)
	public Optional<Usuario> findByNome(String nome) {
		return usuariosRepository.findByNome(nome);
	}

	@Transactional(readOnly = true)
	public Optional<Usuario> findByEmail(String email) {
		return usuariosRepository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	public Usuario buscarComGrupos(Long codigo) {
		return usuariosRepository.buscarComGrupos(codigo);
	}
}
