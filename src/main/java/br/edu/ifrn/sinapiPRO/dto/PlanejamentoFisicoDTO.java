package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PlanejamentoFisicoDTO {

	private String itemizacao;
	private String descricao;
	private String etapa;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private int duracaoMeses;
	private BigDecimal valor;
	private BigDecimal percentualDoTotal;

	public String getItemizacao() { return itemizacao; }
	public void setItemizacao(String itemizacao) { this.itemizacao = itemizacao; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public String getEtapa() { return etapa; }
	public void setEtapa(String etapa) { this.etapa = etapa; }
	public LocalDate getDataInicio() { return dataInicio; }
	public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
	public LocalDate getDataFim() { return dataFim; }
	public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
	public int getDuracaoMeses() { return duracaoMeses; }
	public void setDuracaoMeses(int duracaoMeses) { this.duracaoMeses = duracaoMeses; }
	public BigDecimal getValor() { return valor; }
	public void setValor(BigDecimal valor) { this.valor = valor; }
	public BigDecimal getPercentualDoTotal() { return percentualDoTotal; }
	public void setPercentualDoTotal(BigDecimal percentualDoTotal) { this.percentualDoTotal = percentualDoTotal; }
}
