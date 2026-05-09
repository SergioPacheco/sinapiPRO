package com.sinapipro.persistencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.sinapipro.model.Orcamento;
import com.sinapipro.model.OrcamentoSituacao;
import com.sinapipro.model.TipoOrcamento;
import com.sinapipro.model.Usuario;
import com.sinapipro.repository.EtapasRepository;
import com.sinapipro.repository.OrcamentosRepository;
import com.sinapipro.service.AuditService;
import com.sinapipro.service.OrcamentoService;
import com.sinapipro.service.UsuarioService;

@RunWith(MockitoJUnitRunner.class)
public class OrcamentoServiceTeste {

    @Mock
    private OrcamentosRepository orcamentosRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EtapasRepository etapasRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private OrcamentoService service;

    @Before
    public void setUp() {
        // AuditService é @Autowired field (não construtor) — injetar manualmente
        ReflectionTestUtils.setField(service, "auditService", auditService);
    }

    @Test
    public void should_saveOrcamento_when_validUserExists() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNome("admin");
        usuario.setCodigo(1L);

        Orcamento orcamento = new Orcamento();
        orcamento.setNome("Orçamento Teste");
        orcamento.setUsuario(usuario);
        orcamento.setSituacao(OrcamentoSituacao.ABERTO);
        orcamento.setTipoOrcamento(TipoOrcamento.ESTIMATIVA);

        when(usuarioService.findByNome("admin")).thenReturn(Optional.of(usuario));
        when(orcamentosRepository.save(any(Orcamento.class))).thenReturn(orcamento);

        // Act
        Orcamento resultado = service.salvar(orcamento);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Orçamento Teste");
        verify(orcamentosRepository, atLeastOnce()).save(any(Orcamento.class));
    }

    @Test
    public void should_throwException_when_userNotFound() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNome("inexistente");

        Orcamento orcamento = new Orcamento();
        orcamento.setNome("Orçamento Teste");
        orcamento.setUsuario(usuario);

        when(usuarioService.findByNome("inexistente")).thenReturn(Optional.empty());
        when(orcamentosRepository.save(any(Orcamento.class))).thenReturn(orcamento);

        // Act & Assert
        assertThatThrownBy(() -> service.salvar(orcamento))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("usuario não encontrado");
    }

    @Test
    public void should_returnOrcamento_when_buscarComItens() {
        // Arrange
        Orcamento orcamento = new Orcamento();
        orcamento.setCodigo(1L);
        orcamento.setNome("Orçamento Teste");

        when(orcamentosRepository.buscarComItens(1L)).thenReturn(orcamento);

        // Act
        Orcamento resultado = service.buscarComItens(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigo()).isEqualTo(1L);
    }
}
