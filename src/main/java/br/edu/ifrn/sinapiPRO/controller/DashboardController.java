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
	
	@GetMapping("/")	
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
		mv.addObject("totalOrcamentos",   orcamentosRepository.count() );
		mv.addObject("totalInsumosSinapi",  insumosRepository.countByBaseInsumoCodigo(1L));
		mv.addObject("totalInsumosPropria", insumosRepository.countByBaseInsumoCodigo(2L));
		mv.addObject("totalComposicoesSinapi",  composicoesRepository.countByBaseInsumoCodigo(1L));
		mv.addObject("totalComposicoesPropria", composicoesRepository.countByBaseInsumoCodigo(2L));
		
		 
			
		return mv;
	}
	
}
