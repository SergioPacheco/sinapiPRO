package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Estado;

public class InsumoDTO {

	private Long codigo;
	private String sku;
	private String descricao;
	private BigDecimal preco;

	public InsumoDTO(Long codigo, String sku, String descricao,  BigDecimal preco) {
		this.codigo = codigo;
		this.sku = sku;
		this.descricao = descricao;
		this.preco = preco;
	}

	public Long getCodigo() {
		return codigo;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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
