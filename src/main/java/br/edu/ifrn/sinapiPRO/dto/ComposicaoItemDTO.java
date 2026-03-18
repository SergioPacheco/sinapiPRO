package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class ComposicaoItemDTO {

	private Long codigo;
	private Long codigoInsumo;
	private String nome;
	private String base;
	private BigDecimal valor;
	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public Long getCodigoInsumo() {
		return codigoInsumo;
	}
	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	
	
}