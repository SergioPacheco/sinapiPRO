package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

@Entity @Table(name = "empresa")
public class Empresa implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotBlank(message = "Nome é obrigatório") private String nome;
	private String cnpj;
	private String telefone;
	private String email;
	private String endereco;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public String getNome() { return nome; } public void setNome(String n) { this.nome = n; }
	public String getCnpj() { return cnpj; } public void setCnpj(String c) { this.cnpj = c; }
	public String getTelefone() { return telefone; } public void setTelefone(String t) { this.telefone = t; }
	public String getEmail() { return email; } public void setEmail(String e) { this.email = e; }
	public String getEndereco() { return endereco; } public void setEndereco(String e) { this.endereco = e; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Empresa)) return false; return codigo != null && codigo.equals(((Empresa)o).codigo); }
}
