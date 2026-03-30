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

import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.model.UnidadeVenda;
import br.edu.ifrn.sinapiPRO.model.Venda;
import br.edu.ifrn.sinapiPRO.repository.VendasRepository;
import br.edu.ifrn.sinapiPRO.service.VendaService;

@RunWith(MockitoJUnitRunner.class)
public class VendaServiceTeste {

    @Mock
    private VendasRepository repository;

    @InjectMocks
    private VendaService service;

    private Venda criarVenda() {
        Obra obra = new Obra();
        obra.setCodigo(1L);
        obra.setNome("Residencial Teste");

        UnidadeVenda unidade = new UnidadeVenda();
        unidade.setCodigo(1L);
        unidade.setIdentificacao("AP-101");
        unidade.setObra(obra);

        Cliente cliente = new Cliente();
        cliente.setCodigo(1L);
        cliente.setNome("João Silva");

        Venda venda = new Venda();
        venda.setUnidade(unidade);
        venda.setCliente(cliente);
        venda.setDataVenda(LocalDate.now());
        venda.setValorVenda(new BigDecimal("250000.00"));
        venda.setSituacao("ATIVA");
        return venda;
    }

    @Test
    public void should_saveVenda_when_valid() {
        // Arrange
        Venda venda = criarVenda();
        when(repository.saveAndFlush(any(Venda.class))).thenReturn(venda);

        // Act
        Venda resultado = service.salvar(venda);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getValorVenda()).isEqualByComparingTo(new BigDecimal("250000.00"));
        assertThat(resultado.getSituacao()).isEqualTo("ATIVA");
        verify(repository).saveAndFlush(venda);
    }

    @Test
    public void should_setParcelas_when_saving() {
        // Arrange
        Venda venda = criarVenda();
        br.edu.ifrn.sinapiPRO.model.ParcelaVenda parcela = new br.edu.ifrn.sinapiPRO.model.ParcelaVenda();
        parcela.setNumero(1);
        parcela.setValor(new BigDecimal("50000.00"));
        parcela.setDataVencimento(LocalDate.now().plusMonths(1));
        venda.getParcelas().add(parcela);

        when(repository.saveAndFlush(any(Venda.class))).thenReturn(venda);

        // Act
        service.salvar(venda);

        // Assert — parcela deve ter referência à venda após salvar
        assertThat(venda.getParcelas()).hasSize(1);
        assertThat(venda.getParcelas().get(0).getVenda()).isSameAs(venda);
    }

    @Test
    public void should_returnVendasByObra_when_findByObra() {
        // Arrange
        Venda v1 = criarVenda();
        Venda v2 = criarVenda();
        when(repository.findByUnidadeObraCodigoOrderByDataVendaDesc(1L))
                .thenReturn(Arrays.asList(v1, v2));

        // Act
        List<Venda> resultado = service.findByObra(1L);

        // Assert
        assertThat(resultado).hasSize(2);
    }

    @Test
    public void should_returnAll_when_findAll() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(criarVenda(), criarVenda(), criarVenda()));

        // Act
        List<Venda> resultado = service.findAll();

        // Assert
        assertThat(resultado).hasSize(3);
    }
}
