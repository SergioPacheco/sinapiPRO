package com.sinapipro.dto;

import java.math.BigDecimal;

public class ValorOrcamentos {

	private BigDecimal valor;
	private Long totalItens;
	
	public ValorOrcamentos() {
		
	}

	public ValorOrcamentos(BigDecimal valor, Long totalItens) {
		this.valor = valor;
		this.totalItens = totalItens;
	}

	public BigDecimal getValor() {
		return valor != null ? valor : BigDecimal.ZERO;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getTotalItens() {
		return totalItens != null ? totalItens : 0L;
	}

	public void setTotalItens(Long totalItens) {
		this.totalItens = totalItens;
	}
	
}
