package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "cliente_endereco")
public class ClienteEndereco implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull(message = "Cliente é obrigatório") @ManyToOne @JoinColumn(name = "codigo_cliente") private Cliente cliente;
	private String tipo;
	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private String cep;
	private String cidade;
	private String estado;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Cliente getCliente() { return cliente; } public void setCliente(Cliente c) { this.cliente = c; }
	public String getTipo() { return tipo; } public void setTipo(String t) { this.tipo = t; }
	public String getLogradouro() { return logradouro; } public void setLogradouro(String l) { this.logradouro = l; }
	public String getNumero() { return numero; } public void setNumero(String n) { this.numero = n; }
	public String getComplemento() { return complemento; } public void setComplemento(String c) { this.complemento = c; }
	public String getBairro() { return bairro; } public void setBairro(String b) { this.bairro = b; }
	public String getCep() { return cep; } public void setCep(String c) { this.cep = c; }
	public String getCidade() { return cidade; } public void setCidade(String c) { this.cidade = c; }
	public String getEstado() { return estado; } public void setEstado(String e) { this.estado = e; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof ClienteEndereco)) return false; return codigo != null && codigo.equals(((ClienteEndereco)o).codigo); }
}
