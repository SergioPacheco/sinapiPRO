package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "insumo")
public class Insumo implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	
	@NaturalId
	@NotNull(message = "Codigo Insumo é obrigatório")
	private Long codigoInsumo; 

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_base_preco")
	private BasePreco basePreco;
	
	@Size(max = 400)
	@NotNull(message = "Descrição é obrigatória")
	private String descricao; 
	
	@NotNull(message = "Unidade é obrigatório")
	private String unidade; 
	
	private BigDecimal precoPadrao;
	
	@Enumerated(EnumType.STRING)
	private Especie especie;
	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getCodigoInsumo() {
		return codigoInsumo;
	}

	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}

	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
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
		
	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
		this.especie = especie;
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
	 
}
