package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal; import java.time.LocalDate; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotBlank; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "contrato")
public class Contrato implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	private String numero;
	@NotBlank(message = "Descrição é obrigatória") private String descricao;
	@NotNull(message = "Obra é obrigatória") @ManyToOne @JoinColumn(name = "codigo_obra") private Obra obra;
	@ManyToOne @JoinColumn(name = "codigo_cliente") private Cliente cliente;
	@Column(name = "data_inicio") private LocalDate dataInicio;
	@Column(name = "data_fim") private LocalDate dataFim;
	@Column(name = "valor_total") private BigDecimal valorTotal = BigDecimal.ZERO;
	private String situacao = "ABERTO";
	private String observacao;
	@OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true) private List<ContratoItem> itens = new ArrayList<>();
	@OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true) private List<Medicao> medicoes = new ArrayList<>();
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public String getNumero() { return numero; } public void setNumero(String n) { this.numero = n; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public Obra getObra() { return obra; } public void setObra(Obra o) { this.obra = o; }
	public Cliente getCliente() { return cliente; } public void setCliente(Cliente c) { this.cliente = c; }
	public LocalDate getDataInicio() { return dataInicio; } public void setDataInicio(LocalDate d) { this.dataInicio = d; }
	public LocalDate getDataFim() { return dataFim; } public void setDataFim(LocalDate d) { this.dataFim = d; }
	public BigDecimal getValorTotal() { return valorTotal; } public void setValorTotal(BigDecimal v) { this.valorTotal = v; }
	public String getSituacao() { return situacao; } public void setSituacao(String s) { this.situacao = s; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public List<ContratoItem> getItens() { return itens; }
	public List<Medicao> getMedicoes() { return medicoes; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Contrato)) return false; return codigo != null && codigo.equals(((Contrato)o).codigo); }
}
