package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "parcela_venda")
public class ParcelaVenda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_venda")
    private Venda venda;

    private Integer numero;

    @NotNull
    private BigDecimal valor;

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    private String situacao = "ABERTA";

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public Venda getVenda() {
	return venda;
}

public void setVenda(Venda venda) {
	this.venda = venda;
}

public Integer getNumero() {
	return numero;
}

public void setNumero(Integer numero) {
	this.numero = numero;
}

public BigDecimal getValor() {
	return valor;
}

public void setValor(BigDecimal valor) {
	this.valor = valor;
}

public LocalDate getDataVencimento() {
	return dataVencimento;
}

public void setDataVencimento(LocalDate dataVencimento) {
	this.dataVencimento = dataVencimento;
}

public LocalDate getDataPagamento() {
	return dataPagamento;
}

public void setDataPagamento(LocalDate dataPagamento) {
	this.dataPagamento = dataPagamento;
}

public String getSituacao() {
	return situacao;
}

public void setSituacao(String situacao) {
	this.situacao = situacao;
}

}
