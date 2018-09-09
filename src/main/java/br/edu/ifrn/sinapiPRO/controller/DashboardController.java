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
	
	@RequestMapping("/")	
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView("Index");
		return mv;
	}
	
	@GetMapping("/dashboard")
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		
		mv.addObject("totalOrcamentos",  orcamentos.count());
		mv.addObject("totalInsumos",     insumos.count());
		mv.addObject("totalComposicoes", composicoes.count());
		mv.addObject("totalClientes",    clientes.count());
		
		return mv;
	}
	
}
