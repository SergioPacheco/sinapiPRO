package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "venda")
public class Venda implements Serializable {

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

    @ManyToOne
    @JoinColumn(name = "codigo_proposta")
    private Proposta proposta;

    @NotNull
    @Column(name = "data_venda")
    private LocalDate dataVenda;

    @NotNull
    @Column(name = "valor_venda")
    private BigDecimal valorVenda;

    private String situacao = "ATIVA";
    private String observacao;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParcelaVenda> parcelas = new ArrayList<>();

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public UnidadeVenda getUnidade() { return unidade; }
    public void setUnidade(UnidadeVenda unidade) { this.unidade = unidade; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Proposta getProposta() { return proposta; }
    public void setProposta(Proposta proposta) { this.proposta = proposta; }
    public LocalDate getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDate dataVenda) { this.dataVenda = dataVenda; }
    public BigDecimal getValorVenda() { return valorVenda; }
    public void setValorVenda(BigDecimal valorVenda) { this.valorVenda = valorVenda; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<ParcelaVenda> getParcelas() { return parcelas; }
    public boolean isNovo() { return codigo == null; }

    @Override
    public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Venda)) return false;
        return codigo != null && codigo.equals(((Venda) o).codigo);
    }
}
