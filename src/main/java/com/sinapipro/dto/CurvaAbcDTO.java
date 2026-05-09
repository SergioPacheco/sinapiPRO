package com.sinapipro.dto;

import java.math.BigDecimal;

public class CurvaAbcDTO {

	private String itemizacao;
	private String descricao;
	private String unidade;
	private BigDecimal quantidade;
	private BigDecimal valorUnitario;
	private BigDecimal valorTotal;
	private BigDecimal percentual;
	private BigDecimal percentualAcumulado;
	private String classificacao;

	public String getItemizacao() {
		return itemizacao;
	}

	public void setItemizacao(String itemizacao) {
		this.itemizacao = itemizacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	public BigDecimal getPercentualAcumulado() {
		return percentualAcumulado;
	}

	public void setPercentualAcumulado(BigDecimal percentualAcumulado) {
		this.percentualAcumulado = percentualAcumulado;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}
}
