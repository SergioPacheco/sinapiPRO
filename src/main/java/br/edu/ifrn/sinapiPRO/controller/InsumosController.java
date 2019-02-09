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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.dto.InsumoDTO;
import br.edu.ifrn.sinapiPRO.dto.ItemBasePrecoDTO;
import br.edu.ifrn.sinapiPRO.model.Especie;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.Insumos;
import br.edu.ifrn.sinapiPRO.repository.filter.InsumoFilter;
import br.edu.ifrn.sinapiPRO.service.InsumoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/insumos")
public class InsumosController {
		
	@Autowired
	private InsumoService cadastroInsumoService;
		 
	@Autowired
	private Insumos insumos;
	
	@Autowired
	private BasePrecosRepository basePrecosRepository;
	
	@Autowired
	private BaseInsumosRepository baseInsumosRepository;
	 
		
	@RequestMapping("/novo")
	public ModelAndView novo(Insumo insumo) {
		
		ModelAndView mv = new ModelAndView("insumo/CadastroInsumo");
		mv.addObject("basePrecos", basePrecosRepository.findAll());
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
		mv.addObject("especies", Especie.values());
		return mv;
	}
	
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Insumo insumo, 
			                   BindingResult result, 
			                   Model model, 
			                   RedirectAttributes attributes) {
		
		if (result.hasErrors()) {
			return novo(insumo);
		}
		try {
			cadastroInsumoService.salvar(insumo);
		} catch(ResourceNotFoundException e) {
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return novo(insumo);
		}
		attributes.addFlashAttribute("mensagem", "Insumo salvo com sucesso!");
		return new ModelAndView("redirect:/insumos/novo");
	}
	 
	@GetMapping
	public ModelAndView pesquisar(InsumoFilter insumoFilter, 
			                     BindingResult result, 
			                     @PageableDefault(size = 30) Pageable pageable, 
			                     HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("insumo/PesquisaInsumos");
		PageWrapper<Insumo> paginaWrapper = new PageWrapper<>(insumos.filtrar(insumoFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	/* 
	 * Lista todos os precos SINAPI importados por Insumo
	 */
	@RequestMapping(value = "/precos", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ItemBasePrecoDTO> pesquisar(Long codigoInsumo) {
		 
		return insumos.listaBasePrecoPorInsumo(codigoInsumo);
	 
	}
	 
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<InsumoDTO> pesquisar(Long codigoBaseInsumo, String codigoOuNome) {
		
		return insumos.porCodigoInsumoOuNome(codigoBaseInsumo, codigoOuNome);
	
	}
	
	@DeleteMapping("/{insumoId}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Insumo insumo) {
		try {
			cadastroInsumoService.excluir(insumo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{insumoID}")
	public ModelAndView editar(@PathVariable("insumoID") Insumo insumo) {
		ModelAndView mv = novo(insumo);
		mv.addObject(insumo);
		return mv;
	}
	
}