package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Competencia;
import br.edu.ifrn.sinapiPRO.repository.CompetenciasRepository;

@Service
public class CompetenciaService {

    @Autowired
    private CompetenciasRepository repository;

    @Transactional
    public Competencia salvar(Competencia competencia) {
        return repository.saveAndFlush(competencia);
    }

    @Transactional
    public void encerrar(Long codigo) {
        Competencia competencia = repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Competência não encontrada"));
        competencia.setEncerrada(true);
        repository.saveAndFlush(competencia);
    }

    public List<Competencia> findAbertas() {
        return repository.findByEncerradaFalseOrderByAnoDescMesDesc();
    }

    public List<Competencia> findAll() {
        return repository.findAll();
    }

    public Competencia getOne(Long codigo) {
        return repository.getOne(codigo);
    }
}
