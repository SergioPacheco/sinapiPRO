CREATE TABLE planejamento_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_orcamento BIGINT NOT NULL,
    codigo_item BIGINT NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    percentual_executado DECIMAL(10,4) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_orcamento) REFERENCES orcamento(codigo),
    FOREIGN KEY (codigo_item) REFERENCES item(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
