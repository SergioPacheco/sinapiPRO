package br.edu.ifrn.sinapiPRO.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.UsuariosRepository;



@Controller
public class SegurancaController {
	
	@Autowired 
	private UsuariosRepository usu;

	@GetMapping("/login")
	public String login(@AuthenticationPrincipal UserDetails  user) {
		
		Optional<Usuario> usuario = usu.porEmailEAtivo("admin@sinapipro.com");
		if (usuario.isPresent()) {
			System.out.println("email="+usuario.get().getEmail()+" Ativo="+usuario.get().getAtivo()+" Senha="+usuario.get().getSenha() );
		} else {
			System.out.println("admin@sinapipro.com nao cadastrado no banco de dados");
		}
		
		if (user != null) {
			return "redirect:/orcamentos";
		}
		System.out.println("@AuthenticationPrincipal NULL??????");
		
		return "Login";
	}
	
	@GetMapping("/403")
	public String acessoNegado() {
		return "403";
	}
	
}
