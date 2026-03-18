package br.edu.ifrn.sinapiPRO.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "ComposicaoItem")
@Table(name = "composicao_item")
public class ComposicaoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	private String codigoItem;     // codigo composiçao ou insumo 
	
	@Column(name = "tipo")
	@Enumerated(EnumType.STRING)
	private Tipo tipo;             /* C=Composicao I=Insumo E=Etapa */
	
    @Column(name = "tipo", insertable = false, updatable = false)
    private String tipoAsText;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_composicao_pai", referencedColumnName="codigo")
	private Composicao composicaoPai;
	
	@ManyToOne
	@JoinColumn(name = "codigo_composicao")
	private Composicao composicao;
	
	@ManyToOne
	@JoinColumn(name = "codigo_insumo")
	private Insumo insumo;
	
	@Size(max = 400)
	private String descricao; 
	
	private String unidade; 
	
	@Column(precision=15, scale=7)
	private BigDecimal coeficiente; 
	
	@Column(precision=15, scale=2)
	private BigDecimal precoUnitario; 
	
	@Column(precision=15, scale=2)
	private BigDecimal custoTotal;

	@Transient 
	public BigDecimal quantidadeComposicao;
	
	public boolean temItens() {
		return composicao.getItens() != null;
	}
	
	public BigDecimal getQuantidadeComposicao() {
		return quantidadeComposicao;
	}

	public void setQuantidadeComposicao(BigDecimal quantidadeComposicao) {
		this.quantidadeComposicao = quantidadeComposicao;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public String getCodigoItem() {
		return codigoItem;
	}

	public void setCodigoItem(String codigoItem) {
		this.codigoItem = codigoItem;
	}

	public Composicao getComposicaoPai() {
		return composicaoPai;
	}

	public void setComposicaoPai(Composicao composicaoPai) {
		this.composicaoPai = composicaoPai;
	}

	public Composicao getComposicao() {
		return composicao;
	}

	public void setComposicao(Composicao composicao) {
		this.composicao = composicao;
	}
	
	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
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
	 
	public boolean isNova() { 
		return this.codigo == null;
	}
	
	public boolean isCompo() {
		return "COMPOSICAO".equals(this.tipo);
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		ComposicaoItem other = (ComposicaoItem) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
}	