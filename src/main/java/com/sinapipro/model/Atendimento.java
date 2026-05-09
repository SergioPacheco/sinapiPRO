package com.sinapipro.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "atendimento")
public class Atendimento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "codigo_obra")
    private Obra obra;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;
    private String tipo;
    private String prioridade = "NORMAL";
    private String situacao = "ABERTO";

    @NotNull
    @Column(name = "data_abertura")
    private LocalDate dataAbertura;

    @Column(name = "data_previsao")
    private LocalDate dataPrevisao;

    @Column(name = "data_encerramento")
    private LocalDate dataEncerramento;

    private String responsavel;

    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemServico> ordensServico = new ArrayList<>();

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public Cliente getCliente() {
	return cliente;
}

public void setCliente(Cliente cliente) {
	this.cliente = cliente;
}

public Obra getObra() {
	return obra;
}

public void setObra(Obra obra) {
	this.obra = obra;
}

public String getTitulo() {
	return titulo;
}

public void setTitulo(String titulo) {
	this.titulo = titulo;
}

public String getDescricao() {
	return descricao;
}

public void setDescricao(String descricao) {
	this.descricao = descricao;
}

public String getTipo() {
	return tipo;
}

public void setTipo(String tipo) {
	this.tipo = tipo;
}

public String getPrioridade() {
	return prioridade;
}

public void setPrioridade(String prioridade) {
	this.prioridade = prioridade;
}

public String getSituacao() {
	return situacao;
}

public void setSituacao(String situacao) {
	this.situacao = situacao;
}

public LocalDate getDataAbertura() {
	return dataAbertura;
}

public void setDataAbertura(LocalDate dataAbertura) {
	this.dataAbertura = dataAbertura;
}

public LocalDate getDataPrevisao() {
	return dataPrevisao;
}

public void setDataPrevisao(LocalDate dataPrevisao) {
	this.dataPrevisao = dataPrevisao;
}

public LocalDate getDataEncerramento() {
	return dataEncerramento;
}

public void setDataEncerramento(LocalDate dataEncerramento) {
	this.dataEncerramento = dataEncerramento;
}

public String getResponsavel() {
	return responsavel;
}

public void setResponsavel(String responsavel) {
	this.responsavel = responsavel;
}

public List<OrdemServico> getOrdensServico() {
	return ordensServico;
}

public boolean isNovo() {
	return codigo == null;
}

}
