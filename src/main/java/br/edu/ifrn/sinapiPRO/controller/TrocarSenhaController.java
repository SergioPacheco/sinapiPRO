package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.UsuariosRepository;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.UsuarioService;

@Controller
public class TrocarSenhaController {

	@Autowired
	private UsuariosRepository usuariosRepository;

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/trocarSenha")
	public ModelAndView form() {
		return new ModelAndView("seguranca/TrocarSenha");
	}

	@PostMapping("/trocarSenha")
	public ModelAndView trocar(@RequestParam String novaSenha,
							   @RequestParam String confirmacao,
							   @AuthenticationPrincipal UsuarioSistema us,
							   RedirectAttributes attributes) {
		if (!novaSenha.equals(confirmacao)) {
			ModelAndView mv = new ModelAndView("seguranca/TrocarSenha");
			mv.addObject("mensagemErro", "Senhas não conferem");
			return mv;
		}
		try {
			Usuario usuario = usuariosRepository.findByEmail(us.getUsername()).orElseThrow();
			usuario.setSenha(novaSenha);
			usuario.setPrimeiroAcesso(false);
			usuarioService.salvar(usuario);
		} catch (Exception e) {
			ModelAndView mv = new ModelAndView("seguranca/TrocarSenha");
			mv.addObject("mensagemErro", e.getMessage());
			return mv;
		}
		attributes.addFlashAttribute("mensagem", "Senha alterada com sucesso!");
		return new ModelAndView("redirect:/");
	}
}
