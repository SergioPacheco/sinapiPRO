CREATE TABLE fornecedor (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(18),
    telefone VARCHAR(20),
    email VARCHAR(100),
    cidade VARCHAR(100),
    codigo_estado BIGINT(20),
    FOREIGN KEY (codigo_estado) REFERENCES estado(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE fornecedor_insumo (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    codigo_fornecedor BIGINT(20) NOT NULL,
    codigo_insumo BIGINT(20) NOT NULL,
    preco DECIMAL(15,2),
    data_cotacao DATE,
    FOREIGN KEY (codigo_fornecedor) REFERENCES fornecedor(codigo),
    FOREIGN KEY (codigo_insumo) REFERENCES insumo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE insumo ADD COLUMN origem VARCHAR(30);
ALTER TABLE insumo ADD COLUMN tipo_equipamento VARCHAR(30);
