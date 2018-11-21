package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "orcamento")
@DynamicUpdate
public class Orcamento implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	
	@NotBlank(message = "Nome da obra é obrigatório")
	private String nome;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_base_insumo")
	private BaseInsumo baseInsumo; 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_base_preco")
	private BasePreco basePreco; 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_estado")
	private Estado estado; 
	
	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;

	private String observacao;

	@ManyToOne
	@JoinColumn(name = "codigo_cliente")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "codigo_usuario")
	private Usuario usuario;

	@Enumerated(EnumType.STRING)
	private SituacaoOrcamento situacao = SituacaoOrcamento.ABERTO;
	
	@OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Item> itens = new ArrayList<>();

	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;

	@Column(name = "valor_mao_obra")
	private BigDecimal valorMaoObra = BigDecimal.ZERO;
	
	@Column(name = "valor_materiais")
	private BigDecimal valorMaterial = BigDecimal.ZERO;
	
	@Column(name = "valor_equipamentos")
	private BigDecimal valorEquipamento = BigDecimal.ZERO;
	
	@Column(name = "bdi_mao_obra")
	private BigDecimal BdiMaoObra = BigDecimal.ZERO;
		
	@Column(name = "bdi_insumos")
	private BigDecimal BdiInsumos = BigDecimal.ZERO;
	
	@Column(name = "bdi_materiais")
	private BigDecimal BdiMateriais = BigDecimal.ZERO;
	
	@Column(name = "leis_sociais")
	private BigDecimal LeisSociais = BigDecimal.ZERO;
	
	@Column(name = "taxa_administracao")
	private BigDecimal TaxaAdministracao = BigDecimal.ZERO;
			
	@Transient
	private String uuid;
	
	@Transient
	private Long etapaCheckBox;


	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public BaseInsumo getBaseInsumo() {
		return baseInsumo;
	}

	public void setBaseInsumo(BaseInsumo baseInsumo) {
		this.baseInsumo = baseInsumo;
	}

	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public SituacaoOrcamento getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoOrcamento situacao) {
		this.situacao = situacao;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
			
	public Long getEtapaCheckBox() {
		return etapaCheckBox;
	}

	public void setEtapaCheckBox(Long etapaCheckBox) {
		this.etapaCheckBox = etapaCheckBox;
	}

	public boolean isSalvarPermitido() {
		return !situacao.equals(SituacaoOrcamento.BLOQUEADO);
	}
	
	public boolean isSalvarProibido() {
		return !isSalvarPermitido();
	}
			
	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public BigDecimal getBdiMaoObra() {
		return BdiMaoObra;
	}

	public void setBdiMaoObra(BigDecimal bdiMaoObra) {
		BdiMaoObra = bdiMaoObra;
	}

	public BigDecimal getBdiInsumos() {
		return BdiInsumos;
	}

	public void setBdiInsumos(BigDecimal bdiInsumos) {
		BdiInsumos = bdiInsumos;
	}

	public BigDecimal getBdiMateriais() {
		return BdiMateriais;
	}

	public void setBdiMateriais(BigDecimal bdiMateriais) {
		BdiMateriais = bdiMateriais;
	}

	public BigDecimal getLeisSociais() {
		return LeisSociais;
	}

	public void setLeisSociais(BigDecimal leisSociais) {
		LeisSociais = leisSociais;
	}

	public BigDecimal getTaxaAdministracao() {
		return TaxaAdministracao;
	}

	public void setTaxaAdministracao(BigDecimal taxaAdministracao) {
		TaxaAdministracao = taxaAdministracao;
	}

	public List<Item> getItens() {
		
		Collections.sort(itens, new Comparator<Item>() {
		        @Override public int compare(Item p1, Item p2) {
		            return p1.getEtapa().getCodigo().intValue() - p2.getEtapa().getCodigo().intValue(); // Ascending
		        }
		});
		Itemizar();
		
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}
	
	public void Itemizar() { 
		
		
		/*
		 * list.sort((o1, o2) -> {
			    int cmp = o1.getGroup().compareTo(o2.getGroup());
			    if (cmp == 0)
			        cmp = Integer.compare(o1.getAge(), o2.getAge());
			    if (cmp == 0)
			        cmp = o1.getName().compareTo(o2.getName());
			    return cmp;
			});
		 */

	    Collections.sort(itens, new Comparator<Item>() {
	        @Override public int compare(Item p1, Item p2) {
	            return p1.getEtapa().getCodigo().intValue() - p2.getEtapa().getCodigo().intValue(); // Ascending
	        }

	    });
		
		Long aux =  0L;
		Long sub = 1L; 
		for (Item item : itens) {
			if (item.getEtapa().getCodigo() == aux) { 
			    if (item.getTipo().equals("E")) { 
			    	item.setItemizacao(item.getEtapa().getCodigo()+".");
			    } else { 
			    	item.setItemizacao(item.getEtapa().getCodigo()+"."+sub+".");
			    	sub++;
			    }
			  
				continue; 
			}
		    aux = item.getEtapa().getCodigo();
		    sub = 1L;
		    if (item.getTipo().equals("E")) { 
		    	item.setItemizacao(item.getEtapa().getCodigo()+".");
		    } else { 
		    	item.setItemizacao(item.getEtapa().getCodigo()+"."+sub+".");
		    	sub++;
		    }
		} 
		
		Collections.sort(itens, (o1, o2) -> (o1.getItemizacao().compareTo(o2.getItemizacao())));
		
	}
	
	
	public boolean isNovo() {
		return codigo == null;
	}
	
	public void adicionarItens(List<Item> itens) {
		this.itens = itens;
		this.itens.forEach(i -> i.setOrcamento(this));
	}
	
	public BigDecimal getValorTotalItens() {
		return getItens().stream()
				.map(Item::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal getValorMaoObra() {
		return getItens().stream()
				.map(Item::getValorMaoObra)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal getValorMaterial() {
		return getItens().stream()
				.map(Item::getValorMaterial)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal getValorEquipamento() {
		return getItens().stream()
				.map(Item::getValorEquipamento)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	
	public void calcularValorTotal() {
		this.valorTotal = getValorTotalItens();
	}
	public void calcularValorMaoObra() {
		this.valorMaoObra = getValorMaoObra();
	}
	public void calcularValorEquipamento() {
		this.valorEquipamento = getValorEquipamento();
	}
	public void calcularValorMaterial() {
		this.valorMaterial = getValorMaterial();
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
		Orcamento other = (Orcamento) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}
