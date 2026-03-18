ALTER TABLE orcamento ADD COLUMN tipo_orcamento VARCHAR(30) DEFAULT 'ESTIMATIVA';
UPDATE orcamento SET tipo_orcamento = 'ESTIMATIVA' WHERE tipo_orcamento IS NULL;
