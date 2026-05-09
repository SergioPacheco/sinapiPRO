package com.sinapipro.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tabela_preco_item")
public class TabelaPrecoItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_tabela")
    private TabelaPreco tabela;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_unidade")
    private UnidadeVenda unidade;

    @NotNull
    private BigDecimal valor;

public Long getCodigo() {
	return codigo;
}

public void setCodigo(Long codigo) {
	this.codigo = codigo;
}

public TabelaPreco getTabela() {
	return tabela;
}

public void setTabela(TabelaPreco tabela) {
	this.tabela = tabela;
}

public UnidadeVenda getUnidade() {
	return unidade;
}

public void setUnidade(UnidadeVenda unidade) {
	this.unidade = unidade;
}

public BigDecimal getValor() {
	return valor;
}

public void setValor(BigDecimal valor) {
	this.valor = valor;
}

}
