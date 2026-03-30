package br.edu.ifrn.sinapiPRO.service;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.ClienteReferencia;
import br.edu.ifrn.sinapiPRO.repository.ClientesReferenciaRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroClienteReferenciaService {
	@Autowired
	private ClientesReferenciaRepository repository;
	@Transactional
	public ClienteReferencia salvar(ClienteReferencia r) {
		return repository.saveAndFlush(r);
	}

	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar.");
		}
	}

	public List<ClienteReferencia> findByCliente(Long codigoCliente) {
		return repository.findByClienteCodigo(codigoCliente);
	}

	public ClienteReferencia getOne(Long c) {
		return repository.getOne(c);
	}
}
