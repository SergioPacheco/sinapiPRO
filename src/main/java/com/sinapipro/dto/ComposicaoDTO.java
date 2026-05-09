package com.sinapipro.dto;

import java.math.BigDecimal;

public class ComposicaoDTO {

	private Long codigo;
	private String codigoComposicao;
	private String descricao;
	private String unidade;
	private BigDecimal custoTotal;
	
	public ComposicaoDTO(Long codigo, String codigoComposicao, String descricao, String unidade, BigDecimal custoTotal) {
		this.codigo = codigo;
		this.codigoComposicao = codigoComposicao; 
		this.descricao = descricao;
		this.unidade = unidade;
		this.custoTotal = custoTotal;
	}

	public Long getCodigo() {
		return codigo;
	}

	public String getCodigoComposicao() {
		return codigoComposicao;
	}

	public void setCodigoComposicao(String codigoComposicao) {
		this.codigoComposicao = codigoComposicao;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
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

	public BigDecimal getCustoTotal() {
		return custoTotal;
	}

	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}
	
}

