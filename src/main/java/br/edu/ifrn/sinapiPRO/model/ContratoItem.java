package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal;
import javax.persistence.*; import javax.validation.constraints.NotBlank; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "contrato_item")
public class ContratoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_contrato") private Contrato contrato;
	@NotBlank(message = "Descrição é obrigatória") private String descricao;
	private String unidade;
	private BigDecimal quantidade = BigDecimal.ZERO;
	@Column(name = "valor_unitario") private BigDecimal valorUnitario = BigDecimal.ZERO;
	@Column(name = "valor_total") private BigDecimal valorTotal = BigDecimal.ZERO;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Contrato getContrato() { return contrato; } public void setContrato(Contrato c) { this.contrato = c; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public String getUnidade() { return unidade; } public void setUnidade(String u) { this.unidade = u; }
	public BigDecimal getQuantidade() { return quantidade; } public void setQuantidade(BigDecimal q) { this.quantidade = q; }
	public BigDecimal getValorUnitario() { return valorUnitario; } public void setValorUnitario(BigDecimal v) { this.valorUnitario = v; }
	public BigDecimal getValorTotal() { return valorTotal; } public void setValorTotal(BigDecimal v) { this.valorTotal = v; }
}
