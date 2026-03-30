CREATE TABLE boleto (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_receita BIGINT,
    nosso_numero VARCHAR(50),
    linha_digitavel VARCHAR(100),
    data_emissao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    valor DECIMAL(19,4) NOT NULL,
    situacao VARCHAR(20) DEFAULT 'EMITIDO',
    banco VARCHAR(100),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_receita) REFERENCES receita(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE cheque (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_conta_bancaria BIGINT,
    numero VARCHAR(20) NOT NULL,
    beneficiario VARCHAR(200),
    valor DECIMAL(19,4) NOT NULL,
    data_emissao DATE NOT NULL,
    data_bom_para DATE,
    situacao VARCHAR(20) DEFAULT 'EMITIDO',
    observacao VARCHAR(200),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_conta_bancaria) REFERENCES conta_bancaria(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
