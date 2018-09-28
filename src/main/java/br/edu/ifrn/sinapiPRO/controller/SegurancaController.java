package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SegurancaController {

	@GetMapping("/login")
	public String login(@AuthenticationPrincipal User user) {
		if (user != null) {
			System.out.println(">>> SegurandaControlle orcamentos");
			return "redirect:/orcamentos";
		}
		System.out.println(">>> SegurandaControlle Login ");
		return "Login";
	}
	
	@GetMapping("/403")
	public String acessoNegado() {
		System.out.println(">>> SegurandaControlle 403");
		return "403";
	}
	
}
