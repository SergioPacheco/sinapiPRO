package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.ClienteEndereco; import br.edu.ifrn.sinapiPRO.repository.ClientesEnderecoRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CadastroClienteEnderecoService {
	@Autowired private ClientesEnderecoRepository repository;
	@Transactional public ClienteEndereco salvar(ClienteEndereco e) { return repository.saveAndFlush(e); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar."); } }
	public List<ClienteEndereco> findByCliente(Long codigoCliente) { return repository.findByClienteCodigo(codigoCliente); }
	public ClienteEndereco getOne(Long c) { return repository.getOne(c); }
}
