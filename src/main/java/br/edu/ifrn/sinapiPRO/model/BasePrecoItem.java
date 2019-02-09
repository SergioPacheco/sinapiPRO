package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity 
@Table(name = "base_preco_item")
public class BasePrecoItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BasePrecoItemID basePrecoItemID; // {codigoBasePreco, CodigoInsumo}
	 		
	@MapsId("codigo_base_preco_PK")          //referencia a propriedade BasePrecoItemID
    @JoinColumn(name = "codigo_base_preco", referencedColumnName = "codigo")
    @ManyToOne
    private BasePreco basePreco;
	
	
	private String anoMes;
	
	private BigDecimal preco;
	
	public BasePrecoItemID getBasePrecoItemID() {
		return basePrecoItemID;
	}

	public void setBasePrecoItemID(BasePrecoItemID basePrecoItemID) {
		this.basePrecoItemID = basePrecoItemID;
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
}
