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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.repository.EstadosRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ObraFilter;
import br.edu.ifrn.sinapiPRO.service.ObraService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/obras")
public class ObrasController {
	
	@Autowired
	private EstadosRepository estados;
	
	@Autowired
	private ObraService obraService;
	
	@Autowired
	private ObrasRepository obrasRepository;

	@RequestMapping("/nova")
	public ModelAndView nova(Obra obra) { 
		ModelAndView mv = new ModelAndView("obra/CadastroObra");
		mv.addObject("estados", estados.findAll());
		return mv;
	}
	
	@RequestMapping(value = { "/nova", "/{codigo}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Obra obra, BindingResult result, RedirectAttributes attributes){
		if(result.hasErrors()){
			return nova(obra);
		}
		
		try {
			obraService.salvar(obra);
		} catch (JaCadastradoException e) {
			result.rejectValue("CEI", e.getMessage(), e.getMessage());
			return nova(obra);
		}
		attributes.addFlashAttribute("mensagem", "Obra salva com sucesso!");
		return new ModelAndView("redirect:/obras/nova");
	}
	
	@GetMapping
	public ModelAndView pesquisar(ObraFilter obraFilter, BindingResult result, 
								  @PageableDefault(size = 5) Pageable pageable, 
								  HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("obra/PesquisaObras");
		
		PageWrapper<Obra> paginaWrapper = new PageWrapper<>(obrasRepository
								.filtrar(obraFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Obra> pesquisar(String nome){
		validarTamanhoNome(nome);
		return obrasRepository.findByNomeStartingWithIgnoreCase(nome);
	}

	private void validarTamanhoNome(String nome) {
		if(StringUtils.isEmpty(nome) || nome.length() < 3){
			throw new IllegalArgumentException();
		}
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e){
		return ResponseEntity.badRequest().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Obra obra = obrasRepository.buscarComCidadeEstado(codigo);
		ModelAndView mv = nova(obra);
		mv.addObject(obra);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			obraService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
