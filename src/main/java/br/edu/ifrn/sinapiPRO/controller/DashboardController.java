package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifrn.sinapiPRO.repository.Clientes;
import br.edu.ifrn.sinapiPRO.repository.Composicoes;
import br.edu.ifrn.sinapiPRO.repository.Insumos;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;


@Controller
public class DashboardController {
	
	@Autowired
	private OrcamentosRepository orcamentosRepository;
	
	@Autowired
	private Insumos insumosRepository;
	
	@Autowired
	private Composicoes composicoesRepository;
	
	@Autowired
	private Clientes clientesRepository;
	
	

	@GetMapping("/")	
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		
		// count() VALOR NULO CAUSA ERRO
		
		//mv.addObject("totalOrcamentos",  orcamentos.count());
		//mv.addObject("totalInsumos",     insumos.count());
		//mv.addObject("totalComposicoes", composicoes.count());
		//mv.addObject("totalClientes",    clientes.count());
		
		mv.addObject("totalOrcamentos",   orcamentosRepository.count() );
		mv.addObject("totalInsumos",         insumosRepository.count() );
		mv.addObject("totalComposicoes", composicoesRepository.count() );
		mv.addObject("totalClientes",       clientesRepository.count() );
		
		return mv;
	}
	
}
