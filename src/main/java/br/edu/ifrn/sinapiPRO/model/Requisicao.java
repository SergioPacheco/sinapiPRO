package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.time.LocalDate; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "requisicao")
public class Requisicao implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	private Integer numero;
	@NotNull(message = "Obra é obrigatória") @ManyToOne @JoinColumn(name = "codigo_obra") private Obra obra;
	@NotNull(message = "Data é obrigatória") @Column(name = "data_requisicao") private LocalDate dataRequisicao;
	private String situacao = "ABERTA";
	private String observacao;
	@OneToMany(mappedBy = "requisicao", cascade = CascadeType.ALL, orphanRemoval = true) private List<RequisicaoItem> itens = new ArrayList<>();
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Integer getNumero() { return numero; } public void setNumero(Integer n) { this.numero = n; }
	public Obra getObra() { return obra; } public void setObra(Obra o) { this.obra = o; }
	public LocalDate getDataRequisicao() { return dataRequisicao; } public void setDataRequisicao(LocalDate d) { this.dataRequisicao = d; }
	public String getSituacao() { return situacao; } public void setSituacao(String s) { this.situacao = s; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public List<RequisicaoItem> getItens() { return itens; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Requisicao)) return false; return codigo != null && codigo.equals(((Requisicao)o).codigo); }
}
