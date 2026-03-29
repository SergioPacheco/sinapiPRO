# Design — Sprint 9: Planejamento Físico-Financeiro

## Data Model

```sql
CREATE TABLE planejamento_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_orcamento BIGINT NOT NULL,
    codigo_item BIGINT NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    percentual_executado DECIMAL(10,4) DEFAULT 0,
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_orcamento) REFERENCES orcamento(codigo),
    FOREIGN KEY (codigo_item) REFERENCES item(codigo)
);
```

## Architecture

```
PlanejamentoItem (entity)
  └→ PlanejamentoItemRepository
       └→ PlanejamentoService
            ├→ salvarPlanejamento(orcamentoId, List<PlanejamentoItemDTO>)
            ├→ calcularCronograma(orcamentoId) → List<CronogramaMes>
            └→ calcularCurvaS(orcamentoId) → List<CurvaSPonto>
                 └→ PlanejamentoController
                      ├→ GET /planejamento/{codigoOrcamento} → tela
                      ├→ POST /planejamento/{codigoOrcamento} → salvar
                      └→ RelatoriosController
                           ├→ GET /relatorios/cronograma/{codigo} → PDF
                           └→ GET /relatorios/curvaS/{codigo} → PDF
```

## DTOs
- `CronogramaMes`: mes, ano, valorPlanejado, valorAcumulado, percentual
- `CurvaSPonto`: periodo, valorPlanejado, valorAcumulado, percentualAcumulado

## Cálculo de distribuição linear
Para cada item com data_inicio e data_fim:
- Número de meses = diferença entre datas
- Valor mensal = valorTotal do item / número de meses
- Distribuir proporcionalmente nos meses
