-- Fase 9: Atendimento/CRM
CREATE TABLE atendimento (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_cliente BIGINT NOT NULL,
    codigo_obra BIGINT,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50),
    prioridade VARCHAR(20) DEFAULT 'NORMAL',
    situacao VARCHAR(20) DEFAULT 'ABERTO',
    data_abertura DATE NOT NULL,
    data_previsao DATE,
    data_encerramento DATE,
    responsavel VARCHAR(200),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ordem_servico (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_atendimento BIGINT NOT NULL,
    numero INT,
    descricao TEXT NOT NULL,
    data_emissao DATE NOT NULL,
    data_execucao DATE,
    situacao VARCHAR(20) DEFAULT 'ABERTA',
    valor DECIMAL(19,4) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_atendimento) REFERENCES atendimento(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE notificacao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    mensagem TEXT,
    tipo VARCHAR(50),
    lida BOOLEAN DEFAULT FALSE,
    data_criacao DATETIME NOT NULL,
    codigo_atendimento BIGINT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_atendimento) REFERENCES atendimento(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Fase 10: Nota Fiscal
CREATE TABLE nota_fiscal_servico (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(50),
    serie VARCHAR(10),
    codigo_cliente BIGINT NOT NULL,
    codigo_obra BIGINT,
    data_emissao DATE NOT NULL,
    valor_servicos DECIMAL(19,4) DEFAULT 0,
    aliquota_iss DECIMAL(10,4) DEFAULT 0,
    valor_iss DECIMAL(19,4) DEFAULT 0,
    valor_liquido DECIMAL(19,4) DEFAULT 0,
    discriminacao TEXT,
    situacao VARCHAR(20) DEFAULT 'EMITIDA',
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Fase 11: GED
CREATE TABLE documento_ged (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    descricao VARCHAR(200),
    tipo_arquivo VARCHAR(50),
    caminho VARCHAR(500),
    tamanho BIGINT,
    data_upload DATETIME NOT NULL,
    codigo_obra BIGINT,
    codigo_cliente BIGINT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_obra) REFERENCES obra(codigo),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Fase 11: Frota
CREATE TABLE veiculo (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    placa VARCHAR(10) NOT NULL,
    modelo VARCHAR(100),
    marca VARCHAR(100),
    ano INT,
    tipo VARCHAR(50),
    ativo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE agendamento_manutencao (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_veiculo BIGINT NOT NULL,
    tipo_manutencao VARCHAR(100) NOT NULL,
    data_agendamento DATE NOT NULL,
    data_realizacao DATE,
    km_atual INT,
    valor DECIMAL(19,4) DEFAULT 0,
    situacao VARCHAR(20) DEFAULT 'AGENDADO',
    observacao TEXT,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_veiculo) REFERENCES veiculo(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
