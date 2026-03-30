package br.edu.ifrn.sinapiPRO.repository.helper.funcao;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.Funcao; import br.edu.ifrn.sinapiPRO.repository.filter.FuncaoFilter;
public interface FuncoesRepositoryQueries { Page<Funcao> filtrar(FuncaoFilter f, Pageable p); }
