package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "composicao")
@DynamicUpdate
public class Composicao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "codigo_composicao")
	private Long codigoComposicao;  
	 
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "codigo_base_preco")
	private BasePreco basePreco;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "codigo_classe")
	private Classe classe;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "codigo_tipo")
	private TipoComposicao tipoComposicao;

	@Enumerated(EnumType.STRING)
	private StatusComposicao ativa = StatusComposicao.ATIVA;
	
	@OneToMany(mappedBy = "composicao", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ItemComposicao> itemComposicao = new ArrayList<>();
		
	@Transient
	private String uuid;
	
	@Size(max = 400)
	@Column(name = "descricao")
	private String descricao; 
	
	@Column(name = "unidade")
	private String unidade;
	
	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;
	
	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;
	
	@Column(name = "custo_total")
	private BigDecimal custoTotal;
	
	@Column(name = "custo_mao_obra")
	private BigDecimal custoMaoObra; 
	
	@Column(name = "perce_mao_obra")
	private BigDecimal percMaoObra;

	@Column(name = "custo_material")
	private BigDecimal custoMaterial;
	
	@Column(name = "perc_material")
	private BigDecimal percMaterial;
	
	@Column(name = "custo_equipamento")
	private BigDecimal custoEquipamento;
	
	@Column(name = "perc_equipamento")
	private BigDecimal percEquipamento;
	
	@Column(name = "custo_servicos_terceiros")
	private BigDecimal custoServicosTerceiros; 

	@Column(name = "perc_servicos_terceiros")
	private BigDecimal percServicosTerceiros;
	
	@Column(name = "custo_outros")
	private BigDecimal custoOutros;
	
	@Column(name = "perc_outros")
	private BigDecimal percOutros;
	
	
	public Long getCodigoComposicao() {
		return codigoComposicao;
	}

	public void setCodigoComposicao(Long codigoComposicao) {
		this.codigoComposicao = codigoComposicao;
	}

	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}

	public Classe getClasse() {
		return classe;
	}

	public void setClasse(Classe classe) {
		this.classe = classe;
	}

	public TipoComposicao getTipoComposicao() {
		return tipoComposicao;
	}

	public void setTipoComposicao(TipoComposicao tipoComposicao) {
		this.tipoComposicao = tipoComposicao;
	}

	public StatusComposicao getAtiva() {
		return ativa;
	}

	public void setAtiva(StatusComposicao ativa) {
		this.ativa = ativa;
	}

	public List<ItemComposicao> getItens() {
		return itemComposicao;
	}

	public void setItens(List<ItemComposicao> itens) {
		this.itemComposicao = itens;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getCustoTotal() {
		return custoTotal;
	}

	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}

	public BigDecimal getCustoMaoObra() {
		return custoMaoObra;
	}

	public void setCustoMaoObra(BigDecimal custoMaoObra) {
		this.custoMaoObra = custoMaoObra;
	}

	public BigDecimal getPercMaoObra() {
		return percMaoObra;
	}

	public void setPercMaoObra(BigDecimal percMaoObra) {
		this.percMaoObra = percMaoObra;
	}

	public BigDecimal getCustoMaterial() {
		return custoMaterial;
	}

	public void setCustoMaterial(BigDecimal custoMaterial) {
		this.custoMaterial = custoMaterial;
	}

	public BigDecimal getPercMaterial() {
		return percMaterial;
	}

	public void setPercMaterial(BigDecimal percMaterial) {
		this.percMaterial = percMaterial;
	}

	public BigDecimal getCustoEquipamento() {
		return custoEquipamento;
	}

	public void setCustoEquipamento(BigDecimal custoEquipamento) {
		this.custoEquipamento = custoEquipamento;
	}

	public BigDecimal getPercEquipamento() {
		return percEquipamento;
	}

	public void setPercEquipamento(BigDecimal percEquipamento) {
		this.percEquipamento = percEquipamento;
	}

	public BigDecimal getCustoServicosTerceiros() {
		return custoServicosTerceiros;
	}

	public void setCustoServicosTerceiros(BigDecimal custoServicosTerceiros) {
		this.custoServicosTerceiros = custoServicosTerceiros;
	}

	public BigDecimal getPercServicosTerceiros() {
		return percServicosTerceiros;
	}

	public void setPercServicosTerceiros(BigDecimal percServicosTerceiros) {
		this.percServicosTerceiros = percServicosTerceiros;
	}

	public BigDecimal getCustoOutros() {
		return custoOutros;
	}

	public void setCustoOutros(BigDecimal custoOutros) {
		this.custoOutros = custoOutros;
	}

	public BigDecimal getPercOutros() {
		return percOutros;
	}

	public void setPercOutros(BigDecimal percOutros) {
		this.percOutros = percOutros;
	}

	public void adicionarItens(List<ItemComposicao> itens) {
		this.itemComposicao = itens;
		this.itemComposicao.forEach(i ->i.setComposicao(this));
	}
	
	public boolean isNova() {
		return codigoComposicao == null;
	}
	
	public BigDecimal getValorTotalItens(){
		return getItens().stream()
				.map(ItemComposicao::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public boolean isSalvarPermitido() {
		return !ativa.equals(StatusComposicao.CANCELADA);
	}
	
	public boolean isSalvarProibido() {
		return !isSalvarPermitido();
	}
	
	public void calcularValorTotal(){
		this.valorTotal = calcularValorTotal(getValorTotalItens()); 
	}
	                                       
	private BigDecimal calcularValorTotal(BigDecimal valorTotalItens) {
		BigDecimal valorTotal = valorTotalItens;
		return valorTotal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoComposicao == null) ? 0 : codigoComposicao.hashCode());
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
		Composicao other = (Composicao) obj;
		if (codigoComposicao == null) {
			if (other.codigoComposicao != null)
				return false;
		} else if (!codigoComposicao.equals(other.codigoComposicao))
			return false;
		return true;
	}
	
	 

}