package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "ordem_servico")
public class OrdemServico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_atendimento")
    private Atendimento atendimento;

    private Integer numero;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_execucao")
    private LocalDate dataExecucao;

    private String situacao = "ABERTA";
    private BigDecimal valor = BigDecimal.ZERO;

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public Atendimento getAtendimento() {
	return atendimento;
}

public void setAtendimento(Atendimento atendimento) {
	this.atendimento = atendimento;
}

public Integer getNumero() {
	return numero;
}

public void setNumero(Integer numero) {
	this.numero = numero;
}

public String getDescricao() {
	return descricao;
}

public void setDescricao(String descricao) {
	this.descricao = descricao;
}

public LocalDate getDataEmissao() {
	return dataEmissao;
}

public void setDataEmissao(LocalDate dataEmissao) {
	this.dataEmissao = dataEmissao;
}

public LocalDate getDataExecucao() {
	return dataExecucao;
}

public void setDataExecucao(LocalDate dataExecucao) {
	this.dataExecucao = dataExecucao;
}

public String getSituacao() {
	return situacao;
}

public void setSituacao(String situacao) {
	this.situacao = situacao;
}

public BigDecimal getValor() {
	return valor;
}

public void setValor(BigDecimal valor) {
	this.valor = valor;
}

public boolean isNovo() {
	return codigo == null;
}

}
