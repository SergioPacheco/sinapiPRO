package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "proposta")
public class Proposta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_unidade")
    private UnidadeVenda unidade;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_cliente")
    private Cliente cliente;

    @NotNull
    @Column(name = "data_proposta")
    private LocalDate dataProposta;

    @NotNull
    @Column(name = "valor_proposto")
    private BigDecimal valorProposto;

    private String situacao = "PENDENTE";
    private String observacao;

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public UnidadeVenda getUnidade() { return unidade; }
    public void setUnidade(UnidadeVenda unidade) { this.unidade = unidade; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public LocalDate getDataProposta() { return dataProposta; }
    public void setDataProposta(LocalDate dataProposta) { this.dataProposta = dataProposta; }
    public BigDecimal getValorProposto() { return valorProposto; }
    public void setValorProposto(BigDecimal valorProposto) { this.valorProposto = valorProposto; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public boolean isNovo() { return codigo == null; }

    @Override
    public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Proposta)) return false;
        return codigo != null && codigo.equals(((Proposta) o).codigo);
    }
}
