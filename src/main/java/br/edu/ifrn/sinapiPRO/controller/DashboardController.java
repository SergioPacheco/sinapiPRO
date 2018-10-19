package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class DashboardController {

	@GetMapping("/")	
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		
		// count() VALOR NULO CAUSA ERRO
		
		//mv.addObject("totalOrcamentos",  orcamentos.count());
		//mv.addObject("totalInsumos",     insumos.count());
		//mv.addObject("totalComposicoes", composicoes.count());
		//mv.addObject("totalClientes",    clientes.count());
		
		mv.addObject("totalOrcamentos",  10);
		mv.addObject("totalInsumos",     10);
		mv.addObject("totalComposicoes", 10);
		mv.addObject("totalClientes",    10);
		
		return mv;
	}
	
}
