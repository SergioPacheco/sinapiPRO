package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "tipo_composicao")
public class TipoComposicao implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long codigo;
	private String nome;
	
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

}	
