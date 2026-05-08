package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.dto.ComposicaoDTO;
import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.ComposicaoItem;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.BaseInsumoService;
import br.edu.ifrn.sinapiPRO.service.BasePrecoService;
import br.edu.ifrn.sinapiPRO.service.ComposicaoClasseService;
import br.edu.ifrn.sinapiPRO.service.ComposicaoService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.RelatorioService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.session.composicao.TabelaComposicaoItemSession;

@Controller
@RequestMapping("/composicoes")
public class ComposicaoController {
	
	private final TabelaComposicaoItemSession tabelaItens;
	private final ComposicaoRepository composicaoRepository;
	private final ComposicaoService composicaoService;
	private final BasePrecoService basePrecoService;
	private final BaseInsumoService baseInsumoService;
	private final ComposicaoClasseService composicaoClassesService;
	private final OrcamentoService orcamentoService;
	private final InsumosRepository insumosRepository;
	private final BaseInsumosRepository baseInsumosRepository;
	private final RelatorioService relatorioService;
	
	public ComposicaoController(
			TabelaComposicaoItemSession tabelaItens,
			ComposicaoRepository composicaoRepository,
			ComposicaoService composicaoService,
			BasePrecoService basePrecoService,
			BaseInsumoService baseInsumoService,
			ComposicaoClasseService composicaoClassesService,
			OrcamentoService orcamentoService,
			InsumosRepository insumosRepository,
			BaseInsumosRepository baseInsumosRepository,
			RelatorioService relatorioService) {
		this.tabelaItens = tabelaItens;
		this.composicaoRepository = composicaoRepository;
		this.composicaoService = composicaoService;
		this.basePrecoService = basePrecoService;
		this.baseInsumoService = baseInsumoService;
		this.composicaoClassesService = composicaoClassesService;
		this.orcamentoService = orcamentoService;
		this.insumosRepository = insumosRepository;
		this.baseInsumosRepository = baseInsumosRepository;
		this.relatorioService = relatorioService;
	}
	
	@RequestMapping("/nova")
	public ModelAndView nova(Composicao composicao) {
		
		ModelAndView mv = new ModelAndView("composicao/CadastroComposicao");
		
		setUuid(composicao);
		
		mv.addObject("itens", composicao.getItens());
		mv.addObject("basePrecos", basePrecoService.findAll());
		mv.addObject("baseInsumos", baseInsumoService.findAll());
		mv.addObject("composicaoClasses", composicaoClassesService.findAll());
		 
		return mv;
	}
	 
	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Composicao composicao, 
			                    BindingResult result, 
			           RedirectAttributes attributes, 
			                 @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		if (result.hasErrors()) {
			return nova(composicao);
		}
		
		composicao.setUsuario(usuarioSistema.getUsuario());
		composicaoService.salvar(composicao);
		
