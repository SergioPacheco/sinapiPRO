package com.sinapipro.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "documento_ged")
public class DocumentoGed implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;

    @Column(name = "tipo_arquivo")
    private String tipoArquivo;

    private String caminho;
    private Long tamanho;

    @NotNull
    @Column(name = "data_upload")
    private LocalDateTime dataUpload;

    @ManyToOne
    @JoinColumn(name = "codigo_obra")
    private Obra obra;

    @ManyToOne
    @JoinColumn(name = "codigo_cliente")
    private Cliente cliente;

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public String getNome() {
	return nome;
}

public void setNome(String nome) {
	this.nome = nome;
}

public String getDescricao() {
	return descricao;
}

public void setDescricao(String descricao) {
	this.descricao = descricao;
}

public String getTipoArquivo() {
	return tipoArquivo;
}

public void setTipoArquivo(String tipoArquivo) {
	this.tipoArquivo = tipoArquivo;
}

public String getCaminho() {
	return caminho;
}

public void setCaminho(String caminho) {
	this.caminho = caminho;
}

public Long getTamanho() {
	return tamanho;
}

public void setTamanho(Long tamanho) {
	this.tamanho = tamanho;
}

public LocalDateTime getDataUpload() {
	return dataUpload;
}

public void setDataUpload(LocalDateTime dataUpload) {
	this.dataUpload = dataUpload;
}

public Obra getObra() {
	return obra;
}

public void setObra(Obra obra) {
	this.obra = obra;
}

public Cliente getCliente() {
	return cliente;
}

public void setCliente(Cliente cliente) {
	this.cliente = cliente;
}

public boolean isNovo() {
	return codigo == null;
}

}
