package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "diario_area")
public class DiarioArea implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotBlank(message = "Nome é obrigatório") private String nome;
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

	public boolean isNovo() {
		return codigo == null;
	}

	@Override
	public int hashCode() {
		return codigo == null ? 0 : codigo.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DiarioArea)) return false;
		return codigo != null && codigo.equals(((DiarioArea)o).codigo);
	}
}
