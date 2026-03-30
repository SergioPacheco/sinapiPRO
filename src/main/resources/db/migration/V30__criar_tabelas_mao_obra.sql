CREATE TABLE competencia (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    mes INT NOT NULL,
    ano INT NOT NULL,
    descricao VARCHAR(50),
    encerrada BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (codigo),
    UNIQUE KEY uk_competencia (mes, ano)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE banco_horas (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_funcionario BIGINT NOT NULL,
    codigo_competencia BIGINT NOT NULL,
    horas_credito DECIMAL(10,2) DEFAULT 0,
    horas_debito DECIMAL(10,2) DEFAULT 0,
    saldo DECIMAL(10,2) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_funcionario) REFERENCES funcionario(codigo),
    FOREIGN KEY (codigo_competencia) REFERENCES competencia(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE movimentacao_hora (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_funcionario BIGINT NOT NULL,
    codigo_competencia BIGINT NOT NULL,
    data_movimentacao DATE NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    horas DECIMAL(10,2) NOT NULL,
    descricao VARCHAR(200),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_funcionario) REFERENCES funcionario(codigo),
    FOREIGN KEY (codigo_competencia) REFERENCES competencia(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE prestacao_contas (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_funcionario BIGINT NOT NULL,
    codigo_competencia BIGINT NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    valor DECIMAL(19,4) NOT NULL,
    data_lancamento DATE NOT NULL,
    tipo VARCHAR(20) DEFAULT 'DESPESA',
    situacao VARCHAR(20) DEFAULT 'PENDENTE',
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_funcionario) REFERENCES funcionario(codigo),
    FOREIGN KEY (codigo_competencia) REFERENCES competencia(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
