# Referência SINAPI

Planilhas oficiais do **Sistema Nacional de Pesquisa de Custos e Índices da Construção Civil** (Caixa Econômica Federal), usadas como base de preços para importação no módulo de Orçamentos.

## Arquivos

| Arquivo | Conteúdo |
|---------|----------|
| `SINAPI_Custo_Ref_Composicoes_Analitico_SP_202412_NaoDesonerado.xlsx` | Composições analíticas (insumos detalhados) |
| `SINAPI_Custo_Ref_Composicoes_Sintetico_SP_202412_NaoDesonerado.xlsx` | Composições sintéticas (custo total) |
| `SINAPI_Preco_Ref_Insumos_SP_202412_NaoDesonerado.xlsx` | Preços de insumos |
| `_SINAPI_Relatório_Família_de_Insumos_2024_12.xlsx` | Famílias de insumos (agrupamento) |
| `Notas_SINAPI.pdf` | Notas metodológicas |
| `*.pdf` (analítico/sintético/insumos) | Versões PDF das planilhas |
| `SINAPI_ref_Insumos_Composicoes_SP_202412_NaoDesonerado.zip` | Pacote completo compactado |

## Parâmetros desta edição

- **Data base:** Dezembro/2024
- **UF:** São Paulo (SP)
- **Regime:** Não Desonerado
- **Fonte:** [SINAPI - Caixa Econômica Federal](https://www.caixa.gov.br/poder-publico/modernizacao-gestao/sinapi/)

## Como atualizar

1. Baixar nova edição em https://www.caixa.gov.br/poder-publico/modernizacao-gestao/sinapi/
2. Substituir os arquivos nesta pasta (manter nomenclatura padrão da Caixa)
3. Executar a importação via endpoint `POST /api/sinapi/import` ou pelo painel admin
4. Atualizar a data base nos orçamentos que usam SINAPI

## Uso no sistema

O módulo `api/src/.../sinapi/` faz o parsing das planilhas XLSX e popula as tabelas:
- `sinapi_composition` — composições (código, descrição, unidade, custo)
- `sinapi_input` — insumos (código, descrição, unidade, preço)
- `sinapi_composition_input` — relação composição × insumo (coeficiente)

> ⚠️ Estes arquivos estão no Git LFS por serem binários pesados (~45MB total).
