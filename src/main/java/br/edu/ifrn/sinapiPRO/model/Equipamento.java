package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import javax.persistence.*; import javax.validation.constraints.NotBlank; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "equipamento")
public class Equipamento implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotBlank(message = "Nome é obrigatório") private String nome;
	private String descricao; private String tipo;
	@Column(name = "numero_serie") private String numeroSerie;
	private boolean ativo = true;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public String getNome() { return nome; } public void setNome(String n) { this.nome = n; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public String getTipo() { return tipo; } public void setTipo(String t) { this.tipo = t; }
	public String getNumeroSerie() { return numeroSerie; } public void setNumeroSerie(String n) { this.numeroSerie = n; }
	public boolean isAtivo() { return ativo; } public void setAtivo(boolean a) { this.ativo = a; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof Equipamento)) return false; return codigo != null && codigo.equals(((Equipamento)o).codigo); }
}
