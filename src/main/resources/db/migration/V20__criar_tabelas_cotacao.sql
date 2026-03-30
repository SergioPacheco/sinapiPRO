CREATE TABLE cotacao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    numero INT,
    codigo_obra BIGINT NOT NULL,
    data_cotacao DATE NOT NULL,
    data_validade DATE,
    situacao VARCHAR(20) DEFAULT 'ABERTA',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE cotacao_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_cotacao BIGINT NOT NULL,
    codigo_insumo BIGINT,
    descricao VARCHAR(200) NOT NULL,
    unidade VARCHAR(20),
    quantidade DECIMAL(19,4) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cotacao) REFERENCES cotacao(codigo),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE cotacao_fornecedor (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_cotacao BIGINT NOT NULL,
    codigo_fornecedor BIGINT NOT NULL,
    email_enviado BOOLEAN DEFAULT FALSE,
    data_envio DATE,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cotacao) REFERENCES cotacao(codigo),
    FOREIGN KEY (codigo_fornecedor) REFERENCES fornecedor(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE resposta_cotacao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_cotacao_item BIGINT NOT NULL,
    codigo_cotacao_fornecedor BIGINT NOT NULL,
    valor_unitario DECIMAL(19,4) DEFAULT 0,
    prazo_entrega INT,
    observacao VARCHAR(200),
    selecionado BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cotacao_item) REFERENCES cotacao_item(codigo),
    FOREIGN KEY (codigo_cotacao_fornecedor) REFERENCES cotacao_fornecedor(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
