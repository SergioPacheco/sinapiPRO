package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.time.LocalDate;
import javax.persistence.*; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "cotacao_fornecedor")
public class CotacaoFornecedor implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_cotacao") private Cotacao cotacao;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_fornecedor") private Fornecedor fornecedor;
	@Column(name = "email_enviado") private boolean emailEnviado = false;
	@Column(name = "data_envio") private LocalDate dataEnvio;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Cotacao getCotacao() { return cotacao; } public void setCotacao(Cotacao c) { this.cotacao = c; }
	public Fornecedor getFornecedor() { return fornecedor; } public void setFornecedor(Fornecedor f) { this.fornecedor = f; }
	public boolean isEmailEnviado() { return emailEnviado; } public void setEmailEnviado(boolean e) { this.emailEnviado = e; }
	public LocalDate getDataEnvio() { return dataEnvio; } public void setDataEnvio(LocalDate d) { this.dataEnvio = d; }
}
