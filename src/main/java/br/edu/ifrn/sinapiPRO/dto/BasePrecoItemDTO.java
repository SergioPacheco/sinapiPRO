package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class BasePrecoItemDTO {
	
	private String nomeBase; 
	private String anoMes; 
	private BigDecimal preco; 
	private BigDecimal precoOnerado; 
	
	public BasePrecoItemDTO() {
	}

	public BasePrecoItemDTO(String nomeBase, String anoMes, BigDecimal preco, BigDecimal precoOnerado) {
		super();
		this.nomeBase = nomeBase; 
		this.anoMes = anoMes;
		this.preco = preco;
		this.precoOnerado = precoOnerado;
	}

	public String getNomeBase() {
		return nomeBase;
	}

	public void setNomeBase(String nomeBase) {
		this.nomeBase = nomeBase;
	}

	public String getAnoMes() {
		return anoMes;
	}

	public void setAnoMes(String anoMes) {
		this.anoMes = anoMes;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public BigDecimal getPrecoOnerado() {
		return precoOnerado;
	}

	public void setPrecoOnerado(BigDecimal precoOnerado) {
		this.precoOnerado = precoOnerado;
	}
}

 