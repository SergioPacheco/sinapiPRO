package com.sinapipro.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "situacao_unidade")
public class SituacaoUnidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String cor;

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public boolean isNovo() {
        return codigo == null;
    }

    @Override
    public int hashCode() {
        return codigo == null ? 0 : codigo.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SituacaoUnidade)) return false;
        SituacaoUnidade other = (SituacaoUnidade) o;
        return codigo != null && codigo.equals(other.codigo);
    }
}
