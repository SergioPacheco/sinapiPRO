package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal; import java.time.LocalDate; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "medicao")
public class Medicao implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_contrato") private Contrato contrato;
	private Integer numero;
	@NotNull(message = "Data é obrigatória") @Column(name = "data_medicao") private LocalDate dataMedicao;
	@Column(name = "data_inicio") private LocalDate dataInicio;
	@Column(name = "data_fim") private LocalDate dataFim;
	@Column(name = "valor_medido") private BigDecimal valorMedido = BigDecimal.ZERO;
	private String situacao = "ABERTA";
	private String observacao;
	@OneToMany(mappedBy = "medicao", cascade = CascadeType.ALL, orphanRemoval = true) private List<MedicaoItem> itens = new ArrayList<>();
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Contrato getContrato() { return contrato; } public void setContrato(Contrato c) { this.contrato = c; }
	public Integer getNumero() { return numero; } public void setNumero(Integer n) { this.numero = n; }
	public LocalDate getDataMedicao() { return dataMedicao; } public void setDataMedicao(LocalDate d) { this.dataMedicao = d; }
	public LocalDate getDataInicio() { return dataInicio; } public void setDataInicio(LocalDate d) { this.dataInicio = d; }
	public LocalDate getDataFim() { return dataFim; } public void setDataFim(LocalDate d) { this.dataFim = d; }
	public BigDecimal getValorMedido() { return valorMedido; } public void setValorMedido(BigDecimal v) { this.valorMedido = v; }
	public String getSituacao() { return situacao; } public void setSituacao(String s) { this.situacao = s; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public List<MedicaoItem> getItens() { return itens; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Medicao)) return false; return codigo != null && codigo.equals(((Medicao)o).codigo); }
}
