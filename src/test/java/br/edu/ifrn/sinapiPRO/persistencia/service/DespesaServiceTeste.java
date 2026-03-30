package br.edu.ifrn.sinapiPRO.persistencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import br.edu.ifrn.sinapiPRO.model.Despesa;
import br.edu.ifrn.sinapiPRO.model.PagamentoDespesa;
import br.edu.ifrn.sinapiPRO.repository.DespesasRepository;
import br.edu.ifrn.sinapiPRO.service.DespesaService;

@RunWith(MockitoJUnitRunner.class)
public class DespesaServiceTeste {

    @Mock
    private DespesasRepository repository;

    @InjectMocks
    private DespesaService service;

    @Test
    public void should_setSituacaoPaga_when_totalPagoEqualsValor() {
        // Arrange
        Despesa despesa = new Despesa();
        despesa.setDescricao("Aluguel");
        despesa.setValor(new BigDecimal("1000.00"));
        despesa.setDataVencimento(LocalDate.now());

        PagamentoDespesa pagamento = new PagamentoDespesa();
        pagamento.setValorPago(new BigDecimal("1000.00"));
        pagamento.setDataPagamento(LocalDate.now());
        despesa.getPagamentos().add(pagamento);

        when(repository.saveAndFlush(any(Despesa.class))).thenReturn(despesa);

        // Act
        Despesa resultado = service.salvar(despesa);

        // Assert
        assertThat(resultado.getSituacao()).isEqualTo("PAGA");
    }

    @Test
    public void should_setSituacaoParcial_when_partialPayment() {
        // Arrange
        Despesa despesa = new Despesa();
        despesa.setDescricao("Fornecedor");
        despesa.setValor(new BigDecimal("2000.00"));
        despesa.setDataVencimento(LocalDate.now());

        PagamentoDespesa pagamento = new PagamentoDespesa();
        pagamento.setValorPago(new BigDecimal("500.00"));
        pagamento.setDataPagamento(LocalDate.now());
        despesa.getPagamentos().add(pagamento);

        when(repository.saveAndFlush(any(Despesa.class))).thenReturn(despesa);

        // Act
        Despesa resultado = service.salvar(despesa);

        // Assert
        assertThat(resultado.getSituacao()).isEqualTo("PARCIAL");
    }

    @Test
    public void should_keepSituacaoAberta_when_noPagamentos() {
        // Arrange
        Despesa despesa = new Despesa();
        despesa.setDescricao("Energia");
        despesa.setValor(new BigDecimal("300.00"));
        despesa.setDataVencimento(LocalDate.now());
        despesa.setSituacao("ABERTA");

        when(repository.saveAndFlush(any(Despesa.class))).thenReturn(despesa);

        // Act
        Despesa resultado = service.salvar(despesa);

        // Assert
        assertThat(resultado.getSituacao()).isEqualTo("ABERTA");
    }

    @Test
    public void should_returnAbertas_when_findAbertas() {
        // Arrange
        Despesa d1 = new Despesa();
        d1.setSituacao("ABERTA");
        Despesa d2 = new Despesa();
        d2.setSituacao("ABERTA");

        when(repository.findBySituacaoOrderByDataVencimentoAsc("ABERTA"))
                .thenReturn(Arrays.asList(d1, d2));

        // Act
        List<Despesa> resultado = service.findAbertas();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(d -> "ABERTA".equals(d.getSituacao()));
    }
}
