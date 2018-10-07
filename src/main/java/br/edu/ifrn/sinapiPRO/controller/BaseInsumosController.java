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
import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumos;
import br.edu.ifrn.sinapiPRO.repository.filter.BaseInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroBaseInsumoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeBaseInsumoJaCadastradaException;

@Controller
@RequestMapping("/baseBaseInsumos")
public class BaseInsumosController {

	@Autowired
	private CadastroBaseInsumoService cadastroBaseInsumoService;
	
	@Autowired
	private BaseInsumos baseInsumos;
	
	@RequestMapping("/nova")
	public ModelAndView nova(BaseInsumo baseInsumo) {
		return new ModelAndView("baseInsumo/CadastroBaseInsumo");
	}
	
	@RequestMapping(value = { "/nova", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastrar(@Valid BaseInsumo baseInsumo, BindingResult result, 
			RedirectAttributes attributes){
		if (result.hasErrors()) {
			return nova(baseInsumo);
		}
		
		try{
			cadastroBaseInsumoService.salvar(baseInsumo);
		} catch(NomeBaseInsumoJaCadastradaException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return nova(baseInsumo);
		}
		attributes.addFlashAttribute("mensagem", "BaseInsumo salvo com sucesso!");
		
		return new ModelAndView("redirect:/baseInsumos/nova");// Redirect
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid BaseInsumo baseInsumo, BindingResult result ){
				
		if(result.hasErrors()){
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
		baseInsumo = cadastroBaseInsumoService.salvar(baseInsumo); 
		return ResponseEntity.ok(baseInsumo);
	}
	
	@GetMapping
	public ModelAndView pesquisar(BaseInsumoFilter baseInsumoFilter, BindingResult result
			,@PageableDefault(size = 5) Pageable pageable, HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("baseInsumo/PesquisaBaseInsumos");
		
		PageWrapper<BaseInsumo> paginaWrapper = new PageWrapper<>(baseInsumos.filtrar(baseInsumoFilter, pageable)
				, httpServletRequest);
		
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		BaseInsumo baseInsumo = baseInsumos.getOne(codigo);
		ModelAndView mv = nova(baseInsumo);
		mv.addObject(baseInsumo);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			cadastroBaseInsumoService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
