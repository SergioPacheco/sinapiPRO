package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal;
import javax.persistence.*; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "resposta_cotacao")
public class RespostaCotacao implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_cotacao_item") private CotacaoItem cotacaoItem;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_cotacao_fornecedor") private CotacaoFornecedor cotacaoFornecedor;
	@Column(name = "valor_unitario") private BigDecimal valorUnitario = BigDecimal.ZERO;
	@Column(name = "prazo_entrega") private Integer prazoEntrega;
	private String observacao;
	private boolean selecionado = false;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public CotacaoItem getCotacaoItem() { return cotacaoItem; } public void setCotacaoItem(CotacaoItem ci) { this.cotacaoItem = ci; }
	public CotacaoFornecedor getCotacaoFornecedor() { return cotacaoFornecedor; } public void setCotacaoFornecedor(CotacaoFornecedor cf) { this.cotacaoFornecedor = cf; }
	public BigDecimal getValorUnitario() { return valorUnitario; } public void setValorUnitario(BigDecimal v) { this.valorUnitario = v; }
	public Integer getPrazoEntrega() { return prazoEntrega; } public void setPrazoEntrega(Integer p) { this.prazoEntrega = p; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public boolean isSelecionado() { return selecionado; } public void setSelecionado(boolean s) { this.selecionado = s; }
}
