package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

@Entity @Table(name = "funcionario")
public class Funcionario implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotBlank(message = "Nome é obrigatório") private String nome;
	private String cpf;
	private String email;
	private String telefone;
	@Column(name = "data_admissao") private LocalDate dataAdmissao;
	@Column(name = "data_demissao") private LocalDate dataDemissao;
	private boolean ativo = true;
	@ManyToOne @JoinColumn(name = "codigo_cargo") private Cargo cargo;
	@ManyToOne @JoinColumn(name = "codigo_funcao") private Funcao funcao;
	@ManyToOne @JoinColumn(name = "codigo_departamento") private Departamento departamento;

	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public String getNome() { return nome; } public void setNome(String n) { this.nome = n; }
	public String getCpf() { return cpf; } public void setCpf(String c) { this.cpf = c; }
	public String getEmail() { return email; } public void setEmail(String e) { this.email = e; }
	public String getTelefone() { return telefone; } public void setTelefone(String t) { this.telefone = t; }
	public LocalDate getDataAdmissao() { return dataAdmissao; } public void setDataAdmissao(LocalDate d) { this.dataAdmissao = d; }
	public LocalDate getDataDemissao() { return dataDemissao; } public void setDataDemissao(LocalDate d) { this.dataDemissao = d; }
	public boolean isAtivo() { return ativo; } public void setAtivo(boolean a) { this.ativo = a; }
	public Cargo getCargo() { return cargo; } public void setCargo(Cargo c) { this.cargo = c; }
	public Funcao getFuncao() { return funcao; } public void setFuncao(Funcao f) { this.funcao = f; }
	public Departamento getDepartamento() { return departamento; } public void setDepartamento(Departamento d) { this.departamento = d; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Funcionario)) return false; return codigo != null && codigo.equals(((Funcionario)o).codigo); }
}
