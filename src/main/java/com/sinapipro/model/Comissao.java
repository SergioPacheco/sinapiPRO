package com.sinapipro.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "comissao")
public class Comissao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_venda")
    private Venda venda;

    @NotBlank(message = "Nome do corretor é obrigatório")
    @Column(name = "nome_corretor")
    private String nomeCorretor;

    private BigDecimal percentual = BigDecimal.ZERO;
    private BigDecimal valor = BigDecimal.ZERO;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    private String situacao = "PENDENTE";

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

public String getNomeCorretor() {
	return nomeCorretor;
}

public void setNomeCorretor(String nomeCorretor) {
	this.nomeCorretor = nomeCorretor;
}

public BigDecimal getPercentual() {
	return percentual;
}

public void setPercentual(BigDecimal percentual) {
	this.percentual = percentual;
}

public BigDecimal getValor() {
	return valor;
}

public void setValor(BigDecimal valor) {
	this.valor = valor;
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

public boolean isNovo() {
	return codigo == null;
}

    @Override
public int hashCode() {
	return codigo == null ? 0 : codigo.hashCode();
}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Comissao)) return false;
        return codigo != null && codigo.equals(((Comissao) o).codigo);
    }
}
