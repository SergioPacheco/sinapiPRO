package br.edu.ifrn.sinapiPRO.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "banco_horas")
public class BancoHoras implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long codigo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_funcionario")
    private Funcionario funcionario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_competencia")
    private Competencia competencia;

    @Column(name = "horas_credito")
    private BigDecimal horasCredito = BigDecimal.ZERO;

    @Column(name = "horas_debito")
    private BigDecimal horasDebito = BigDecimal.ZERO;

    private BigDecimal saldo = BigDecimal.ZERO;

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public Competencia getCompetencia() { return competencia; }
    public void setCompetencia(Competencia competencia) { this.competencia = competencia; }
    public BigDecimal getHorasCredito() { return horasCredito; }
    public void setHorasCredito(BigDecimal horasCredito) { this.horasCredito = horasCredito; }
    public BigDecimal getHorasDebito() { return horasDebito; }
    public void setHorasDebito(BigDecimal horasDebito) { this.horasDebito = horasDebito; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
