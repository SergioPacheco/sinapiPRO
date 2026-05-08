package br.edu.ifrn.sinapiPRO.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.dto.ListaComposicoes;
import br.edu.ifrn.sinapiPRO.dto.ListaInsumos;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.ComposicaoItem;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;

@Service
public class RelatorioService {

	private final FreeMarkerReportService freeMarkerReport;
	private final OrcamentosRepository orcamentosRepository;
	private final InsumosRepository insumosRepository;
	private final ComposicaoRepository composicaoRepository;

	public RelatorioService(
			FreeMarkerReportService freeMarkerReport,
			OrcamentosRepository orcamentosRepository,
			InsumosRepository insumosRepository,
			ComposicaoRepository composicaoRepository) {
		this.freeMarkerReport = freeMarkerReport;
		this.orcamentosRepository = orcamentosRepository;
		this.insumosRepository = insumosRepository;
		this.composicaoRepository = composicaoRepository;
	}

	private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
	private static final DecimalFormat DF4 = new DecimalFormat("#,##0.0000");

	private String emissao() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
	}

	private String fmt(BigDecimal v) {
		return v != null ? DF.format(v) : "0,00";
	}

	private String fmt4(BigDecimal v) {
		return v != null ? DF4.format(v) : "0,0000";
	}

	// ========== LISTA DE INSUMOS (substitui INS00100_JAVA.jasper) ==========

	public byte[] gerarRelatorioListaInsumos(ListaInsumos opcao) {
		List<Insumo> insumos = insumosRepository.findAll();

		if (opcao.getEspecie() != null) {
			insumos = insumos.stream()
					.filter(i -> opcao.getEspecie().equals(i.getEspecie()))
					.collect(Collectors.toList());
		}

		List<Map<String, String>> rows = insumos.stream().map(i -> {
			Map<String, String> m = new HashMap<>();
			m.put("codigo", String.valueOf(i.getCodigo()));
			m.put("descricao", i.getDescricao() != null ? i.getDescricao() : "");
			m.put("unidade", i.getUnidade() != null ? i.getUnidade() : "");
			m.put("especie", i.getEspecie() != null ? i.getEspecie().getDescricao() : "");
			m.put("preco", fmt(i.getPrecoPadrao()));
			return m;
		}).collect(Collectors.toList());

		Map<String, Object> data = new HashMap<>();
		data.put("insumos", rows);
		data.put("baseInsumo", opcao.getBaseInsumo() != null ? opcao.getBaseInsumo().getNome() : "Todas");
		data.put("filtroEspecie", opcao.getEspecie() != null ? opcao.getEspecie().getDescricao() : "Todas");
		data.put("emissao", emissao());

		return freeMarkerReport.gerarPdf("lista-insumos.ftl", data);
	}

	// ========== LISTA DE COMPOSIÇÕES (substitui COM00100_JAVA.jasper) ==========

	public byte[] gerarRelatorioListaComposicoes(ListaComposicoes opcao) {
		List<Composicao> composicoes = composicaoRepository.findAll();

		String tipoRelatorio = "0".equals(opcao.getRelatorio()) || opcao.getRelatorio() == null ? "Sintético" : "Analítico";

		List<Map<String, Object>> rows = composicoes.stream().map(c -> {
			Map<String, Object> m = new HashMap<>();
			m.put("codigo", String.valueOf(c.getCodigo()));
			m.put("descricao", c.getDescricao() != null ? c.getDescricao() : "");
			m.put("unidade", c.getUnidade() != null ? c.getUnidade() : "");
			m.put("classe", c.getComposicaoClasse() != null ? c.getComposicaoClasse().getNome() : "");
			m.put("custoUnitario", fmt(c.getCustoTotal()));
			if ("Analítico".equals(tipoRelatorio) && c.getItens() != null) {
				List<Map<String, String>> itens = c.getItens().stream().map(ci -> {
					Map<String, String> im = new HashMap<>();
					im.put("descricao", ci.getInsumo() != null ? ci.getInsumo().getDescricao() : "");
					im.put("unidade", ci.getInsumo() != null && ci.getInsumo().getUnidade() != null ? ci.getInsumo().getUnidade() : "");
					im.put("coeficiente", fmt4(ci.getCoeficiente()));
					im.put("custoUnitario", fmt(ci.getInsumo() != null ? ci.getInsumo().getPrecoPadrao() : null));
					return im;
				}).collect(Collectors.toList());
				m.put("itens", itens);
			}
			return m;
		}).collect(Collectors.toList());

		Map<String, Object> data = new HashMap<>();
		data.put("composicoes", rows);
		data.put("basePreco", opcao.getBasePreco() != null ? opcao.getBasePreco().getNome() : "Todas");
		data.put("tipoRelatorio", tipoRelatorio);
		data.put("emissao", emissao());

		return freeMarkerReport.gerarPdf("lista-composicoes.ftl", data);
	}

	// ========== IMPRIMIR ORÇAMENTO (substitui ORC00100_JAVA.jasper) ==========

	public byte[] gerarRelatorioImprimirOrcamento(Long codigo, String nomeUsuario) {
		Orcamento orc = orcamentosRepository.buscarComItens(codigo);

		List<Map<String, String>> rows = orc.getItens().stream().map(item -> {
			Map<String, String> m = new HashMap<>();
			m.put("itemizacao", item.getItemizacao() != null ? item.getItemizacao() : "");
			m.put("descricao", item.getDescricao() != null ? item.getDescricao() : "");
			m.put("unidade", item.getUnidade() != null ? item.getUnidade() : "");
			m.put("quantidade", fmt4(item.getQuantidade()));
			m.put("valorUnitario", fmt(item.getValorUnitario()));
			m.put("valorMaoObra", fmt(item.getValorMaoObra()));
			m.put("valorMaterial", fmt(item.getValorMaterial()));
			m.put("valorEquipamento", fmt(item.getValorEquipamento()));
			m.put("valorTotal", fmt(item.getValorTotal()));
			return m;
		}).collect(Collectors.toList());

		Map<String, Object> data = new HashMap<>();
		Map<String, String> orcMap = new HashMap<>();
		orcMap.put("nome", orc.getNome() != null ? orc.getNome() : "");
		orcMap.put("tipoOrcamento", orc.getTipoOrcamento() != null ? orc.getTipoOrcamento().getDescricao() : "");
		orcMap.put("obra", orc.getObra() != null ? orc.getObra().getNome() : "");
		orcMap.put("cliente", orc.getCliente() != null ? orc.getCliente().getNome() : "");
		orcMap.put("estado", orc.getEstado() != null ? orc.getEstado().getNome() : "");
		orcMap.put("situacao", orc.getSituacao() != null ? orc.getSituacao().getDescricao() : "");
		data.put("orcamento", orcMap);
		data.put("itens", rows);
		data.put("totalMaoObra", fmt(orc.calculaValorMaoObra()));
		data.put("totalMaterial", fmt(orc.calculaValorMaterial()));
		data.put("totalEquipamento", fmt(orc.calculaValorEquipamento()));
		data.put("subTotal", fmt(orc.calculaValorSubTotal()));
		data.put("percLeisSociais", fmt(orc.getPercentualLeisSociais()));
		data.put("valorLeisSociais", fmt(orc.calculaValorLeisSociais()));
		data.put("percBdi", fmt(orc.getPercentualBdi()));
		data.put("valorBdi", fmt(orc.calculaValorBDI()));
		data.put("percTaxaAdm", fmt(orc.getPercentualTaxaAdm()));
		data.put("valorTaxaAdm", fmt(orc.calculaValorTaxaAdm()));
		data.put("totalGeral", fmt(orc.calculaValorTotalComTaxas()));
		data.put("emissao", emissao());

		return freeMarkerReport.gerarPdf("orcamento.ftl", data);
	}

	// ========== IMPRIMIR COMPOSIÇÃO DETALHE (substitui COM00400_JAVA.jasper) ==========

	public byte[] gerarRelatorioImprimirComposicao(Long codigo, String nomeUsuario) {
		Optional<Composicao> opt = composicaoRepository.findById(codigo);
		Composicao comp = opt.orElseThrow(() -> new RuntimeException("Composição não encontrada"));

		Map<String, String> compMap = new HashMap<>();
		compMap.put("codigo", String.valueOf(comp.getCodigo()));
		compMap.put("descricao", comp.getDescricao() != null ? comp.getDescricao() : "");
		compMap.put("unidade", comp.getUnidade() != null ? comp.getUnidade() : "");
		compMap.put("classe", comp.getComposicaoClasse() != null ? comp.getComposicaoClasse().getNome() : "");

		BigDecimal custoTotal = BigDecimal.ZERO;
		List<Map<String, String>> rows = new ArrayList<>();
		if (comp.getItens() != null) {
			for (ComposicaoItem ci : comp.getItens()) {
				Map<String, String> m = new HashMap<>();
				Insumo ins = ci.getInsumo();
				m.put("codigo", ins != null ? String.valueOf(ins.getCodigo()) : "");
				m.put("descricao", ins != null && ins.getDescricao() != null ? ins.getDescricao() : "");
				m.put("unidade", ins != null && ins.getUnidade() != null ? ins.getUnidade() : "");
				m.put("coeficiente", fmt4(ci.getCoeficiente()));
				BigDecimal preco = ins != null ? ins.getPrecoPadrao() : BigDecimal.ZERO;
				m.put("precoUnitario", fmt(preco));
				BigDecimal custo = ci.getCoeficiente() != null && preco != null
						? ci.getCoeficiente().multiply(preco) : BigDecimal.ZERO;
				m.put("custo", fmt(custo));
				custoTotal = custoTotal.add(custo);
				rows.add(m);
			}
		}

		Map<String, Object> data = new HashMap<>();
		data.put("composicao", compMap);
		data.put("itens", rows);
		data.put("custoTotal", fmt(custoTotal));
		data.put("emissao", emissao());

		return freeMarkerReport.gerarPdf("composicao-detalhe.ftl", data);
	}

	// ========== IMPRIMIR COMPOSIÇÕES DO ORÇAMENTO (substitui COM00201_JAVA.jasper) ==========

	public byte[] gerarRelatorioImprimirComposicoesOrcamento(Long codigo, String nomeUsuario) {
		return gerarRelatorioImprimirOrcamento(codigo, nomeUsuario);
	}

	// ========== EXPORTAR XLS (mantido — Apache POI) ==========

	public byte[] exportarOrcamentoXls(Orcamento orcamento) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Orçamento");

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		String[] colunas = {"Item", "Tipo", "Descrição", "Unidade", "Quantidade",
				"Vl. Unitário", "Mão de Obra", "Material", "Equipamento", "Total"};
		XSSFRow header = sheet.createRow(0);
		for (int i = 0; i < colunas.length; i++) {
			header.createCell(i).setCellValue(colunas[i]);
			header.getCell(i).setCellStyle(headerStyle);
		}

		int rowNum = 1;
		for (Item item : orcamento.getItens()) {
			XSSFRow row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(item.getItemizacao() != null ? item.getItemizacao() : "");
			row.createCell(1).setCellValue(item.getTipo() != null ? item.getTipo().name() : "");
			row.createCell(2).setCellValue(item.getDescricao() != null ? item.getDescricao() : "");
			row.createCell(3).setCellValue(item.getUnidade() != null ? item.getUnidade() : "");
			row.createCell(4).setCellValue(item.getQuantidade() != null ? item.getQuantidade().doubleValue() : 0);
			row.createCell(5).setCellValue(item.getValorUnitario() != null ? item.getValorUnitario().doubleValue() : 0);
			row.createCell(6).setCellValue(item.getValorMaoObra() != null ? item.getValorMaoObra().doubleValue() : 0);
			row.createCell(7).setCellValue(item.getValorMaterial() != null ? item.getValorMaterial().doubleValue() : 0);
			row.createCell(8).setCellValue(item.getValorEquipamento() != null ? item.getValorEquipamento().doubleValue() : 0);
			row.createCell(9).setCellValue(item.getValorTotal() != null ? item.getValorTotal().doubleValue() : 0);
		}

		for (int i = 0; i < colunas.length; i++) {
			sheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();
		return out.toByteArray();
	}
}
