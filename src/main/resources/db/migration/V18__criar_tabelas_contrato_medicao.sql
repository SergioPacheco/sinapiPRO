CREATE TABLE contrato (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(50),
    descricao VARCHAR(200) NOT NULL,
    codigo_obra BIGINT NOT NULL,
    codigo_cliente BIGINT,
    data_inicio DATE,
    data_fim DATE,
    valor_total DECIMAL(19,4) DEFAULT 0,
    situacao VARCHAR(20) DEFAULT 'ABERTO',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE contrato_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_contrato BIGINT NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    unidade VARCHAR(20),
    quantidade DECIMAL(19,4) DEFAULT 0,
    valor_unitario DECIMAL(19,4) DEFAULT 0,
    valor_total DECIMAL(19,4) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_contrato) REFERENCES contrato(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE medicao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_contrato BIGINT NOT NULL,
    numero INT NOT NULL,
    data_medicao DATE NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    valor_medido DECIMAL(19,4) DEFAULT 0,
    situacao VARCHAR(20) DEFAULT 'ABERTA',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_contrato) REFERENCES contrato(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE medicao_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_medicao BIGINT NOT NULL,
    codigo_contrato_item BIGINT NOT NULL,
    quantidade_medida DECIMAL(19,4) DEFAULT 0,
    percentual_executado DECIMAL(10,4) DEFAULT 0,
    valor_medido DECIMAL(19,4) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_medicao) REFERENCES medicao(codigo),
    FOREIGN KEY (codigo_contrato_item) REFERENCES contrato_item(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
