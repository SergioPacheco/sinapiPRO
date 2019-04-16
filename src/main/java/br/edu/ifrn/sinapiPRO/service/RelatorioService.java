/**
 * INS00100 - Lista de Insumos por Base de Insumo 
 * 
 * COM00100 - Lista de Composições por Base de Insumo 
 * COM00201 - Lista as Composições do Orçamento
 * COM00400 -  
 *
 *  
 * COM00400
 * COM00500
 * 
 * ORCA00100 - Relatório do Orçamento 
 */

package br.edu.ifrn.sinapiPRO.service;

import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.dto.ListaComposicoes;
import br.edu.ifrn.sinapiPRO.dto.ListaInsumos;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class RelatorioService {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private OrcamentosRepository orcamentosRepository;
	
	/*  
	 *  INS00100_JAVA.jasper -  Lista de Insumos por Base de Insumo
	 */
	public byte[] gerarRelatorioListaInsumos(ListaInsumos opcao) throws Exception {
		
		StringBuilder cBase = new StringBuilder("");
		if (opcao.getBaseInsumo() == null) {
			cBase.append("1");  
		} else {
			cBase.append(opcao.getBaseInsumo().getCodigo() );
		}
		 
		StringBuilder cEspecie = new StringBuilder("");
		if (opcao.getEspecie()!= null) {
			cEspecie.append("especie ='");
			cEspecie.append(opcao.getEspecie());
			cEspecie.append("' AND");
		}
		StringBuilder cOrdem = new StringBuilder("");
		if (opcao.getOrdem() == null) {
			cOrdem.append("insumo.descricao");
			if (opcao.getTipoOrdem() == null) {
				cOrdem.append(" ASC");
			} else {
				cOrdem.append(opcao.getTipoOrdem());
			}
		}
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("format", "pdf");
		
		parametros.put("_Parameter10", cBase );
		parametros.put("_Parameter2",  cOrdem);
		parametros.put("_Parameter4", "INSTITUTO FEDERAL DE EDUCAÇÃO, CIÊNCIA E TECNOLOGIA");    // Nome do Cliente
		parametros.put("_Parameter3", "RIO GRANDE DO NORTE - PARNAMIRIM");  // Dados do filtro
		parametros.put("_Parameter9", cEspecie);
		parametros.put("_Parameter7", "/home/sergio/Documents/sinapiPRO/src/main/resources/relatorios/ifrn.png");
		
		InputStream inputStream = this.getClass()
									  .getResourceAsStream("/relatorios/INS00100_JAVA.jasper");
		
		Connection connection = this.dataSource.getConnection();
		
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, connection);
		    return JasperExportManager.exportReportToPdf(jasperPrint);
			
		} finally {
			connection.close();
		}
	}
	
	/*  
	 *  COM00100_JAVA.jasper -  Lista de Composicoes por Base de Insumo
	 */
	public byte[] gerarRelatorioListaComposicoes(ListaComposicoes opcao) throws Exception {
		
		String cBase = "1";
		if (opcao.getBasePreco() != null) {
			cBase = opcao.getBasePreco().getCodigo().toString();
		}
		 
		StringBuilder cOrdem = new StringBuilder("");
		if (opcao.getOrdem() == null) {
			cOrdem.append("composicao.descricao");
			if (opcao.getTipoOrdem() == null) {
				cOrdem.append(" ASC");
			} else {
				cOrdem.append(opcao.getTipoOrdem());
			}
		}
		
		StringBuilder cRelatorio = new StringBuilder("");
		StringBuilder cTitulo = new StringBuilder("");
		cTitulo.append("RELATÓRIO DE COMPOSIÇÕES [");
		cTitulo.append(opcao.getNomeUsuario());
		cTitulo.append("] Hora Emissão: ");
		cTitulo.append(new SimpleDateFormat("HH:mm:ss").format(new Date() ));
		cTitulo.append(" - "); 
		
		if (opcao.getRelatorio() == null) {
			cRelatorio.append("0");  
			cTitulo.append("Sintético");
		} else {
			cRelatorio.append(opcao.getRelatorio()); 
			if (opcao.getRelatorio().equals("0")) { 
				cTitulo.append("Sintético");
			} else {
				cTitulo.append("Analítico");
			}
		}
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("format", "pdf");
		parametros.put("_Parameter1", cRelatorio.toString());
		parametros.put("_Parameter3", "INSTITUTO FEDERAL DE EDUCAÇÃO, CIÊNCIA E TECNOLOGIA");    // Nome do Cliente
		parametros.put("_Parameter4", "RIO GRANDE DO NORTE - PARNAMIRIM");  // Dados do filtro
		parametros.put("_Parameter5", cTitulo.toString());
		parametros.put("_Parameter7", "/home/sergio/Documents/sinapiPRO/src/main/resources/relatorios/ifrn.png");
		parametros.put("_Parameter9", cBase.toString());
		parametros.put("_Parameter10", cOrdem.toString());
		
		InputStream inputStream = this.getClass()
									  .getResourceAsStream("/relatorios/COM00100_JAVA.jasper");
		
		Connection connection = this.dataSource.getConnection();
		
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, connection);
		    return JasperExportManager.exportReportToPdf(jasperPrint);
			
		} finally {
			connection.close();
		}
	}
	/*  
	 *  ORC00100_JAVA.jasper -  Imprimir o relatório do orçamento selecionado
	 */
	public byte[] gerarRelatorioImprimirOrcamento(Long codigo, String nomeUsuario) throws Exception {
		 
		Optional<Orcamento> orcamentoExiste = orcamentosRepository.findById(codigo); 
		Orcamento orcamento = orcamentoExiste.get();
		
		orcamento.setTotalBDI(orcamento.calculaValorBDI());   
		orcamento.setTotaLeisSociais(orcamento.calculaValorLeisSociais());
		orcamento.setTotalTaxaAdm(orcamento.calculaValorTaxaAdm());
		orcamento.setSubTotal(orcamento.calculaValorSubTotal());
		orcamento.setTotalTaxas(orcamento.calculaValorTaxas());
		orcamento.setValorTotal(orcamento.calculaValorTotalComTaxas());

		orcamentosRepository.saveAndFlush(orcamento);
		
		StringBuilder cRelatorio = new StringBuilder("");
		StringBuilder cTitulo = new StringBuilder("");
		cTitulo.append("[");
		cTitulo.append(nomeUsuario);
		cTitulo.append("] Hora Emissão: ");
		cTitulo.append(new SimpleDateFormat("HH:mm:ss").format(new Date() ));
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("format", "pdf");
		parametros.put("_Parameter1", cRelatorio.toString());
		parametros.put("_Parameter4", "INSTITUTO FEDERAL DE EDUCAÇÃO, CIÊNCIA E TECNOLOGIA");    // Nome do Cliente
		parametros.put("_Parameter3", "RIO GRANDE DO NORTE - PARNAMIRIM");  // Dados do filtro
		parametros.put("_Parameter5", cTitulo.toString());
	//	parametros.put("_Parameter7", "/home/sergio/Documents/sinapiPRO/src/main/resources/static/images/logo-black.png");
		parametros.put("_Parameter7", "/home/sergio/Documents/sinapiPRO/src/main/resources/relatorios/ifrn.png");
		parametros.put("_Parameter10", codigo.toString());
		
		InputStream inputStream = this.getClass()
									  .getResourceAsStream("/relatorios/ORC00100_JAVA.jasper");
		Connection connection = this.dataSource.getConnection();
		
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, connection);
		    return JasperExportManager.exportReportToPdf(jasperPrint);
			
		} finally {
			connection.close();
		}
	}
	
	/*  
	 *  COM00201_JAVA.jasper -  Imprimir as composições do orçamento
	 */
	public byte[] gerarRelatorioImprimirComposicoesOrcamento(Long codigo, String nomeUsuario) throws Exception {
		 
		Optional<Orcamento> orcamentoExiste = orcamentosRepository.findById(codigo); 
		Orcamento orcamento = orcamentoExiste.get();
		
		orcamento.setTotalBDI(orcamento.calculaValorBDI());   
		orcamento.setTotaLeisSociais(orcamento.calculaValorLeisSociais());
		orcamento.setTotalTaxaAdm(orcamento.calculaValorTaxaAdm());
		orcamento.setSubTotal(orcamento.calculaValorSubTotal());
		orcamento.setTotalTaxas(orcamento.calculaValorTaxas());
		orcamento.setValorTotal(orcamento.calculaValorTotalComTaxas());

		orcamentosRepository.saveAndFlush(orcamento);
		
		StringBuilder cRelatorio = new StringBuilder("");
		StringBuilder cTitulo = new StringBuilder("");
		cTitulo.append("[");
		cTitulo.append(nomeUsuario);
		cTitulo.append("] Hora Emissão: ");
		cTitulo.append(new SimpleDateFormat("HH:mm:ss").format(new Date() ));
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("format", "pdf");
		parametros.put("_Parameter1", cRelatorio.toString());
		parametros.put("_Parameter4", "INSTITUTO FEDERAL DE EDUCAÇÃO, CIÊNCIA E TECNOLOGIA");    // Nome do Cliente
		parametros.put("_Parameter3", "RIO GRANDE DO NORTE - PARNAMIRIM");  // Dados do filtro
		parametros.put("_Parameter5", cTitulo.toString());
		parametros.put("_Parameter7", "/home/sergio/Documents/sinapiPRO/src/main/resources/relatorios/ifrn.png");
		parametros.put("_Parameter10", codigo.toString().trim());
		
		InputStream inputStream = this.getClass()
									  .getResourceAsStream("/relatorios/COM00201_JAVA.jasper");
		Connection connection = this.dataSource.getConnection();
		
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, connection);
		    return JasperExportManager.exportReportToPdf(jasperPrint);
			
		} finally {
			connection.close();
		}
	}
 
	/*  
	 *  COM00400_JAVA.jasper -  Lista de Composicoes por Base de Insumo
	 */
	public byte[] gerarRelatorioImprimirComposicao(Long codigo, String nomeUsuario) throws Exception {
		 
		
		StringBuilder cRelatorio = new StringBuilder("");
		StringBuilder cTitulo = new StringBuilder("");
		cTitulo.append("RELATÓRIO COMPOSIÇÃO [");
		cTitulo.append(nomeUsuario);
		cTitulo.append("] Hora Emissão: ");
		cTitulo.append(new SimpleDateFormat("HH:mm:ss").format(new Date() ));
		cTitulo.append(" - "); 
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("format", "pdf");
		parametros.put("_Parameter1", cRelatorio.toString());
		parametros.put("_Parameter4", "INSTITUTO FEDERAL DE EDUCAÇÃO, CIÊNCIA E TECNOLOGIA");    // Nome do Cliente
		parametros.put("_Parameter3", "RIO GRANDE DO NORTE - PARNAMIRIM");  // Dados do filtro
		parametros.put("_Parameter5", cTitulo.toString());
		parametros.put("_Parameter7", "/home/sergio/Documents/sinapiPRO/src/main/resources/relatorios/ifrn.png");
		parametros.put("_Parameter8", "/home/sergio/Documents/sinapiPRO/src/main/resources/relatorios/composicaoOrcamento.png");
		parametros.put("_Parameter9", codigo);
		
		
		InputStream inputStream = this.getClass()
									  .getResourceAsStream("/relatorios/COM00400_JAVA.jasper");
		
		Connection connection = this.dataSource.getConnection();
		
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, connection);
		    return JasperExportManager.exportReportToPdf(jasperPrint);
			
		} finally {
			connection.close();
		}
	}
}


