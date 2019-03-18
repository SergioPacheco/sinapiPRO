package br.edu.ifrn.sinapiPRO.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;


@Controller
public class DashboardController {
	
	@Autowired
	private OrcamentosRepository orcamentosRepository;
	
	@Autowired
	private InsumosRepository insumosRepository;
	
	@Autowired
	private ComposicaoRepository composicoesRepository;
	
	@Autowired
	private ClientesRepository clientesRepository;
	
	@GetMapping("/")	
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		mv.addObject("totalOrcamentos",   orcamentosRepository.count() );
		mv.addObject("totalInsumos",         insumosRepository.count() );
		mv.addObject("totalComposicoes", composicoesRepository.count() );
		mv.addObject("totalClientes",       clientesRepository.count() );
			
		return mv;
	}
	
}
