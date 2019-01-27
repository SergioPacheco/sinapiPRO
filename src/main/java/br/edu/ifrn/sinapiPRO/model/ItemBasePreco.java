
package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "ItemBasePreco")
@Table(name = "item_base_preco")
public class ItemBasePreco implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BasePrecoId baseKey; // {basePrecoID, codigoInsumoID}
	 
	@ManyToOne
	@JoinColumn(name = "basePrecoID", nullable=false, insertable = false, updatable = false)
	private BasePreco basePreco; 
		
	private String anoMes;
	
	private BigDecimal preco;
	
	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
	
	public String getAnoMes() {
		return anoMes;
	}

	public void setAnoMes(String anoMes) {
		this.anoMes = anoMes;
	}

	public BasePrecoId getBaseKey() {
		return baseKey;
	}

	public void setBaseKey(BasePrecoId baseKey) {
		this.baseKey = baseKey;
	}
	
}
