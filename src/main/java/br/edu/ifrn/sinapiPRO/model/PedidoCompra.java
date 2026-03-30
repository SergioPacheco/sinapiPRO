package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal; import java.time.LocalDate; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "pedido_compra")
public class PedidoCompra implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	private Integer numero;
	@NotNull(message = "Obra é obrigatória") @ManyToOne @JoinColumn(name = "codigo_obra") private Obra obra;
	@ManyToOne @JoinColumn(name = "codigo_fornecedor") private Fornecedor fornecedor;
	@NotNull(message = "Data é obrigatória") @Column(name = "data_pedido") private LocalDate dataPedido;
	@Column(name = "data_entrega") private LocalDate dataEntrega;
	@Column(name = "valor_total") private BigDecimal valorTotal = BigDecimal.ZERO;
	private String situacao = "ABERTO";
	private String observacao;
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true) private List<PedidoItem> itens = new ArrayList<>();
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true) private List<NotaFiscal> notasFiscais = new ArrayList<>();
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Integer getNumero() { return numero; } public void setNumero(Integer n) { this.numero = n; }
	public Obra getObra() { return obra; } public void setObra(Obra o) { this.obra = o; }
	public Fornecedor getFornecedor() { return fornecedor; } public void setFornecedor(Fornecedor f) { this.fornecedor = f; }
	public LocalDate getDataPedido() { return dataPedido; } public void setDataPedido(LocalDate d) { this.dataPedido = d; }
	public LocalDate getDataEntrega() { return dataEntrega; } public void setDataEntrega(LocalDate d) { this.dataEntrega = d; }
	public BigDecimal getValorTotal() { return valorTotal; } public void setValorTotal(BigDecimal v) { this.valorTotal = v; }
	public String getSituacao() { return situacao; } public void setSituacao(String s) { this.situacao = s; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public List<PedidoItem> getItens() { return itens; }
	public List<NotaFiscal> getNotasFiscais() { return notasFiscais; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof PedidoCompra)) return false; return codigo != null && codigo.equals(((PedidoCompra)o).codigo); }
}
