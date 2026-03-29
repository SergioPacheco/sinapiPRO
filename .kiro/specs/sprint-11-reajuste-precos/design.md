# Design — Sprint 11: Reajuste de Preços + Aplicação em Lote

## Data Model — Migration V14

```sql
CREATE TABLE orcamento_baseline (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_orcamento BIGINT NOT NULL,
    descricao VARCHAR(200),
    data_gravacao DATETIME NOT NULL,
    valor_total DECIMAL(19,4),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_orcamento) REFERENCES orcamento(codigo)
);

CREATE TABLE orcamento_baseline_item (
    codigo BIGINT NOT NULL AUTO_INCREMENT,
    codigo_baseline BIGINT NOT NULL,
    codigo_item BIGINT NOT NULL,
    valor_unitario DECIMAL(19,4),
    quantidade DECIMAL(19,4),
    valor_total DECIMAL(19,4),
    PRIMARY KEY (codigo),
    FOREIGN KEY (codigo_baseline) REFERENCES orcamento_baseline(codigo),
    FOREIGN KEY (codigo_item) REFERENCES item(codigo)
);
```

## Entities

```java
@Entity
public class OrcamentoBaseline {
    Long codigo;
    Orcamento orcamento;
    String descricao;
    LocalDateTime dataGravacao;
    BigDecimal valorTotal;
    List<OrcamentoBaselineItem> itens;
}

@Entity
public class OrcamentoBaselineItem {
    Long codigo;
    OrcamentoBaseline baseline;
    Item item;
    BigDecimal valorUnitario;
    BigDecimal quantidade;
    BigDecimal valorTotal;
}
```

## Architecture

```
ReajusteService (@Service)
  ├→ reajustarPercentual(codigoOrcamento, percentual, especie?) → int (itens afetados)
  ├→ reajustarValor(codigoOrcamento, valor, List<Long> codigosItens) → int
  ├→ aplicarPrecoSinapi(codigoOrcamento, codigoBasePreco, onerado, especie?) → int
  └→ previewReajuste(codigoOrcamento, percentual, especie?) → List<ReajustePreviewDTO>

BaselineService (@Service)
  ├→ gravarBaseline(codigoOrcamento, descricao) → OrcamentoBaseline
  ├→ listarBaselines(codigoOrcamento) → List<OrcamentoBaseline>
  └→ compararBaseline(codigoBaseline) → List<BaselineComparativoDTO>
```

## DTOs

```java
public class ReajustePreviewDTO {
    Long codigoItem;
    String descricao;
    BigDecimal valorAtual;
    BigDecimal valorNovo;
    BigDecimal diferenca;
    BigDecimal percentualVariacao;
}

public class BaselineComparativoDTO {
    String descricao;
    BigDecimal valorBaseline;
    BigDecimal valorAtual;
    BigDecimal diferenca;
    BigDecimal percentualVariacao;
}
```

## Endpoints

```
ReajusteController (@Controller, @RequestMapping("/reajuste"))
  GET  /{codigoOrcamento}                    → tela principal de reajuste
  POST /{codigoOrcamento}/percentual         → aplicar reajuste percentual
  POST /{codigoOrcamento}/valor              → aplicar reajuste por valor
  POST /{codigoOrcamento}/aplicarSinapi      → aplicar preços SINAPI em lote
  GET  /{codigoOrcamento}/preview            → preview AJAX (JSON)

BaselineController (@Controller, @RequestMapping("/baseline"))
  GET  /{codigoOrcamento}                    → listar baselines
  POST /{codigoOrcamento}                    → gravar novo baseline
  GET  /{codigoOrcamento}/{codigoBaseline}   → comparativo

DigitacaoRapidaController (@Controller)
  GET  /digitacaoRapida/{codigoOrcamento}    → tela de digitação rápida
  POST /digitacaoRapida/{codigoOrcamento}    → adicionar item (AJAX)
```

## Telas Thymeleaf

### Reajuste.html
- Aba "Percentual": campo %, select espécie (opcional), botão preview, botão aplicar
- Aba "Valor": campo R$, seleção de itens (checkboxes), botão aplicar
- Aba "SINAPI": select base de preço, radio onerado/desonerado, select espécie, botão aplicar
- Tabela de preview com valores antes/depois

### Baseline.html
- Lista de baselines gravados (data, descrição, valor total)
- Botão "Gravar Baseline"
- Link para comparativo

### BaselineComparativo.html
- Tabela: item, valor baseline, valor atual, diferença, %

### DigitacaoRapida.html
- Form inline: autocomplete composição/insumo, quantidade, valor unitário
- Tabela de itens adicionados (atualiza via AJAX)
- Vincula à etapa selecionada do usuário

## Arquivos a criar/modificar

| Arquivo | Ação |
|---|---|
| `V14__criar_tabelas_baseline.sql` | Criar |
| `model/OrcamentoBaseline.java` | Criar |
| `model/OrcamentoBaselineItem.java` | Criar |
| `repository/OrcamentoBaselineRepository.java` | Criar |
| `dto/ReajustePreviewDTO.java` | Criar |
| `dto/BaselineComparativoDTO.java` | Criar |
| `service/ReajusteService.java` | Criar |
| `service/BaselineService.java` | Criar |
| `controller/ReajusteController.java` | Criar |
| `controller/BaselineController.java` | Criar |
| `controller/DigitacaoRapidaController.java` | Criar |
| `templates/reajuste/Reajuste.html` | Criar |
| `templates/baseline/Baseline.html` | Criar |
| `templates/baseline/BaselineComparativo.html` | Criar |
| `templates/orcamento/DigitacaoRapida.html` | Criar |
| `templates/fragments/LayoutPadrao.html` | Adicionar links no menu |
