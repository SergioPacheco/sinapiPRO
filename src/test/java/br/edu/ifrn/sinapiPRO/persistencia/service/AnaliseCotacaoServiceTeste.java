package br.edu.ifrn.sinapiPRO.persistencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import br.edu.ifrn.sinapiPRO.model.Cotacao;
import br.edu.ifrn.sinapiPRO.model.CotacaoFornecedor;
import br.edu.ifrn.sinapiPRO.model.CotacaoItem;
import br.edu.ifrn.sinapiPRO.model.Fornecedor;
import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.model.RespostaCotacao;
import br.edu.ifrn.sinapiPRO.repository.CotacoesRepository;
import br.edu.ifrn.sinapiPRO.repository.PedidosCompraRepository;
import br.edu.ifrn.sinapiPRO.service.AnaliseCotacaoService;

@RunWith(MockitoJUnitRunner.class)
public class AnaliseCotacaoServiceTeste {

    @Mock
    private CotacoesRepository cotacaoRepository;

    @Mock
    private PedidosCompraRepository pedidoRepository;

    @InjectMocks
    private AnaliseCotacaoService service;

    private Cotacao criarCotacaoComRespostas() {
        Obra obra = new Obra();
        obra.setCodigo(1L);
        obra.setNome("Obra Teste");

        Fornecedor f1 = new Fornecedor();
        f1.setCodigo(1L);
        f1.setNome("Fornecedor A");

        Fornecedor f2 = new Fornecedor();
        f2.setCodigo(2L);
        f2.setNome("Fornecedor B");

        CotacaoFornecedor cf1 = new CotacaoFornecedor();
        cf1.setCodigo(1L);
        cf1.setFornecedor(f1);

        CotacaoFornecedor cf2 = new CotacaoFornecedor();
        cf2.setCodigo(2L);
        cf2.setFornecedor(f2);

        CotacaoItem item = new CotacaoItem();
        item.setCodigo(1L);
        item.setDescricao("Cimento CP-II");
        item.setUnidade("sc");
        item.setQuantidade(new BigDecimal("100"));

        RespostaCotacao r1 = new RespostaCotacao();
        r1.setCodigo(1L);
        r1.setCotacaoItem(item);
        r1.setCotacaoFornecedor(cf1);
        r1.setValorUnitario(new BigDecimal("28.50"));
        r1.setPrazoEntrega(5);

        RespostaCotacao r2 = new RespostaCotacao();
        r2.setCodigo(2L);
        r2.setCotacaoItem(item);
        r2.setCotacaoFornecedor(cf2);
        r2.setValorUnitario(new BigDecimal("31.00"));
        r2.setPrazoEntrega(3);

        item.getRespostas().add(r1);
        item.getRespostas().add(r2);

        Cotacao cotacao = new Cotacao();
        cotacao.setCodigo(1L);
        cotacao.setObra(obra);
        cotacao.setNumero(1);
        cotacao.getItens().add(item);
        cotacao.getFornecedores().add(cf1);
        cotacao.getFornecedores().add(cf2);
        cf1.setCotacao(cotacao);
        cf2.setCotacao(cotacao);

        return cotacao;
    }

    @Test
    public void should_returnAnalise_with_menorPreco_first() {
        // Arrange
        Cotacao cotacao = criarCotacaoComRespostas();
        when(cotacaoRepository.findById(1L)).thenReturn(Optional.of(cotacao));

        // Act
        List<AnaliseCotacaoService.ItemAnalise> analise = service.analisarCotacao(1L);

        // Assert
        assertThat(analise).hasSize(1);
        AnaliseCotacaoService.ItemAnalise ia = analise.get(0);
        assertThat(ia.getMenorPreco()).isEqualByComparingTo(new BigDecimal("28.50"));
        assertThat(ia.getFornecedorMenorPreco()).isEqualTo("Fornecedor A");
        // Respostas ordenadas por preço (menor primeiro)
        assertThat(ia.getRespostas().get(0).getValorUnitario())
                .isEqualByComparingTo(new BigDecimal("28.50"));
    }

    @Test
    public void should_calcularEconomia_between_maior_and_menor() {
        // Arrange
        Cotacao cotacao = criarCotacaoComRespostas();
        when(cotacaoRepository.findById(1L)).thenReturn(Optional.of(cotacao));

        // Act
        List<AnaliseCotacaoService.ItemAnalise> analise = service.analisarCotacao(1L);

        // Assert — economia = 31.00 - 28.50 = 2.50
        assertThat(analise.get(0).getEconomia()).isEqualByComparingTo(new BigDecimal("2.50"));
    }

    @Test
    public void should_selecionarMenorPreco_automatically() {
        // Arrange
        Cotacao cotacao = criarCotacaoComRespostas();
        when(cotacaoRepository.findById(1L)).thenReturn(Optional.of(cotacao));
        when(cotacaoRepository.saveAndFlush(any())).thenReturn(cotacao);

        // Act
        int count = service.selecionarMenorPrecoAutomatico(1L);

        // Assert
        assertThat(count).isEqualTo(1);
        // Resposta de menor preço deve estar selecionada
        RespostaCotacao menorPreco = cotacao.getItens().get(0).getRespostas().stream()
                .filter(RespostaCotacao::isSelecionado)
                .findFirst()
                .orElse(null);
        assertThat(menorPreco).isNotNull();
        assertThat(menorPreco.getValorUnitario()).isEqualByComparingTo(new BigDecimal("28.50"));
    }

    @Test
    public void should_gerarPedidos_grouped_by_fornecedor() {
        // Arrange
        Cotacao cotacao = criarCotacaoComRespostas();
        // Seleciona a resposta do Fornecedor A
        cotacao.getItens().get(0).getRespostas().get(0).setSelecionado(true);
        when(cotacaoRepository.findById(1L)).thenReturn(Optional.of(cotacao));
        when(cotacaoRepository.saveAndFlush(any())).thenReturn(cotacao);
        when(pedidoRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var pedidos = service.gerarPedidos(1L);

        // Assert — 1 pedido para o Fornecedor A
        assertThat(pedidos).hasSize(1);
        assertThat(pedidos.get(0).getFornecedor().getNome()).isEqualTo("Fornecedor A");
        assertThat(pedidos.get(0).getItens()).hasSize(1);
        assertThat(pedidos.get(0).getValorTotal())
                .isEqualByComparingTo(new BigDecimal("2850.00")); // 100 × 28.50
    }

    @Test
    public void should_throwException_when_noRespostaSelecionada() {
        // Arrange
        Cotacao cotacao = criarCotacaoComRespostas();
        // Nenhuma resposta selecionada
        when(cotacaoRepository.findById(1L)).thenReturn(Optional.of(cotacao));

        // Act & Assert
        assertThatThrownBy(() -> service.gerarPedidos(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Nenhuma resposta selecionada");
    }
}
