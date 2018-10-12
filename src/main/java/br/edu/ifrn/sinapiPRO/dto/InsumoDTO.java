package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class InsumoDTO {

	private Long codigo;
	private Long codigoInsumo;
	private String descricao;
	private BigDecimal preco;

	public InsumoDTO(Long codigo, Long codigoInsumo, String descricao,  BigDecimal preco) {
		this.codigo = codigo;
		this.codigoInsumo = codigoInsumo;
		this.descricao = descricao;
		this.preco = preco;
	}

	public Long getCodigo() {
		return codigo;
	}
	
	public Long getCodigoInsumo() {
		return codigoInsumo;
	}

	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
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

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
	
}
