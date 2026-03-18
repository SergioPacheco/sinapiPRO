package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	
	@OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval=true)
	private List<Item> itens = new ArrayList<>();
	
	@NotBlank(message = "Descriçao é obrigatório")
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

	@ManyToOne
	@JoinColumn(name = "codigo_cliente")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "codigo_usuario")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "codigo_obra")
	private Obra obra;

	@Enumerated(EnumType.STRING)
	private Desoneracao desoneracao;
	
	@Enumerated(EnumType.STRING)
	private OrcamentoSituacao situacao = OrcamentoSituacao.ABERTO;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_orcamento")
	private TipoOrcamento tipoOrcamento = TipoOrcamento.ESTIMATIVA;
	
	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;
	
	@Column(name = "sub_total")
	private BigDecimal subTotal = BigDecimal.ZERO;
	
	@Column(name = "total_bdi")
	private BigDecimal totalBDI = BigDecimal.ZERO;
	
	@Column(name = "total_leis_sociais")
	private BigDecimal totaLeisSociais = BigDecimal.ZERO;
	
	@Column(name = "total_taxa_adm")
	private BigDecimal totalTaxaAdm = BigDecimal.ZERO;
	
	@Column(name = "total_taxas")
	private BigDecimal totalTaxas = BigDecimal.ZERO;

	@Column(name = "percentual_bdi")
	private BigDecimal percentualBdi;
	
	@Column(name = "percentual_leis_sociais")
	private BigDecimal percentualLeisSociais;
	
	@Column(name = "tipo_arredondamento") // E-exato T-para baixo G-geral C-somente composições
	private String tipoArredondamento;
	
	@Column(name = "decimais_arredondamento")
	private BigDecimal decimaisArredondamento;
	
	@Column(name = "percentual_taxa_adm")
	private BigDecimal percentualTaxaAdm;
			
	@Column(name = "observacao")
	private String observacao;
	
	@Transient
	private String uuid;
	
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

	public Obra getObra() {
		return obra;
	}

	public void setObra(Obra obra) {
		this.obra = obra;
	}

	public OrcamentoSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(OrcamentoSituacao situacao) {
		this.situacao = situacao;
	}

	public TipoOrcamento getTipoOrcamento() {
		return tipoOrcamento;
	}

	public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
		this.tipoOrcamento = tipoOrcamento;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Desoneracao getDesoneracao() {
		return desoneracao;
	}

	public void setDesoneracao(Desoneracao desoneracao) {
		this.desoneracao = desoneracao;
	}

	public boolean isSalvarPermitido() {
		return !situacao.equals(OrcamentoSituacao.BLOQUEADO);
	}
	
	public boolean isSalvarProibido() {
		return !isSalvarPermitido();
	}
	
	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getTotalBDI() {
		return totalBDI;
	}

	public void setTotalBDI(BigDecimal totalBDI) {
		this.totalBDI = totalBDI;
	}

	public BigDecimal getTotaLeisSociais() {
		return totaLeisSociais;
	}

	public void setTotaLeisSociais(BigDecimal totaLeisSociais) {
		this.totaLeisSociais = totaLeisSociais;
	}

	public BigDecimal getTotalTaxaAdm() {
		return totalTaxaAdm;
	}

	public void setTotalTaxaAdm(BigDecimal totalTaxaAdm) {
		this.totalTaxaAdm = totalTaxaAdm;
	}

	public BigDecimal getTotalTaxas() {
		return totalTaxas;
	}

	public void setTotalTaxas(BigDecimal totalTaxas) {
		this.totalTaxas = totalTaxas;
	}

	public BigDecimal getPercentualBdi() {
		return percentualBdi;
	}

	public void setPercentualBdi(BigDecimal percentualBdi) {
		this.percentualBdi = percentualBdi;
	}

	public BigDecimal getPercentualLeisSociais() {
		return percentualLeisSociais;
	}

	public void setPercentualLeisSociais(BigDecimal percentualLeisSociais) {
		this.percentualLeisSociais = percentualLeisSociais;
	}

	public BigDecimal getPercentualTaxaAdm() {
		return percentualTaxaAdm;
	}

	public void setPercentualTaxaAdm(BigDecimal percentualTaxaAdm) {
		this.percentualTaxaAdm = percentualTaxaAdm;
	}

	public String getTipoArredondamento() {
		return tipoArredondamento;
	}

	public void setTipoArredondamento(String tipoArredondamento) {
		this.tipoArredondamento = tipoArredondamento;
	}

	public BigDecimal getDecimaisArredondamento() {
		return decimaisArredondamento;
	}

	public void setDecimaisArredondamento(BigDecimal decimaisArredondamento) {
		this.decimaisArredondamento = decimaisArredondamento;
	}

	
	
	
	public void setItens(List<Item> itens) {
		this.itens = itens;
	}
	
	
	
	public List<Item> getItens() {
		/*
		Collections.sort(itens, new Comparator<Item>() {
		        @Override public int compare(Item p1, Item p2) {
		            return p1.getEtapa().getCodigo().intValue() - p2.getEtapa().getCodigo().intValue(); // Ascending
		        }
		});
		Itemizar();
		*/
		return itens;
	}
	
	public void Itemizar() {
		if (itens == null || itens.isEmpty()) return;

		// Build etapa hierarchy number: etapa.codigo -> "1.2."
		Map<Long, String> etapaNumero = new LinkedHashMap<>();
		Map<Long, List<Etapa>> filhosPorPai = new HashMap<>();
		List<Etapa> raizes = new ArrayList<>();

		// Collect unique etapas from items
		Map<Long, Etapa> etapasMap = new LinkedHashMap<>();
		for (Item item : itens) {
			if (item.getEtapa() != null) {
				etapasMap.put(item.getEtapa().getCodigo(), item.getEtapa());
			}
		}

		// Classify root vs child etapas
		for (Etapa e : etapasMap.values()) {
			if (e.getEtapaPai() == null) {
				raizes.add(e);
			} else {
				Long paiId = e.getEtapaPai().getCodigo();
				filhosPorPai.computeIfAbsent(paiId, k -> new ArrayList<>()).add(e);
			}
		}

		// Sort roots by codigo for stable ordering
		raizes.sort(Comparator.comparing(Etapa::getCodigo));

		// Assign hierarchical numbers via DFS
		numerarEtapas(raizes, "", etapaNumero, filhosPorPai);

		// Assign itemizacao to each item
		Map<Long, Long> contadorPorEtapa = new HashMap<>();
		// Sort items by etapa hierarchy then by tipo (ETAPA first)
		itens.sort((a, b) -> {
			String na = etapaNumero.getOrDefault(a.getEtapa().getCodigo(), "999.");
			String nb = etapaNumero.getOrDefault(b.getEtapa().getCodigo(), "999.");
			int cmp = na.compareTo(nb);
			if (cmp != 0) return cmp;
			// ETAPA items come first within same etapa
			if (a.getTipo() == Tipo.ETAPA && b.getTipo() != Tipo.ETAPA) return -1;
			if (a.getTipo() != Tipo.ETAPA && b.getTipo() == Tipo.ETAPA) return 1;
			return 0;
		});

		for (Item item : itens) {
			Long etapaId = item.getEtapa().getCodigo();
			String prefixo = etapaNumero.getOrDefault(etapaId, etapaId + ".");

			if (item.getTipo() == Tipo.ETAPA) {
				item.setItemizacao(prefixo);
			} else {
				Long sub = contadorPorEtapa.getOrDefault(etapaId, 1L);
				item.setItemizacao(prefixo + sub + ".");
				contadorPorEtapa.put(etapaId, sub + 1);
			}
		}

		itens.sort((o1, o2) -> o1.getItemizacao().compareTo(o2.getItemizacao()));
	}

	private void numerarEtapas(List<Etapa> etapas, String prefixo,
			Map<Long, String> resultado, Map<Long, List<Etapa>> filhosPorPai) {
		for (int i = 0; i < etapas.size(); i++) {
			Etapa e = etapas.get(i);
			String numero = prefixo + (i + 1) + ".";
			resultado.put(e.getCodigo(), numero);
			List<Etapa> filhos = filhosPorPai.get(e.getCodigo());
			if (filhos != null) {
				filhos.sort(Comparator.comparing(Etapa::getCodigo));
				numerarEtapas(filhos, numero, resultado, filhosPorPai);
			}
		}
	}
	
	public boolean isNovo() {
		return codigo == null;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public BigDecimal calculaValorTotalItens() {
		return getItens().stream()
				.filter(i -> {
                    if (i.getValorTotal() != null) {
                        return true;
                    }
                    return false;
                })
				.map(Item::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal calculaValorMaoObra() {
		return getItens().stream()
				.filter(i -> {
                    if (i.getValorMaoObra() != null) {
                        return true;
                    }
                    return false;
                })
				.map(Item::getValorMaoObra)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal calculaValorMaterial() {
		return getItens().stream()
				.filter(i -> {
                    if (i.getValorMaterial() != null) {
                        return true;
                    }
                    return false;
                })
				.map(Item::getValorMaterial)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal calculaValorEquipamento() {
		return getItens().stream()
				.filter(i -> {
                    if (i.getValorEquipamento() != null) {
                        return true;
                    }
                    return false;
                })
				.map(Item::getValorEquipamento)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal getValorTotal() {
		return this.valorTotal;
	}
			
	 
	private static final BigDecimal CEM = new BigDecimal("100");

	private BigDecimal percentualOuZero(BigDecimal percentual) {
		return percentual != null ? percentual : BigDecimal.ZERO;
	}

	/** Leis Sociais aplicadas SOMENTE sobre mão de obra */
	public BigDecimal calculaValorLeisSociais() {
		return calculaValorMaoObra()
				.multiply(percentualOuZero(this.percentualLeisSociais))
				.divide(CEM, 2, BigDecimal.ROUND_HALF_UP);
	}

	/** BDI aplicado sobre (subtotal + leis sociais) — cascata */
	public BigDecimal calculaValorBDI() {
		BigDecimal base = calculaValorSubTotal().add(calculaValorLeisSociais());
		return base.multiply(percentualOuZero(this.percentualBdi))
				.divide(CEM, 2, BigDecimal.ROUND_HALF_UP);
	}

	/** Taxa Adm aplicada sobre (subtotal + leis sociais + BDI) — cascata */
	public BigDecimal calculaValorTaxaAdm() {
		BigDecimal base = calculaValorSubTotal().add(calculaValorLeisSociais()).add(calculaValorBDI());
		return base.multiply(percentualOuZero(this.percentualTaxaAdm))
				.divide(CEM, 2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal calculaValorSubTotal() {
		return calculaValorMaoObra().add(calculaValorMaterial()).add(calculaValorEquipamento());
	}

	/** Soma de todas as taxas em cascata */
	public BigDecimal calculaValorTaxas() {
		return calculaValorLeisSociais().add(calculaValorBDI()).add(calculaValorTaxaAdm());
	}

	/** SubTotal + taxas em cascata */
	public BigDecimal calculaValorTotalComTaxas() {
		return calculaValorSubTotal().add(calculaValorTaxas());
	}
	
	public void addItem(Item item) {
		itens.add(item);
		item.setOrcamento(this);
	}
	
	public void removeItem(Item item) {
		item.setOrcamento(null);
		this.itens.remove(item);
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
/*
 * 
NONE    = Não faz nada com o objeto (padrão)
MERGE   = Atualiza filhos quando atualiza o pai, somente se já estiver persisitido
PERSIST = Salva o filho quando salva o pai
REFRESH = Salva o pai e mantém o filho sem alterar
REMOVE  = Remove o filho quando remove o pai e vice-versa
ALL = Executa todas as operações de cascade

class Parent {
    String name;
    @OneToMany(mappedBy = "parent", 
               fetch = FetchType.LAZY, 
               cascade = CascadeType.ALL, orphanRemoval = true)
    List<Child> children;

    public void addChild(Child child) {
        child.setParent(this);
        children.add(child);
    }

    public void removeChild(Child child) {
        children.remove(child);
        if (child != null) {
            child.setParent(null);
        }
    }
}

class Child {
    String name;
    @ManyToOne
    Parent parent;

    @OneToOne(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    Toy toy;
}

class Toy {
    String name;

    @OneToOne
    Child child;
}
*/

