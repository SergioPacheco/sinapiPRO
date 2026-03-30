package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.math.BigDecimal;
import javax.persistence.*; import javax.validation.constraints.NotBlank; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "conta_bancaria")
public class ContaBancaria implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotBlank(message = "Banco é obrigatório") private String banco;
	private String agencia;
	@NotBlank(message = "Conta é obrigatória") private String conta;
	private String descricao;
	@Column(name = "saldo_inicial") private BigDecimal saldoInicial = BigDecimal.ZERO;
	@Column(name = "saldo_atual") private BigDecimal saldoAtual = BigDecimal.ZERO;
	private boolean ativa = true;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public String getBanco() { return banco; } public void setBanco(String b) { this.banco = b; }
	public String getAgencia() { return agencia; } public void setAgencia(String a) { this.agencia = a; }
	public String getConta() { return conta; } public void setConta(String c) { this.conta = c; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public BigDecimal getSaldoInicial() { return saldoInicial; } public void setSaldoInicial(BigDecimal s) { this.saldoInicial = s; }
	public BigDecimal getSaldoAtual() { return saldoAtual; } public void setSaldoAtual(BigDecimal s) { this.saldoAtual = s; }
	public boolean isAtiva() { return ativa; } public void setAtiva(boolean a) { this.ativa = a; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof ContaBancaria)) return false; return codigo != null && codigo.equals(((ContaBancaria)o).codigo); }
}
