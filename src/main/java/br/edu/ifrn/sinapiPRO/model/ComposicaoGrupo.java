package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "composicao_grupo")
public class ComposicaoGrupo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long codigo;
	
	private String nome;
	
	@NotNull(message = "Classe é obrigatória")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_composicao_classe")
	@JsonIgnore
	private ComposicaoClasse composicaoClasse;
	
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

	public ComposicaoClasse getComposicaoClasse() {
		return composicaoClasse;
	}
	
	public void setComposicaoClasse(ComposicaoClasse classe) {
		this.composicaoClasse = classe;
	}
	
	public boolean temClasse() {
		return composicaoClasse != null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComposicaoGrupo other = (ComposicaoGrupo) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
}	
