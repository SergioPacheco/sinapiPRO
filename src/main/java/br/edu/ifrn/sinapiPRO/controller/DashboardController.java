package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifrn.sinapiPRO.repository.Clientes;
import br.edu.ifrn.sinapiPRO.repository.Composicoes;
import br.edu.ifrn.sinapiPRO.repository.Insumos;
import br.edu.ifrn.sinapiPRO.repository.Orcamentos;

@Controller
public class DashboardController {

	@Autowired
	private Orcamentos orcamentos;
	
	@Autowired
	private Composicoes composicoes;
	
	@Autowired
	private Insumos insumos;
	
	@Autowired
	private Clientes clientes;
	
	@GetMapping("/")	
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		
		// count() VALOR NULO CAUSA ERRO
		
		//mv.addObject("totalOrcamentos",  orcamentos.count());
		//mv.addObject("totalInsumos",     insumos.count());
		//mv.addObject("totalComposicoes", composicoes.count());
		//mv.addObject("totalClientes",    clientes.count());
		
		mv.addObject("totalOrcamentos",  10);
		mv.addObject("totalInsumos",     insumos.count());
		mv.addObject("totalComposicoes", 10);
		mv.addObject("totalClientes",    10);
		
		return mv;
	}
	
}
