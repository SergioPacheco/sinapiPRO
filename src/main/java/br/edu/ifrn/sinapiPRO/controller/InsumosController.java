package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.dto.BasePrecoItemDTO;
import br.edu.ifrn.sinapiPRO.dto.InsumoDTO;
import br.edu.ifrn.sinapiPRO.model.Especie;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.InsumoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.InsumoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/insumos")
public class InsumosController {
		
	@Autowired
	private InsumosRepository insumosRepository;
	
	@Autowired
	private InsumoService insumoService;
		 
	@Autowired
	private BasePrecosRepository basePrecosRepository;
	
	@Autowired
	private BaseInsumosRepository baseInsumosRepository;
	
	@GetMapping
	public ModelAndView pesquisar(InsumoFilter insumoFilter, 
			                     BindingResult result, 
			                     @PageableDefault(size = 30) Pageable pageable, 
			                     HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("insumo/PesquisaInsumos");
		mv.addObject("basePrecos",  basePrecosRepository.findAll());
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
		mv.addObject("especies",   Especie.values());
		
		PageWrapper<Insumo> paginaWrapper = new PageWrapper<>(insumosRepository.filtrar(insumoFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
		
	@GetMapping("/novo")
	public ModelAndView novo(Insumo insumo) {
		
		ModelAndView mv = new ModelAndView("insumo/CadastroInsumo");
		mv.addObject("basePrecos",  basePrecosRepository.findAll());
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
		mv.addObject("especies",   Especie.values());
		return mv;
	}
	
	@PostMapping(value = "/novo", params = "salvar")
	public ModelAndView salvar(Insumo insumo, 
			                   BindingResult result, 
			                   RedirectAttributes attributes, 
			                   @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		// validarInsumo(insumo, result);
		if (result.hasErrors()) {
			return novo(insumo);
		}
		insumo.setUsuario(usuarioSistema.getUsuario());
		try {
			
			if (!insumo.isNovo()) {
				if (insumo.isSinapi()) {
					attributes.addFlashAttribute("mensagem", "Proibido alterar dados da tabela base SINAPI");
					return new ModelAndView("redirect:/insumos/novo");
				}
			}
			
			insumoService.salvar(insumo);
		} catch(ResourceNotFoundException e) {
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return novo(insumo);
		}
		attributes.addFlashAttribute("mensagem", "Insumo salvo com sucesso!");
		
		return new ModelAndView("redirect:/insumos/"+insumo.getCodigo());
	}
	
	/**
	 * copiar insumo para outra base de insumo. A nova base vem no próprio insumo alterado
	 * 
	 * @param insumo
	 * @param result
	 * @param attributes
	 * @param usuarioSistema
	 * @return
	 */
	@PostMapping(value = "/novo", params = "copiar")
	public ModelAndView copiar(Insumo insumo, 
			                   BindingResult result, 
			                   RedirectAttributes attributes, 
			                   @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		// validarInsumo(insumo, result);
		
		if (result.hasErrors()) {
			return novo(insumo);
		}
		
		if (insumo.isSinapi()) {
			attributes.addFlashAttribute("mensagem", "Proibido copiar para a base SINAPI");
			return new ModelAndView("redirect:/insumos/novo");
		}
		
		insumo.setUsuario(usuarioSistema.getUsuario());
		
		Optional<Insumo> insumoExistente = insumosRepository.findByBaseInsumoAndCodigoInsumo(insumo.getBaseInsumo(), insumo.getCodigoInsumo());
		 
		if(insumoExistente.isPresent()) {
			attributes.addFlashAttribute("mensagem", "Insumo já foi copiado");
			return new ModelAndView("redirect:/insumos/novo");
		} 
		
		try {
			
			Insumo novo = new Insumo();
			novo.setCodigoInsumo(insumo.getCodigoInsumo());
			novo.setBaseInsumo(insumo.getBaseInsumo());
			novo.setBasePreco(insumo.getBasePreco());
			novo.setUsuario(insumo.getUsuario());
			novo.setDescricao(insumo.getDescricao()); 
			novo.setUnidade(insumo.getUnidade()); 
			novo.setPrecoPadrao(insumo.getPrecoPadrao());
			novo.setEspecie(insumo.getEspecie());
			
			insumoService.salvar(novo);
			
		} catch(ResourceNotFoundException e) {
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return novo(insumo);
		}
		attributes.addFlashAttribute("mensagem", "Insumo salvo com sucesso!");
		return new ModelAndView("redirect:/insumos/"+insumo.getCodigo());
	}
	
	 
	/**
	 * Lista todas os precos SINAPI importados para o Insumo 
	 * 
	 * @param codigoInsumo
	 * @return
	 */
	@RequestMapping(value = "/precos", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<BasePrecoItemDTO> pesquisar(Long codigoInsumo) {
		
		return insumosRepository.listaBasePrecoPorInsumo(codigoInsumo);
	}
	
	/**
	 *  
	 * @param codigoBaseInsumo
	 * @param nome
	 * @return
	 */
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<InsumoDTO> pesquisar(String porDescricao) {
		
		return insumosRepository.porDescricao(porDescricao);
	
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Insumo insumo) {
		try {
			insumoService.excluir(insumo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	 
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Insumo insumo) {
	
		ModelAndView mv = novo(insumo);
		mv.addObject(insumo);
		return mv;

	}
}