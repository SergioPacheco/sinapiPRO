package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import br.edu.ifrn.sinapiPRO.dto.CurvaAbcDTO;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Etapa;
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
	@RequestMapping(value = { "/novo", "/{codigo}" }, method = RequestMethod.POST)
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
			 				  @PageableDefault(size = 50) Pageable pageable, 
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
			                      @PageableDefault(size = 50) Pageable pageable, 
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
	
	/**
	 *  EXPORTAR XLS - Exporta orçamento como planilha Excel
	 */
	@GetMapping("/exportarXls/{codigo}")
	public ResponseEntity<byte[]> exportarXls(@PathVariable("codigo") Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		if (orcamento == null) {
			return ResponseEntity.notFound().build();
		}
		try {
			byte[] xls = relatorioService.exportarOrcamentoXls(orcamento);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento-" + codigo + ".xlsx")
					.body(xls);
		} catch (Exception e) {
			return ResponseEntity.status(500).build();
		}
	}

	/**
	 *  CURVA ABC - Itens do orçamento ordenados por custo total decrescente
	 */
	@GetMapping("/curvaAbc/{codigo}")
	public ModelAndView curvaAbc(@PathVariable("codigo") Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		if (orcamento == null) {
			return new ModelAndView("redirect:/orcamentos");
		}

		List<Item> itens = new ArrayList<>(orcamento.getItens());
		itens.removeIf(i -> i.getTipo() == Tipo.ETAPA);
		Collections.sort(itens, Comparator.comparing(Item::getValorTotal).reversed());

		BigDecimal totalGeral = orcamento.calculaValorTotalItens();
		BigDecimal acumulado = BigDecimal.ZERO;
		List<CurvaAbcDTO> curva = new ArrayList<>();

		for (Item item : itens) {
			CurvaAbcDTO dto = new CurvaAbcDTO();
			dto.setItemizacao(item.getItemizacao());
			dto.setDescricao(item.getDescricao());
			dto.setUnidade(item.getUnidade());
			dto.setQuantidade(item.getQuantidade());
			dto.setValorUnitario(item.getValorUnitario());
			dto.setValorTotal(item.getValorTotal());

			BigDecimal perc = totalGeral.compareTo(BigDecimal.ZERO) > 0
					? item.getValorTotal().multiply(new BigDecimal("100")).divide(totalGeral, 2, RoundingMode.HALF_UP)
					: BigDecimal.ZERO;
			dto.setPercentual(perc);

			acumulado = acumulado.add(perc);
			dto.setPercentualAcumulado(acumulado);

			if (acumulado.compareTo(new BigDecimal("80")) <= 0) {
				dto.setClassificacao("A");
			} else if (acumulado.compareTo(new BigDecimal("95")) <= 0) {
				dto.setClassificacao("B");
			} else {
				dto.setClassificacao("C");
			}
			curva.add(dto);
		}

		ModelAndView mv = new ModelAndView("atual/CurvaAbc");
		mv.addObject("orcamento", orcamento);
		mv.addObject("curvaAbc", curva);
		mv.addObject("totalGeral", totalGeral);
		return mv;
	}

	@GetMapping("/analitico/{codigo}")
	public ModelAndView orcamentoAnalitico(@PathVariable("codigo") Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		if (orcamento == null) return new ModelAndView("redirect:/orcamentos");
		orcamento.Itemizar();
		java.util.Map<Etapa, java.util.List<Item>> etapas = new java.util.LinkedHashMap<>();
		for (Item item : orcamento.getItens()) {
			if (item.getTipo() != Tipo.ETAPA) {
				etapas.computeIfAbsent(item.getEtapa(), k -> new java.util.ArrayList<>()).add(item);
			}
		}
		ModelAndView mv = new ModelAndView("atual/OrcamentoAnalitico");
		mv.addObject("orcamento", orcamento);
		mv.addObject("etapas", etapas.entrySet());
		mv.addObject("subtotal", orcamento.calculaValorSubTotal());
		mv.addObject("taxas", orcamento.calculaValorTaxas());
		mv.addObject("tributos", orcamento.calculaValorTributos());
		mv.addObject("total", orcamento.calculaValorTotalComTaxas());
		return mv;
	}

	@GetMapping("/globalMaterialMO/{codigo}")
	public ModelAndView globalMaterialMO(@PathVariable("codigo") Long codigo) {
		Orcamento orc = orcamentoService.buscarComItens(codigo);
		if (orc == null) return new ModelAndView("redirect:/orcamentos");
		BigDecimal mo = orc.calculaValorMaoObra();
		BigDecimal mat = orc.calculaValorMaterial();
		BigDecimal equip = orc.calculaValorEquipamento();
		BigDecimal sub = orc.calculaValorSubTotal();
		BigDecimal cem = new BigDecimal("100");
		ModelAndView mv = new ModelAndView("atual/GlobalMaterialMO");
		mv.addObject("orcamento", orc);
		mv.addObject("maoObra", mo);
		mv.addObject("material", mat);
		mv.addObject("equipamento", equip);
		mv.addObject("subtotal", sub);
		mv.addObject("percMO", sub.compareTo(BigDecimal.ZERO) > 0 ? mo.multiply(cem).divide(sub, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		mv.addObject("percMat", sub.compareTo(BigDecimal.ZERO) > 0 ? mat.multiply(cem).divide(sub, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		mv.addObject("percEquip", sub.compareTo(BigDecimal.ZERO) > 0 ? equip.multiply(cem).divide(sub, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		mv.addObject("leisSociais", orc.calculaValorLeisSociais());
		mv.addObject("bdi", orc.calculaValorBDI());
		mv.addObject("taxaAdm", orc.calculaValorTaxaAdm());
		mv.addObject("tributos", orc.calculaValorTributos());
		mv.addObject("total", orc.calculaValorTotalComTaxas());
		return mv;
	}

	@GetMapping("/servicos/{codigo}")
	public ModelAndView servicosOrcamento(@PathVariable("codigo") Long codigo) {
		Orcamento orc = orcamentoService.buscarComItens(codigo);
		if (orc == null) return new ModelAndView("redirect:/orcamentos");
		orc.Itemizar();
		List<Item> servicos = new ArrayList<>();
		BigDecimal totalServicos = BigDecimal.ZERO;
		for (Item item : orc.getItens()) {
			if (item.getTipo() == Tipo.COMPOSICAO) {
				servicos.add(item);
				if (item.getValorTotal() != null) totalServicos = totalServicos.add(item.getValorTotal());
			}
		}
		ModelAndView mv = new ModelAndView("atual/ServicosOrcamento");
		mv.addObject("orcamento", orc);
		mv.addObject("servicos", servicos);
		mv.addObject("totalServicos", totalServicos);
		return mv;
	}

	@GetMapping("/exportarCsv/{codigo}")
	public ResponseEntity<byte[]> exportarCsv(@PathVariable("codigo") Long codigo) {
		Orcamento orc = orcamentoService.buscarComItens(codigo);
		if (orc == null) return ResponseEntity.notFound().build();
		orc.Itemizar();
		StringBuilder sb = new StringBuilder();
		sb.append("Item;Tipo;Descrição;Unidade;Quantidade;Vl.Unitário;MãoObra;Material;Equipamento;Total\n");
		for (Item item : orc.getItens()) {
			sb.append(item.getItemizacao() != null ? item.getItemizacao() : "").append(";");
			sb.append(item.getTipo() != null ? item.getTipo().name() : "").append(";");
			sb.append(item.getDescricao() != null ? item.getDescricao().replace(";", ",") : "").append(";");
			sb.append(item.getUnidade() != null ? item.getUnidade() : "").append(";");
			sb.append(item.getQuantidade() != null ? item.getQuantidade() : "0").append(";");
			sb.append(item.getValorUnitario() != null ? item.getValorUnitario() : "0").append(";");
			sb.append(item.getValorMaoObra() != null ? item.getValorMaoObra() : "0").append(";");
			sb.append(item.getValorMaterial() != null ? item.getValorMaterial() : "0").append(";");
			sb.append(item.getValorEquipamento() != null ? item.getValorEquipamento() : "0").append(";");
			sb.append(item.getValorTotal() != null ? item.getValorTotal() : "0").append("\n");
		}
		byte[] csv = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento-" + codigo + ".csv")
				.body(csv);
	}

	@GetMapping("/exportarRtf/{codigo}")
	public ResponseEntity<byte[]> exportarRtf(@PathVariable("codigo") Long codigo) {
		Orcamento orc = orcamentoService.buscarComItens(codigo);
		if (orc == null) return ResponseEntity.notFound().build();
		orc.Itemizar();
		StringBuilder rtf = new StringBuilder();
		rtf.append("{\\rtf1\\ansi\\deff0\n");
		rtf.append("{\\b Orçamento: ").append(orc.getNome()).append("}\n\\par\\par\n");
		rtf.append("{\\b Item\\tab Tipo\\tab Descrição\\tab Und\\tab Qtd\\tab Vl.Unit\\tab Total}\n\\par\n");
		for (Item item : orc.getItens()) {
			rtf.append(item.getItemizacao() != null ? item.getItemizacao() : "").append("\\tab ");
			rtf.append(item.getTipo() != null ? item.getTipo().name() : "").append("\\tab ");
			rtf.append(item.getDescricao() != null ? item.getDescricao() : "").append("\\tab ");
			rtf.append(item.getUnidade() != null ? item.getUnidade() : "").append("\\tab ");
			rtf.append(item.getQuantidade() != null ? item.getQuantidade() : "0").append("\\tab ");
			rtf.append(item.getValorUnitario() != null ? item.getValorUnitario() : "0").append("\\tab ");
			rtf.append(item.getValorTotal() != null ? item.getValorTotal() : "0").append("\\par\n");
		}
		rtf.append("\\par{\\b Total: R$ ").append(orc.calculaValorTotalComTaxas()).append("}\n");
		rtf.append("}");
		byte[] bytes = rtf.toString().getBytes(java.nio.charset.StandardCharsets.US_ASCII);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, "application/rtf")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento-" + codigo + ".rtf")
				.body(bytes);
	}

	@GetMapping("/comparativo")
	public ModelAndView comparativo(@org.springframework.web.bind.annotation.RequestParam("venda") Long codigoVenda,
									@org.springframework.web.bind.annotation.RequestParam("execucao") Long codigoExecucao) {
		Orcamento venda = orcamentoService.buscarComItens(codigoVenda);
		Orcamento execucao = orcamentoService.buscarComItens(codigoExecucao);
		if (venda == null || execucao == null) return new ModelAndView("redirect:/orcamentos");
		venda.Itemizar();
		execucao.Itemizar();
		BigDecimal totalV = venda.calculaValorTotalComTaxas();
		BigDecimal totalE = execucao.calculaValorTotalComTaxas();
		BigDecimal diff = totalE.subtract(totalV);
		BigDecimal percDiff = totalV.compareTo(BigDecimal.ZERO) > 0
				? diff.multiply(new BigDecimal("100")).divide(totalV, 2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO;
		ModelAndView mv = new ModelAndView("atual/ComparativoVendaExecucao");
		mv.addObject("venda", venda);
		mv.addObject("execucao", execucao);
		mv.addObject("totalVenda", totalV);
		mv.addObject("totalExecucao", totalE);
		mv.addObject("diferenca", diff);
		mv.addObject("percentualDiferenca", percDiff);
		return mv;
	}
}
