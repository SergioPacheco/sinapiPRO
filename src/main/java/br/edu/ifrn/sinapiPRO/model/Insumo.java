package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "insumo")
public class Insumo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private InsumoID insumoID;           // {baseInsumo, codigo_insumo}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_base_preco", referencedColumnName = "codigo")
	private BasePreco basePreco;
	
	@Size(max = 400)
	@NotNull(message = "Descrição é obrigatória")
	private String descricao; 
	
	@NotNull(message = "Unidade é obrigatório")
	private String unidade; 
	
	private BigDecimal precoPadrao;
	
	@Enumerated(EnumType.STRING)
	private Especie especie;
		
		
	public InsumoID getInsumoId() {
		return insumoID;
	}

	public void setInsumoId(InsumoID insumoId) {
		this.insumoID = insumoId;
	}
	
	public Long getCodigoInsumo() { 
		return this.insumoID.getCodigoInsumo();
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
		return insumoID == null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((insumoID == null) ? 0 : insumoID.hashCode());
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
		if (insumoID == null) {
			if (other.insumoID != null)
				return false;
		} else if (!insumoID.equals(other.insumoID))
			return false;
		return true;
	}
	 
}
