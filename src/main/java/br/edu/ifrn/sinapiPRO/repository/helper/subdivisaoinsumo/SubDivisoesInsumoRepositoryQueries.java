package br.edu.ifrn.sinapiPRO.repository.helper.subdivisaoinsumo;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo; import br.edu.ifrn.sinapiPRO.repository.filter.SubDivisaoInsumoFilter;
public interface SubDivisoesInsumoRepositoryQueries { Page<SubDivisaoInsumo> filtrar(SubDivisaoInsumoFilter f, Pageable p); }
