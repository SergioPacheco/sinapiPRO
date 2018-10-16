
package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "ItemBasePreco")
@Table(name = "item_base_preco")
public class ItemBasePreco implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BaseKey baseKey; // {basePrecoID, codigoInsumoID}
	 
	@ManyToOne
	@JoinColumn(name = "basePrecoID", nullable=false, insertable = false, updatable = false)
	private BasePreco basePreco; 
	
	/*
	 * Notice that the @ManyToOne association instructs Hibernate to ignore 
	 * inserts and updates issued on this mapping since the basePrecoID is 
	 * controlled by the @EmbeddedId.
	 */
	
	@NotNull(message = "Valor é obrigatório")
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

	public BaseKey getBaseKey() {
		return baseKey;
	}

	public void setBaseKey(BaseKey baseKey) {
		this.baseKey = baseKey;
	}
	
}
