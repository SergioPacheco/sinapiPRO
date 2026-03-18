-- Composição: percentuais avançados
ALTER TABLE composicao ADD COLUMN percentual_taxacao DECIMAL(10,4) DEFAULT 0;
ALTER TABLE composicao ADD COLUMN percentual_tributacao DECIMAL(10,4) DEFAULT 0;
ALTER TABLE composicao ADD COLUMN percentual_perdas DECIMAL(10,4) DEFAULT 0;
ALTER TABLE composicao ADD COLUMN percentual_bonificacao DECIMAL(10,4) DEFAULT 0;

-- Orçamento: BDI detalhado por tipo
ALTER TABLE orcamento ADD COLUMN percentual_bdi_insumo DECIMAL(10,4);
ALTER TABLE orcamento ADD COLUMN percentual_bdi_servico DECIMAL(10,4);
ALTER TABLE orcamento ADD COLUMN percentual_bdi_terceiro DECIMAL(10,4);
ALTER TABLE orcamento ADD COLUMN percentual_bdi_ferramenta DECIMAL(10,4);
