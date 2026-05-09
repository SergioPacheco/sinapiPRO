package com.sinapipro.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "agendamento_manutencao")
public class AgendamentoManutencao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_veiculo")
    private Veiculo veiculo;

    @NotBlank(message = "Tipo de manutenção é obrigatório")
    @Column(name = "tipo_manutencao")
    private String tipoManutencao;

    @NotNull
    @Column(name = "data_agendamento")
    private LocalDate dataAgendamento;

    @Column(name = "data_realizacao")
    private LocalDate dataRealizacao;

    @Column(name = "km_atual")
    private Integer kmAtual;

    private BigDecimal valor = BigDecimal.ZERO;
    private String situacao = "AGENDADO";
    private String observacao;

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public Veiculo getVeiculo() {
	return veiculo;
}

public void setVeiculo(Veiculo veiculo) {
	this.veiculo = veiculo;
}

public String getTipoManutencao() {
	return tipoManutencao;
}

public void setTipoManutencao(String tipoManutencao) {
	this.tipoManutencao = tipoManutencao;
}

public LocalDate getDataAgendamento() {
	return dataAgendamento;
}

public void setDataAgendamento(LocalDate dataAgendamento) {
	this.dataAgendamento = dataAgendamento;
}

public LocalDate getDataRealizacao() {
	return dataRealizacao;
}

public void setDataRealizacao(LocalDate dataRealizacao) {
	this.dataRealizacao = dataRealizacao;
}

public Integer getKmAtual() {
	return kmAtual;
}

public void setKmAtual(Integer kmAtual) {
	this.kmAtual = kmAtual;
}

public BigDecimal getValor() {
	return valor;
}

public void setValor(BigDecimal valor) {
	this.valor = valor;
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
