package br.edu.ifrn.sinapiPRO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.helper.insumo.InsumosQueries;

@Repository
public interface Insumos extends JpaRepository<Insumo, Long>, InsumosQueries {

}
