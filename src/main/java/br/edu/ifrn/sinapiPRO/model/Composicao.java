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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;

import br.edu.ifrn.sinapiPRO.utils.Lib;

@Entity(name = "Composicao")
@Table(name = "composicao", 
	   uniqueConstraints = {@UniqueConstraint(columnNames = {"codigo_composicao", "codigo_base_insumo"})})
@DynamicUpdate
public class Composicao  {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotNull(message = "O código da composição é obrigatório")
	@Column(name = "codigo_composicao")
	private Long codigoComposicao;   
	  
	@NotNull(message = "A base de insumo é obrigatória")
	@ManyToOne
	@JoinColumn(name = "codigo_base_insumo")
    private BaseInsumo  baseInsumo;
	
	@OneToMany(mappedBy = "composicao", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComposicaoItem> itens = new ArrayList<>();
 
	@ManyToOne 
	@JoinColumn(name = "codigo_base_preco")
	private BasePreco basePreco;
	
	@ManyToOne 
	@JoinColumn(name = "codigo_composicao_classe")
	private ComposicaoClasse composicaoClasse;
	
	@ManyToOne 
	@JoinColumn(name = "codigo_composicao_grupo")
	private ComposicaoGrupo composicaoGrupo;
	
	@Enumerated(EnumType.STRING)
	private SituacaoComposicao ativa = SituacaoComposicao.ATIVA;
			
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
	
	@Column(name = "perc_mao_obra", precision=10, scale=7)
	private BigDecimal percMaoObra;

	@Column(name = "custo_material", precision=15, scale=2)
	private BigDecimal custoMaterial;
	
	@Column(name = "perc_material",  precision=10, scale=7)
	private BigDecimal percMaterial;
	
	@Column(name = "custo_equipamento", precision=15, scale=2)
	private BigDecimal custoEquipamento;
	
	@Column(name = "perc_equipamento",  precision=10, scale=7)
	private BigDecimal percEquipamento;
	
	@Transient
	private String uuid;
	
	@Transient
	public boolean sinapi;
	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getCodigoComposicao() {
		return codigoComposicao;
	}

	public void setCodigoComposicao(Long codigoComposicao) {
		this.codigoComposicao = codigoComposicao;
	}

	public BaseInsumo getBaseInsumo() {
		return baseInsumo;
	}

	public void setBaseInsumo(BaseInsumo baseInsumo) {
		this.baseInsumo = baseInsumo;
	}

	public List<ComposicaoItem> getItens() {
		return itens;
	}

	public void setItens(List<ComposicaoItem> itens) {
		this.itens = itens;
	}

	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}

	public ComposicaoClasse getComposicaoClasse() {
		return composicaoClasse;
	}

	public void setComposicaoClasse(ComposicaoClasse composicaoClasse) {
		this.composicaoClasse = composicaoClasse;
	}

	public ComposicaoGrupo getComposicaoGrupo() {
		return composicaoGrupo;
	}

	public void setComposicaoGrupo(ComposicaoGrupo composicaoGrupo) {
		this.composicaoGrupo = composicaoGrupo;
	}

	public SituacaoComposicao getAtiva() {
		return ativa;
	}

	public void setAtiva(SituacaoComposicao ativa) {
		this.ativa = ativa;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void adicionarItens(List<ComposicaoItem> itens) {
		this.itens = itens;
		this.itens.forEach(i -> i.setComposicao(this));
	}
	
	public BigDecimal getValorTotalItens(){
		return getItens().stream()
				.map(ComposicaoItem::getValorTotal)
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

	public boolean isNova() { 
		return this.codigo == null;
	}
		
	public void setSinapi(boolean sinapi) {
		this.sinapi = sinapi;
	}

	public boolean getSinapi() {
		if (baseInsumo.getNome() == null) {
			return false;
		}
		return "SINAPI".equals(Lib.Trim(baseInsumo.getNome())); 
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