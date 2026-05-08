package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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
import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Especie;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.InsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.filter.ListaInsumosFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.BaseInsumoService;
import br.edu.ifrn.sinapiPRO.service.BasePrecoService;
import br.edu.ifrn.sinapiPRO.service.InsumoService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/insumos")
public class InsumosController {
		
	private final InsumoService insumoService;
	private final BasePrecoService basePrecoService;
	private final BaseInsumoService baseInsumoService;
	private final OrcamentoService orcamentoService;
	private final BaseInsumosRepository baseInsumosRepository;
	private final InsumosRepository insumosRepository;
		 
	public InsumosController(
			InsumoService insumoService,
			BasePrecoService basePrecoService,
			BaseInsumoService baseInsumoService,
			OrcamentoService orcamentoService,
			BaseInsumosRepository baseInsumosRepository,
			InsumosRepository insumosRepository) {
		this.insumoService = insumoService;
		this.basePrecoService = basePrecoService;
		this.baseInsumoService = baseInsumoService;
		this.orcamentoService = orcamentoService;
		this.baseInsumosRepository = baseInsumosRepository;
		this.insumosRepository = insumosRepository;
	}
	
	@GetMapping
	public ModelAndView pesquisar(InsumoFilter insumoFilter, 
			                     BindingResult result, 
			                     @PageableDefault(size = 30) Pageable pageable, 
			                     HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("insumo/PesquisaInsumos");
		mv.addObject("basePrecos",  basePrecoService.findAll());
		mv.addObject("baseInsumos", baseInsumoService.findAll());
		mv.addObject("especies",   Especie.values());
		
		PageWrapper<Insumo> paginaWrapper = new PageWrapper<>(insumoService
				.filtrar(insumoFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
		
	@GetMapping("/novo")
	public ModelAndView novo(Insumo insumo) {
		
		ModelAndView mv = new ModelAndView("insumo/CadastroInsumo");
		mv.addObject("basePrecos",  basePrecoService.findAll());
		mv.addObject("baseInsumos", baseInsumoService.findAll());
		mv.addObject("especies",   Especie.values());
		// TODO: setar usuario
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
		} catch(JaCadastradoException e) {
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return novo(insumo);
		}
		attributes.addFlashAttribute("mensagem", "Insumo salvo com sucesso!");
		
		return new ModelAndView("redirect:/insumos/"+insumo.getCodigo());
	}
	
	/**
	 * copia insumo para outra base de insumo. A nova base vem no próprio insumo alterado
	 * 
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
		
		Optional<Insumo> insumoExistente = insumoService
				.findByBaseInsumoAndCodigoInsumo(insumo.getBaseInsumo(), insumo.getCodigoInsumo());
		 
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
			
		} catch(JaCadastradoException e) {
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return novo(insumo);
		}
		attributes.addFlashAttribute("mensagem", "Insumo salvo com sucesso!");
		return new ModelAndView("redirect:/insumos/"+insumo.getCodigo());
	}
	 
	/**
	 * Lista todas os precos SINAPI importados para o Insumo 
	 */
	@RequestMapping(value = "/precos", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<BasePrecoItemDTO> pesquisaPrecos(String codigoInsumo) {
		
		return insumoService.listaPrecosPorInsumo(codigoInsumo);
	}
	
	/**
	 * Retorna Json de pesquisa por descricao do insumo (AJAX) 
	 */
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<InsumoDTO> pesquisar(String porDescricao) {
		
		return insumoService.porDescricao(porDescricao);
	
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
	
	/**
	 * edita pelo codigo do insumo (codigo_insumo) 
	 */
	@GetMapping("/editaPorCodigo/{codigo}")
	public ModelAndView editaPorCodigo(@PathVariable String codigo) {
		
		Optional<BaseInsumo> sinapi = baseInsumosRepository.findById(1L); // base vir como paramentro
		
		Optional<Insumo> insumo = insumosRepository.findByBaseInsumoAndCodigoInsumo(sinapi.get(), codigo);  
		
		if (insumo.isPresent()) {
			ModelAndView mv = novo(insumo.get());
			mv.addObject(insumo.get());
			return mv;
		}
		return new ModelAndView("/insumo/PesquisaInsumos");
		
	}
	
	/**
	 * edita pela ID do insumo (codigo)
	 */
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Insumo insumo) {
		
		if (insumo == null || insumo.getCodigo() == null) {
			System.out.println("EDITA INSUMO: Insumo não existe");
			return new ModelAndView("redirect:/insumos");
		}
		
		ModelAndView mv = novo(insumo);
		mv.addObject(insumo);
		return mv;
	}
	/**
	 * ADDINSUMO - Adiciona o insumo para os itens do Orçamento Atual
	 */
	@GetMapping("/adicionarInsumo/{codigo}")
	public ModelAndView addInsumo(@PathVariable("codigo") Insumo insumo,
								  RedirectAttributes attributes, 
								  @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		Optional<Etapa> etapaSelecionada = Optional.empty();
		
		etapaSelecionada = orcamentoService
							.findEtapaSelecionada(usuarioSistema.getUsername());

		if (!etapaSelecionada.isPresent()) {
			attributes.addFlashAttribute("mensagem", "Etapa não foi selecionada");
			return new ModelAndView("redirect:/insumos");
		}
	
		Optional<Orcamento> orcamentoAtual = orcamentoService
							.findOrcamentoAtual(usuarioSistema.getUsername());
		
		if (!orcamentoAtual.isPresent()) {
			attributes.addFlashAttribute("mensagem", "Selecione o orçamento");
			return new ModelAndView("redirect:/orcamentos");
		}
		 
		Item item = new Item();
		item.setValorEquipamento(BigDecimal.ZERO);
		item.setValorMaoObra(BigDecimal.ZERO);
		item.setValorMaterial(BigDecimal.ZERO);
		
		item.setTipo(Tipo.INSUMO); 
		item.setOrcamento(orcamentoAtual.get());
		item.setDescricao(insumo.getDescricao());
		item.setEspecie(insumo.getEspecie());
		item.setUnidade(insumo.getUnidade());
		item.setQuantidade(BigDecimal.ONE);
		item.setValorUnitario(insumo.getPrecoPadrao()); // TODO: Pesquisar na base de preço do orcamento
		item.setInsumo(insumo);
		item.setItemizacao(etapaSelecionada.get().getCodigo().toString()+".");
		item.setEtapa(etapaSelecionada.get());
		// item.calculaTotalItem();
		switch (insumo.getEspecie()) {
			case MAO_DE_OBRA:
				item.setValorMaoObra(item.getValorTotal());
				break;
			case MATERIAL: 
				item.setValorMaterial(item.getValorTotal());	
				break;
			case EQUIPAMENTO:
				item.setValorEquipamento(item.getValorTotal());	
				break;
		}
		
		orcamentoAtual.get().addItem(item); 
		
		orcamentoService.salvar(orcamentoAtual.get()); 
		
		return new ModelAndView("redirect:/atual");
	}
	
	/**
	 * lista dos insumos do orçamento 
	 */
	@GetMapping("/listaInsumos/{codigo}")
	public ModelAndView listaInsumos(@PathVariable("codigo") Orcamento orcamento,
								 	 ListaInsumosFilter filter,
			                         @PageableDefault(size = 20) Pageable pageable, 
			                         HttpServletRequest httpServletRequest) {
		
		filter.setOrcamento(orcamento);
		
		ModelAndView mv = new ModelAndView("/insumo/ListaInsumos");
		
		PageWrapper<Item> paginaWrapper = new PageWrapper<>(insumoService
				.filtrarInsumos(filter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		mv.addObject("especies",   Especie.values());
		
		return mv;
		 
	}
	
	
	
}
