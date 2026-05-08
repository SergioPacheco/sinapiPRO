package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.AgendamentoManutencao;
import br.edu.ifrn.sinapiPRO.model.Veiculo;
import br.edu.ifrn.sinapiPRO.repository.AgendamentosManutencaoRepository;
import br.edu.ifrn.sinapiPRO.repository.VeiculosRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class FrotaService extends AbstractSimpleCrudService<Veiculo, VeiculosRepository> {

	private final VeiculosRepository veiculosRepository;
	private final AgendamentosManutencaoRepository agendamentosRepository;

	public FrotaService(VeiculosRepository veiculosRepository, AgendamentosManutencaoRepository agendamentosRepository) {
		super(veiculosRepository, "Impossível apagar o veículo.", "Veículo não encontrado.");
		this.veiculosRepository = veiculosRepository;
		this.agendamentosRepository = agendamentosRepository;
	}

	@Transactional
	public Veiculo salvarVeiculo(Veiculo veiculo) {
		return salvar(veiculo);
	}

	@Transactional
	public AgendamentoManutencao salvarAgendamento(AgendamentoManutencao agendamento) {
		return agendamentosRepository.saveAndFlush(agendamento);
	}

	@Transactional(readOnly = true)
	public List<Veiculo> findVeiculos() {
		return veiculosRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<AgendamentoManutencao> findAgendamentos(Long codigoVeiculo) {
		return agendamentosRepository.findByVeiculoCodigoOrderByDataAgendamentoDesc(codigoVeiculo);
	}

	@Transactional(readOnly = true)
	public Veiculo getVeiculo(Long codigo) {
		return buscarPorCodigo(codigo);
	}

	@Transactional(readOnly = true)
	public AgendamentoManutencao getAgendamento(Long codigo) {
		return agendamentosRepository.findById(codigo).orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
	}
}
