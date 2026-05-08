package br.edu.ifrn.sinapiPRO.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.UsuariosRepository;

@Controller
public class SegurancaController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SegurancaController.class);
	
	private final UsuariosRepository usuariosRepository;

	public SegurancaController(UsuariosRepository usuariosRepository) {
		this.usuariosRepository = usuariosRepository;
	}

	@GetMapping("/login")
	public String login(@AuthenticationPrincipal UserDetails  user) {
		
		Optional<Usuario> usuario = usuariosRepository.porEmailEAtivo("admin@sinapipro.com");
		if (usuario.isPresent()) {
			LOGGER.debug("Usuario administrativo padrao localizado. email={}, ativo={}",
					usuario.get().getEmail(), usuario.get().getAtivo());
		} else {
			LOGGER.debug("Usuario administrativo padrao nao cadastrado no banco de dados.");
		}
		
		if (user != null) {
			return "redirect:/orcamentos";
		}
		
		return "Login";
	}
	
	@GetMapping("/403")
	public String acessoNegado() {
		return "403";
	}
	
}
