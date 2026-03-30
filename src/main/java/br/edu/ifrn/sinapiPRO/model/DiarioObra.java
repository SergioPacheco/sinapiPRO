package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable; import java.time.LocalDate; import java.util.ArrayList; import java.util.List;
import javax.persistence.*; import javax.validation.constraints.NotNull; import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "diario_obra")
public class DiarioObra implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull(message = "Obra é obrigatória") @ManyToOne @JoinColumn(name = "codigo_obra") private Obra obra;
	@NotNull(message = "Data é obrigatória") private LocalDate data;
	private String entrada1; private String saida1; private String entrada2; private String saida2;
	private String observacao;
	@ManyToOne @JoinColumn(name = "codigo_clima") private DiarioClima clima;
	@ManyToOne @JoinColumn(name = "codigo_area") private DiarioArea area;
	@OneToMany(mappedBy = "diario", cascade = CascadeType.ALL, orphanRemoval = true) private List<DiarioMaoObra> maoObra = new ArrayList<>();
	@OneToMany(mappedBy = "diario", cascade = CascadeType.ALL, orphanRemoval = true) private List<DiarioEquipamento> equipamentos = new ArrayList<>();
	@OneToMany(mappedBy = "diario", cascade = CascadeType.ALL, orphanRemoval = true) private List<DiarioOcorrencia> ocorrencias = new ArrayList<>();
	@OneToMany(mappedBy = "diario", cascade = CascadeType.ALL, orphanRemoval = true) private List<DiarioServico> servicos = new ArrayList<>();
	public Long getCodigo() { return codigo; } public void setCodigo(Long c) { this.codigo = c; }
	public Obra getObra() { return obra; } public void setObra(Obra o) { this.obra = o; }
	public LocalDate getData() { return data; } public void setData(LocalDate d) { this.data = d; }
	public String getEntrada1() { return entrada1; } public void setEntrada1(String s) { this.entrada1 = s; }
	public String getSaida1() { return saida1; } public void setSaida1(String s) { this.saida1 = s; }
	public String getEntrada2() { return entrada2; } public void setEntrada2(String s) { this.entrada2 = s; }
	public String getSaida2() { return saida2; } public void setSaida2(String s) { this.saida2 = s; }
	public String getObservacao() { return observacao; } public void setObservacao(String o) { this.observacao = o; }
	public DiarioClima getClima() { return clima; } public void setClima(DiarioClima c) { this.clima = c; }
	public DiarioArea getArea() { return area; } public void setArea(DiarioArea a) { this.area = a; }
	public List<DiarioMaoObra> getMaoObra() { return maoObra; }
	public List<DiarioEquipamento> getEquipamentos() { return equipamentos; }
	public List<DiarioOcorrencia> getOcorrencias() { return ocorrencias; }
	public List<DiarioServico> getServicos() { return servicos; }
	public boolean isNovo() { return codigo == null; }
	@Override public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }
	@Override public boolean equals(Object o) { if (!(o instanceof DiarioObra)) return false; return codigo != null && codigo.equals(((DiarioObra)o).codigo); }
}
