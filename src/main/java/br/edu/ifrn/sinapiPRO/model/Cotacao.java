package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.time.LocalDate; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "cotacao")
public class Cotacao implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	private Integer numero;
	@NotNull(message = "Obra é obrigatória") @ManyToOne @JoinColumn(name = "codigo_obra") private Obra obra;
	@NotNull(message = "Data é obrigatória") @Column(name = "data_cotacao") private LocalDate dataCotacao;
	@Column(name = "data_validade") private LocalDate dataValidade;
	private String situacao = "ABERTA";
	private String observacao;
	@OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true) private List<CotacaoItem> itens = new ArrayList<>();
	@OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true) private List<CotacaoFornecedor> fornecedores = new ArrayList<>();
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Integer getNumero() { return numero; } public void setNumero(Integer n) { this.numero = n; }
	public Obra getObra() { return obra; } public void setObra(Obra o) { this.obra = o; }
	public LocalDate getDataCotacao() { return dataCotacao; } public void setDataCotacao(LocalDate d) { this.dataCotacao = d; }
	public LocalDate getDataValidade() { return dataValidade; } public void setDataValidade(LocalDate d) { this.dataValidade = d; }
	public String getSituacao() { return situacao; } public void setSituacao(String s) { this.situacao = s; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public List<CotacaoItem> getItens() { return itens; }
	public List<CotacaoFornecedor> getFornecedores() { return fornecedores; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Cotacao)) return false; return codigo != null && codigo.equals(((Cotacao)o).codigo); }
}
