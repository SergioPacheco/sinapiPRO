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

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "etapa")
public class Etapa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Etapa() {
	}

	
	public Etapa(String nome) {
		super();
		this.nome = nome;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	
	private String nome;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_etapa_pai")
	private Etapa etapaPai;
			
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
	public Etapa getEtapaPai() {
		return etapaPai;
	}
	public void setEtapaPai(Etapa etapaPai) {
		this.etapaPai = etapaPai;
	}
	public boolean isNova() {
		return codigo == null;
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
		Etapa other = (Etapa) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
}	