package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import javax.persistence.*; import javax.validation.constraints.NotBlank; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "cliente_referencia")
public class ClienteReferencia implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull(message = "Cliente é obrigatório") @ManyToOne @JoinColumn(name = "codigo_cliente") private Cliente cliente;
	@NotBlank(message = "Nome é obrigatório") private String nome;
	private String telefone;
	private String tipo;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Cliente getCliente() { return cliente; } public void setCliente(Cliente c) { this.cliente = c; }
	public String getNome() { return nome; } public void setNome(String n) { this.nome = n; }
	public String getTelefone() { return telefone; } public void setTelefone(String t) { this.telefone = t; }
	public String getTipo() { return tipo; } public void setTipo(String t) { this.tipo = t; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof ClienteReferencia)) return false; return codigo != null && codigo.equals(((ClienteReferencia)o).codigo); }
}
