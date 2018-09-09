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
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;

	private String sku;  
	 
	@Enumerated(EnumType.STRING)
	private Base base;
	 
	@Column(name = "ano_mes")
	private String anoMes;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "codigo_estado")
	private Estado estado;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "codigo_classe")
	private Classe classe;
	
	@ManyToOne
	@JoinColumn(name = "codigo_usuario")
	private Usuario usuario;

	@Enumerated(EnumType.STRING)
	private StatusComposicao status = StatusComposicao.ATIVA;
	
	@OneToMany(mappedBy = "composicao", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<ItemComposicao> itens = new ArrayList<>();
		
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
	
	@Column(name = "percentual_mao_obra")
	private BigDecimal percentualMaoObra;

	@Column(name = "custo_material")
	private BigDecimal custoMaterial;
	
	@Column(name = "percentual_material")
	private BigDecimal percentualMaterial;
	
	@Column(name = "custo_equipamento")
	private BigDecimal custoEquipamento;
	
	@Column(name = "percentual_equipamento")
	private BigDecimal percentualEquipamento;
	
	@Column(name = "custo_servicos_terceiros")
	private BigDecimal custoServicosTerceiros; 

	@Column(name = "percentual_servicos_terceiros")
	private BigDecimal percentualServicosTerceiros;
	
	@Column(name = "custo_outros")
	private BigDecimal custoOutros;
	
	@Column(name = "percentual_outros")
	private BigDecimal percentualOutros;
	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}
		
	public String getAnoMes() {
		return anoMes;
	}

	public void setAnoMes(String anoMes) {
		this.anoMes = anoMes;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
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

	public Classe getClasse() {
		return classe;
	}

	public void setClasse(Classe classe) {
		this.classe = classe;
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

	public BigDecimal getPercentualMaoObra() {
		return percentualMaoObra;
	}

	public void setPercentualMaoObra(BigDecimal percentualMaoObra) {
		this.percentualMaoObra = percentualMaoObra;
	}

	public BigDecimal getCustoMaterial() {
		return custoMaterial;
	}

	public void setCustoMaterial(BigDecimal custoMaterial) {
		this.custoMaterial = custoMaterial;
	}

	public BigDecimal getPercentualMaterial() {
		return percentualMaterial;
	}

	public void setPercentualMaterial(BigDecimal percentualMaterial) {
		this.percentualMaterial = percentualMaterial;
	}

	public BigDecimal getCustoEquipamento() {
		return custoEquipamento;
	}

	public void setCustoEquipamento(BigDecimal custoEquipamento) {
		this.custoEquipamento = custoEquipamento;
	}

	public BigDecimal getPercentualEquipamento() {
		return percentualEquipamento;
	}

	public void setPercentualEquipamento(BigDecimal percentualEquipamento) {
		this.percentualEquipamento = percentualEquipamento;
	}

	public BigDecimal getCustoServicosTerceiros() {
		return custoServicosTerceiros;
	}

	public void setCustoServicosTerceiros(BigDecimal custoServicosTerceiros) {
		this.custoServicosTerceiros = custoServicosTerceiros;
	}

	public BigDecimal getPercentualServicosTerceiros() {
		return percentualServicosTerceiros;
	}

	public void setPercentualServicosTerceiros(BigDecimal percentualServicosTerceiros) {
		this.percentualServicosTerceiros = percentualServicosTerceiros;
	}

	public BigDecimal getCustoOutros() {
		return custoOutros;
	}

	public void setCustoOutros(BigDecimal custoOutros) {
		this.custoOutros = custoOutros;
	}

	public BigDecimal getPercentualOutros() {
		return percentualOutros;
	}

	public void setPercentualOutros(BigDecimal percentualOutros) {
		this.percentualOutros = percentualOutros;
	}
	
	public boolean isNova(){
		return codigo == null;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public void adicionarItens(List<ItemComposicao> itens) {
		this.itens = itens;
		this.itens.forEach(i ->i.setComposicao(this));
	}
	
	public BigDecimal getValorTotalItens(){
		return getItens().stream()
				.map(ItemComposicao::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public boolean isSalvarPermitido() {
		return !status.equals(StatusComposicao.CANCELADA);
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