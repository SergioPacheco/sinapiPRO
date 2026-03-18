ALTER TABLE etapa ADD COLUMN codigo_etapa_pai BIGINT(20) NULL;
ALTER TABLE etapa ADD CONSTRAINT fk_etapa_pai FOREIGN KEY (codigo_etapa_pai) REFERENCES etapa(codigo);
