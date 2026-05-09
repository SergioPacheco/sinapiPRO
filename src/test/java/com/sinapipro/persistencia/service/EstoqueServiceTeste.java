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

import com.sinapipro.model.Estoque;
import com.sinapipro.model.Insumo;
import com.sinapipro.model.MovimentoEstoque;
import com.sinapipro.model.Obra;
import com.sinapipro.repository.EstoqueRepository;
import com.sinapipro.service.EstoqueService;

@RunWith(MockitoJUnitRunner.class)
public class EstoqueServiceTeste {

    @Mock
    private EstoqueRepository repository;

    @InjectMocks
    private EstoqueService service;

    private Estoque criarEstoque(BigDecimal qtdAtual, BigDecimal custoMedio) {
        Obra obra = new Obra();
        obra.setCodigo(1L);

        Insumo insumo = new Insumo();
        insumo.setCodigo(1L);
        insumo.setDescricao("Cimento");

        Estoque estoque = new Estoque();
        estoque.setCodigo(1L);
        estoque.setObra(obra);
        estoque.setInsumo(insumo);
        estoque.setQuantidadeAtual(qtdAtual);
        estoque.setQuantidadeMinima(new BigDecimal("10"));
        estoque.setCustoMedio(custoMedio);
        return estoque;
    }

    @Test
    public void should_calcularCustoMedioPonderado_on_entrada() {
        // Arrange — estoque atual: 100 un @ R$10,00
        Estoque estoque = criarEstoque(new BigDecimal("100"), new BigDecimal("10.00"));
        when(repository.findById(1L)).thenReturn(Optional.of(estoque));
        when(repository.saveAndFlush(any())).thenReturn(estoque);

        MovimentoEstoque entrada = new MovimentoEstoque();
        entrada.setTipo("ENTRADA");
        entrada.setQuantidade(new BigDecimal("50"));
        entrada.setDataMovimento(LocalDate.now());

        // Act — entrada de 50 un @ R$12,00
        service.movimentar(1L, entrada, new BigDecimal("12.00"));

        // Assert — CMP = (100×10 + 50×12) / 150 = (1000+600)/150 = 10.6667
        assertThat(estoque.getQuantidadeAtual()).isEqualByComparingTo(new BigDecimal("150"));
        assertThat(estoque.getCustoMedio()).isEqualByComparingTo(new BigDecimal("10.6667"));
    }

    @Test
    public void should_notChangeCustoMedio_on_saida() {
        // Arrange
        Estoque estoque = criarEstoque(new BigDecimal("100"), new BigDecimal("10.00"));
        when(repository.findById(1L)).thenReturn(Optional.of(estoque));
        when(repository.saveAndFlush(any())).thenReturn(estoque);

        MovimentoEstoque saida = new MovimentoEstoque();
        saida.setTipo("SAIDA");
        saida.setQuantidade(new BigDecimal("30"));
        saida.setDataMovimento(LocalDate.now());

        // Act
        service.movimentar(1L, saida, null);

        // Assert — custo médio não muda na saída
        assertThat(estoque.getQuantidadeAtual()).isEqualByComparingTo(new BigDecimal("70"));
        assertThat(estoque.getCustoMedio()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    public void should_throwException_when_saldoInsuficiente() {
        // Arrange
        Estoque estoque = criarEstoque(new BigDecimal("20"), new BigDecimal("10.00"));
        when(repository.findById(1L)).thenReturn(Optional.of(estoque));

        MovimentoEstoque saida = new MovimentoEstoque();
        saida.setTipo("SAIDA");
        saida.setQuantidade(new BigDecimal("50")); // mais do que disponível
        saida.setDataMovimento(LocalDate.now());

        // Act & Assert
        assertThatThrownBy(() -> service.movimentar(1L, saida, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    public void should_throwException_when_entrada_sem_custo() {
        // Arrange
        Estoque estoque = criarEstoque(BigDecimal.ZERO, BigDecimal.ZERO);
        when(repository.findById(1L)).thenReturn(Optional.of(estoque));

        MovimentoEstoque entrada = new MovimentoEstoque();
        entrada.setTipo("ENTRADA");
        entrada.setQuantidade(new BigDecimal("10"));
        entrada.setDataMovimento(LocalDate.now());

        // Act & Assert — custo obrigatório para entrada
        assertThatThrownBy(() -> service.movimentar(1L, entrada, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Custo unitário é obrigatório");
    }

    @Test
    public void should_ajustarQuantidade_directly() {
        // Arrange
        Estoque estoque = criarEstoque(new BigDecimal("100"), new BigDecimal("10.00"));
        when(repository.findById(1L)).thenReturn(Optional.of(estoque));
        when(repository.saveAndFlush(any())).thenReturn(estoque);

        MovimentoEstoque ajuste = new MovimentoEstoque();
        ajuste.setTipo("AJUSTE");
        ajuste.setQuantidade(new BigDecimal("85")); // inventário físico
        ajuste.setDataMovimento(LocalDate.now());

        // Act
        service.movimentar(1L, ajuste, null);

        // Assert — quantidade ajustada diretamente, custo médio mantido
        assertThat(estoque.getQuantidadeAtual()).isEqualByComparingTo(new BigDecimal("85"));
        assertThat(estoque.getCustoMedio()).isEqualByComparingTo(new BigDecimal("10.00"));
    }
}
