package br.edu.ifrn.sinapiPRO.utils.report;

import java.util.Map;

public class ImprimirReport {

	private int tipoRelatorio;
	private String name;
	private Map<String, Object> params;
	private String nomeArquivo;
	private String cRetEscolhido;
	private String cBase;
	private Long numero;
	private boolean gerado = false;
	private boolean existe = false;
	private Object bean;
	private ReportParam paramsReport;

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getCRetEscolhido() {
		return cRetEscolhido;
	}

	public void setCRetEscolhido(String retEscolhido) {
		cRetEscolhido = retEscolhido;
	}

	public String getCBase() {
		return cBase;
	}

	public void setCBase(String base) {
		cBase = base;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public boolean isGerado() {
		return gerado;
	}

	public void setGerado(boolean gerado) {
		this.gerado = gerado;
	}

	public boolean isExiste() {
		return existe;
	}

	public void setExiste(boolean existe) {
		this.existe = existe;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public ReportParam getParamsReport() {
		return paramsReport;
	}

	public void setParamsReport(ReportParam paramsReport) {
		this.paramsReport = paramsReport;
	}

}
