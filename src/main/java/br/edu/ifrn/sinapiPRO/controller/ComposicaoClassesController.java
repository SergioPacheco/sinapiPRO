package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoClassesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoClasseFilter;
import br.edu.ifrn.sinapiPRO.service.ComposicaoClasseService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/composicaoClasses")
public class ComposicaoClassesController {
 
	@Autowired
	private ComposicaoClasseService composicaoClasseService;
	
	@Autowired
	private ComposicaoClassesRepository composicaoClassesRepository;
	
	@RequestMapping("/nova")
	public ModelAndView nova(ComposicaoClasse classe) {
		return new ModelAndView("composicaoClasse/CadastroComposicaoClasse");
	}
	
	@RequestMapping(value = { "/nova", "/{codigo}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid ComposicaoClasse composicaoClasse, BindingResult result, 
			RedirectAttributes attributes){
		if (result.hasErrors()) {
			return nova(composicaoClasse);
		}
		
		try{
			composicaoClasseService.salvar(composicaoClasse);
		} catch(JaCadastradoException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return nova(composicaoClasse);
		}
		attributes.addFlashAttribute("mensagem", "Classe da Composição salva com sucesso!");
		
		return new ModelAndView("redirect:/composicaoClasse/nova");// Redirect
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid ComposicaoClasse composicaoClasse, BindingResult result ){
		
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		composicaoClasse = composicaoClasseService.salvar(composicaoClasse); 
		return ResponseEntity.ok(composicaoClasse);
	}
	
	@GetMapping
	public ModelAndView pesquisar(ComposicaoClasseFilter classeFilter, BindingResult result
			,@PageableDefault(size = 15) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("composicaoClasse/PesquisaComposicaoClasses");
		
		PageWrapper<ComposicaoClasse> paginaWrapper = new PageWrapper<>(composicaoClassesRepository.filtrar(classeFilter, pageable)
				, httpServletRequest);
		
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		ComposicaoClasse composicaoClasse = composicaoClassesRepository.getOne(codigo);
		ModelAndView mv = nova(composicaoClasse);
		mv.addObject(composicaoClasse);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") ComposicaoClasse composicaoClasse) {
		try {
			composicaoClasseService.excluir(composicaoClasse);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
