package com.sinapipro.dto;

import java.math.BigDecimal;

public class CronogramaMes {

	private int ano;
	private int mes;
	private String periodo;
	private BigDecimal valorPlanejado = BigDecimal.ZERO;
	private BigDecimal valorAcumulado = BigDecimal.ZERO;
	private BigDecimal percentual = BigDecimal.ZERO;

	public CronogramaMes(int ano, int mes) {
		this.ano = ano;
		this.mes = mes;
		this.periodo = String.format("%02d/%d", mes, ano);
	}

	public int getAno() {
		return ano;
	}

	public int getMes() {
		return mes;
	}

	public String getPeriodo() {
		return periodo;
	}

	public BigDecimal getValorPlanejado() {
		return valorPlanejado;
	}

	public void setValorPlanejado(BigDecimal valorPlanejado) {
		this.valorPlanejado = valorPlanejado;
	}

	public BigDecimal getValorAcumulado() {
		return valorAcumulado;
	}

	public void setValorAcumulado(BigDecimal valorAcumulado) {
		this.valorAcumulado = valorAcumulado;
	}

	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	public void adicionarValor(BigDecimal valor) {
		this.valorPlanejado = this.valorPlanejado.add(valor);
	}
}
