package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "nota_fiscal_servico")
public class NotaFiscalServico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    private String numero;
    private String serie;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "codigo_obra")
    private Obra obra;

    @NotNull
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "valor_servicos")
    private BigDecimal valorServicos = BigDecimal.ZERO;

    @Column(name = "aliquota_iss")
    private BigDecimal aliquotaIss = BigDecimal.ZERO;

    @Column(name = "valor_iss")
    private BigDecimal valorIss = BigDecimal.ZERO;

    @Column(name = "valor_liquido")
    private BigDecimal valorLiquido = BigDecimal.ZERO;

    private String discriminacao;
    private String situacao = "EMITIDA";

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }
    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
    public BigDecimal getValorServicos() { return valorServicos; }
    public void setValorServicos(BigDecimal valorServicos) { this.valorServicos = valorServicos; }
    public BigDecimal getAliquotaIss() { return aliquotaIss; }
    public void setAliquotaIss(BigDecimal aliquotaIss) { this.aliquotaIss = aliquotaIss; }
    public BigDecimal getValorIss() { return valorIss; }
    public void setValorIss(BigDecimal valorIss) { this.valorIss = valorIss; }
    public BigDecimal getValorLiquido() { return valorLiquido; }
    public void setValorLiquido(BigDecimal valorLiquido) { this.valorLiquido = valorLiquido; }
    public String getDiscriminacao() { return discriminacao; }
    public void setDiscriminacao(String discriminacao) { this.discriminacao = discriminacao; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public boolean isNovo() { return codigo == null; }
}
