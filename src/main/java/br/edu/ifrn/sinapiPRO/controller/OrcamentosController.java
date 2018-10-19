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
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.OrcamentoFilter;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/orcamentos")
public class OrcamentosController {

	@Autowired
	private OrcamentoService orcamentoService;
	
	@Autowired
	private OrcamentosRepository orcamentosRepository;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Orcamento orcamento) {
		return new ModelAndView("orcamento/CadastroOrcamento");
	}
	
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid Orcamento orcamento, BindingResult result, 
			RedirectAttributes attributes){
		if (result.hasErrors()) {
			return novo(orcamento);
		}
		
		try{
			orcamentoService.salvar(orcamento);
		} catch(ResourceNotFoundException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return novo(orcamento);
		}
		attributes.addFlashAttribute("mensagem", "Orcamento salvo com sucesso!");
		
		return new ModelAndView("redirect:/orcamentos/novo");// Redirect
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid Orcamento orcamento, BindingResult result ){
		
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		orcamento = orcamentoService.salvar(orcamento); 
		return ResponseEntity.ok(orcamento);
	}
	
	@GetMapping
	public ModelAndView pesquisar(OrcamentoFilter orcamentoFilter, BindingResult result
			,@PageableDefault(size = 5) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("orcamento/PesquisaOrcamentos");
		
		PageWrapper<Orcamento> paginaWrapper = new PageWrapper<>(orcamentosRepository.filtrar(orcamentoFilter, pageable), httpServletRequest);
		
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentosRepository.getOne(codigo);
		ModelAndView mv = novo(orcamento);
		mv.addObject(orcamento);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			orcamentoService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
