package com.sinapipro.repository.helper.cargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.Cargo;
import com.sinapipro.repository.filter.CargoFilter;
public interface CargosRepositoryQueries { Page<Cargo> filtrar(CargoFilter f, Pageable p);
}
