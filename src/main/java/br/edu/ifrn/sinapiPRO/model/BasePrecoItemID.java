package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BasePrecoItemID implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	@Column(name = "codigo_base_preco_PK")
	private Long codigoBasePreco;
	
	@Column(name = "codigo_base_insumo_PK")
	private Long codigoInsumo; 
	
	public BasePrecoItemID() { 
	}
	
	public BasePrecoItemID(Long codigoBasePreco, Long codigoInsumo) { 
		this.codigoBasePreco = codigoBasePreco;
		this.codigoInsumo = codigoInsumo;
	}
	
	public Long getCodigoBasePreco() {
		return codigoBasePreco;
	}

	public void setCodigoBasePreco(Long codigoBasePreco) {
		this.codigoBasePreco = codigoBasePreco;
	}

	public Long getCodigoInsumo() {
		return codigoInsumo;
	}

	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoBasePreco == null) ? 0 : codigoBasePreco.hashCode());
		result = prime * result + ((codigoInsumo == null) ? 0 : codigoInsumo.hashCode());
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
		BasePrecoItemID other = (BasePrecoItemID) obj;
		if (codigoBasePreco == null) {
			if (other.codigoBasePreco != null)
				return false;
		} else if (!codigoBasePreco.equals(other.codigoBasePreco))
			return false;
		if (codigoInsumo == null) {
			if (other.codigoInsumo != null)
				return false;
		} else if (!codigoInsumo.equals(other.codigoInsumo))
			return false;
		return true;
	}
}