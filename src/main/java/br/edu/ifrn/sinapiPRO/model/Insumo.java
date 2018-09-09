package br.edu.ifrn.sinapiPRO.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "insumo")
public class Insumo {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	
	private String sku; 
	
	@Column(name = "ano_mes")
	@NotNull(message = "Valor é obrigatório")
	private String anoMes; 
	
	@NotNull(message = "Valor é obrigatório")
	@Enumerated(EnumType.STRING)
	private Base base; 
	
	@Size(max = 400)
	@NotNull(message = "Valor é obrigatório")
	private String descricao; 
	
	@NotNull(message = "Valor é obrigatório")
	private String unidade; 
	
	@NotNull(message = "Valor é obrigatório")
	private BigDecimal preco;
	
	@NotNull(message = "O estado é obrigatório")
	@ManyToOne
	@JoinColumn(name = "codigo_estado")
	private Estado estado;

	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getAnoMes() {
		return anoMes;
	}	
	public Base getBase() {
		return base;
	}
	public void setBase(Base base) {
		this.base = base;
	}
	public void setAnoMes(String anoMes) {
		this.anoMes = anoMes;
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
	public BigDecimal getPreco() {
		return preco;
	}
	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
	
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public boolean isNovo() {
		return codigo == null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Insumo other = (Insumo) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Insumo [codigo=" + codigo + ", descricao=" + descricao + ", unidade=" + unidade + ", preco=" + preco + "]";
	} 
}
