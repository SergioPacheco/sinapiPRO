package com.sinapipro.model;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity @Table(name = "sub_divisao_insumo")
public class SubDivisaoInsumo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;
	@NotBlank(message = "Nome é obrigatório") private String nome;
	@NotNull(message = "Divisão é obrigatória")
	@ManyToOne @JoinColumn(name = "codigo_divisao")
	private DivisaoInsumo divisao;
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

	public DivisaoInsumo getDivisao() {
		return divisao;
	}

	public void setDivisao(DivisaoInsumo divisao) {
		this.divisao = divisao;
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
		if (!(o instanceof SubDivisaoInsumo)) return false;
		SubDivisaoInsumo x = (SubDivisaoInsumo) o;
		return codigo != null && codigo.equals(x.codigo);
	}
}