		attributes.addFlashAttribute("mensagem", "Composição salva com sucesso!");
		return new ModelAndView("redirect:/composicoes/"+composicao.getCodigo());
	}
	
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigo, String uuid){
		
		if (codigo == null) { System.out.println("codigo ITEM NULO");
		} else { 			  System.out.println("CODIGO INSUMO "+ codigo);
		}
		Insumo insumo = insumosRepository.findById(codigo).get();
		tabelaItens.adicionarItem(uuid, insumo, new BigDecimal(1));
		
		return mvTabelaItensComposicao(uuid); 
	}
	
	@PutMapping("/item/{codigoInsumo}")
	public ModelAndView alterarQuantidadeItem(
			      		@PathVariable("codigoInsumo") Insumo insumo, BigDecimal coeficiente, String uuid){
		 
		tabelaItens.alterarCoeficiente(uuid, insumo, coeficiente); 
		return mvTabelaItensComposicao(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoInsumo}")
	public ModelAndView excluirItem (@PathVariable("codigoInsumo") Insumo insumo, 
								     @PathVariable("uuid") String uuid) {
		
		tabelaItens.excluirItem(uuid, insumo);
		return mvTabelaItensComposicao(uuid);
	}

	@GetMapping
	public ModelAndView pesquisar(ComposicaoFilter composicaoFilter,
			                      @PageableDefault(size = 20) Pageable pageable, 
			                      HttpServletRequest httpServletRequest) {
		
		ModelAndView mv = new ModelAndView("/composicao/PesquisaComposicoes");
		mv.addObject("baseInsumos", baseInsumoService.findAll());
		mv.addObject("composicaoClasses", composicaoClassesService.findAll());
		
		PageWrapper<Composicao> paginaWrapper = new PageWrapper<>(composicaoRepository
				.filtrar(composicaoFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	/**
	 * PESQUISAR - Pesquisa todas composições pela descrição por base de insumo 
	 */
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ComposicaoDTO> pesquisar(String porDescricao) {
		return composicaoRepository.porDescricao(porDescricao);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		
		Composicao composicao = composicaoRepository.buscarComItens(codigo);
		setUuid(composicao);
		for (ComposicaoItem	item : composicao.getItens() ) {
			if (item.getTipo().equals("INSUMO")) {
				tabelaItens.adicionarItem(composicao.getUuid(), item.getInsumo(), item.getCoeficiente());
			} else {
				System.out.println("COMPOSICAO");
			}
		}
		
		ModelAndView mv = nova(composicao);
		mv.addObject(composicao);
		return mv;
	}
	
	/**
	 *  EDITA POR CODIGO - edita o insumo pelo codigo do insumo 
	 */
	@GetMapping("/editaPorCodigo/{codigo}")
	public ModelAndView editaPorCodigo(@PathVariable String codigo) {
		
		Optional<BaseInsumo> sinapi = baseInsumosRepository.findById(1L); // base vir como paramentro
		Optional<Composicao> composicao = composicaoRepository.findByBaseInsumoAndCodigoComposicao(sinapi.get(), codigo);
		
		if (composicao.isPresent()) {
			ModelAndView mv = nova(composicao.get());
			mv.addObject(composicao.get());
			return mv;
		}
		return new ModelAndView("/composicao/PesquisaComposicoes");
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Composicao composicao) {
		try {
			composicaoService.excluir(composicao);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
		
	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Composicao composicao, BindingResult result
				, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			composicaoService.cancelar(composicao);
		} catch (AccessDeniedException e) {
			ModelAndView mv = new ModelAndView("error");
			mv.addObject("status", 403);
			return mv;
		}
		
		attributes.addFlashAttribute("mensagem", "Composicao cancelada com sucesso");
		return new ModelAndView("redirect:/composicoes/" + composicao.getCodigo());
	}
	
	/**
	 *  IMPRIMIR - Imprime a composição selecionada
	 */
	@PostMapping(value = "/nova", params = "imprimir")
	public ResponseEntity<byte[]> imprimir (Composicao composicao, 
			                   @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		System.out.println("Imprimir composicao");
		byte[] relatorio = null;
		try {
			relatorio = relatorioService
					.gerarRelatorioImprimirComposicao(composicao.getCodigo(), 
							usuarioSistema.getUsername());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	private ModelAndView mvTabelaItensComposicao(String uuid) {
		
		ModelAndView mv = new ModelAndView("composicao/TabelaItensComposicao");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		
		return mv;
	}
	
	private void setUuid(Composicao composicao) {
		if(StringUtils.isEmpty(composicao.getUuid())){
			composicao.setUuid(UUID.randomUUID().toString());
		}
	}
	
	@GetMapping("/adicionarComposicao/{codigo}")
	public ModelAndView addComposicao(@PathVariable("codigo") Composicao composicao, 
									  @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
	
		Optional<Orcamento> orcamentoAtual = orcamentoService.findOrcamentoAtual(usuarioSistema.getUsername());
		
		if (!orcamentoAtual.isPresent()) {
			System.out.println("ADICIONA COMPOSIÇÃO: Orçamento Atual não está presente da variavel global");
			return new ModelAndView("redirect:/orcamentos");
		}
		
		Optional<Etapa> etapaSelecionada = orcamentoService
				.findEtapaSelecionada(usuarioSistema.getUsername());
		
		Item item = new Item();
		item.setOrcamento(orcamentoAtual.get());
		item.setTipo(Tipo.COMPOSICAO); 
		item.setItemizacao(etapaSelecionada.get().getCodigo().toString()+".");
		item.setDescricao(composicao.getDescricao());
		item.setUnidade(composicao.getUnidade());
		item.setQuantidade(BigDecimal.ONE);
		item.setValorUnitario(composicao.getCustoTotal()); // TODO: Pesquisar na base de preço do orcamento
		item.setEtapa(etapaSelecionada.get());
		item.setComposicao(composicao);
		item.setValorMaoObra(composicao.getCustoMaoObra()); // multiply quantidade
		item.setValorMaterial(composicao.getCustoMaterial());
		item.setValorEquipamento(composicao.getCustoEquipamento());

		orcamentoAtual.get().addItem(item); 
		orcamentoService.salvar(orcamentoAtual.get()); 

		return new ModelAndView("redirect:/atual");
	}
	
}
