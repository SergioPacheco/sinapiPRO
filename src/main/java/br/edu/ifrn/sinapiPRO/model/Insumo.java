package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "Insumo")
@Table(name = "insumo", 
	   uniqueConstraints = {@UniqueConstraint(columnNames = {"codigo_insumo", "codigo_base_insumo"})})
public class Insumo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	
	@NotNull(message = "O código do insumo é obrigatório")
	@Column(name = "codigo_insumo")
	private String codigoInsumo; 
	
	@NotNull(message = "A base de insumo é obrigatória")
	@ManyToOne
	@JoinColumn(name = "codigo_base_insumo")
    private BaseInsumo  baseInsumo;
		    	
	@ManyToOne 
	@JoinColumn(name = "codigo_base_preco")
	private BasePreco basePreco;
	
	@ManyToOne 
	@JoinColumn(name = "codigo_usuario")
	private Usuario usuario;
	
	@Size(max = 400)
	@NotNull(message = "Descrição é obrigatória")
	private String descricao; 
	
	@NotNull(message = "Unidade é obrigatório")
	private String unidade; 
	
	@DecimalMin(value = "0.00", message = "O valor do preço padrão não pode ser negativo")
	private BigDecimal precoPadrao;
	
	@NotNull(message = "A espécie é obrigatória")
	@Enumerated(EnumType.STRING)
	private Especie especie;
			
	//@Transient
	//public boolean sinapi; 
	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
 
	public BaseInsumo getBaseInsumo() {
		return baseInsumo;
	}

	public void setBaseInsumo(BaseInsumo baseInsumo) {
		this.baseInsumo = baseInsumo;
	}

	public String getCodigoInsumo() {
		return codigoInsumo;
	}

	public void setCodigoInsumo(String codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}

	public BasePreco getBasePreco() {
		return basePreco;
	}

	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

	public BigDecimal getPrecoPadrao() {
		return precoPadrao;
	}

	public void setPrecoPadrao(BigDecimal precoPadrao) {
		this.precoPadrao = precoPadrao;
	}

	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
		this.especie = especie;
	}

	@ManyToMany
	@JoinTable(name = "tributo_insumo",
		joinColumns = @JoinColumn(name = "codigo_insumo"),
		inverseJoinColumns = @JoinColumn(name = "codigo_tributo"))
	private java.util.List<Tributo> tributos = new java.util.ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "origem")
	private OrigemInsumo origem;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_equipamento")
	private TipoEquipamento tipoEquipamento;

	public java.util.List<Tributo> getTributos() {
		return tributos;
	}

	public void setTributos(java.util.List<Tributo> tributos) {
		this.tributos = tributos;
	}

	public OrigemInsumo getOrigem() {
		return origem;
	}

	public void setOrigem(OrigemInsumo origem) {
		this.origem = origem;
	}

	public TipoEquipamento getTipoEquipamento() {
		return tipoEquipamento;
	}

	public void setTipoEquipamento(TipoEquipamento tipoEquipamento) {
		this.tipoEquipamento = tipoEquipamento;
	}

	public boolean isNovo() { 
		return this.codigo == null;
	}
		
	public boolean isSinapi() {
		return "SINAPI".equals(baseInsumo.getNome()); 	
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
		Insumo other = (Insumo) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	
	
}
