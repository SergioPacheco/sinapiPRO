package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "diario_ocorrencia")
public class DiarioOcorrencia implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_diario") private DiarioObra diario;
	private String descricao;
	@ManyToOne @JoinColumn(name = "codigo_acidente") private DiarioAcidente acidente;
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public DiarioObra getDiario() { return diario; } public void setDiario(DiarioObra d) { this.diario = d; }
	public String getDescricao() { return descricao; } public void setDescricao(String d) { this.descricao = d; }
	public DiarioAcidente getAcidente() { return acidente; } public void setAcidente(DiarioAcidente a) { this.acidente = a; }
}
