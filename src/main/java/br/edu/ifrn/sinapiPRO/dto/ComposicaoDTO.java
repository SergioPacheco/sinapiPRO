package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class ComposicaoDTO {

	private Long codigo;
	private String descricao;
	private String unidade;
	private BigDecimal custoTotal;
	
	public ComposicaoDTO(Long codigo, String descricao, String unidade, BigDecimal custoTotal) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.unidade = unidade;
		this.custoTotal = custoTotal;
	}

	public Long getCodigo() {
		return codigo;
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

	public void setCustoTotal(BigDecimal valor) {
		this.custoTotal = valor;
	}

	
}
