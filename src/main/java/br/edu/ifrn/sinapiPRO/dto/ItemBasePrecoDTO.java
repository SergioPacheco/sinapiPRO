package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class ItemBasePrecoDTO {
	
	private String anoMes; 
	private BigDecimal preco; 
	
	public ItemBasePrecoDTO() {}

	public ItemBasePrecoDTO(String anoMes, BigDecimal preco) {
		super();
		this.anoMes = anoMes;
		this.preco = preco;
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

}

 