package br.edu.ifrn.sinapiPRO.model;

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

@Entity
@Table(name = "composicao")
@DynamicUpdate
public class Composicao  {
	
	@Id
	private Long codigo;   // codigo da composicao  
	
	@OneToMany(mappedBy = "composicao", 
		    cascade = CascadeType.ALL, 
      orphanRemoval = true)
	private List<ItemComposicao> itens = new ArrayList<>();

	@ManyToOne 
	@JoinColumn(name = "codigo_base_preco")
	private BasePreco basePreco;
	
	@ManyToOne 
	@JoinColumn(name = "codigo_base_insumo")
	private BaseInsumo baseInsumo;
	
	@ManyToOne 
	@JoinColumn(name = "codigo_classe")
	private Classe classe;
	
	@ManyToOne 
	@JoinColumn(name = "codigo_tipo")
	private TipoComposicao tipoComposicao;
	
	@Enumerated(EnumType.STRING)
	private SituacaoComposicao ativa = SituacaoComposicao.ATIVA;
			
	@Transient
	private String uuid;
	
	@Size(max = 400)
	@Column(name = "descricao")
	private String descricao; 
	
	@Column(name = "unidade")
	private String unidade;
	
	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;
	
	@Column(name = "valor_total", precision=15, scale=2)
	private BigDecimal valorTotal = BigDecimal.ZERO;
	
	@Column(name = "custo_total", precision=15, scale=2)
	private BigDecimal custoTotal;
	
	@Column(name = "custo_mao_obra", precision=15, scale=2)
	private BigDecimal custoMaoObra; 
	
	@Column(name = "perce_mao_obra", precision=10, scale=7)
	private BigDecimal percMaoObra;

	@Column(name = "custo_material", precision=15, scale=2)
	private BigDecimal custoMaterial;
	
	@Column(name = "perc_material",  precision=10, scale=7)
	private BigDecimal percMaterial;
	
	@Column(name = "custo_equipamento", precision=15, scale=2)
	private BigDecimal custoEquipamento;
	
	@Column(name = "perc_equipamento",  precision=10, scale=7)
	private BigDecimal percEquipamento;
	
		
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}

	public BaseInsumo getBaseInsumo() {
		return baseInsumo;
	}

	public void setBaseInsumo(BaseInsumo baseInsumo) {
		this.baseInsumo = baseInsumo;
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

	public SituacaoComposicao getAtiva() {
		return ativa;
	}

	public void setAtiva(SituacaoComposicao ativa) {
		this.ativa = ativa;
	}

	public List<ItemComposicao> getItens() {
		return itens;
	}

	public void setItens(List<ItemComposicao> itens) {
		this.itens = itens;
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


	public void adicionarItens(List<ItemComposicao> itens) {
		this.itens = itens;
		this.itens.forEach(i -> i.setComposicao(this));
	}
	
	public boolean isNova() {
		return codigo == null;
	}
	
	public BigDecimal getValorTotalItens(){
		return getItens().stream()
				.map(ItemComposicao::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public boolean isSalvarPermitido() {
		return !ativa.equals(SituacaoComposicao.CANCELADA);
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
		Composicao other = (Composicao) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}