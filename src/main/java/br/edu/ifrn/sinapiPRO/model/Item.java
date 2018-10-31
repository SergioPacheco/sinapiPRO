package br.edu.ifrn.sinapiPRO.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "item_orcamento")
public class Item  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	private String tipo;        /* C=Composicao I=Insumo E=Etapa */
	
	private Long codigoItem; 
	private String itemizacao; 
	private String especie; 
	private String unidade; 
	private BigDecimal quantidade  = BigDecimal.ZERO;

	@Column(name = "valor_unitario")
	private BigDecimal valorUnitario  = BigDecimal.ZERO;
	
	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;
	
	@Column(name = "valor_Mao_Obra")
	private BigDecimal valorMaoObra = BigDecimal.ZERO;
	
	@Column(name = "valor_Material")
	private BigDecimal valorMaterial = BigDecimal.ZERO;
		
	@Column(name = "valor_Equipamento")
	private BigDecimal valorEquipamento = BigDecimal.ZERO;

	@ManyToOne
	@JoinColumn(name = "codigo_etapa", nullable=false)
	private Etapa etapa;
	
	@ManyToOne
	@JoinColumn(name = "codigo_composicao", nullable=true)
	private Composicao composicao;
	
	@ManyToOne
	@JoinColumn(name = "codigo_insumo", referencedColumnName = "codigoInsumo", nullable=true)
	private Insumo insumo;

	@ManyToOne
	@JoinColumn(name = "codigo_orcamento")
	private Orcamento orcamento;
	
	public Long getCodigo() {
		return codigo;
	}

	public Long getCodigoItem() {
		return codigoItem;
	}
	
	public void setCodigoItem(Long codigoItem) {
		this.codigoItem = codigoItem;
	}

	public void setValorMaoObra(BigDecimal valorMaoObra) {
		this.valorMaoObra = valorMaoObra;
	}
	public void setValorMaterial(BigDecimal valorMaterial) {
		this.valorMaterial = valorMaterial;
	}
	public void setValorEquipamento(BigDecimal valorEquipamento) {
		this.valorEquipamento = valorEquipamento;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getItemizacao() {
		return itemizacao;
	}

	public void setItemizacao(String itemizacao) {
		this.itemizacao = itemizacao;
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	
	public Etapa getEtapa() {
		return etapa;
	}

	public void setEtapa(Etapa etapa) {
		this.etapa = etapa;
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

	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	public BigDecimal getValorTotal() {
		return valorUnitario.multiply(quantidade);
	}
	
	public BigDecimal getValorMaterial() {
		if ("C".equals(this.tipo)) {
			return composicao.getCustoMaterial();
		} else { 
			if ("I".equals(this.tipo)) {
				return this.insumo.getPrecoPadrao();
			}
		}
		return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorMaoObra() {
		if ("C".equals(this.tipo)) {
			return composicao.getCustoMaoObra();
		} else { 
			if ("I".equals(this.tipo)) {
				return this.insumo.getPrecoPadrao();
			}
		}
		return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorEquipamento() {
		if ("C".equals(this.tipo)) {
			return composicao.getCustoEquipamento();
		} else { 
			if ("I".equals(this.tipo)) {
				return this.insumo.getPrecoPadrao();
			}
		}
		return BigDecimal.ZERO;
	}
	
	public BigDecimal getValorTotalBDI() {
		return valorUnitario.multiply(quantidade).multiply(BigDecimal.valueOf(1.15));
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
		Item other = (Item) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	/*
	@Override
	public int compareTo(Item o) {
		 return this.getEtapa().getCodigo().compareTo(((Item) o).getEtapa().getCodigo());
	}
	*/	
}
