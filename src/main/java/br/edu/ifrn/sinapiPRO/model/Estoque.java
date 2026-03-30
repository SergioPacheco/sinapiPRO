package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "estoque")
public class Estoque implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_obra") private Obra obra;
	@NotNull @ManyToOne @JoinColumn(name = "codigo_insumo") private Insumo insumo;
	@Column(name = "quantidade_atual") private BigDecimal quantidadeAtual = BigDecimal.ZERO;
	@Column(name = "quantidade_minima") private BigDecimal quantidadeMinima = BigDecimal.ZERO;
	private String localizacao;
	@OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true) private List<MovimentoEstoque> movimentos = new ArrayList<>();
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Obra getObra() {
		return obra;
	}

	public void setObra(Obra obra) {
		this.obra = obra;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}

	public BigDecimal getQuantidadeAtual() {
		return quantidadeAtual;
	}

	public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
		this.quantidadeAtual = quantidadeAtual;
	}

	public BigDecimal getQuantidadeMinima() {
		return quantidadeMinima;
	}

	public void setQuantidadeMinima(BigDecimal quantidadeMinima) {
		this.quantidadeMinima = quantidadeMinima;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public List<MovimentoEstoque> getMovimentos() {
		return movimentos;
	}

	public boolean isNovo() {
		return codigo == null;
	}

	@Override
	public int hashCode() {
		return codigo == null ? 0 : codigo.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Estoque)) return false;
		return codigo != null && codigo.equals(((Estoque)o).codigo);
	}
}
