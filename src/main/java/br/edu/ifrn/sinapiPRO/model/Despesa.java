package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal; import java.time.LocalDate; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotBlank; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "despesa")
public class Despesa implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotBlank(message = "Descrição é obrigatória") private String descricao;
	@ManyToOne @JoinColumn(name = "codigo_obra") private Obra obra;
	@ManyToOne @JoinColumn(name = "codigo_fornecedor") private Fornecedor fornecedor;
	@ManyToOne @JoinColumn(name = "codigo_plano_contas") private PlanoContas planoContas;
	@NotNull(message = "Valor é obrigatório") private BigDecimal valor;
	@NotNull(message = "Vencimento é obrigatório") @Column(name = "data_vencimento") private LocalDate dataVencimento;
	@Column(name = "data_competencia") private LocalDate dataCompetencia;
	private String situacao = "ABERTA";
	private String observacao;
	@OneToMany(mappedBy = "despesa", cascade = CascadeType.ALL, orphanRemoval = true) private List<PagamentoDespesa> pagamentos = new ArrayList<>();
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public Obra getObra() { return obra; } public void setObra(Obra o) { this.obra = o; }
	public Fornecedor getFornecedor() { return fornecedor; } public void setFornecedor(Fornecedor f) { this.fornecedor = f; }
	public PlanoContas getPlanoContas() { return planoContas; } public void setPlanoContas(PlanoContas p) { this.planoContas = p; }
	public BigDecimal getValor() { return valor; } public void setValor(BigDecimal v) { this.valor = v; }
	public LocalDate getDataVencimento() { return dataVencimento; } public void setDataVencimento(LocalDate d) { this.dataVencimento = d; }
	public LocalDate getDataCompetencia() { return dataCompetencia; } public void setDataCompetencia(LocalDate d) { this.dataCompetencia = d; }
	public String getSituacao() { return situacao; } public void setSituacao(String s) { this.situacao = s; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public List<PagamentoDespesa> getPagamentos() { return pagamentos; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Despesa)) return false; return codigo != null && codigo.equals(((Despesa)o).codigo); }
}
