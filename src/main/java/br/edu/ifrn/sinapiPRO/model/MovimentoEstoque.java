package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal; import java.time.LocalDate;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "movimento_estoque")
public class MovimentoEstoque implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_estoque") private Estoque estoque;
	@NotNull private String tipo; // ENTRADA, SAIDA, AJUSTE
	@NotNull private BigDecimal quantidade;
	@NotNull @Column(name = "data_movimento") private LocalDate dataMovimento;
	private String documento;
	private String observacao;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Estoque getEstoque() { return estoque; } public void setEstoque(Estoque e) { this.estoque = e; }
	public String getTipo() { return tipo; } public void setTipo(String t) { this.tipo = t; }
	public BigDecimal getQuantidade() { return quantidade; } public void setQuantidade(BigDecimal q) { this.quantidade = q; }
	public LocalDate getDataMovimento() { return dataMovimento; } public void setDataMovimento(LocalDate d) { this.dataMovimento = d; }
	public String getDocumento() { return documento; } public void setDocumento(String d) { this.documento = d; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
}
