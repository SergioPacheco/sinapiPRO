package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal;
import javax.persistence.*; import javax.validation.constraints.NotBlank; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "requisicao_item")
public class RequisicaoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_requisicao") private Requisicao requisicao;
	@ManyToOne @JoinColumn(name = "codigo_insumo") private Insumo insumo;
	@NotBlank(message = "Descrição é obrigatória") private String descricao;
	private String unidade;
	private BigDecimal quantidade = BigDecimal.ZERO;
	@Column(name = "quantidade_atendida") private BigDecimal quantidadeAtendida = BigDecimal.ZERO;
	private String situacao = "PENDENTE";
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Requisicao getRequisicao() { return requisicao; } public void setRequisicao(Requisicao r) { this.requisicao = r; }
	public Insumo getInsumo() { return insumo; } public void setInsumo(Insumo i) { this.insumo = i; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public String getUnidade() { return unidade; } public void setUnidade(String u) { this.unidade = u; }
	public BigDecimal getQuantidade() { return quantidade; } public void setQuantidade(BigDecimal q) { this.quantidade = q; }
	public BigDecimal getQuantidadeAtendida() { return quantidadeAtendida; } public void setQuantidadeAtendida(BigDecimal q) { this.quantidadeAtendida = q; }
	public String getSituacao() { return situacao; } public void setSituacao(String s) { this.situacao = s; }
}
