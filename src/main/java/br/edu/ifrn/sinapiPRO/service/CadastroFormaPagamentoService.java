package br.edu.ifrn.sinapiPRO.service;
import java.util.*;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.FormaPagamento;
import br.edu.ifrn.sinapiPRO.repository.FormasPagamentoRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FormaPagamentoFilter;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroFormaPagamentoService {
	@Autowired
	private FormasPagamentoRepository repository;
	@Transactional
	public FormaPagamento salvar(FormaPagamento fp) {
		Optional<FormaPagamento> e = repository.findByNomeIgnoreCase(fp.getNome());
		if (e.isPresent() && !e.get().getCodigo().equals(fp.getCodigo())) throw new JaCadastradoException("Forma de pagamento já cadastrada");
		return repository.saveAndFlush(fp);
	}
	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<FormaPagamento> filtrar(FormaPagamentoFilter f, Pageable p) {
		return repository.filtrar(f, p);
	}

	public List<FormaPagamento> findAll() {
		return repository.findAll();
	}

	public FormaPagamento getOne(Long c) {
		return repository.getOne(c);
	}
}
