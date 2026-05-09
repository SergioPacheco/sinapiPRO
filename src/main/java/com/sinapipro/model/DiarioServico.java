package com.sinapipro.model;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "diario_servico")
public class DiarioServico implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_diario") private DiarioObra diario;
	private String descricao;
	private BigDecimal quantidade = BigDecimal.ZERO;
	private String unidade;
	@Column(name = "percentual_executado") private BigDecimal percentualExecutado = BigDecimal.ZERO;
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public DiarioObra getDiario() {
		return diario;
	}

	public void setDiario(DiarioObra diario) {
		this.diario = diario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getPercentualExecutado() {
		return percentualExecutado;
	}

	public void setPercentualExecutado(BigDecimal percentualExecutado) {
		this.percentualExecutado = percentualExecutado;
	}
}
