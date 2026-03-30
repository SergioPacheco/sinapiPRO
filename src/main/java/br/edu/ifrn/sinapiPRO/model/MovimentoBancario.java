package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "movimento_bancario")
public class MovimentoBancario implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_conta_bancaria") private ContaBancaria contaBancaria;
	@ManyToOne @JoinColumn(name = "codigo_historico") private HistoricoBancario historico;
	@NotNull
	private String tipo; // CREDITO, DEBITO
	@NotNull
	private BigDecimal valor;
	@NotNull @Column(name = "data_movimento") private LocalDate dataMovimento;
	private String documento;
	private String descricao;
	@Column(name = "saldo_apos") private BigDecimal saldoApos;
	private boolean conciliado = false;
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public ContaBancaria getContaBancaria() {
		return contaBancaria;
	}

	public void setContaBancaria(ContaBancaria contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	public HistoricoBancario getHistorico() {
		return historico;
	}

	public void setHistorico(HistoricoBancario historico) {
		this.historico = historico;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDate getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(LocalDate dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getSaldoApos() {
		return saldoApos;
	}

	public void setSaldoApos(BigDecimal saldoApos) {
		this.saldoApos = saldoApos;
	}

	public boolean isConciliado() {
		return conciliado;
	}

	public void setConciliado(boolean conciliado) {
		this.conciliado = conciliado;
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
		if (!(o instanceof MovimentoBancario)) return false;
		return codigo != null && codigo.equals(((MovimentoBancario)o).codigo);
	}
}
