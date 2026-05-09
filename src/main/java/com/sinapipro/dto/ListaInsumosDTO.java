package com.sinapipro.dto;

import java.math.BigDecimal;

import com.sinapipro.model.Especie;

public class ListaInsumosDTO {

	private String codigo;
	private String descricao; 
	private Especie especie; 
	private String unidade; 
	private BigDecimal quantidade; 
	private BigDecimal preco; 
	private BigDecimal total;
	
	public ListaInsumosDTO(String codigo, String descricao, Especie especie, String unidade, BigDecimal quantidade,
			BigDecimal preco, BigDecimal total) {
		 
		this.codigo = codigo;
		this.descricao = descricao;
		this.especie = especie;
		this.unidade = unidade;
		this.quantidade = quantidade;
		this.preco = preco;
		this.total = total;
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Especie getEspecie() {
		return especie;
	}
	public void setEspecie(Especie especie) {
		this.especie = especie;
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
	public BigDecimal getPreco() {
		return preco;
	}
	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	} 
	 
	
}
