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
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	private String tipo;        /* C=Composicao I=Insumo E=Etapa */
	
	private String nivel;                  
	private String subNivel; 
	
	private Integer sequencia; 
	private Integer itemizacao; 
	
	
	private String especie; 
	private String unidade; 
	private BigDecimal quantidade;

	@Column(name = "valor_unitario")
	private BigDecimal valorUnitario;

	@ManyToOne
	@JoinColumn(name = "codigo_etapa")
	private Etapa etapa;
	
	@ManyToOne
	@JoinColumn(name = "codigo_composicao")
	private Composicao composicao;
	
	@ManyToOne
	@JoinColumn(name = "codigo_insumo")
	private Insumo insumo;

	@ManyToOne
	@JoinColumn(name = "codigo_orcamento")
	private Orcamento orcamento;
	
	@ManyToOne
	@JoinColumn(name = "codigo_usario")
	private Usuario usuario;
	

	public Long getCodigo() {
		return codigo;
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

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getSubNivel() {
		return subNivel;
	}

	public void setSubNivel(String subNivel) {
		this.subNivel = subNivel;
	}

	public Integer getSequencia() {
		return sequencia;
	}

	public void setSequencia(Integer sequencia) {
		this.sequencia = sequencia;
	}

	public Integer getItemizacao() {
		return itemizacao;
	}

	public void setItemizacao(Integer itemizacao) {
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
	
	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;

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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public BigDecimal getValorTotal() {
		return valorUnitario.multiply(quantidade);
	}
	
	public BigDecimal getValorTotalBDI() {
		return valorUnitario.multiply(quantidade).multiply(BigDecimal.valueOf(1.15));
	}
	public BigDecimal getValorMaterial() {
		return composicao.getCustoMaterial();
	}
	public BigDecimal getValoMaoObral() {
		return composicao.getCustoMaoObra();
	}
	public BigDecimal getValoEquipamentos() {
		return composicao.getCustoMaoObra();
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
