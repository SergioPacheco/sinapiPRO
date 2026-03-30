package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "pedido_item")
public class PedidoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_pedido") private PedidoCompra pedido;
	@ManyToOne @JoinColumn(name = "codigo_insumo") private Insumo insumo;
	@NotBlank(message = "Descrição é obrigatória") private String descricao;
	private String unidade;
	private BigDecimal quantidade = BigDecimal.ZERO;
	@Column(name = "valor_unitario") private BigDecimal valorUnitario = BigDecimal.ZERO;
	@Column(name = "valor_total") private BigDecimal valorTotal = BigDecimal.ZERO;
	@Column(name = "quantidade_recebida") private BigDecimal quantidadeRecebida = BigDecimal.ZERO;
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public PedidoCompra getPedido() {
		return pedido;
	}

	public void setPedido(PedidoCompra pedido) {
		this.pedido = pedido;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getQuantidadeRecebida() {
		return quantidadeRecebida;
	}

	public void setQuantidadeRecebida(BigDecimal quantidadeRecebida) {
		this.quantidadeRecebida = quantidadeRecebida;
	}
}
