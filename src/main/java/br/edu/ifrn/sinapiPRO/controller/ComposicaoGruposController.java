package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.ComposicaoGrupo;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoClassesRepository;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoGruposRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoGrupoFilter;
import br.edu.ifrn.sinapiPRO.service.ComposicaoGrupoService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/composicaoGrupos")
public class ComposicaoGruposController {

	@Autowired
	private ComposicaoGruposRepository composicaoGruposRepository;
	
	@Autowired
	private ComposicaoClassesRepository composicaoClassesRepository;
	
	@Autowired
	private ComposicaoGrupoService composicaoGrupoService;
	
	@RequestMapping("/nova")
	public ModelAndView nova(ComposicaoGrupo composicaoGrupo) {
		ModelAndView mv = new ModelAndView("composicaoGrupo/CadastroComposicaoGrupo");
		mv.addObject("composicaoClasses", composicaoClassesRepository.findAll());
		return mv;
	}
	
	/**
	 * Pesquisa para a DropList
	 */
	@Cacheable(value = "grupos", key = "#codigoComposicaoClasse")
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ComposicaoGrupo> pesquisarPorCodigoComposicaoClasse(
		   @RequestParam(name = "classe", defaultValue = "-1") Long codigoComposicaoClasse) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {	}
		
		return  composicaoGruposRepository.findAllByComposicaoClasseCodigo(codigoComposicaoClasse);
	}
	
	@PostMapping("/novo")
	@CacheEvict(value = "grupos", key = "#composicaoGrupo.composicaoClasse.codigo", condition = "#composicaoGrupo.temClasse()")
	public ModelAndView salvar(@Valid ComposicaoGrupo composicaoGrupo, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return nova(composicaoGrupo);
		}
		
		try {
			composicaoGrupoService.salvar(composicaoGrupo);
		} catch (JaCadastradoException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return nova(composicaoGrupo);
		}
		
		attributes.addFlashAttribute("mensagem", "Grupo salvo com sucesso!");
		return new ModelAndView("redirect:/composicaoGrupos/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(ComposicaoGrupoFilter composicaoGrupoFilter, 
			                      BindingResult result, @PageableDefault(size = 10) Pageable pageable, 
			                      HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("composicaoGrupo/PesquisaComposicaoGrupos");
		mv.addObject("ComposicaoClasses", composicaoClassesRepository.findAll());
		
		PageWrapper<ComposicaoGrupo> paginaWrapper = new PageWrapper<>(composicaoGruposRepository.filtrar(composicaoGrupoFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
}
