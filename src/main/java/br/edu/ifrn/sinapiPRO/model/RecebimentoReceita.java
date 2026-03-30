package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal; import java.time.LocalDate;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "recebimento_receita")
public class RecebimentoReceita implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_receita") private Receita receita;
	@ManyToOne @JoinColumn(name = "codigo_conta_bancaria") private ContaBancaria contaBancaria;
	@NotNull @Column(name = "valor_recebido") private BigDecimal valorRecebido;
	@NotNull @Column(name = "data_recebimento") private LocalDate dataRecebimento;
	private String observacao;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Receita getReceita() { return receita; } public void setReceita(Receita r) { this.receita = r; }
	public ContaBancaria getContaBancaria() { return contaBancaria; } public void setContaBancaria(ContaBancaria c) { this.contaBancaria = c; }
	public BigDecimal getValorRecebido() { return valorRecebido; } public void setValorRecebido(BigDecimal v) { this.valorRecebido = v; }
	public LocalDate getDataRecebimento() { return dataRecebimento; } public void setDataRecebimento(LocalDate d) { this.dataRecebimento = d; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
}
