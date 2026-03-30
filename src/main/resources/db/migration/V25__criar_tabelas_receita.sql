CREATE TABLE receita (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(200) NOT NULL,
    codigo_obra BIGINT,
    codigo_cliente BIGINT,
    codigo_plano_contas BIGINT,
    valor DECIMAL(19,4) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_competencia DATE,
    situacao VARCHAR(20) DEFAULT 'ABERTA',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo),
    FOREIGN KEY (codigo_plano_contas) REFERENCES plano_contas(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE recebimento_receita (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_receita BIGINT NOT NULL,
    codigo_conta_bancaria BIGINT,
    valor_recebido DECIMAL(19,4) NOT NULL,
    data_recebimento DATE NOT NULL,
    observacao VARCHAR(200),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_receita) REFERENCES receita(codigo),
    FOREIGN KEY (codigo_conta_bancaria) REFERENCES conta_bancaria(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
