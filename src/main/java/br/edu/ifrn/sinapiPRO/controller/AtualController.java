package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.filter.AtualFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.ItemService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.RelatorioService;
import br.edu.ifrn.sinapiPRO.service.UsuarioService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/atual")
public class AtualController {

	private final ItemService itemService;
	private final UsuarioService usuarioService;
	private final OrcamentoService orcamentoService; 
	private final RelatorioService relatorioService;
	
	@Autowired
	public AtualController(ItemService itemService, OrcamentoService orcamentoService, UsuarioService usuarioService, RelatorioService relatorioService ) {
		this.itemService = itemService;
		this.usuarioService = usuarioService;
		this.orcamentoService = orcamentoService;
		this.relatorioService = relatorioService;
	}
	
	@RequestMapping("/novo")
	public ModelAndView novo(Item item, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		ModelAndView mv = new ModelAndView("atual/PesquisaAtual");
		
		if (item.isNovo()) { 
			Optional<Orcamento> orcamentoAtual = orcamentoService.findOrcamentoAtual(usuarioSistema.getUsername());
			if (!orcamentoAtual.isPresent()) {
				return new ModelAndView("redirect:/orcamentos");
			}
		
			item.setOrcamento(orcamentoAtual.get());
		}
		 
		return mv;
	}
	
	/**
	 * SALVAR - Salva o Item do Orçamento
	 * 
	 */
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Item item,  AtualFilter filter,	
			                          BindingResult result, Model model, 
	 		                          RedirectAttributes attributes, 
	 		                          @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		if (result.hasErrors()) {
			return novo(item, usuarioSistema);
		}
		try{
			if (item.getQuantidade().compareTo(BigDecimal.ZERO) == 0 || 
				item.getQuantidade() == null) {
				item.setQuantidade(new BigDecimal("1"));
			}
			// TODO: Testar se especie e nulo 
			// Calcular totais
			if ((item.getTipo() == Tipo.INSUMO)) {
				switch (item.getEspecie()) {
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
			}
			if ((item.getTipo() == Tipo.COMPOSICAO)) {
				item.setValorMaoObra(item.getComposicao().getCustoMaoObra().multiply(item.getQuantidade()));
				item.setValorMaterial(item.getComposicao().getCustoMaterial().multiply(item.getQuantidade()));
				item.setValorEquipamento(item.getComposicao().getCustoEquipamento().multiply(item.getQuantidade()));
			}
			itemService.salvar(item);
			
		} catch(JaCadastradoException e){
			result.rejectValue("nome",e.getMessage(), e.getMessage());
			return new ModelAndView("atual/PesquisaAtual");
		}
		attributes.addFlashAttribute("mensagem", "Item Orcamento salvo com sucesso!");
		
		return new ModelAndView("redirect:/atual");
	}
	
	/**
	 * EDITAR - Edita o item do orçamento
	 */
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Item item, 
			 				  AtualFilter filter, BindingResult result,
			 				  @PageableDefault(size = 10) Pageable pageable, 
			 				  HttpServletRequest httpServletRequest, 
			 				  @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		Optional<Orcamento> orcamentoAtual = orcamentoService.findOrcamentoAtual(usuarioSistema.getUsername());
		
		if (!orcamentoAtual.isPresent()) {
			return new ModelAndView("redirect:/orcamentos");
		}
		 
		filter.setOrcamento(orcamentoAtual.get());
		
		PageWrapper<Item> paginaWrapper = new PageWrapper<>(itemService
								.filtrar(filter, pageable), httpServletRequest);
		
		ModelAndView mv = novo(item, usuarioSistema);
				
		mv.addObject(item);
		
		mv.addObject("etapas",  itemService.findEtapasOrcamento(item.getOrcamento().getCodigo()));
		mv.addObject("orcamentoAtual", item.getOrcamento());
		mv.addObject("pagina" , paginaWrapper);
		
		mv.addObject("totalMaoObra",    orcamentoAtual.get().calculaValorMaoObra()); 
		mv.addObject("totalMaterial",     orcamentoAtual.get().calculaValorMaterial());
		mv.addObject("totalEquipamento",  orcamentoAtual.get().calculaValorEquipamento());
		//mv.addObject("totalBDI",        orcamentoAtual.get().getBdi());
		mv.addObject("totalOrcamento",   orcamentoAtual.get().calculaValorTotalItens()); 
		
