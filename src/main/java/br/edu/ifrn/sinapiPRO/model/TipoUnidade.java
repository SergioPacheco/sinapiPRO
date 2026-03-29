package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tipo_unidade")
public class TipoUnidade implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@NotBlank(message = "Sigla é obrigatória")
	private String sigla;

	public Long getCodigo() { return codigo; }
	public void setCodigo(Long codigo) { this.codigo = codigo; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getSigla() { return sigla; }
	public void setSigla(String sigla) { this.sigla = sigla; }
	public boolean isNovo() { return codigo == null; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		TipoUnidade other = (TipoUnidade) obj;
		if (codigo == null) return other.codigo == null;
		return codigo.equals(other.codigo);
	}
}
