package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "caracteristica_unidade")
public class CaracteristicaUnidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_unidade")
    private UnidadeVenda unidade;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private String valor;

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public UnidadeVenda getUnidade() { return unidade; }
    public void setUnidade(UnidadeVenda unidade) { this.unidade = unidade; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}
