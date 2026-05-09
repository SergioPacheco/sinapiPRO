package com.sinapipro.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.sinapipro.dto.ListaComposicoes;
import com.sinapipro.dto.ListaInsumos;
import com.sinapipro.dto.PeriodoRelatorio;
import com.sinapipro.dto.PlanejamentoFisicoDTO;
import com.sinapipro.model.Especie;
import com.sinapipro.model.Item;
import com.sinapipro.model.Orcamento;
import com.sinapipro.model.Tipo;
import com.sinapipro.security.UsuarioSistema;
import com.sinapipro.service.BaseInsumoService;
import com.sinapipro.service.BasePrecoService;
import com.sinapipro.service.ComissaoService;
import com.sinapipro.service.DespesaService;
import com.sinapipro.service.FreeMarkerReportService;
import com.sinapipro.service.MovimentoBancarioService;
import com.sinapipro.service.OrcamentoService;
import com.sinapipro.service.PlanejamentoService;
import com.sinapipro.service.PlanoContasService;
import com.sinapipro.service.ReceitaService;
import com.sinapipro.service.RelatorioOperacionalService;
import com.sinapipro.service.RelatorioService;
import com.sinapipro.service.UnidadeVendaService;
import com.sinapipro.service.VendaService;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {
	
	private final RelatorioService relatorioService;
	private final BaseInsumoService baseInsumoService;
	private final BasePrecoService basePrecoService;
	private final OrcamentoService orcamentoService;
	private final PlanejamentoService planejamentoService;
	private final FreeMarkerReportService freeMarkerReport;
	private final UnidadeVendaService unidadeVendaService;
	private final VendaService vendaService;
	private final ComissaoService comissaoService;
	private final DespesaService despesaService;
	private final ReceitaService receitaService;
	private final MovimentoBancarioService movimentoBancarioService;
	private final PlanoContasService planoContasService;
	private final RelatorioOperacionalService relatorioOperacionalService;
	
	public RelatoriosController(
			RelatorioService relatorioService,
			BaseInsumoService baseInsumoService,
			BasePrecoService basePrecoService,
			OrcamentoService orcamentoService,
			PlanejamentoService planejamentoService,
			FreeMarkerReportService freeMarkerReport,
			UnidadeVendaService unidadeVendaService,
			VendaService vendaService,
			ComissaoService comissaoService,
			DespesaService despesaService,
			ReceitaService receitaService,
			MovimentoBancarioService movimentoBancarioService,
			PlanoContasService planoContasService,
			RelatorioOperacionalService relatorioOperacionalService) {
		this.relatorioService = relatorioService;
		this.baseInsumoService = baseInsumoService;
		this.basePrecoService = basePrecoService;
		this.orcamentoService = orcamentoService;
		this.planejamentoService = planejamentoService;
		this.freeMarkerReport = freeMarkerReport;
		this.unidadeVendaService = unidadeVendaService;
		this.vendaService = vendaService;
		this.comissaoService = comissaoService;
		this.despesaService = despesaService;
		this.receitaService = receitaService;
		this.movimentoBancarioService = movimentoBancarioService;
		this.planoContasService = planoContasService;
		this.relatorioOperacionalService = relatorioOperacionalService;
	}
	
	@GetMapping("/listaInsumos")
	public ModelAndView relatorioListagemInsumos() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioListaInsumos");
		mv.addObject("baseInsumos", baseInsumoService.findAll());
		mv.addObject("especies",   Especie.values());
		mv.addObject(new ListaInsumos());
		return mv;
	}
	
	@PostMapping("/listaInsumos")
	public ResponseEntity<byte[]> gerarRelatorioListagemInsumos(ListaInsumos listaInsumos) {
		
		byte[] relatorio = relatorioService.gerarRelatorioListaInsumos(listaInsumos); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@GetMapping("/listaComposicoes")
	public ModelAndView relatorioListaComposicoes() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioListaComposicoes");
		mv.addObject("basePrecos", basePrecoService.findAll());
		mv.addObject(new ListaComposicoes());
		return mv;
	}
	
	@PostMapping("/listaComposicoes")
	public ResponseEntity<byte[]> gerarRelatorioListaInsumos(ListaComposicoes listaComposicoes, 
			                      @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		listaComposicoes.setNomeUsuario(usuarioSistema.getUsername());									 
		byte[] relatorio = relatorioService.gerarRelatorioListaComposicoes(listaComposicoes); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@PostMapping("/imprimirComposicao")
	public ResponseEntity<byte[]> gerarRelatorioImprimirComposicao(Long codigo, 
			                      @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		byte[] relatorio = relatorioService
				.gerarRelatorioImprimirComposicao(codigo, usuarioSistema.getUsername()); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@PostMapping("/imprimirOrcamento")
	public ResponseEntity<byte[]> gerarRelatorioImprimirOrcamento(Long codigo, 
			                      @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		byte[] relatorio = relatorioService
				.gerarRelatorioImprimirOrcamento(codigo, usuarioSistema.getUsername()); 
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@GetMapping("/orcamentosEmitidos")
	public ModelAndView relatorioOrcamentosEmitidos() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioOrcamentosEmitidos");
		mv.addObject(new PeriodoRelatorio());
		return mv;
	}

	@GetMapping("/orcamentoAnalitico/{codigo}")
	public ModelAndView relatorioOrcamentoAnalitico(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		ModelAndView mv = new ModelAndView("relatorio/RelatorioOrcamentoAnalitico");
		mv.addObject("orcamento", orcamento);
		return mv;
	}

	@GetMapping("/globalMaterialMO/{codigo}")
	public ModelAndView relatorioGlobalMaterialMO(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		ModelAndView mv = new ModelAndView("relatorio/RelatorioGlobalMaterialMO");
		mv.addObject("orcamento", orcamento);

		List<Item> itensMO = orcamento.getItens().stream()
				.filter(i -> i.getValorMaoObra() != null && i.getValorMaoObra().signum() > 0)
				.collect(Collectors.toList());
		List<Item> itensMat = orcamento.getItens().stream()
				.filter(i -> i.getValorMaterial() != null && i.getValorMaterial().signum() > 0)
				.collect(Collectors.toList());

		BigDecimal totalMO = orcamento.calculaValorMaoObra();
		BigDecimal totalMat = orcamento.calculaValorMaterial();
		BigDecimal totalEq = orcamento.calculaValorEquipamento();
		BigDecimal totalGeral = totalMO.add(totalMat).add(totalEq);

		mv.addObject("itensMaoObra", itensMO);
		mv.addObject("itensMaterial", itensMat);
		mv.addObject("totalMaoObra", totalMO);
		mv.addObject("totalMaterial", totalMat);
		mv.addObject("totalEquipamento", totalEq);
		mv.addObject("totalGeral", totalGeral);
		mv.addObject("percMaoObra", totalGeral.signum() != 0 ? totalMO.multiply(BigDecimal.valueOf(100)).divide(totalGeral, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		mv.addObject("percMaterial", totalGeral.signum() != 0 ? totalMat.multiply(BigDecimal.valueOf(100)).divide(totalGeral, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		mv.addObject("percEquipamento", totalGeral.signum() != 0 ? totalEq.multiply(BigDecimal.valueOf(100)).divide(totalGeral, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		return mv;
	}

	@GetMapping("/servicosOrcamento/{codigo}")
	public ModelAndView relatorioServicosOrcamento(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		ModelAndView mv = new ModelAndView("relatorio/RelatorioServicosOrcamento");
		mv.addObject("orcamento", orcamento);

		List<Item> servicos = orcamento.getItens().stream()
				.filter(i -> Tipo.COMPOSICAO.equals(i.getTipo()))
				.collect(Collectors.toList());
		BigDecimal totalServicos = servicos.stream()
				.map(i -> i.getValorTotal() != null ? i.getValorTotal() : BigDecimal.ZERO)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		mv.addObject("servicos", servicos);
		mv.addObject("totalServicos", totalServicos);
		return mv;
	}

	@GetMapping("/exportCsv/{codigo}")
	public ResponseEntity<byte[]> exportarCsv(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		StringBuilder csv = new StringBuilder();
		csv.append("Item;Tipo;Descrição;Unidade;Quantidade;Vl.Unitário;Mão de Obra;Material;Equipamento;Total\n");
		for (Item item : orcamento.getItens()) {
			csv.append(val(item.getItemizacao())).append(";");
			csv.append(val(item.getTipo())).append(";");
			csv.append(val(item.getDescricao())).append(";");
			csv.append(val(item.getUnidade())).append(";");
			csv.append(dec(item.getQuantidade())).append(";");
			csv.append(dec(item.getValorUnitario())).append(";");
			csv.append(dec(item.getValorMaoObra())).append(";");
			csv.append(dec(item.getValorMaterial())).append(";");
			csv.append(dec(item.getValorEquipamento())).append(";");
			csv.append(dec(item.getValorTotal())).append("\n");
		}
		byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento_" + codigo + ".csv")
				.body(bytes);
	}

	@GetMapping("/exportRtf/{codigo}")
	public ResponseEntity<byte[]> exportarRtf(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		StringBuilder rtf = new StringBuilder();
		rtf.append("{\\rtf1\\ansi\\deff0\n");
		rtf.append("{\\b Orçamento: ").append(orcamento.getNome()).append("}\\par\\par\n");
		rtf.append("\\trowd\\cellx1500\\cellx3000\\cellx6000\\cellx7500\\cellx9000\n");
		rtf.append("\\intbl {\\b Item}\\cell {\\b Tipo}\\cell {\\b Descrição}\\cell {\\b Qtd}\\cell {\\b Total}\\cell\\row\n");
		for (Item item : orcamento.getItens()) {
			rtf.append("\\intbl ").append(val(item.getItemizacao())).append("\\cell ");
			rtf.append(val(item.getTipo())).append("\\cell ");
			rtf.append(val(item.getDescricao())).append("\\cell ");
			rtf.append(dec(item.getQuantidade())).append("\\cell ");
			rtf.append(dec(item.getValorTotal())).append("\\cell\\row\n");
		}
		rtf.append("}\n");
		byte[] bytes = rtf.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, "application/rtf")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento_" + codigo + ".rtf")
				.body(bytes);
	}

	private String val(Object o) {
		return o != null ? o.toString() : "";
	}

	private String dec(BigDecimal v) {
		return v != null ? v.toPlainString() : "0";
	}

	@GetMapping("/cronograma/{codigo}")
	public ResponseEntity<byte[]> relatorioCronograma(@PathVariable Long codigo) {
		Map<String, Object> data = montarDadosCronograma(codigo);
		byte[] pdf = freeMarkerReport.gerarPdf("cronograma-financeiro.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/curvaS/{codigo}")
	public ResponseEntity<byte[]> relatorioCurvaS(@PathVariable Long codigo) {
		Map<String, Object> data = montarDadosCronograma(codigo);
		byte[] pdf = freeMarkerReport.gerarPdf("curva-s.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	private Map<String, Object> montarDadosCronograma(Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		var cronograma = planejamentoService.calcularCronograma(codigo);

		DecimalFormat df = new DecimalFormat("#,##0.00");
		List<Map<String, String>> rows = cronograma.stream().map(cm -> {
			Map<String, String> m = new HashMap<>();
			m.put("periodo", cm.getPeriodo());
			m.put("valorPlanejado", df.format(cm.getValorPlanejado()));
			m.put("valorAcumulado", df.format(cm.getValorAcumulado()));
			m.put("percentual", df.format(cm.getPercentual()));
			return m;
		}).collect(Collectors.toList());

		Map<String, Object> data = new HashMap<>();
		data.put("orcamento", orcamento.getNome());
		data.put("cronograma", rows);
		data.put("totalGeral", df.format(orcamento.calculaValorTotalComTaxas()));
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		return data;
	}

	@GetMapping("/planejamentoFisico/{codigo}")
	public ResponseEntity<byte[]> relatorioPlanejamentoFisico(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		List<PlanejamentoFisicoDTO> itens = planejamentoService.montarPlanejamentoFisico(codigo);

		DecimalFormat df = new DecimalFormat("#,##0.00");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Agrupar por etapa
		Map<String, List<PlanejamentoFisicoDTO>> porEtapa = new LinkedHashMap<>();
		for (PlanejamentoFisicoDTO dto : itens) {
			porEtapa.computeIfAbsent(dto.getEtapa(), k -> new ArrayList<>()).add(dto);
		}

		List<Map<String, Object>> etapas = new ArrayList<>();
		for (var entry : porEtapa.entrySet()) {
			Map<String, Object> etapa = new HashMap<>();
			etapa.put("nome", entry.getKey());
			BigDecimal subtotal = BigDecimal.ZERO;
			List<Map<String, String>> rows = new ArrayList<>();
			for (PlanejamentoFisicoDTO dto : entry.getValue()) {
				Map<String, String> row = new HashMap<>();
				row.put("itemizacao", dto.getItemizacao() != null ? dto.getItemizacao() : "");
				row.put("descricao", dto.getDescricao() != null ? dto.getDescricao() : "");
				row.put("dataInicio", dto.getDataInicio() != null ? dto.getDataInicio().format(dtf) : "");
				row.put("dataFim", dto.getDataFim() != null ? dto.getDataFim().format(dtf) : "");
				row.put("duracaoMeses", String.valueOf(dto.getDuracaoMeses()));
				row.put("valor", df.format(dto.getValor()));
				row.put("percentual", df.format(dto.getPercentualDoTotal()));
				rows.add(row);
				subtotal = subtotal.add(dto.getValor());
			}
			etapa.put("itens", rows);
			etapa.put("subtotal", df.format(subtotal));
			BigDecimal totalGeral = orcamento.calculaValorTotalComTaxas();
			etapa.put("percentual", totalGeral.signum() != 0
					? df.format(subtotal.multiply(BigDecimal.valueOf(100)).divide(totalGeral, 2, RoundingMode.HALF_UP))
					: "0,00");
			etapas.add(etapa);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("orcamento", orcamento.getNome());
		data.put("etapas", etapas);
		data.put("totalGeral", df.format(orcamento.calculaValorTotalComTaxas()));
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("planejamento-fisico.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/mapaVendas/{codigoObra}")
	public ResponseEntity<byte[]> mapaVendas(@PathVariable Long codigoObra) {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		var unidades = unidadeVendaService.findByObra(codigoObra);
		var vendas = vendaService.findByObra(codigoObra);

		Map<Long, com.sinapipro.model.Venda> vendaMap = vendas.stream()
				.collect(Collectors.toMap(
						v -> v.getUnidade().getCodigo(), v -> v, (a, b) -> a));

		List<Map<String, String>> rows = unidades.stream().map(u -> {
			Map<String, String> row = new HashMap<>();
			row.put("identificacao", u.getIdentificacao() != null ? u.getIdentificacao() : "");
			row.put("tipo", u.getTipo() != null ? u.getTipo() : "");
			row.put("bloco", u.getBloco() != null ? u.getBloco() : "");
			row.put("areaPrivativa", u.getAreaPrivativa() != null ? df.format(u.getAreaPrivativa()) : "");
			row.put("valorBase", df.format(u.getValorBase()));
			row.put("situacao", u.getSituacao() != null ? u.getSituacao().getNome() : "Disponível");
			com.sinapipro.model.Venda venda = vendaMap.get(u.getCodigo());
			row.put("cliente", venda != null ? venda.getCliente().getNome() : "");
			row.put("dataVenda", venda != null ? venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
			return row;
		}).collect(Collectors.toList());

		long totalVendidas = vendaMap.size();
		Map<String, Object> data = new HashMap<>();
		data.put("obra", unidades.isEmpty() ? "" : unidades.get(0).getObra().getNome());
		data.put("unidades", rows);
		data.put("totalUnidades", unidades.size());
		data.put("totalVendidas", totalVendidas);
		data.put("totalDisponiveis", unidades.size() - totalVendidas);
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("mapa-vendas.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/resumoVendas/{codigoObra}")
	public ResponseEntity<byte[]> resumoVendas(@PathVariable Long codigoObra) {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		var vendas = vendaService.findByObra(codigoObra);

		List<Map<String, String>> rows = vendas.stream().map(v -> {
			Map<String, String> row = new HashMap<>();
			row.put("unidade", v.getUnidade().getIdentificacao());
			row.put("cliente", v.getCliente().getNome());
			row.put("dataVenda", v.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			row.put("valorVenda", df.format(v.getValorVenda()));
			row.put("situacao", v.getSituacao());
			row.put("totalParcelas", String.valueOf(v.getParcelas().size()));
			return row;
		}).collect(Collectors.toList());

		java.math.BigDecimal totalVendas = vendas.stream()
				.map(com.sinapipro.model.Venda::getValorVenda)
				.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

		Map<String, Object> data = new HashMap<>();
		data.put("obra", vendas.isEmpty() ? "" : vendas.get(0).getUnidade().getObra().getNome());
		data.put("vendas", rows);
		data.put("totalVendas", df.format(totalVendas));
		data.put("periodo", "Todos");
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("resumo-vendas.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/resumoCorretor/{codigoObra}")
	public ResponseEntity<byte[]> resumoCorretor(@PathVariable Long codigoObra) {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		var vendas = vendaService.findByObra(codigoObra);

		// Agrupa comissões por corretor
		Map<String, List<Map<String, String>>> porCorretor = new LinkedHashMap<>();
		Map<String, java.math.BigDecimal> totaisPorCorretor = new LinkedHashMap<>();

		for (var venda : vendas) {
			var comissoes = comissaoService.findByVenda(venda.getCodigo());
			for (var c : comissoes) {
				String corretor = c.getNomeCorretor();
				porCorretor.computeIfAbsent(corretor, k -> new ArrayList<>());
				Map<String, String> row = new HashMap<>();
				row.put("unidade", venda.getUnidade().getIdentificacao());
				row.put("cliente", venda.getCliente().getNome());
				row.put("dataVenda", venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				row.put("valorVenda", df.format(venda.getValorVenda()));
				row.put("percentual", df.format(c.getPercentual()));
				row.put("valorComissao", df.format(c.getValor()));
				row.put("situacao", c.getSituacao());
				porCorretor.get(corretor).add(row);
				totaisPorCorretor.merge(corretor, c.getValor(), java.math.BigDecimal::add);
			}
		}

		List<Map<String, Object>> corretores = new ArrayList<>();
		java.math.BigDecimal totalGeral = java.math.BigDecimal.ZERO;
		for (var entry : porCorretor.entrySet()) {
			Map<String, Object> c = new HashMap<>();
			c.put("nome", entry.getKey());
			c.put("comissoes", entry.getValue());
			java.math.BigDecimal total = totaisPorCorretor.getOrDefault(entry.getKey(), java.math.BigDecimal.ZERO);
			c.put("totalComissoes", df.format(total));
			corretores.add(c);
			totalGeral = totalGeral.add(total);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("obra", vendas.isEmpty() ? "" : vendas.get(0).getUnidade().getObra().getNome());
		data.put("corretores", corretores);
		data.put("totalGeralComissoes", df.format(totalGeral));
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("resumo-corretor.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/fluxoCaixa")
	public ResponseEntity<byte[]> fluxoCaixa(
			@RequestParam(required = false) Long codigoConta,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate inicio,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fim) {

		DecimalFormat df = new DecimalFormat("#,##0.00");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<Map<String, String>> lancamentos = new ArrayList<>();
		java.math.BigDecimal saldo = java.math.BigDecimal.ZERO;

		// Receitas
		receitaService.findAll().stream()
				.filter(r -> r.getDataVencimento() != null)
				.filter(r -> inicio == null || !r.getDataVencimento().isBefore(inicio))
				.filter(r -> fim == null || !r.getDataVencimento().isAfter(fim))
				.sorted(Comparator.comparing(r -> r.getDataVencimento()))
				.forEach(r -> {
					Map<String, String> row = new HashMap<>();
					row.put("data", r.getDataVencimento().format(dtf));
					row.put("descricao", r.getDescricao());
					row.put("tipo", "CREDITO");
					row.put("valor", df.format(r.getValor()));
					row.put("saldo", "");
					lancamentos.add(row);
				});

		// Despesas
		despesaService.findAll().stream()
				.filter(d -> d.getDataVencimento() != null)
				.filter(d -> inicio == null || !d.getDataVencimento().isBefore(inicio))
				.filter(d -> fim == null || !d.getDataVencimento().isAfter(fim))
				.sorted(Comparator.comparing(d -> d.getDataVencimento()))
				.forEach(d -> {
					Map<String, String> row = new HashMap<>();
					row.put("data", d.getDataVencimento().format(dtf));
					row.put("descricao", d.getDescricao());
					row.put("tipo", "DEBITO");
					row.put("valor", df.format(d.getValor()));
					row.put("saldo", "");
					lancamentos.add(row);
				});

		// Ordena por data
		lancamentos.sort(Comparator.comparing(m -> m.get("data")));

		// Calcula saldo acumulado
		java.math.BigDecimal saldoAcum = java.math.BigDecimal.ZERO;
		for (Map<String, String> row : lancamentos) {
			java.math.BigDecimal valor = new java.math.BigDecimal(row.get("valor").replace(".", "").replace(",", "."));
			if ("CREDITO".equals(row.get("tipo"))) {
				saldoAcum = saldoAcum.add(valor);
			} else {
				saldoAcum = saldoAcum.subtract(valor);
			}
			row.put("saldo", df.format(saldoAcum));
		}

		String periodo = (inicio != null ? inicio.format(dtf) : "Início") + " a " + (fim != null ? fim.format(dtf) : "Hoje");
		Map<String, Object> data = new HashMap<>();
		data.put("lancamentos", lancamentos);
		data.put("saldoFinal", df.format(saldoAcum));
		data.put("periodo", periodo);
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("fluxo-caixa.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/balancete")
	public ResponseEntity<byte[]> balancete(
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate inicio,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fim) {

		DecimalFormat df = new DecimalFormat("#,##0.00");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Agrupa despesas por plano de contas
		Map<String, java.math.BigDecimal[]> contaMap = new LinkedHashMap<>();

		despesaService.findAll().forEach(d -> {
			String conta = d.getPlanoContas() != null
					? d.getPlanoContas().getNumero() + " - " + d.getPlanoContas().getDescricao()
					: "Sem Conta";
			contaMap.computeIfAbsent(conta, k -> new java.math.BigDecimal[]{java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO});
			contaMap.get(conta)[1] = contaMap.get(conta)[1].add(d.getValor()); // débito
		});

		receitaService.findAll().forEach(r -> {
			String conta = r.getPlanoContas() != null
					? r.getPlanoContas().getNumero() + " - " + r.getPlanoContas().getDescricao()
					: "Sem Conta";
			contaMap.computeIfAbsent(conta, k -> new java.math.BigDecimal[]{java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO});
			contaMap.get(conta)[0] = contaMap.get(conta)[0].add(r.getValor()); // crédito
		});

		java.math.BigDecimal totalDebito = java.math.BigDecimal.ZERO;
		java.math.BigDecimal totalCredito = java.math.BigDecimal.ZERO;
		List<Map<String, String>> contas = new ArrayList<>();

		for (var entry : contaMap.entrySet()) {
			Map<String, String> row = new HashMap<>();
			String[] parts = entry.getKey().split(" - ", 2);
			row.put("numero", parts[0]);
			row.put("descricao", parts.length > 1 ? parts[1] : entry.getKey());
			java.math.BigDecimal credito = entry.getValue()[0];
			java.math.BigDecimal debito = entry.getValue()[1];
			row.put("credito", df.format(credito));
			row.put("debito", df.format(debito));
			row.put("saldo", df.format(credito.subtract(debito)));
			contas.add(row);
			totalCredito = totalCredito.add(credito);
			totalDebito = totalDebito.add(debito);
		}

		String periodo = (inicio != null ? inicio.format(dtf) : "Início") + " a " + (fim != null ? fim.format(dtf) : "Hoje");
		Map<String, Object> data = new HashMap<>();
		data.put("contas", contas);
		data.put("totalCredito", df.format(totalCredito));
		data.put("totalDebito", df.format(totalDebito));
		data.put("saldoGeral", df.format(totalCredito.subtract(totalDebito)));
		data.put("periodo", periodo);
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("balancete.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/dre")
	public ResponseEntity<byte[]> dre(
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate inicio,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fim) {

		DecimalFormat df = new DecimalFormat("#,##0.00");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Agrupa receitas por plano de contas
		Map<String, java.math.BigDecimal> receitasMap = new LinkedHashMap<>();
		receitaService.findAll().forEach(r -> {
			String desc = r.getPlanoContas() != null ? r.getPlanoContas().getDescricao() : "Receitas Diversas";
			receitasMap.merge(desc, r.getValor(), java.math.BigDecimal::add);
		});

		// Agrupa despesas por plano de contas
		Map<String, java.math.BigDecimal> despesasMap = new LinkedHashMap<>();
		despesaService.findAll().forEach(d -> {
			String desc = d.getPlanoContas() != null ? d.getPlanoContas().getDescricao() : "Despesas Diversas";
			despesasMap.merge(desc, d.getValor(), java.math.BigDecimal::add);
		});

		java.math.BigDecimal totalReceitas = receitasMap.values().stream().reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
		java.math.BigDecimal totalDespesas = despesasMap.values().stream().reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

		List<Map<String, String>> receitas = receitasMap.entrySet().stream().map(e -> {
			Map<String, String> row = new HashMap<>();
			row.put("descricao", e.getKey());
			row.put("valor", df.format(e.getValue()));
			return row;
		}).collect(Collectors.toList());

		List<Map<String, String>> despesas = despesasMap.entrySet().stream().map(e -> {
			Map<String, String> row = new HashMap<>();
			row.put("descricao", e.getKey());
			row.put("valor", df.format(e.getValue()));
			return row;
		}).collect(Collectors.toList());

		String periodo = (inicio != null ? inicio.format(dtf) : "Início") + " a " + (fim != null ? fim.format(dtf) : "Hoje");
		Map<String, Object> data = new HashMap<>();
		data.put("receitas", receitas);
		data.put("despesas", despesas);
		data.put("totalReceitas", df.format(totalReceitas));
		data.put("totalDespesas", df.format(totalDespesas));
		data.put("resultado", df.format(totalReceitas.subtract(totalDespesas)));
		data.put("periodo", periodo);
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("dre.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/inadimplencia")
	public ResponseEntity<byte[]> inadimplencia(@RequestParam(required = false) Long codigoObra) {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		var inadimplentes = relatorioOperacionalService.findInadimplentes(codigoObra);
		BigDecimal total = relatorioOperacionalService.calcularTotalInadimplente(codigoObra);

		List<Map<String, String>> rows = inadimplentes.stream().map(pi -> {
			Map<String, String> row = new HashMap<>();
			row.put("unidade", pi.getVenda().getUnidade().getIdentificacao());
			row.put("cliente", pi.getVenda().getCliente().getNome());
			row.put("numeroParcela", String.valueOf(pi.getParcela().getNumero()));
			row.put("vencimento", pi.getParcela().getDataVencimento().format(dtf));
			row.put("valor", df.format(pi.getParcela().getValor()));
			row.put("diasAtraso", String.valueOf(pi.getDiasAtraso()));
			return row;
		}).collect(Collectors.toList());

		Map<String, Object> data = new HashMap<>();
		data.put("parcelas", rows);
		data.put("totalInadimplente", df.format(total));
		data.put("totalParcelas", inadimplentes.size());
		data.put("obra", codigoObra != null ? "Obra #" + codigoObra : "Todas as Obras");
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("inadimplencia.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/posicaoEstoque/{codigoObra}")
	public ResponseEntity<byte[]> posicaoEstoque(@PathVariable Long codigoObra) {
		DecimalFormat df = new DecimalFormat("#,##0.00");

		var posicoes = relatorioOperacionalService.getPosicaoEstoque(codigoObra);
		BigDecimal valorTotal = relatorioOperacionalService.calcularValorTotalEstoque(codigoObra);

		List<Map<String, String>> rows = posicoes.stream().map(pos -> {
			Map<String, String> row = new HashMap<>();
			row.put("insumo", pos.getEstoque().getInsumo().getDescricao());
			row.put("qtdAtual", df.format(pos.getEstoque().getQuantidadeAtual()));
			row.put("qtdMinima", df.format(pos.getEstoque().getQuantidadeMinima()));
			row.put("custoMedio", pos.getEstoque().getCustoMedio() != null
					? df.format(pos.getEstoque().getCustoMedio()) : "0,00");
			row.put("valorTotal", df.format(pos.getValorTotal()));
			row.put("status", pos.getStatus());
			return row;
		}).collect(Collectors.toList());

		Map<String, Object> data = new HashMap<>();
		data.put("itens", rows);
		data.put("valorTotal", df.format(valorTotal));
		data.put("obra", "Obra #" + codigoObra);
		data.put("emissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("posicao-estoque.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}
}
