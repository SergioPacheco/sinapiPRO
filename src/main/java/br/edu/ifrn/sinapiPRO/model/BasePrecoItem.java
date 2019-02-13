package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.JoinFormula;

@Entity(name = "BasePrecoItem")
@Table(name = "base_preco_item", 
	   uniqueConstraints = {@UniqueConstraint(columnNames = {"codigo_insumo", "codigo_base_preco"})})
public class BasePrecoItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotNull(message = "O código do insumo é obrigatório")
	@Column(name = "codigo_insumo")
	private Long codigoInsumo; 
	
	@NotNull(message = "A base de preço é obrigatória")
	@ManyToOne
	@JoinColumn(name = "codigo_base_preco")
    private BasePreco basePreco;
					
	private String anoMes;
	
	private BigDecimal preco;
	
	private BigDecimal precoOnerado;
	
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

	public BigDecimal getPrecoOnerado() {
		return precoOnerado;
	}

	public void setPrecoOnerado(BigDecimal precoOnerado) {
		this.precoOnerado = precoOnerado;
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
