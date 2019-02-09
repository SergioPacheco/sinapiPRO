package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class InsumoID implements Serializable {
	 
	private static final long serialVersionUID = 1L;
		
	
	@Column(name = "codigo_base_insumo_PK")
	private Long codigoBaseInsumo;
	
	@Column(name = "codigo_insumo_PK")
	private Long codigoInsumo; 
	
	public InsumoID() {
	}
	
	public InsumoID(Long codigoBaseInsumo, Long codigoInsumo) {
		this.codigoBaseInsumo = codigoBaseInsumo; 
		this.codigoInsumo     = codigoInsumo;
	}
	
	
	public Long getCodigoBaseInsumo() {
		return codigoBaseInsumo;
	}

	public void setCodigoBaseInsumo(Long codigoBaseInsumo) {
		this.codigoBaseInsumo = codigoBaseInsumo;
	}

	public Long getCodigoInsumo() {
		return this.codigoInsumo;
	}

	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoBaseInsumo == null) ? 0 : codigoBaseInsumo.hashCode());
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
		InsumoID other = (InsumoID) obj;
		if (codigoBaseInsumo == null) {
			if (other.codigoBaseInsumo != null)
				return false;
		} else if (!codigoBaseInsumo.equals(other.codigoBaseInsumo))
			return false;
		if (codigoInsumo == null) {
			if (other.codigoInsumo != null)
				return false;
		} else if (!codigoInsumo.equals(other.codigoInsumo))
			return false;
		return true;
	}

}