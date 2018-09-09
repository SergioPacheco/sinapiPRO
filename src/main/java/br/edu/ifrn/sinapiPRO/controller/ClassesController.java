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
import br.edu.ifrn.sinapiPRO.model.Classe;
import br.edu.ifrn.sinapiPRO.repository.Classes;
import br.edu.ifrn.sinapiPRO.repository.filter.ClasseFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroClasseService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeClasseJaCadastradaException;

@Controller
@RequestMapping("/classes")
public class ClassesController {

	@Autowired
	private CadastroClasseService cadastroClasseService;
	
	@Autowired
	private Classes classes;
	
	@RequestMapping("/nova")
	public ModelAndView nova(Classe classe) {
		return new ModelAndView("classe/CadastroClasse");
	}
	
	@RequestMapping(value = { "/nova", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid Classe classe, BindingResult result, 
			RedirectAttributes attributes){
		if (result.hasErrors()) {
			return nova(classe);
		}
		
		try{
			cadastroClasseService.salvar(classe);
		} catch(NomeClasseJaCadastradaException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return nova(classe);
		}
		attributes.addFlashAttribute("mensagem", "Classe salvo com sucesso!");
		
		return new ModelAndView("redirect:/classes/nova");// Redirect
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Classe classe, BindingResult result ){
		
		/*O ponto de "?" é porque não sabemos qual vai ser o retorno, tem hora que pode ser o 
		 * badRequest ou um ok.*/
		
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		classe = cadastroClasseService.salvar(classe); 
		return ResponseEntity.ok(classe);
	}
	
	@GetMapping
	public ModelAndView pesquisar(ClasseFilter classeFilter, BindingResult result
			,@PageableDefault(size = 5) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("classe/PesquisaClasses");
		
		PageWrapper<Classe> paginaWrapper = new PageWrapper<>(classes.filtrar(classeFilter, pageable)
				, httpServletRequest);
		
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
/*	Esse método não funciona é porque existe o conflito de converters, que configuramos no WebConfig, 
 * ai nesse caso você precisa fazer a pesquisa mesmo no controller.  
 * @GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Classe classe) {
		ModelAndView mv = novo(classe);
		mv.addObject(classe);
		return mv;
	}*/
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Classe classe = classes.getOne(codigo);
		ModelAndView mv = nova(classe);
		mv.addObject(classe);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			cadastroClasseService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
