package com.sinapipro.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "cheque")
public class Cheque implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @ManyToOne
    @JoinColumn(name = "codigo_conta_bancaria")
    private ContaBancaria contaBancaria;

    @NotBlank(message = "Número é obrigatório")
    private String numero;

    private String beneficiario;

    @NotNull
    private BigDecimal valor;

    @NotNull
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_bom_para")
    private LocalDate dataBomPara;

    private String situacao = "EMITIDO";
    private String observacao;

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public ContaBancaria getContaBancaria() {
	return contaBancaria;
}

public void setContaBancaria(ContaBancaria contaBancaria) {
	this.contaBancaria = contaBancaria;
}

public String getNumero() {
	return numero;
}

public void setNumero(String numero) {
	this.numero = numero;
}

public String getBeneficiario() {
	return beneficiario;
}

public void setBeneficiario(String beneficiario) {
	this.beneficiario = beneficiario;
}

public BigDecimal getValor() {
	return valor;
}

public void setValor(BigDecimal valor) {
	this.valor = valor;
}

public LocalDate getDataEmissao() {
	return dataEmissao;
}

public void setDataEmissao(LocalDate dataEmissao) {
	this.dataEmissao = dataEmissao;
}

public LocalDate getDataBomPara() {
	return dataBomPara;
}

public void setDataBomPara(LocalDate dataBomPara) {
	this.dataBomPara = dataBomPara;
}

public String getSituacao() {
	return situacao;
}

public void setSituacao(String situacao) {
	this.situacao = situacao;
}

public String getObservacao() {
	return observacao;
}

public void setObservacao(String observacao) {
	this.observacao = observacao;
}

public boolean isNovo() {
	return codigo == null;
}

}
