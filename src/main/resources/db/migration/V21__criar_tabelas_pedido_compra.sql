CREATE TABLE pedido_compra (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    numero INT,
    codigo_obra BIGINT NOT NULL,
    codigo_fornecedor BIGINT,
    data_pedido DATE NOT NULL,
    data_entrega DATE,
    valor_total DECIMAL(19,4) DEFAULT 0,
    situacao VARCHAR(20) DEFAULT 'ABERTO',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_fornecedor) REFERENCES fornecedor(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE pedido_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_pedido BIGINT NOT NULL,
    codigo_insumo BIGINT,
    descricao VARCHAR(200) NOT NULL,
    unidade VARCHAR(20),
    quantidade DECIMAL(19,4) DEFAULT 0,
    valor_unitario DECIMAL(19,4) DEFAULT 0,
    valor_total DECIMAL(19,4) DEFAULT 0,
    quantidade_recebida DECIMAL(19,4) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_pedido) REFERENCES pedido_compra(codigo),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE nota_fiscal (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_pedido BIGINT NOT NULL,
    numero VARCHAR(50),
    data_emissao DATE,
    valor DECIMAL(19,4) DEFAULT 0,
    observacao VARCHAR(200),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_pedido) REFERENCES pedido_compra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
