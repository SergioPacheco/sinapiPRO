package com.sinapipro.dto;

import java.math.BigDecimal;

public class BaselineComparativoDTO {

	private String descricao;
	private BigDecimal valorBaseline;
	private BigDecimal valorAtual;
	private BigDecimal diferenca;
	private BigDecimal percentualVariacao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValorBaseline() {
		return valorBaseline;
	}

	public void setValorBaseline(BigDecimal valorBaseline) {
		this.valorBaseline = valorBaseline;
	}

	public BigDecimal getValorAtual() {
		return valorAtual;
	}

	public void setValorAtual(BigDecimal valorAtual) {
		this.valorAtual = valorAtual;
	}

	public BigDecimal getDiferenca() {
		return diferenca;
	}

	public void setDiferenca(BigDecimal diferenca) {
		this.diferenca = diferenca;
	}

	public BigDecimal getPercentualVariacao() {
		return percentualVariacao;
	}

	public void setPercentualVariacao(BigDecimal percentualVariacao) {
		this.percentualVariacao = percentualVariacao;
	}
}
