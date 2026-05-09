package com.sinapipro.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "movimentacao_hora")
public class MovimentacaoHora implements Serializable {

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

    @NotNull
    @Column(name = "data_movimentacao")
    private LocalDate dataMovimentacao;

    @NotNull
    private String tipo; // CREDITO, DEBITO, EXTRA

    @NotNull
    private BigDecimal horas;

    private String descricao;

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

public LocalDate getDataMovimentacao() {
	return dataMovimentacao;
}

public void setDataMovimentacao(LocalDate dataMovimentacao) {
	this.dataMovimentacao = dataMovimentacao;
}

public String getTipo() {
	return tipo;
}

public void setTipo(String tipo) {
	this.tipo = tipo;
}

public BigDecimal getHoras() {
	return horas;
}

public void setHoras(BigDecimal horas) {
	this.horas = horas;
}

public String getDescricao() {
	return descricao;
}

public void setDescricao(String descricao) {
	this.descricao = descricao;
}

public boolean isNovo() {
	return codigo == null;
}

}
