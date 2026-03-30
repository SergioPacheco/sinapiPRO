package br.edu.ifrn.sinapiPRO.persistencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import br.edu.ifrn.sinapiPRO.model.Atendimento;
import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.model.Notificacao;
import br.edu.ifrn.sinapiPRO.repository.AtendimentosRepository;
import br.edu.ifrn.sinapiPRO.repository.NotificacoesRepository;
import br.edu.ifrn.sinapiPRO.service.AtendimentoSlaService;

@RunWith(MockitoJUnitRunner.class)
public class AtendimentoSlaServiceTeste {

    @Mock
    private AtendimentosRepository atendimentoRepository;

    @Mock
    private NotificacoesRepository notificacaoRepository;

    @InjectMocks
    private AtendimentoSlaService service;

    private Atendimento criarAtendimento(String prioridade, int diasAtras) {
        Cliente cliente = new Cliente();
        cliente.setCodigo(1L);
        cliente.setNome("Cliente Teste");

        Atendimento a = new Atendimento();
        a.setCodigo(1L);
        a.setTitulo("Problema na obra");
        a.setCliente(cliente);
        a.setPrioridade(prioridade);
        a.setSituacao("ABERTO");
        a.setDataAbertura(LocalDate.now().minusDays(diasAtras));
        return a;
    }

    @Test
    public void should_detectSlaViolado_for_URGENTE_after_8h() {
        // Arrange — URGENTE aberto há 2 dias (> 8 horas)
        Atendimento a = criarAtendimento("URGENTE", 2);

        // Act & Assert
        assertThat(service.isSlaViolado(a)).isTrue();
        assertThat(service.calcularHorasRestantes(a)).isNegative();
    }

    @Test
    public void should_notDetectSlaViolado_for_BAIXA_after_1day() {
        // Arrange — BAIXA aberto há 1 dia (SLA = 168h = 7 dias)
        Atendimento a = criarAtendimento("BAIXA", 1);

        // Act & Assert
        assertThat(service.isSlaViolado(a)).isFalse();
        assertThat(service.calcularHorasRestantes(a)).isPositive();
    }

    @Test
    public void should_escalarPrioridade_when_slaVencido() {
        // Arrange — NORMAL aberto há 5 dias (SLA = 72h = 3 dias → vencido)
        Atendimento a = criarAtendimento("NORMAL", 5);
        when(atendimentoRepository.findAll()).thenReturn(Arrays.asList(a));
        when(atendimentoRepository.save(any())).thenReturn(a);
        when(notificacaoRepository.save(any())).thenReturn(new Notificacao());

        // Act
        int count = service.processarEscalacoes();

        // Assert — escalado de NORMAL para ALTA
        assertThat(count).isEqualTo(1);
        assertThat(a.getPrioridade()).isEqualTo("ALTA");
        verify(notificacaoRepository).save(any(Notificacao.class));
    }

    @Test
    public void should_notEscalar_URGENTE_already_max() {
        // Arrange — URGENTE já no máximo
        Atendimento a = criarAtendimento("URGENTE", 5);
        when(atendimentoRepository.findAll()).thenReturn(Arrays.asList(a));

        // Act
        int count = service.processarEscalacoes();

        // Assert — não escala (já é URGENTE)
        assertThat(count).isEqualTo(0);
        assertThat(a.getPrioridade()).isEqualTo("URGENTE");
    }

    @Test
    public void should_encerrarAtendimento_and_generateNotificacao() {
        // Arrange
        Atendimento a = criarAtendimento("NORMAL", 2);
        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(a));
        when(atendimentoRepository.saveAndFlush(any())).thenReturn(a);
        when(notificacaoRepository.saveAndFlush(any())).thenReturn(new Notificacao());

        // Act
        Atendimento resultado = service.encerrar(1L, "Problema resolvido");

        // Assert
        assertThat(resultado.getSituacao()).isEqualTo("ENCERRADO");
        assertThat(resultado.getDataEncerramento()).isEqualTo(LocalDate.now());
        verify(notificacaoRepository).saveAndFlush(any(Notificacao.class));
    }

    @Test
    public void should_findAtendimentosEmRisco() {
        // Arrange — ALTA aberto há 20h (SLA=24h, 20% restante = 4.8h → em risco)
        Atendimento emRisco = criarAtendimento("ALTA", 0);
        emRisco.setDataAbertura(LocalDate.now().minusDays(1)); // ~20h atrás
        Atendimento seguro = criarAtendimento("BAIXA", 1); // 1 dia, SLA=168h, muito tempo restante

        when(atendimentoRepository.findAll()).thenReturn(Arrays.asList(emRisco, seguro));

        // Act
        List<Atendimento> resultado = service.findAtendimentosEmRisco();

        // Assert — apenas o ALTA em risco
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPrioridade()).isEqualTo("ALTA");
    }
}
