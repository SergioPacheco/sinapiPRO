package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.edu.ifrn.sinapiPRO.dto.ListaComposicoes;
import br.edu.ifrn.sinapiPRO.dto.ListaInsumos;
import br.edu.ifrn.sinapiPRO.dto.PeriodoRelatorio;
import br.edu.ifrn.sinapiPRO.dto.PlanejamentoFisicoDTO;
import br.edu.ifrn.sinapiPRO.model.Especie;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.BaseInsumoService;
import br.edu.ifrn.sinapiPRO.service.BasePrecoService;
import br.edu.ifrn.sinapiPRO.service.ComissaoService;
import br.edu.ifrn.sinapiPRO.service.DespesaService;
import br.edu.ifrn.sinapiPRO.service.FreeMarkerReportService;
import br.edu.ifrn.sinapiPRO.service.MovimentoBancarioService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.PlanejamentoService;
import br.edu.ifrn.sinapiPRO.service.PlanoContasService;
import br.edu.ifrn.sinapiPRO.service.ReceitaService;
import br.edu.ifrn.sinapiPRO.service.RelatorioService;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;
import br.edu.ifrn.sinapiPRO.service.VendaService;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {
	
	@Autowired
	private RelatorioService relatorioService;
	
	@Autowired 
	private BaseInsumoService baseInsumoService; 
	
	@Autowired 
	private BasePrecoService basePrecoService;

	@Autowired
	private OrcamentoService orcamentoService;

	@Autowired
	private PlanejamentoService planejamentoService;

	@Autowired
	private FreeMarkerReportService freeMarkerReport;
	
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
		java.util.Map<String, Object> data = montarDadosCronograma(codigo);
		byte[] pdf = freeMarkerReport.gerarPdf("cronograma-financeiro.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/curvaS/{codigo}")
	public ResponseEntity<byte[]> relatorioCurvaS(@PathVariable Long codigo) {
		java.util.Map<String, Object> data = montarDadosCronograma(codigo);
		byte[] pdf = freeMarkerReport.gerarPdf("curva-s.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	private java.util.Map<String, Object> montarDadosCronograma(Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		var cronograma = planejamentoService.calcularCronograma(codigo);

		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		List<java.util.Map<String, String>> rows = cronograma.stream().map(cm -> {
			java.util.Map<String, String> m = new java.util.HashMap<>();
			m.put("periodo", cm.getPeriodo());
			m.put("valorPlanejado", df.format(cm.getValorPlanejado()));
			m.put("valorAcumulado", df.format(cm.getValorAcumulado()));
			m.put("percentual", df.format(cm.getPercentual()));
			return m;
		}).collect(Collectors.toList());

		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("orcamento", orcamento.getNome());
		data.put("cronograma", rows);
		data.put("totalGeral", df.format(orcamento.calculaValorTotalComTaxas()));
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
		return data;
	}

	@GetMapping("/planejamentoFisico/{codigo}")
	public ResponseEntity<byte[]> relatorioPlanejamentoFisico(@PathVariable Long codigo) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigo);
		List<PlanejamentoFisicoDTO> itens = planejamentoService.montarPlanejamentoFisico(codigo);

		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Agrupar por etapa
		java.util.Map<String, List<PlanejamentoFisicoDTO>> porEtapa = new java.util.LinkedHashMap<>();
		for (PlanejamentoFisicoDTO dto : itens) {
			porEtapa.computeIfAbsent(dto.getEtapa(), k -> new java.util.ArrayList<>()).add(dto);
		}

		List<java.util.Map<String, Object>> etapas = new java.util.ArrayList<>();
		for (var entry : porEtapa.entrySet()) {
			java.util.Map<String, Object> etapa = new java.util.HashMap<>();
			etapa.put("nome", entry.getKey());
			BigDecimal subtotal = BigDecimal.ZERO;
			List<java.util.Map<String, String>> rows = new java.util.ArrayList<>();
			for (PlanejamentoFisicoDTO dto : entry.getValue()) {
				java.util.Map<String, String> row = new java.util.HashMap<>();
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

		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("orcamento", orcamento.getNome());
		data.put("etapas", etapas);
		data.put("totalGeral", df.format(orcamento.calculaValorTotalComTaxas()));
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("planejamento-fisico.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@Autowired
	private UnidadeVendaService unidadeVendaService;

	@Autowired
	private VendaService vendaService;

	@Autowired
	private ComissaoService comissaoService;

	@GetMapping("/mapaVendas/{codigoObra}")
	public ResponseEntity<byte[]> mapaVendas(@PathVariable Long codigoObra) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		var unidades = unidadeVendaService.findByObra(codigoObra);
		var vendas = vendaService.findByObra(codigoObra);

		java.util.Map<Long, br.edu.ifrn.sinapiPRO.model.Venda> vendaMap = vendas.stream()
				.collect(java.util.stream.Collectors.toMap(
						v -> v.getUnidade().getCodigo(), v -> v, (a, b) -> a));

		List<java.util.Map<String, String>> rows = unidades.stream().map(u -> {
			java.util.Map<String, String> row = new java.util.HashMap<>();
			row.put("identificacao", u.getIdentificacao() != null ? u.getIdentificacao() : "");
			row.put("tipo", u.getTipo() != null ? u.getTipo() : "");
			row.put("bloco", u.getBloco() != null ? u.getBloco() : "");
			row.put("areaPrivativa", u.getAreaPrivativa() != null ? df.format(u.getAreaPrivativa()) : "");
			row.put("valorBase", df.format(u.getValorBase()));
			row.put("situacao", u.getSituacao() != null ? u.getSituacao().getNome() : "Disponível");
			br.edu.ifrn.sinapiPRO.model.Venda venda = vendaMap.get(u.getCodigo());
			row.put("cliente", venda != null ? venda.getCliente().getNome() : "");
			row.put("dataVenda", venda != null ? venda.getDataVenda().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
			return row;
		}).collect(java.util.stream.Collectors.toList());

		long totalVendidas = vendaMap.size();
		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("obra", unidades.isEmpty() ? "" : unidades.get(0).getObra().getNome());
		data.put("unidades", rows);
		data.put("totalUnidades", unidades.size());
		data.put("totalVendidas", totalVendidas);
		data.put("totalDisponiveis", unidades.size() - totalVendidas);
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("mapa-vendas.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/resumoVendas/{codigoObra}")
	public ResponseEntity<byte[]> resumoVendas(@PathVariable Long codigoObra) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		var vendas = vendaService.findByObra(codigoObra);

		List<java.util.Map<String, String>> rows = vendas.stream().map(v -> {
			java.util.Map<String, String> row = new java.util.HashMap<>();
			row.put("unidade", v.getUnidade().getIdentificacao());
			row.put("cliente", v.getCliente().getNome());
			row.put("dataVenda", v.getDataVenda().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			row.put("valorVenda", df.format(v.getValorVenda()));
			row.put("situacao", v.getSituacao());
			row.put("totalParcelas", String.valueOf(v.getParcelas().size()));
			return row;
		}).collect(java.util.stream.Collectors.toList());

		java.math.BigDecimal totalVendas = vendas.stream()
				.map(br.edu.ifrn.sinapiPRO.model.Venda::getValorVenda)
				.reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("obra", vendas.isEmpty() ? "" : vendas.get(0).getUnidade().getObra().getNome());
		data.put("vendas", rows);
		data.put("totalVendas", df.format(totalVendas));
		data.put("periodo", "Todos");
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("resumo-vendas.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/resumoCorretor/{codigoObra}")
	public ResponseEntity<byte[]> resumoCorretor(@PathVariable Long codigoObra) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		var vendas = vendaService.findByObra(codigoObra);

		// Agrupa comissões por corretor
		java.util.Map<String, List<java.util.Map<String, String>>> porCorretor = new java.util.LinkedHashMap<>();
		java.util.Map<String, java.math.BigDecimal> totaisPorCorretor = new java.util.LinkedHashMap<>();

		for (var venda : vendas) {
			var comissoes = comissaoService.findByVenda(venda.getCodigo());
			for (var c : comissoes) {
				String corretor = c.getNomeCorretor();
				porCorretor.computeIfAbsent(corretor, k -> new java.util.ArrayList<>());
				java.util.Map<String, String> row = new java.util.HashMap<>();
				row.put("unidade", venda.getUnidade().getIdentificacao());
				row.put("cliente", venda.getCliente().getNome());
				row.put("dataVenda", venda.getDataVenda().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				row.put("valorVenda", df.format(venda.getValorVenda()));
				row.put("percentual", df.format(c.getPercentual()));
				row.put("valorComissao", df.format(c.getValor()));
				row.put("situacao", c.getSituacao());
				porCorretor.get(corretor).add(row);
				totaisPorCorretor.merge(corretor, c.getValor(), java.math.BigDecimal::add);
			}
		}

		List<java.util.Map<String, Object>> corretores = new java.util.ArrayList<>();
		java.math.BigDecimal totalGeral = java.math.BigDecimal.ZERO;
		for (var entry : porCorretor.entrySet()) {
			java.util.Map<String, Object> c = new java.util.HashMap<>();
			c.put("nome", entry.getKey());
			c.put("comissoes", entry.getValue());
			java.math.BigDecimal total = totaisPorCorretor.getOrDefault(entry.getKey(), java.math.BigDecimal.ZERO);
			c.put("totalComissoes", df.format(total));
			corretores.add(c);
			totalGeral = totalGeral.add(total);
		}

		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("obra", vendas.isEmpty() ? "" : vendas.get(0).getUnidade().getObra().getNome());
		data.put("corretores", corretores);
		data.put("totalGeralComissoes", df.format(totalGeral));
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("resumo-corretor.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@Autowired
	private DespesaService despesaService;

	@Autowired
	private ReceitaService receitaService;

	@Autowired
	private MovimentoBancarioService movimentoBancarioService;

	@Autowired
	private PlanoContasService planoContasService;

	@GetMapping("/fluxoCaixa")
	public ResponseEntity<byte[]> fluxoCaixa(
			@RequestParam(required = false) Long codigoConta,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fim) {

		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<java.util.Map<String, String>> lancamentos = new java.util.ArrayList<>();
		java.math.BigDecimal saldo = java.math.BigDecimal.ZERO;

		// Receitas
		receitaService.findAll().stream()
				.filter(r -> r.getDataVencimento() != null)
				.filter(r -> inicio == null || !r.getDataVencimento().isBefore(inicio))
				.filter(r -> fim == null || !r.getDataVencimento().isAfter(fim))
				.sorted(java.util.Comparator.comparing(r -> r.getDataVencimento()))
				.forEach(r -> {
					java.util.Map<String, String> row = new java.util.HashMap<>();
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
				.sorted(java.util.Comparator.comparing(d -> d.getDataVencimento()))
				.forEach(d -> {
					java.util.Map<String, String> row = new java.util.HashMap<>();
					row.put("data", d.getDataVencimento().format(dtf));
					row.put("descricao", d.getDescricao());
					row.put("tipo", "DEBITO");
					row.put("valor", df.format(d.getValor()));
					row.put("saldo", "");
					lancamentos.add(row);
				});

		// Ordena por data
		lancamentos.sort(java.util.Comparator.comparing(m -> m.get("data")));

		// Calcula saldo acumulado
		java.math.BigDecimal saldoAcum = java.math.BigDecimal.ZERO;
		for (java.util.Map<String, String> row : lancamentos) {
			java.math.BigDecimal valor = new java.math.BigDecimal(row.get("valor").replace(".", "").replace(",", "."));
			if ("CREDITO".equals(row.get("tipo"))) {
				saldoAcum = saldoAcum.add(valor);
			} else {
				saldoAcum = saldoAcum.subtract(valor);
			}
			row.put("saldo", df.format(saldoAcum));
		}

		String periodo = (inicio != null ? inicio.format(dtf) : "Início") + " a " + (fim != null ? fim.format(dtf) : "Hoje");
		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("lancamentos", lancamentos);
		data.put("saldoFinal", df.format(saldoAcum));
		data.put("periodo", periodo);
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("fluxo-caixa.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/balancete")
	public ResponseEntity<byte[]> balancete(
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fim) {

		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Agrupa despesas por plano de contas
		java.util.Map<String, java.math.BigDecimal[]> contaMap = new java.util.LinkedHashMap<>();

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
		List<java.util.Map<String, String>> contas = new java.util.ArrayList<>();

		for (var entry : contaMap.entrySet()) {
			java.util.Map<String, String> row = new java.util.HashMap<>();
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
		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("contas", contas);
		data.put("totalCredito", df.format(totalCredito));
		data.put("totalDebito", df.format(totalDebito));
		data.put("saldoGeral", df.format(totalCredito.subtract(totalDebito)));
		data.put("periodo", periodo);
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("balancete.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}

	@GetMapping("/dre")
	public ResponseEntity<byte[]> dre(
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fim) {

		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Agrupa receitas por plano de contas
		java.util.Map<String, java.math.BigDecimal> receitasMap = new java.util.LinkedHashMap<>();
		receitaService.findAll().forEach(r -> {
			String desc = r.getPlanoContas() != null ? r.getPlanoContas().getDescricao() : "Receitas Diversas";
			receitasMap.merge(desc, r.getValor(), java.math.BigDecimal::add);
		});

		// Agrupa despesas por plano de contas
		java.util.Map<String, java.math.BigDecimal> despesasMap = new java.util.LinkedHashMap<>();
		despesaService.findAll().forEach(d -> {
			String desc = d.getPlanoContas() != null ? d.getPlanoContas().getDescricao() : "Despesas Diversas";
			despesasMap.merge(desc, d.getValor(), java.math.BigDecimal::add);
		});

		java.math.BigDecimal totalReceitas = receitasMap.values().stream().reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
		java.math.BigDecimal totalDespesas = despesasMap.values().stream().reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

		List<java.util.Map<String, String>> receitas = receitasMap.entrySet().stream().map(e -> {
			java.util.Map<String, String> row = new java.util.HashMap<>();
			row.put("descricao", e.getKey());
			row.put("valor", df.format(e.getValue()));
			return row;
		}).collect(java.util.stream.Collectors.toList());

		List<java.util.Map<String, String>> despesas = despesasMap.entrySet().stream().map(e -> {
			java.util.Map<String, String> row = new java.util.HashMap<>();
			row.put("descricao", e.getKey());
			row.put("valor", df.format(e.getValue()));
			return row;
		}).collect(java.util.stream.Collectors.toList());

		String periodo = (inicio != null ? inicio.format(dtf) : "Início") + " a " + (fim != null ? fim.format(dtf) : "Hoje");
		java.util.Map<String, Object> data = new java.util.HashMap<>();
		data.put("receitas", receitas);
		data.put("despesas", despesas);
		data.put("totalReceitas", df.format(totalReceitas));
		data.put("totalDespesas", df.format(totalDespesas));
		data.put("resultado", df.format(totalReceitas.subtract(totalDespesas)));
		data.put("periodo", periodo);
		data.put("emissao", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

		byte[] pdf = freeMarkerReport.gerarPdf("dre.ftl", data);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(pdf);
	}
}
