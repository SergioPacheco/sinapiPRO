CREATE TABLE orcamento_baseline (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_orcamento BIGINT NOT NULL,
    descricao VARCHAR(200),
    data_gravacao DATETIME NOT NULL,
    valor_total DECIMAL(19,4),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_orcamento) REFERENCES orcamento(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE orcamento_baseline_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_baseline BIGINT NOT NULL,
    codigo_item BIGINT NOT NULL,
    valor_unitario DECIMAL(19,4),
    quantidade DECIMAL(19,4),
    valor_total DECIMAL(19,4),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_baseline) REFERENCES orcamento_baseline(codigo),
    FOREIGN KEY (codigo_item) REFERENCES item(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
