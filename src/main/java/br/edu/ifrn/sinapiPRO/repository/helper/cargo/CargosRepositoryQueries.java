package br.edu.ifrn.sinapiPRO.repository.helper.cargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.Cargo;
import br.edu.ifrn.sinapiPRO.repository.filter.CargoFilter;
public interface CargosRepositoryQueries { Page<Cargo> filtrar(CargoFilter f, Pageable p);
}
