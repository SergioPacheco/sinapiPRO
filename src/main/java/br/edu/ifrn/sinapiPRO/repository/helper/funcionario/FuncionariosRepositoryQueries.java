package br.edu.ifrn.sinapiPRO.repository.helper.funcionario;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.Funcionario; import br.edu.ifrn.sinapiPRO.repository.filter.FuncionarioFilter;
public interface FuncionariosRepositoryQueries { Page<Funcionario> filtrar(FuncionarioFilter f, Pageable p); }
