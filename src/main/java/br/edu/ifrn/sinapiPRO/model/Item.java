package br.edu.ifrn.sinapiPRO.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "item")
public class Item  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "codigo_orcamento")
	private Orcamento orcamento;
	
	@Column(name = "tipo")
	@Enumerated(EnumType.STRING)
	private Tipo tipo;         /* C=Composicao I=Insumo E=Etapa */
	
    @Column(name = "tipo", insertable = false, updatable = false) // usado em criteria
    private String tipoAsText;
	
	private String descricao; 
	
	private String itemizacao;
	
	@Enumerated(EnumType.STRING) /* MAO_DE_OBRA MATERIAL EQUIPAMENTOS */
	private Especie especie; 
	
	private String unidade; 
	
	private BigDecimal quantidade;  
	
	@ManyToOne 
	@JoinColumn(name = "codigo_etapa") 
	private Etapa etapa;

	@ManyToOne 
	@JoinColumn(name = "codigo_composicao") 
	private Composicao composicao; 
	
	@ManyToOne 
	@JoinColumn(name = "codigo_insumo") 
	private Insumo insumo;
	
	@Column(name = "valor_unitario")
	private BigDecimal valorUnitario;   
	
	@Column(name = "valor_mao_obra")
	private BigDecimal valorMaoObra;
	
	@Column(name = "valor_material")
	private BigDecimal valorMaterial; 
	
	@Column(name = "valor_equipamento")
	private BigDecimal valorEquipamento; 

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

	public String getItemizacao() {
		return itemizacao;
	}

	public void setItemizacao(String itemizacao) {
		this.itemizacao = itemizacao;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
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
		
	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
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

	public Etapa getEtapa() {
		return etapa;
	}

	public void setEtapa(Etapa etapa) {
		this.etapa = etapa;
	}
	
	public boolean isNovo() {
		return codigo == null;
	}
	
	public BigDecimal getValorTotal() {
		return valorUnitario.multiply((quantidade));
	}
    
	/* 
	public void calculaTotalItem() {
		this.valorTotal.add( Optional.ofNullable(this.valorUnitario).orElse(BigDecimal.ZERO)
				  .multiply( Optional.ofNullable(this.quantidade).orElse(BigDecimal.ZERO) ) ); 
	}
	  
	public void calculaValorTotal() {
		
		calculaTotalItem();
		
		if (this.tipo.equals(Tipo.INSUMO)) { 
			this.valorMaoObra.add(this.especie == Especie.MAO_DE_OBRA ? 
				   Optional.ofNullable(this.valorTotal).orElse(BigDecimal.ZERO) 
				:  BigDecimal.ZERO);
			this.valorMaterial.add(this.especie == Especie.MATERIAL ? 
				   Optional.ofNullable(this.valorTotal).orElse(BigDecimal.ZERO) 
				:  BigDecimal.ZERO);
			this.valorEquipamento.add(this.especie == Especie.EQUIPAMENTO ? 
				   Optional.ofNullable(this.valorTotal).orElse(BigDecimal.ZERO) 
				:  BigDecimal.ZERO);
		}
		if (this.tipo.equals(Tipo.COMPOSICAO)) { 
			// usar o que já estiver nos campos
		}
		if (this.tipo.equals(Tipo.ETAPA)) { 
			// TODO: Calcula sub-totais da etapa
			this.valorMaoObra     = BigDecimal.ZERO;
			this.valorMaterial    = BigDecimal.ZERO;
			this.valorEquipamento = BigDecimal.ZERO; 
			this.valorTotal       = BigDecimal.ZERO; 
		}
	}
	 */
	 
	public BigDecimal getValorMaoObra() {
		return valorMaoObra;
	}

	public void setValorMaoObra(BigDecimal valorMaoObra) {
		this.valorMaoObra = valorMaoObra;
	}

	public BigDecimal getValorMaterial() {
		return valorMaterial;
	}

	public void setValorMaterial(BigDecimal valorMaterial) {
		this.valorMaterial = valorMaterial;
	}

	public BigDecimal getValorEquipamento() {
		return valorEquipamento;
	}

	public void setValorEquipamento(BigDecimal valorEquipamento) {
		this.valorEquipamento = valorEquipamento;
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
	
}


/*

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
			
*/	

/*
@Override
public int compareTo(Item o) {
	 return this.getEtapa().getCodigo().compareTo(((Item) o).getEtapa().getCodigo());
}
*/	
