CREATE TABLE movimento_bancario (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_conta_bancaria BIGINT NOT NULL,
    codigo_historico BIGINT,
    tipo VARCHAR(20) NOT NULL,
    valor DECIMAL(19,4) NOT NULL,
    data_movimento DATE NOT NULL,
    documento VARCHAR(100),
    descricao VARCHAR(200),
    saldo_apos DECIMAL(19,4),
    conciliado BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_conta_bancaria) REFERENCES conta_bancaria(codigo),
    FOREIGN KEY (codigo_historico) REFERENCES historico_bancario(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