		return mv;
	}
	
	/**
	 * PESQUISAR - pesquisa os itens do orçamento e edita o item selecionado
	 */
	@GetMapping
	public ModelAndView pesquisar(Item item,
								  AtualFilter filter, 
			                      BindingResult result,
			                      @PageableDefault(size = 10) Pageable pageable, 
			                      HttpServletRequest httpServletRequest,
			                      @AuthenticationPrincipal UsuarioSistema usuarioSistema){
		                       
		Optional<Orcamento> orcamentoAtual = orcamentoService.findOrcamentoAtual(usuarioSistema.getUsername());
		
		if (!orcamentoAtual.isPresent()) {
			return new ModelAndView("redirect:/orcamentos");
		}
		
		filter.setOrcamento(orcamentoAtual.get());
		
		PageWrapper<Item> paginaWrapper = new PageWrapper<>(itemService
				.filtrar(filter, pageable), httpServletRequest);
		
		// Salva Etapa selecionada 
		if (filter.getEtapa() != null && filter.getEtapa().getCodigo() != null) { 
			System.out.println("ETAPA "+filter.getEtapa().getCodigo()+" "+filter.getEtapa().getNome());
			Optional<Usuario> usuario = usuarioService.findByEmail(usuarioSistema.getUsername());
			  if (usuario.isPresent()) {
				  Usuario editaUsuario = usuario.get();
				  editaUsuario.setEtapaSelecionada(filter.getEtapa());
				  usuarioService.salvar(editaUsuario);
				  System.out.println("PESQUISAR: Etapa FOI selecionada");
			  } else {
				  System.out.println("USUARIO "+usuarioSistema.getUsername()+" NÃO FOI ENCONTRADO");
			  }
		} else {
			System.out.println("PESQUISAR: Etapa NAO FOI selecionada");
		}
		
		ModelAndView mv = new ModelAndView("atual/PesquisaAtual");
		
		mv.addObject("etapas",  itemService.findEtapasOrcamento(orcamentoAtual.get().getCodigo() ));
		mv.addObject("orcamentoAtual",  orcamentoAtual.get());
		mv.addObject("pagina" ,         paginaWrapper);
		mv.addObject("totalMaoObra",    orcamentoAtual.get().calculaValorMaoObra()); 
		mv.addObject("totalMaterial",   orcamentoAtual.get().calculaValorMaterial());
		mv.addObject("totalEquipamento",orcamentoAtual.get().calculaValorEquipamento());
		//mv.addObject("totalBDI",        orcamentoAtual.get().getBdi());
		mv.addObject("totalOrcamento",  orcamentoAtual.get().calculaValorTotalItens()); 
		
		return mv;
	}
	
	/*
	 * EXCLUIR - Exclui um item do orçamento
	 */
	@DeleteMapping("/{codigo}")
	public ModelAndView excluir(@PathVariable("codigo") Item item) {
		try {
			
			//TODO: Ao deletar a ETAPA deletar todos os itens da etapa 
			
			itemService.excluir(item.getCodigo());
			
		} catch (ImpossivelExcluirEntidadeException e) {
			return new ModelAndView("redirect:/atual");
		}
		return new ModelAndView("redirect:/atual");
	} 
	/*
	 * ITEMIZAR - Reorganiza a itemização do orçamento
	 */
	@GetMapping("/itemizar/{codigo}")
	public ModelAndView itemizar(@PathVariable("codigo") Orcamento orcamento) {
		
		orcamento.Itemizar();
		orcamentoService.salvar(orcamento);
		
		return new ModelAndView("redirect:/atual");
	} 
	
	/**
	 *  IMPRIMIR - Imprime o orçamento selecionado
	 */
	@GetMapping("/imprimirOrcamento/{codigo}")
	public ResponseEntity<byte[]> imprimir (@PathVariable("codigo") Long codigo, 
			                   @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		byte[] relatorio = null;
		try {
			relatorio = relatorioService
					.gerarRelatorioImprimirOrcamento(codigo, usuarioSistema.getUsername());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	/**
	 *  COM00201 - IMPRIMIR COMPOSICOES DO ORÇAMENTO 
	 */
	@GetMapping("/imprimirComposicoesOrcamento/{codigo}")
	public ResponseEntity<byte[]> imprimirComposicoesOrcamento (@PathVariable("codigo") Long codigo, 
			                   @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		byte[] relatorio = null;
		try {
			relatorio = relatorioService
					.gerarRelatorioImprimirComposicoesOrcamento(codigo, usuarioSistema.getUsername());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	
}

 
