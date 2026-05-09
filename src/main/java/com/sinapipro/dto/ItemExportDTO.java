package com.sinapipro.dto;

import java.math.BigDecimal;

import com.sinapipro.model.Item;

public class ItemExportDTO {

	private Long codigo;
	private String tipo;
	private String descricao;
	private String itemizacao;
	private String especie;
	private String unidade;
	private BigDecimal quantidade;
	private BigDecimal valorUnitario;
	private BigDecimal valorTotal;
	private BigDecimal valorMaoObra;
	private BigDecimal valorMaterial;
	private BigDecimal valorEquipamento;
	private String etapaNome;

	public ItemExportDTO(Item item) {
		this.codigo = item.getCodigo();
		this.tipo = item.getTipo() != null ? item.getTipo().name() : null;
		this.descricao = item.getDescricao();
		this.itemizacao = item.getItemizacao();
		this.especie = item.getEspecie() != null ? item.getEspecie().name() : null;
		this.unidade = item.getUnidade();
		this.quantidade = item.getQuantidade();
		this.valorUnitario = item.getValorUnitario();
		this.valorTotal = item.getValorTotal();
		this.valorMaoObra = item.getValorMaoObra();
		this.valorMaterial = item.getValorMaterial();
		this.valorEquipamento = item.getValorEquipamento();
		this.etapaNome = item.getEtapa() != null ? item.getEtapa().getNome() : null;
	}

	public Long getCodigo() {
		return codigo;
	}

	public String getTipo() {
		return tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getItemizacao() {
		return itemizacao;
	}

	public String getEspecie() {
		return especie;
	}

	public String getUnidade() {
		return unidade;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public BigDecimal getValorMaoObra() {
		return valorMaoObra;
	}

	public BigDecimal getValorMaterial() {
		return valorMaterial;
	}

	public BigDecimal getValorEquipamento() {
		return valorEquipamento;
	}

	public String getEtapaNome() {
		return etapaNome;
	}
}
