package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "competencia")
public class Competencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    private Integer mes;

    @NotNull
    private Integer ano;

    private String descricao;
    private boolean encerrada = false;

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isEncerrada() { return encerrada; }
    public void setEncerrada(boolean encerrada) { this.encerrada = encerrada; }
    public boolean isNovo() { return codigo == null; }

    public String getLabel() {
        return String.format("%02d/%d", mes, ano);
    }

    @Override
    public int hashCode() { return codigo == null ? 0 : codigo.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Competencia)) return false;
        return codigo != null && codigo.equals(((Competencia) o).codigo);
    }
}
