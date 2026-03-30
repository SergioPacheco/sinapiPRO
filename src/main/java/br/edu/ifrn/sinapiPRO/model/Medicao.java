package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "medicao")
public class Medicao implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_contrato") private Contrato contrato;
	private Integer numero;
	@NotNull(message = "Data é obrigatória") @Column(name = "data_medicao") private LocalDate dataMedicao;
	@Column(name = "data_inicio") private LocalDate dataInicio;
	@Column(name = "data_fim") private LocalDate dataFim;
	@Column(name = "valor_medido") private BigDecimal valorMedido = BigDecimal.ZERO;
	private String situacao = "ABERTA";
	private String observacao;
	@OneToMany(mappedBy = "medicao", cascade = CascadeType.ALL, orphanRemoval = true) private List<MedicaoItem> itens = new ArrayList<>();
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public LocalDate getDataMedicao() {
		return dataMedicao;
	}

	public void setDataMedicao(LocalDate dataMedicao) {
		this.dataMedicao = dataMedicao;
	}

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDate dataInicio) {
		this.dataInicio = dataInicio;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}

	public void setDataFim(LocalDate dataFim) {
		this.dataFim = dataFim;
	}

	public BigDecimal getValorMedido() {
		return valorMedido;
	}

	public void setValorMedido(BigDecimal valorMedido) {
		this.valorMedido = valorMedido;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<MedicaoItem> getItens() {
		return itens;
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
		if (!(o instanceof Medicao)) return false;
		return codigo != null && codigo.equals(((Medicao)o).codigo);
	}
}
