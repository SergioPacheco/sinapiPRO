package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class InsumoDTO {

	private Long codigo; 
	private String codigoInsumo;
	private String descricao;
	private String unidade; 
	private BigDecimal precoPadrao;
	
	public InsumoDTO(	Long codigo,
					    String codigoInsumo, 
						String descricao,  
						String unidade,
						BigDecimal precoPadrao) {
		super();
		this.codigo = codigo;
		this.codigoInsumo = codigoInsumo;
		this.descricao = descricao;
		this.unidade = unidade;
		this.precoPadrao = precoPadrao;
	}
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getCodigoInsumo() {
		return codigoInsumo;
	}

	public void setCodigoInsumo(String codigo) {
		this.codigoInsumo = codigo;
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

	public BigDecimal getPrecoPadrao() {
		return precoPadrao;
	}

	public void setPrecoPadrao(BigDecimal precoPadrao) {
		this.precoPadrao = precoPadrao;
	}
	
}
