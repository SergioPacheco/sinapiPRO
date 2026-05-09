package com.sinapipro.model;

import java.math.BigDecimal;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "orcamento_baseline_item")
public class OrcamentoBaselineItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;

	@ManyToOne(optional = false)
	@JoinColumn(name = "codigo_baseline")
	private OrcamentoBaseline baseline;

	@ManyToOne(optional = false)
	@JoinColumn(name = "codigo_item")
	private Item item;

	@Column(name = "valor_unitario")
	private BigDecimal valorUnitario;

	private BigDecimal quantidade;

	@Column(name = "valor_total")
	private BigDecimal valorTotal;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public OrcamentoBaseline getBaseline() {
		return baseline;
	}

	public void setBaseline(OrcamentoBaseline baseline) {
		this.baseline = baseline;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
}
