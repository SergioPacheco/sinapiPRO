package br.edu.ifrn.sinapiPRO.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "item_composicao")
public class ItemComposicao {

	@EmbeddedId
	private ComposicaoKey composicaoKey; // {composicaoID, itemID}
	
	private String tipo; 
	
	@ManyToOne
	@JoinColumn(name = "composicaoID", nullable=false, insertable = false, updatable = false)
	private Composicao composicao;
	
	@ManyToOne
	private BasePreco basePreco;
	
	@Size(max = 400)
	private String descricao; 
	
	private String unidade; 
	
	@Column(length = 15, precision = 7)
	private BigDecimal coeficiente; // Quantidade usada
	
	@Column(name = "preco_unitario")
	private BigDecimal precoUnitario; 
	
	@Column(name = "custo_total")
	private BigDecimal custoTotal;
	
	
	public ComposicaoKey getComposicaoKey() {
		return composicaoKey;
	}

	public void setComposicaoKey(ComposicaoKey composicaoKey) {
		this.composicaoKey = composicaoKey;
	}

	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}

	public Composicao getComposicao() {
		return composicao;
	}

	public void setComposicao(Composicao composicao) {
		this.composicao = composicao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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

	public BigDecimal getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(BigDecimal coeficiente) {
		this.coeficiente = coeficiente;
	}

	public BigDecimal getPrecoUnitario() {
		return precoUnitario;
	}

	public void setPrecoUnitario(BigDecimal precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public BigDecimal getCustoTotal() {
		return custoTotal;
	}

	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}
	
	public BigDecimal getValorTotal(){
		return precoUnitario.multiply(coeficiente);
	}
	
	
	
	
	
}	
