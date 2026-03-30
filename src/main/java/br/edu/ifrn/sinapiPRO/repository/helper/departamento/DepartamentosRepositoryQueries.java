package br.edu.ifrn.sinapiPRO.repository.helper.departamento;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.Departamento; import br.edu.ifrn.sinapiPRO.repository.filter.DepartamentoFilter;
public interface DepartamentosRepositoryQueries { Page<Departamento> filtrar(DepartamentoFilter f, Pageable p); }