/*
paramaters.put("fromDate", fromDate);
paramaters.put("toDate", toDate);
if (!output.equals("pdf"))
{
    paramaters.put("IS_IGNORE_PAGINATION", true);
}
else
    paramaters.put("IS_IGNORE_PAGINATION", false);

JasperPrint jasperPrint = null;
jasperPrint = JasperFillManager.fillReport(CompiledReport,paramaters, connection);

if (output.equals("html")) {
    generateHtmlResponse(response, jasperPrint);
} else if (output.equals("pdf")) {
    generatePdfResponse(response, jasperPrint);
} else if(output.equals("excel")) {
    generateXLResponse(response, jasperPrint);
}


Map<String, Object> parametro = new HashMap<String, Object>();
    parametro.put("USUARIO", UConstante.NAME_MINISTERIO_USER);
    parametro.put("RUTA_LOGO", PuenteFile.getRutaFiles(FacesContext.getCurrentInstance(), PuenteFile.RUTA_IMG_LOGO));
    parametro.put("PATH_SYSTEM", rutaFileSystemHD);
    parametro.put("WHERE_DATA", WHERE_REGISTRO);
    parametro.put("WHERE_PROYECTO_USUARIO", WHERE_PROYECTO_USUARIO);
    parametro.put("WHERE_ZONA", WHERE_ZONA);
    parametro.put("NAME_APP", RutaFile.NAME_APP);
    parametro.put("ID_USUARIO", getUsuario().getId());
    parametro.put("ID_PROYECTO", beanProyecto.getId());
    parametro.put("SUBREPORT_DIR", SUBREPORT_DIR);

    System.out.println(">>>>>> PARAMETROS :" + parametro.toString());

	try {
		JasperPrint jasperPrint = JasperFillManager.fillReport(path, parametro, PgConnector.getConexion());
	    JRXlsExporter xlsExporter = new JRXlsExporter();
	    xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	    xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(PATH_REPORT_FILE + nameExcel);
	    SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
	    SimpleXlsExporterConfiguration xlsExporterConfiguration = new SimpleXlsExporterConfiguration();
	    xlsReportConfiguration.setOnePagePerSheet(true);
	    xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(false);
	    xlsReportConfiguration.setDetectCellType(true);
	    xlsReportConfiguration.setWhitePageBackground(false);
	    xlsExporter.setConfiguration(xlsReportConfiguration);
	    xlsExporter.exportReport();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}


 */









