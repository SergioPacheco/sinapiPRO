CREATE TABLE despesa (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(200) NOT NULL,
    codigo_obra BIGINT,
    codigo_fornecedor BIGINT,
    codigo_plano_contas BIGINT,
    valor DECIMAL(19,4) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_competencia DATE,
    situacao VARCHAR(20) DEFAULT 'ABERTA',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_fornecedor) REFERENCES fornecedor(codigo),
    FOREIGN KEY (codigo_plano_contas) REFERENCES plano_contas(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE pagamento_despesa (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_despesa BIGINT NOT NULL,
    codigo_conta_bancaria BIGINT,
    valor_pago DECIMAL(19,4) NOT NULL,
    data_pagamento DATE NOT NULL,
    observacao VARCHAR(200),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_despesa) REFERENCES despesa(codigo),
    FOREIGN KEY (codigo_conta_bancaria) REFERENCES conta_bancaria(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
