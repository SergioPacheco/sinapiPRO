package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.repository.Etapas;
import br.edu.ifrn.sinapiPRO.repository.filter.EtapaFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroEtapaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeEtapaJaCadastradaException;

@Controller
@RequestMapping("/etapas")
public class EtapasController {

	@Autowired
	private CadastroEtapaService cadastroEtapaService;
	
	@Autowired
	private Etapas etapas;

	@RequestMapping("/nova")
	public ModelAndView nova(Etapa etapa) {
		return new ModelAndView("etapa/CadastroEtapa");
	}
	
	@RequestMapping(value = { "/nova", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid Etapa etapa, BindingResult result, 
			RedirectAttributes attributes){
		if (result.hasErrors()) {
			return nova(etapa);
		}
		
		try{
			cadastroEtapaService.salvar(etapa);
		} catch(NomeEtapaJaCadastradaException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return nova(etapa);
		}
		attributes.addFlashAttribute("mensagem", "Etapa salvo com sucesso!");
		
		return new ModelAndView("redirect:/etapas/nova");// Redirect
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Etapa etapa, BindingResult result ){
		
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		etapa = cadastroEtapaService.salvar(etapa); 
		return ResponseEntity.ok(etapa);
	}
	
	@GetMapping
	public ModelAndView pesquisar(EtapaFilter etapaFilter, BindingResult result
			,@PageableDefault(size = 25) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("etapa/PesquisaEtapas");
		
		PageWrapper<Etapa> paginaWrapper = new PageWrapper<>(etapas.filtrar(etapaFilter, pageable)
				, httpServletRequest);
		
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Etapa> pesquisar(String codigoOuNome) {
		if (codigoOuNome.equals("***")) { 
			return etapas.findAll();
		} else {
			return etapas.findByNomeStartingWithIgnoreCase(codigoOuNome);
		}
	}
	
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Etapa etapa = etapas.getOne(codigo);
		ModelAndView mv = nova(etapa);
		mv.addObject(etapa);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			cadastroEtapaService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
