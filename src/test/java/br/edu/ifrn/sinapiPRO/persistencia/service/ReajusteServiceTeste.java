package br.edu.ifrn.sinapiPRO.persistencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import br.edu.ifrn.sinapiPRO.model.Especie;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.BasePrecoItemRepository;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.ReajusteService;

@RunWith(MockitoJUnitRunner.class)
public class ReajusteServiceTeste {

    @Mock
    private OrcamentoService orcamentoService;

    @Mock
    private OrcamentosRepository orcamentosRepository;

    @Mock
    private BasePrecosRepository basePrecosRepository;

    @Mock
    private BasePrecoItemRepository basePrecoItemRepository;

    @InjectMocks
    private ReajusteService service;

    private Orcamento criarOrcamentoComItens() {
        Orcamento orcamento = new Orcamento();
        orcamento.setCodigo(1L);

        Item item1 = new Item();
        item1.setCodigo(1L);
        item1.setTipo(Tipo.INSUMO);
        item1.setEspecie(Especie.MATERIAL);
        item1.setValorUnitario(new BigDecimal("100.00"));
        item1.setQuantidade(BigDecimal.ONE);

        Item item2 = new Item();
        item2.setCodigo(2L);
        item2.setTipo(Tipo.INSUMO);
        item2.setEspecie(Especie.MAO_DE_OBRA);
        item2.setValorUnitario(new BigDecimal("200.00"));
        item2.setQuantidade(BigDecimal.ONE);

        orcamento.getItens().add(item1);
        orcamento.getItens().add(item2);
        return orcamento;
    }

    @Test
    public void should_reajustarTodosItens_when_semFiltroEspecie() {
        // Arrange
        Orcamento orcamento = criarOrcamentoComItens();
        when(orcamentoService.buscarComItens(1L)).thenReturn(orcamento);
        when(orcamentosRepository.saveAndFlush(any())).thenReturn(orcamento);

        // Act
        int count = service.reajustarPercentual(1L, new BigDecimal("10"), null);

        // Assert
        assertThat(count).isEqualTo(2);
        assertThat(orcamento.getItens().get(0).getValorUnitario())
                .isEqualByComparingTo(new BigDecimal("110.0000"));
        assertThat(orcamento.getItens().get(1).getValorUnitario())
                .isEqualByComparingTo(new BigDecimal("220.0000"));
    }

    @Test
    public void should_reajustarApenasEspecie_when_filtroEspecieInformado() {
        // Arrange
        Orcamento orcamento = criarOrcamentoComItens();
        when(orcamentoService.buscarComItens(1L)).thenReturn(orcamento);
        when(orcamentosRepository.saveAndFlush(any())).thenReturn(orcamento);

        // Act
        int count = service.reajustarPercentual(1L, new BigDecimal("10"), Especie.MATERIAL);

        // Assert
        assertThat(count).isEqualTo(1);
        // Material reajustado
        assertThat(orcamento.getItens().get(0).getValorUnitario())
                .isEqualByComparingTo(new BigDecimal("110.0000"));
        // Mão de obra não reajustada
        assertThat(orcamento.getItens().get(1).getValorUnitario())
                .isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    public void should_previewReajuste_without_saving() {
        // Arrange
        Orcamento orcamento = criarOrcamentoComItens();
        when(orcamentoService.buscarComItens(1L)).thenReturn(orcamento);

        // Act
        var preview = service.previewReajuste(1L, new BigDecimal("5"), null);

        // Assert
        assertThat(preview).hasSize(2);
        assertThat(preview.get(0).getValorNovo()).isEqualByComparingTo(new BigDecimal("105.0000"));
        // Não deve ter salvo
        verify(orcamentosRepository, never()).saveAndFlush(any());
    }
}
