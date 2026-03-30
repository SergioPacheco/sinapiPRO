package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotBlank; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "plano_contas")
public class PlanoContas implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotBlank(message = "Número é obrigatório") private String numero;
	@NotBlank(message = "Descrição é obrigatória") private String descricao;
	@NotBlank(message = "Tipo é obrigatório") private String tipo; // RECEITA, DESPESA, ATIVO, PASSIVO
	private Integer nivel = 1;
	@ManyToOne @JoinColumn(name = "codigo_pai") private PlanoContas pai;
	@OneToMany(mappedBy = "pai") private List<PlanoContas> filhos = new ArrayList<>();
	private boolean ativo = true;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public String getNumero() { return numero; } public void setNumero(String n) { this.numero = n; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public String getTipo() { return tipo; } public void setTipo(String t) { this.tipo = t; }
	public Integer getNivel() { return nivel; } public void setNivel(Integer n) { this.nivel = n; }
	public PlanoContas getPai() { return pai; } public void setPai(PlanoContas p) { this.pai = p; }
	public List<PlanoContas> getFilhos() { return filhos; }
	public boolean isAtivo() { return ativo; } public void setAtivo(boolean a) { this.ativo = a; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof PlanoContas)) return false; return codigo != null && codigo.equals(((PlanoContas)o).codigo); }
}
