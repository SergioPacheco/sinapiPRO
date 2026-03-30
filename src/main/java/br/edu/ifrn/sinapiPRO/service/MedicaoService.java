package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Medicao;
import br.edu.ifrn.sinapiPRO.repository.MedicoesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class MedicaoService {
	@Autowired
	private MedicoesRepository repository;

	@Autowired
	private ValidacaoNegocioService validacao;

	@Transactional
	public Medicao salvar(Medicao m) {
		// Validações de negócio
		if (m.isNovo()) {
			validacao.validarContratoParaMedicao(m.getContrato().getCodigo());
		}
		validacao.validarNumeromedicaoUnico(m.getContrato().getCodigo(), m.getNumero(), m.getCodigo());

		BigDecimal total = m.getItens().stream()
			.map(i -> { i.setMedicao(m); return i.getValorMedido() != null ? i.getValorMedido() : BigDecimal.ZERO; })
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		m.setValorMedido(total);
		return repository.saveAndFlush(m);
	}
	@Transactional
	public void excluir(Long codigo) {
		try {
			repository.deleteById(codigo);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar a medição.");
		}
	}

	@Transactional(readOnly = true)
	public List<Medicao> findByContrato(Long codigoContrato) {
		return repository.findByContratoCodigoOrderByNumeroAsc(codigoContrato);
	}

	@Transactional(readOnly = true)
	public Medicao buscarComItens(Long codigo) {
		return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Medição não encontrada"));
	}
}
