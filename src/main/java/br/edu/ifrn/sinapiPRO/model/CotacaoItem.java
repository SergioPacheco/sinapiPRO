package br.edu.ifrn.sinapiPRO.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;
@Entity @Table(name = "cotacao_item")
public class CotacaoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native") @GenericGenerator(name = "native", strategy = "native") private Long codigo;
	@ManyToOne(optional = false) @JoinColumn(name = "codigo_cotacao") private Cotacao cotacao;
	@ManyToOne @JoinColumn(name = "codigo_insumo") private Insumo insumo;
	@NotBlank(message = "Descrição é obrigatória") private String descricao;
	private String unidade;
	private BigDecimal quantidade = BigDecimal.ZERO;
	@OneToMany(mappedBy = "cotacaoItem", cascade = CascadeType.ALL, orphanRemoval = true) private List<RespostaCotacao> respostas = new ArrayList<>();
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Cotacao getCotacao() {
		return cotacao;
	}

	public void setCotacao(Cotacao cotacao) {
		this.cotacao = cotacao;
	}

	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public List<RespostaCotacao> getRespostas() {
		return respostas;
	}
}
