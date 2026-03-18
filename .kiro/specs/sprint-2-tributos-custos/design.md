# Design — Sprint 2: Tributos e Custos

## Data model

### Migration V08
```sql
CREATE TABLE tipo_custo (codigo BIGINT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL);
CREATE TABLE tributo (codigo BIGINT AUTO_INCREMENT PRIMARY KEY, descricao VARCHAR(100) NOT NULL, percentual DECIMAL(10,4), codigo_estado BIGINT, FOREIGN KEY (codigo_estado) REFERENCES estado(codigo));
CREATE TABLE tributo_insumo (codigo_tributo BIGINT, codigo_insumo BIGINT, PRIMARY KEY (codigo_tributo, codigo_insumo));
CREATE TABLE tributo_composicao (codigo_tributo BIGINT, codigo_composicao BIGINT, PRIMARY KEY (codigo_tributo, codigo_composicao));
ALTER TABLE item_orcamento ADD COLUMN codigo_tipo_custo BIGINT NULL;
```

## Patterns followed
- Entity/Repository/Service/Controller/Filter/RepositoryImpl — same as Etapa
- Thymeleaf views: Cadastro + Pesquisa — same as etapa/CadastroEtapa.html
- Constructor injection, @Transactional in service
