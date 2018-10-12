package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ComposicaoKey implements Serializable {

	private Long composicaoID;
	private Long itemID; 
	
	public ComposicaoKey() {
	}

	public ComposicaoKey(Long codigoComposicao, Long codigoItem) {
		this.composicaoID = codigoComposicao;
		this.itemID = codigoItem;
		
	}



	public Long getComposicaoID() {
		return composicaoID;
	}

	public void setComposicaoID(Long composicaoID) {
		this.composicaoID = composicaoID;
	}

	public Long getItemID() {
		return itemID;
	}

	public void setItemID(Long itemID) {
		this.itemID = itemID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((composicaoID == null) ? 0 : composicaoID.hashCode());
		result = prime * result + ((itemID == null) ? 0 : itemID.hashCode());
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
		ComposicaoKey other = (ComposicaoKey) obj;
		if (composicaoID == null) {
			if (other.composicaoID != null)
				return false;
		} else if (!composicaoID.equals(other.composicaoID))
			return false;
		if (itemID == null) {
			if (other.itemID != null)
				return false;
		} else if (!itemID.equals(other.itemID))
			return false;
		return true;
	}
	
}
