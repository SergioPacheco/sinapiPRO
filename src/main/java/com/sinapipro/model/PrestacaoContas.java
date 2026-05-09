package com.sinapipro.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "prestacao_contas")
public class PrestacaoContas implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_funcionario")
    private Funcionario funcionario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_competencia")
    private Competencia competencia;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull
    private BigDecimal valor;

    @NotNull
    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;

    private String tipo = "DESPESA";
    private String situacao = "PENDENTE";

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public Funcionario getFuncionario() {
	return funcionario;
}

public void setFuncionario(Funcionario funcionario) {
	this.funcionario = funcionario;
}

public Competencia getCompetencia() {
	return competencia;
}

public void setCompetencia(Competencia competencia) {
	this.competencia = competencia;
}

public String getDescricao() {
	return descricao;
}

public void setDescricao(String descricao) {
	this.descricao = descricao;
}

public BigDecimal getValor() {
	return valor;
}

public void setValor(BigDecimal valor) {
	this.valor = valor;
}

public LocalDate getDataLancamento() {
	return dataLancamento;
}

public void setDataLancamento(LocalDate dataLancamento) {
	this.dataLancamento = dataLancamento;
}

public String getTipo() {
	return tipo;
}

public void setTipo(String tipo) {
	this.tipo = tipo;
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

}
