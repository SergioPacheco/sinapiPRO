package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import br.edu.ifrn.sinapiPRO.dto.OrcamentoExportDTO;
import br.edu.ifrn.sinapiPRO.model.Desoneracao;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.TipoOrcamento;
import br.edu.ifrn.sinapiPRO.repository.filter.OrcamentoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.BaseInsumoService;
import br.edu.ifrn.sinapiPRO.service.BasePrecoService;
import br.edu.ifrn.sinapiPRO.service.EstadoService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/orcamentos")
public class OrcamentosController {

	private final EstadoService estadoService;
	private final BasePrecoService basePrecoService;
	private final BaseInsumoService baseInsumoService;
	private final OrcamentoService orcamentoService;
	
	@Autowired
	public OrcamentosController(OrcamentoService orcamentoService, EstadoService estadoService,
								BasePrecoService basePrecoService, BaseInsumoService baseInsumoService) {
		this.orcamentoService = orcamentoService;
		this.estadoService = estadoService; 
		this.basePrecoService = basePrecoService;
		this.baseInsumoService = baseInsumoService;
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(Orcamento orcamento) {
		ModelAndView mv = new ModelAndView("orcamento/CadastroOrcamento");
		mv.addObject("estados", estadoService.findAll());
		mv.addObject("basePrecos", basePrecoService.findAll());
		mv.addObject("baseInsumos", baseInsumoService.findAll());
		mv.addObject("desoneracoes", Desoneracao.values());
		mv.addObject("tiposOrcamento", TipoOrcamento.values());
		return mv;
	}
	 
	@PostMapping({ "/novo", "{\\d+}" })
	public ModelAndView salvar(@Valid Orcamento orcamento,
			                   BindingResult result,
	 		                   RedirectAttributes attributes,
	 		                   @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		if (result.hasErrors()) {
			return novo(orcamento);
		}
		try {
			orcamento.setUsuario(usuarioSistema.getUsuario());
			orcamentoService.salvar(orcamento);
		} catch(ResourceNotFoundException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return novo(orcamento);
		}
		attributes.addFlashAttribute("mensagem", "Orcamento salvo com sucesso!");
		return new ModelAndView("redirect:/atual");
	}
	
	@GetMapping
	public ModelAndView pesquisar(OrcamentoFilter orcamentoFilter, BindingResult result, 
								  @PageableDefault(size = 10) Pageable pageable, 
			                      HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView("orcamento/PesquisaOrcamentos");
		PageWrapper<Orcamento> paginaWrapper = new PageWrapper<>(orcamentoService
									.filtrar(orcamentoFilter, pageable), httpServletRequest);
		mv.addObject("pagina" , paginaWrapper);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Orcamento orcamento) {
		try {
			orcamentoService.excluir(orcamento);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	} 
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		if (orcamento == null) {
			return new ModelAndView("redirect:/orcamentos");
		}
		ModelAndView mv = novo(orcamento);
		mv.addObject(orcamento);
		return mv;
	}

	@GetMapping("/{codigo}/export")
	public @ResponseBody ResponseEntity<OrcamentoExportDTO> exportar(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		if (orcamento == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(new OrcamentoExportDTO(orcamento));
	}

	/**
	 *  Redireciona para o Orçamento Atual
	 */
	@GetMapping("/atual")
	public ModelAndView atual(@AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		return orcamentoService.getCodigoOrcamentoAtual(usuarioSistema.getUsername())
				.map(codigo -> new ModelAndView("redirect:/atual/" + codigo))
				.orElse(new ModelAndView("redirect:/orcamentos"));
	}	
	
	/**
	 *  Seleciona este Orçamento como Atual
	 */
	@GetMapping("/acessaOrcamento/{codigo}")
	public ModelAndView acessaOrcamento(@PathVariable Long codigo,
										@AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		orcamentoService.selecionarOrcamento(usuarioSistema.getUsername(), codigo);
		return new ModelAndView("redirect:/atual/" + codigo);
	}	

	@PostMapping("/gerarVenda/{codigo}")
	public ModelAndView gerarVenda(@PathVariable Long codigo, RedirectAttributes attributes) {
		Orcamento venda = orcamentoService.copiarOrcamento(codigo, TipoOrcamento.VENDA);
		attributes.addFlashAttribute("mensagem", "Orçamento de Venda gerado com sucesso! Código: " + venda.getCodigo());
		return new ModelAndView("redirect:/orcamentos");
	}

	@PostMapping("/gerarExecucao/{codigo}")
	public ModelAndView gerarExecucao(@PathVariable Long codigo, RedirectAttributes attributes) {
		Orcamento exec = orcamentoService.copiarOrcamento(codigo, TipoOrcamento.EXECUCAO);
		attributes.addFlashAttribute("mensagem", "Orçamento de Execução gerado com sucesso! Código: " + exec.getCodigo());
		return new ModelAndView("redirect:/orcamentos");
	}

}
