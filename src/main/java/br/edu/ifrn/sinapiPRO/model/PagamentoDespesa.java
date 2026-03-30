package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal; import java.time.LocalDate;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "pagamento_despesa")
public class PagamentoDespesa implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_despesa") private Despesa despesa;
	@ManyToOne @JoinColumn(name = "codigo_conta_bancaria") private ContaBancaria contaBancaria;
	@NotNull @Column(name = "valor_pago") private BigDecimal valorPago;
	@NotNull @Column(name = "data_pagamento") private LocalDate dataPagamento;
	private String observacao;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Despesa getDespesa() { return despesa; } public void setDespesa(Despesa d) { this.despesa = d; }
	public ContaBancaria getContaBancaria() { return contaBancaria; } public void setContaBancaria(ContaBancaria c) { this.contaBancaria = c; }
	public BigDecimal getValorPago() { return valorPago; } public void setValorPago(BigDecimal v) { this.valorPago = v; }
	public LocalDate getDataPagamento() { return dataPagamento; } public void setDataPagamento(LocalDate d) { this.dataPagamento = d; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
}
