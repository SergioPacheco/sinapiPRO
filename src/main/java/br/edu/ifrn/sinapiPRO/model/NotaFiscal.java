package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "nota_fiscal")
public class NotaFiscal implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_pedido") private PedidoCompra pedido;
	private String numero;
	@Column(name = "data_emissao") private LocalDate dataEmissao;
	private BigDecimal valor = BigDecimal.ZERO;
	private String observacao;
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

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public LocalDate getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(LocalDate dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}
