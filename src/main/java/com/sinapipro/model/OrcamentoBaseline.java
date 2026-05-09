package com.sinapipro.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "orcamento_baseline")
public class OrcamentoBaseline {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;

	@ManyToOne(optional = false)
	@JoinColumn(name = "codigo_orcamento")
	private Orcamento orcamento;

	private String descricao;

	@Column(name = "data_gravacao", nullable = false)
	private LocalDateTime dataGravacao;

	@Column(name = "valor_total")
	private BigDecimal valorTotal;

	@OneToMany(mappedBy = "baseline", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrcamentoBaselineItem> itens = new ArrayList<>();

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getDataGravacao() {
		return dataGravacao;
	}

	public void setDataGravacao(LocalDateTime dataGravacao) {
		this.dataGravacao = dataGravacao;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public List<OrcamentoBaselineItem> getItens() {
		return itens;
	}

	public void setItens(List<OrcamentoBaselineItem> itens) {
		this.itens = itens;
	}
}
