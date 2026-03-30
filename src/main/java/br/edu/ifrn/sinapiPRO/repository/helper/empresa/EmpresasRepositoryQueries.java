package br.edu.ifrn.sinapiPRO.repository.helper.empresa;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.Empresa; import br.edu.ifrn.sinapiPRO.repository.filter.EmpresaFilter;
public interface EmpresasRepositoryQueries { Page<Empresa> filtrar(EmpresaFilter f, Pageable p); }
