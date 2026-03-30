package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tabela_preco")
public class TabelaPreco implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_obra")
    private Obra obra;

    @Column(name = "data_vigencia")
    private LocalDate dataVigencia;

    private boolean ativa = true;

    @OneToMany(mappedBy = "tabela", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TabelaPrecoItem> itens = new ArrayList<>();

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }
    public LocalDate getDataVigencia() { return dataVigencia; }
    public void setDataVigencia(LocalDate dataVigencia) { this.dataVigencia = dataVigencia; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public List<TabelaPrecoItem> getItens() { return itens; }
    public boolean isNovo() { return codigo == null; }

    @Override
    public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TabelaPreco)) return false;
        return codigo != null && codigo.equals(((TabelaPreco) o).codigo);
    }
}
