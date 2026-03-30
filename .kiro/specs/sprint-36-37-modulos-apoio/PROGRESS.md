# Sprints 36–37 — Módulos de Apoio — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Sprint 36 — GED + Frota
- `DocumentoGed` (nome, tipo, caminho, tamanho, obra, cliente)
- `Veiculo` (placa, modelo, marca, ano, tipo, ativo)
- `AgendamentoManutencao` (veiculo, tipo, data, km, valor, situação)
- V32: documento_ged, veiculo, agendamento_manutencao

**GED — `GedUploadService`:**
- Upload real com MultipartFile
- Validação MIME type (OWASP File Upload Cheat Sheet)
- Limite 50 MB, nome UUID, organização por ano/mês
- Download de arquivo, exclusão física

**Frota — `AlertaManutencaoService`:**
- Alertas por data: vencida e próxima (7 dias antecedência)
- Alertas por KM: intervalos padrão (óleo 5k, revisão 10k, pneus 40k, freios 20k)
- Ordenação por criticidade: CRITICO > ALTO > MEDIO

### Sprint 37 — Relatórios Gerais
- `RelatorioOperacionalService`:
  - Inadimplência: parcelas vencidas com dias de atraso
  - Posição de estoque: qtd atual, custo médio, valor total, status
- `JobCostingService` (EVM — PMBOK/NBR ISO 21500):
  - PV, EV, AC, BAC, CV, SV, CPI, SPI, EAC, ETC, VAC
  - Dashboard visual com indicadores EXCELENTE/OK/ATENÇÃO/CRÍTICO
