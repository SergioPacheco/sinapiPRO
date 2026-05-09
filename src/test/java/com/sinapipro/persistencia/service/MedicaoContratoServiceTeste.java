package com.sinapipro.persistencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sinapipro.model.Contrato;
import com.sinapipro.model.ContratoItem;
import com.sinapipro.model.Medicao;
import com.sinapipro.model.MedicaoItem;
import com.sinapipro.model.Obra;
import com.sinapipro.repository.ContratosRepository;
import com.sinapipro.repository.DespesasRepository;
import com.sinapipro.repository.MedicoesRepository;
import com.sinapipro.service.MedicaoContratoService;

@RunWith(MockitoJUnitRunner.class)
public class MedicaoContratoServiceTeste {

    @Mock
    private MedicoesRepository medicaoRepository;

    @Mock
    private ContratosRepository contratoRepository;

    @Mock
    private DespesasRepository despesaRepository;

    @InjectMocks
    private MedicaoContratoService service;

    private Medicao criarMedicao() {
        Obra obra = new Obra();
        obra.setCodigo(1L);
        obra.setNome("Obra Teste");

        Contrato contrato = new Contrato();
        contrato.setCodigo(1L);
        contrato.setDescricao("Contrato de Alvenaria");
        contrato.setNumero("001/2026");
        contrato.setObra(obra);
        contrato.setValorTotal(new BigDecimal("100000.00"));

        ContratoItem ci = new ContratoItem();
        ci.setCodigo(1L);
        ci.setDescricao("Alvenaria de tijolo");
        ci.setUnidade("m²");
        ci.setQuantidade(new BigDecimal("500"));
        ci.setValorUnitario(new BigDecimal("200.00"));
        ci.setValorTotal(new BigDecimal("100000.00"));
        ci.setContrato(contrato);
        contrato.getItens().add(ci);

        MedicaoItem mi = new MedicaoItem();
        mi.setCodigo(1L);
        mi.setContratoItem(ci);
        mi.setQuantidadeMedida(new BigDecimal("100")); // 20% do total

        Medicao medicao = new Medicao();
        medicao.setCodigo(1L);
        medicao.setContrato(contrato);
        medicao.setNumero(1);
        medicao.setDataMedicao(LocalDate.now());
        medicao.setSituacao("ABERTA");
        medicao.getItens().add(mi);
        mi.setMedicao(medicao);

        return medicao;
    }

    @Test
    public void should_calcularValorMedido_correctly() {
        // Arrange
        Medicao medicao = criarMedicao();
        when(medicaoRepository.saveAndFlush(any())).thenReturn(medicao);
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(medicao.getContrato()));

        // Act
        Medicao resultado = service.calcularMedicao(medicao);

        // Assert — 100 m² × R$ 200,00 = R$ 20.000,00
        assertThat(resultado.getValorMedido()).isEqualByComparingTo(new BigDecimal("20000.00"));
    }

    @Test
    public void should_calcularPercentualExecutado_correctly() {
        // Arrange
        Medicao medicao = criarMedicao();
        when(medicaoRepository.saveAndFlush(any())).thenReturn(medicao);
        when(contratoRepository.findById(1L)).thenReturn(Optional.of(medicao.getContrato()));

        // Act
        service.calcularMedicao(medicao);

        // Assert — 100/500 = 20%
        assertThat(medicao.getItens().get(0).getPercentualExecutado())
                .isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    public void should_aprovarMedicao_and_generateDespesa() {
        // Arrange
        Medicao medicao = criarMedicao();
        medicao.setValorMedido(new BigDecimal("20000.00"));
        when(medicaoRepository.findById(1L)).thenReturn(Optional.of(medicao));
        when(medicaoRepository.saveAndFlush(any())).thenReturn(medicao);
        when(despesaRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var despesa = service.aprovarMedicao(1L, new BigDecimal("5"));

        // Assert
        assertThat(medicao.getSituacao()).isEqualTo("APROVADA");
        // Valor líquido = 20000 - 5% = 20000 - 1000 = 19000
        assertThat(despesa.getValor()).isEqualByComparingTo(new BigDecimal("19000.00"));
        assertThat(despesa.getDataVencimento()).isEqualTo(medicao.getDataMedicao().plusDays(30));
    }

    @Test
    public void should_throwException_when_aprovando_medicao_nao_aberta() {
        // Arrange
        Medicao medicao = criarMedicao();
        medicao.setSituacao("APROVADA");
        when(medicaoRepository.findById(1L)).thenReturn(Optional.of(medicao));

        // Act & Assert
        assertThatThrownBy(() -> service.aprovarMedicao(1L, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("APROVADA");
    }

    @Test
    public void should_useDefaultRetencao_when_null() {
        // Arrange
        Medicao medicao = criarMedicao();
        medicao.setValorMedido(new BigDecimal("10000.00"));
        when(medicaoRepository.findById(1L)).thenReturn(Optional.of(medicao));
        when(medicaoRepository.saveAndFlush(any())).thenReturn(medicao);
        when(despesaRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act — null usa padrão 5%
        var despesa = service.aprovarMedicao(1L, null);

        // Assert — 10000 - 5% = 9500
        assertThat(despesa.getValor()).isEqualByComparingTo(new BigDecimal("9500.00"));
    }
}
