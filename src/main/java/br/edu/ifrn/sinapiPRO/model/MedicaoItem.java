package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "medicao_item")
public class MedicaoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_medicao") private Medicao medicao;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_contrato_item") private ContratoItem contratoItem;
	@Column(name = "quantidade_medida") private BigDecimal quantidadeMedida = BigDecimal.ZERO;
	@Column(name = "percentual_executado") private BigDecimal percentualExecutado = BigDecimal.ZERO;
	@Column(name = "valor_medido") private BigDecimal valorMedido = BigDecimal.ZERO;
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Medicao getMedicao() {
		return medicao;
	}

	public void setMedicao(Medicao medicao) {
		this.medicao = medicao;
	}

	public ContratoItem getContratoItem() {
		return contratoItem;
	}

	public void setContratoItem(ContratoItem ci) {
		this.contratoItem = ci;
	}

	public BigDecimal getQuantidadeMedida() {
		return quantidadeMedida;
	}

	public void setQuantidadeMedida(BigDecimal quantidadeMedida) {
		this.quantidadeMedida = quantidadeMedida;
	}

	public BigDecimal getPercentualExecutado() {
		return percentualExecutado;
	}

	public void setPercentualExecutado(BigDecimal percentualExecutado) {
		this.percentualExecutado = percentualExecutado;
	}

	public BigDecimal getValorMedido() {
		return valorMedido;
	}

	public void setValorMedido(BigDecimal valorMedido) {
		this.valorMedido = valorMedido;
	}
}
