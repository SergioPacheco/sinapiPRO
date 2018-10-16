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
import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumos;
import br.edu.ifrn.sinapiPRO.repository.BasePrecos;
import br.edu.ifrn.sinapiPRO.repository.Estados;
import br.edu.ifrn.sinapiPRO.repository.filter.BasePrecoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroBasePrecoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeBasePrecoJaCadastradaException;

@Controller
@RequestMapping("/basePrecos")
public class BasePrecosController  {

	@Autowired
	private CadastroBasePrecoService cadastroBasePrecoService;
	
	@Autowired
	private SinapiController sinapiController;
	
	@Autowired
	private BasePrecos basePrecos;
	
	@Autowired
	private BaseInsumos baseInsumos;
	
	@Autowired
	private Estados estados;
	
	@RequestMapping("/nova")
	public ModelAndView nova(BasePreco basePreco) {
		
		ModelAndView mv = new ModelAndView("basePreco/CadastroBasePreco");
		mv.addObject("estados", estados.findAll()); 
		mv.addObject("baseInsumos", baseInsumos.findAll()); 
		
		return mv;
	}
	
	@RequestMapping(value = { "/nova", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid BasePreco basePreco, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return nova(basePreco);
		}
		
		try{
			cadastroBasePrecoService.salvar(basePreco);
		} catch(NomeBasePrecoJaCadastradaException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return nova(basePreco);
		}
		attributes.addFlashAttribute("mensagem", "Base de Preço salvo com sucesso!");
		
		return new ModelAndView("redirect:/basePrecos/nova"); 
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid BasePreco basePreco, BindingResult result ){
		
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		basePreco = cadastroBasePrecoService.salvar(basePreco); 
		return ResponseEntity.ok(basePreco);
	}
	
	@GetMapping
	public ModelAndView pesquisar(BasePrecoFilter basePrecoFilter, BindingResult result
			,@PageableDefault(size = 5) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("basePreco/PesquisaBasePreco");
		
		PageWrapper<BasePreco> paginaWrapper = new PageWrapper<>(basePrecos.filtrar(basePrecoFilter, pageable)
				, httpServletRequest);
		
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		BasePreco basePreco = basePrecos.getOne(codigo);
		ModelAndView mv = nova(basePreco);
		mv.addObject(basePreco);
		return mv;
	}
	
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			cadastroBasePrecoService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 
	 * @param codigo - código da base de preços
	 * @return
	 */
	@GetMapping("importaInsumo/{codigo}")
	public @ResponseBody ResponseEntity<?>  importarInsumos(@PathVariable Long codigo) {
		
	 
		sinapiController.importaInsumos(codigo);
		 
		return ResponseEntity.ok().build();
	}
	/**
	 * 
	 * @param codigo - Código da base preços
	 * @return
	 */
	@GetMapping("importaComposicao/{codigo}")
	public @ResponseBody ResponseEntity<?>  importarComposicoes(@PathVariable Long codigo) {
				 
		sinapiController.importaComposicoes(codigo);
		 
		return ResponseEntity.ok().build();
	}
		
}
