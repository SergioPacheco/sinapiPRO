# Design — Sprint 10: Cronograma Financeiro + Curva S

## Pré-requisitos (Sprint 9 — já implementado)
- `PlanejamentoItem` entity (orcamento, item, dataInicio, dataFim, percentualExecutado)
- `PlanejamentoService.calcularCronograma()` → `List<CronogramaMes>`
- `cronograma-financeiro.ftl` (tabela + barras)

## Novos DTOs

```java
// Linha do relatório Planejamento Físico
public class PlanejamentoFisicoDTO {
    private String itemizacao;
    private String descricao;
    private String etapa;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int duracaoMeses;
    private BigDecimal valor;
    private BigDecimal percentualDoTotal;
}
```

## Novos métodos no PlanejamentoService

```java
// Monta dados para relatório Planejamento Físico agrupado por etapa
List<PlanejamentoFisicoDTO> montarPlanejamentoFisico(Long codigoOrcamento);
```

## Novos endpoints no RelatoriosController

```
GET /relatorios/curvaS/{codigo}              → PDF curva-s.ftl
GET /relatorios/planejamentoFisico/{codigo}  → PDF planejamento-fisico.ftl
```

## Nova tela Thymeleaf

```
GET /planejamento/{codigoOrcamento}/gantt → CronogramaGantt.html
```

### CronogramaGantt.html
- Tabela: linhas = itens com planejamento, colunas = meses do período
- Célula preenchida (background colorido) se o item está ativo naquele mês
- Última linha: total planejado por mês (reutiliza calcularCronograma)
- CSS inline para barras (mesmo padrão do Cronograma.html existente)

## Novos templates FreeMarker

### curva-s.ftl
- Cabeçalho: nome do orçamento, data emissão
- Tabela: período, valor planejado, valor acumulado, % acumulado
- Seção gráfica: barras horizontais com marcos 25/50/75/100%
- Rodapé: total geral do orçamento

### planejamento-fisico.ftl
- Cabeçalho: nome do orçamento, data emissão
- Tabela agrupada por etapa:
  - Linha de grupo: nome da etapa, subtotal
  - Linhas: itemização, descrição, início, fim, duração, valor, %
- Totalização geral

## Arquivos a criar/modificar

| Arquivo | Ação |
|---|---|
| `dto/PlanejamentoFisicoDTO.java` | Criar |
| `service/PlanejamentoService.java` | Adicionar `montarPlanejamentoFisico()` |
| `controller/RelatoriosController.java` | Adicionar 2 endpoints |
| `controller/PlanejamentoController.java` | Adicionar endpoint gantt |
| `templates/relatorio/ftl/curva-s.ftl` | Criar |
| `templates/relatorio/ftl/planejamento-fisico.ftl` | Criar |
| `templates/planejamento/CronogramaGantt.html` | Criar |
| `templates/fragments/LayoutPadrao.html` | Adicionar links no menu |
