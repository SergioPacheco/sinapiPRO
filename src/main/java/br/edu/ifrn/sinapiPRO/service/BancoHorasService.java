package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.BancoHoras;
import br.edu.ifrn.sinapiPRO.model.MovimentacaoHora;
import br.edu.ifrn.sinapiPRO.repository.BancoHorasRepository;
import br.edu.ifrn.sinapiPRO.repository.MovimentacoesHoraRepository;

@Service
public class BancoHorasService {

    @Autowired
    private BancoHorasRepository bancoHorasRepository;

    @Autowired
    private MovimentacoesHoraRepository movimentacoesRepository;

    @Transactional
    public MovimentacaoHora registrarMovimentacao(MovimentacaoHora movimentacao) {
        movimentacoesRepository.saveAndFlush(movimentacao);
        recalcularSaldo(movimentacao.getFuncionario().getCodigo(),
                movimentacao.getCompetencia().getCodigo());
        return movimentacao;
    }

    @Transactional
    public void recalcularSaldo(Long codigoFuncionario, Long codigoCompetencia) {
        List<MovimentacaoHora> movimentos = movimentacoesRepository
                .findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataMovimentacaoAsc(
                        codigoFuncionario, codigoCompetencia);

        BigDecimal credito = movimentos.stream()
                .filter(m -> "CREDITO".equals(m.getTipo()) || "EXTRA".equals(m.getTipo()))
                .map(MovimentacaoHora::getHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal debito = movimentos.stream()
                .filter(m -> "DEBITO".equals(m.getTipo()))
                .map(MovimentacaoHora::getHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BancoHoras banco = bancoHorasRepository
                .findByFuncionarioCodigoAndCompetenciaCodigo(codigoFuncionario, codigoCompetencia)
                .orElse(new BancoHoras());

        banco.setHorasCredito(credito);
        banco.setHorasDebito(debito);
        banco.setSaldo(credito.subtract(debito));
        bancoHorasRepository.saveAndFlush(banco);
    }

    @Transactional(readOnly = true)
    public List<BancoHoras> findByCompetencia(Long codigoCompetencia) {
        return bancoHorasRepository.findByCompetenciaCodigoOrderByFuncionarioNomeAsc(codigoCompetencia);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoHora> findMovimentacoes(Long codigoFuncionario, Long codigoCompetencia) {
        return movimentacoesRepository
                .findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataMovimentacaoAsc(
                        codigoFuncionario, codigoCompetencia);
    }
}
