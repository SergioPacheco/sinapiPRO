package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tributo")
public class Tributo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;

	@NotBlank(message = "Descrição é obrigatória")
	private String descricao;

	@Column(precision = 10, scale = 4)
	private BigDecimal percentual;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_estado")
	private Estado estado;

	@ManyToMany(mappedBy = "tributos")
	private java.util.List<Insumo> insumos = new java.util.ArrayList<>();

	@ManyToMany(mappedBy = "tributos")
	private java.util.List<Composicao> composicoes = new java.util.ArrayList<>();

	public Long getCodigo() { return codigo; }
	public void setCodigo(Long codigo) { this.codigo = codigo; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public BigDecimal getPercentual() { return percentual; }
	public void setPercentual(BigDecimal percentual) { this.percentual = percentual; }
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }
	public java.util.List<Insumo> getInsumos() { return insumos; }
	public void setInsumos(java.util.List<Insumo> insumos) { this.insumos = insumos; }
	public java.util.List<Composicao> getComposicoes() { return composicoes; }
	public void setComposicoes(java.util.List<Composicao> composicoes) { this.composicoes = composicoes; }
	public boolean isNovo() { return codigo == null; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Tributo other = (Tributo) obj;
		if (codigo == null) return other.codigo == null;
		return codigo.equals(other.codigo);
	}
}
