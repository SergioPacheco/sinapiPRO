package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.NotaFiscalServico;
import br.edu.ifrn.sinapiPRO.repository.NotasFiscaisServicoRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
@Service
public class NotaFiscalServicoService {
@Autowired
private NotasFiscaisServicoRepository repository;
@Transactional
public NotaFiscalServico salvar(NotaFiscalServico nf) {
        if (nf.getValorServicos() != null && nf.getAliquotaIss() != null) {
            BigDecimal iss = nf.getValorServicos().multiply(nf.getAliquotaIss()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            nf.setValorIss(iss);
            nf.setValorLiquido(nf.getValorServicos().subtract(iss));
        }
return repository.saveAndFlush(nf);
}
@Transactional
public void excluir(Long c) {
	try {
		repository.deleteById(c);
		repository.flush();
	} catch (PersistenceException e) {
		throw new ImpossivelExcluirEntidadeException("Impossível apagar a nota fiscal.");
	}
}

public List<NotaFiscalServico> findAll() {
	return repository.findAll();
}

public NotaFiscalServico getOne(Long c) {
	return repository.getOne(c);
}

}
