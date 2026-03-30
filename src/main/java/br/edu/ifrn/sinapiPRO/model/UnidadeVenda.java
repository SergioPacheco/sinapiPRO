package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "unidade_venda")
public class UnidadeVenda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull(message = "Obra é obrigatória")
    @ManyToOne
    @JoinColumn(name = "codigo_obra")
    private Obra obra;

    @NotBlank(message = "Identificação é obrigatória")
    private String identificacao;

    private String tipo;
    private String bloco;
    private String andar;

    @Column(name = "area_privativa")
    private BigDecimal areaPrivativa;

    @Column(name = "area_total")
    private BigDecimal areaTotal;

    @Column(name = "valor_base")
    private BigDecimal valorBase = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "codigo_situacao")
    private SituacaoUnidade situacao;

    private String descricao;

    @OneToMany(mappedBy = "unidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaracteristicaUnidade> caracteristicas = new ArrayList<>();

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }
    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getBloco() { return bloco; }
    public void setBloco(String bloco) { this.bloco = bloco; }
    public String getAndar() { return andar; }
    public void setAndar(String andar) { this.andar = andar; }
    public BigDecimal getAreaPrivativa() { return areaPrivativa; }
    public void setAreaPrivativa(BigDecimal areaPrivativa) { this.areaPrivativa = areaPrivativa; }
    public BigDecimal getAreaTotal() { return areaTotal; }
    public void setAreaTotal(BigDecimal areaTotal) { this.areaTotal = areaTotal; }
    public BigDecimal getValorBase() { return valorBase; }
    public void setValorBase(BigDecimal valorBase) { this.valorBase = valorBase; }
    public SituacaoUnidade getSituacao() { return situacao; }
    public void setSituacao(SituacaoUnidade situacao) { this.situacao = situacao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public List<CaracteristicaUnidade> getCaracteristicas() { return caracteristicas; }
    public boolean isNovo() { return codigo == null; }

    @Override
    public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UnidadeVenda)) return false;
        UnidadeVenda other = (UnidadeVenda) o;
        return codigo != null && codigo.equals(other.codigo);
    }
}
