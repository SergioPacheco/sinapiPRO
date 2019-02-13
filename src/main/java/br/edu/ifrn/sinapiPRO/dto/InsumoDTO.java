package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class InsumoDTO {

	private Long codigoInsumo;
	private String nomeBaseInsumo; 
	private String descricao;
	private String unidade; 
	private BigDecimal precoPadrao;
	
	public InsumoDTO(	Long codigoInsumo, 
				        String nomeBaseInsumo,
						String descricao,  
						String unidade,
						BigDecimal precoPadrao) {
		super();
		this.codigoInsumo = codigoInsumo;
		this.nomeBaseInsumo = nomeBaseInsumo; 
		this.descricao = descricao;
		this.unidade = unidade;
		this.precoPadrao = precoPadrao;
	}

	public Long getCodigoInsumo() {
		return codigoInsumo;
	}

	public void setCodigoInsumo(Long codigo) {
		this.codigoInsumo = codigo;
	}
	
	public String getNomeBaseInsumo() {
		return nomeBaseInsumo;
	}

	public void setNomeBaseInsumo(String nomeBaseInsumo) {
		this.nomeBaseInsumo = nomeBaseInsumo;
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
