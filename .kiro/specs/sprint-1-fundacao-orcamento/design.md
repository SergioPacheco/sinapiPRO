# Design — Sprint 1: Fundação do Orçamento

## Approach
Três mudanças independentes que podem ser implementadas em paralelo: novo enum + campo para tipo de orçamento, correção dos cálculos de taxas, e hierarquia de etapas. Cada uma tem sua própria migration.

## Components affected

| Component | Change type | Risk |
|-----------|------------|------|
| `OrcamentoSituacao` / novo `TipoOrcamento` | New enum | Low |
| `Orcamento.java` | Add field `tipoOrcamento` | Low |
| `Orcamento.java` (cálculos) | Modify `calculaValorTaxas()`, `calculaValorTotalComTaxas()` | Med — muda valores financeiros |
| `Etapa.java` | Add field `etapaPai` (self-reference) | Med |
| `Orcamento.Itemizar()` | Rewrite for 4 levels | High — lógica complexa, sem testes |
| `CadastroOrcamento.html` | Add combo tipo | Low |
| `PesquisaOrcamentos.html` | Add column tipo | Low |
| `EtapasController` / tela | Add combo etapa pai | Low |
| Flyway V06, V07 | New migrations | Low |

## Data model changes

### Migration V06 — tipo_orcamento
```sql
ALTER TABLE orcamento ADD COLUMN tipo_orcamento VARCHAR(30) DEFAULT 'ESTIMATIVA';
UPDATE orcamento SET tipo_orcamento = 'ESTIMATIVA' WHERE tipo_orcamento IS NULL;
```

### Migration V07 — etapa_pai
```sql
ALTER TABLE etapa ADD COLUMN codigo_etapa_pai BIGINT(20) NULL;
ALTER TABLE etapa ADD CONSTRAINT fk_etapa_pai FOREIGN KEY (codigo_etapa_pai) REFERENCES etapa(codigo);
```

## Key decisions

| Decision | Rationale | Alternatives |
|----------|-----------|-------------|
| `TipoOrcamento` como enum separado de `OrcamentoSituacao` | Tipo (estimativa/venda/execução) é diferente de situação (aberto/bloqueado/concluído). Um orçamento de Venda pode estar Aberto ou Concluído | Unificar em um único enum |
| Taxas em cascata no model | Manter cálculos no `Orcamento.java` (padrão existente) em vez de mover para service | Mover para OrcamentoService |
| Auto-referência em Etapa | Simples, suporta N níveis, compatível com dados existentes (pai=null = raiz) | Tabela de closure, nested sets |

## Risks

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Cálculo cascata muda valores de orçamentos existentes | High | Comparar valores antes/depois em orçamentos de teste |
| `Itemizar()` sem testes — rewrite pode quebrar | High | Testar manualmente com orçamento existente antes de commitar |
| NPE em `calculaValorBDI()` se percentuais forem null | Med | Adicionar null checks (usar BigDecimal.ZERO como default) |

## Rollback strategy
- Cada migration é aditiva (ADD COLUMN) — rollback = DROP COLUMN
- Enum `TipoOrcamento` é novo — remover campo e enum reverte
- Cálculo cascata: guardar fórmula antiga comentada para referência
