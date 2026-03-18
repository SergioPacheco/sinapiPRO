package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "fornecedor_insumo")
public class FornecedorInsumo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long codigo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_fornecedor", nullable = false)
	private Fornecedor fornecedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_insumo", nullable = false)
	private Insumo insumo;

	@Column(precision = 15, scale = 2)
	private BigDecimal preco;

	@Column(name = "data_cotacao")
	private LocalDate dataCotacao;

	public Long getCodigo() { return codigo; }
	public void setCodigo(Long codigo) { this.codigo = codigo; }
	public Fornecedor getFornecedor() { return fornecedor; }
	public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
	public Insumo getInsumo() { return insumo; }
	public void setInsumo(Insumo insumo) { this.insumo = insumo; }
	public BigDecimal getPreco() { return preco; }
	public void setPreco(BigDecimal preco) { this.preco = preco; }
	public LocalDate getDataCotacao() { return dataCotacao; }
	public void setDataCotacao(LocalDate dataCotacao) { this.dataCotacao = dataCotacao; }
}
