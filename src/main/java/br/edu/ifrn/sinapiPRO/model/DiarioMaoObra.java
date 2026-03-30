package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "diario_mao_obra")
public class DiarioMaoObra implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_diario") private DiarioObra diario;
	private String descricao;
	private Integer quantidade = 0;
	@Column(name = "horas_trabalhadas") private BigDecimal horasTrabalhadas = BigDecimal.ZERO;
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public DiarioObra getDiario() {
		return diario;
	}

	public void setDiario(DiarioObra diario) {
		this.diario = diario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getHorasTrabalhadas() {
		return horasTrabalhadas;
	}

	public void setHorasTrabalhadas(BigDecimal horasTrabalhadas) {
		this.horasTrabalhadas = horasTrabalhadas;
	}
}
