package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class BaseKey implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	private Long basePrecoID;
	private Long insumoID; 
	
	public BaseKey() {
	}

	public BaseKey(Long basePrecoID, Long insumoID) {
		this.basePrecoID = basePrecoID;
		this.insumoID = insumoID;
	}

	public Long getBasePrecoID() {
		return basePrecoID;
	}

	public void setBasePrecoID(Long basePrecoID) {
		this.basePrecoID = basePrecoID;
	}

	public Long getInsumoID() {
		return insumoID;
	}

	public void setInsumoID(Long insumoID) {
		this.insumoID = insumoID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((basePrecoID == null) ? 0 : basePrecoID.hashCode());
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
		BaseKey other = (BaseKey) obj;
		if (basePrecoID == null) {
			if (other.basePrecoID != null)
				return false;
		} else if (!basePrecoID.equals(other.basePrecoID))
			return false;
		if (insumoID == null) {
			if (other.insumoID != null)
				return false;
		} else if (!insumoID.equals(other.insumoID))
			return false;
		return true;
	}


}