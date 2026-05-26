@import com.sinapipro.api.finance.domain.Payable
@import com.sinapipro.api.finance.domain.Receivable
@import java.util.List
@import java.time.LocalDate
@param List<Payable> payables
@param List<Receivable> receivables
@param LocalDate from
@param LocalDate to
<html xmlns="http://www.w3.org/1999/xhtml"><head><style>
body{font-family:Arial,sans-serif;font-size:11px;padding:20px}
h1{color:#1a56db;font-size:16px}h2{font-size:13px;margin-top:16px}
table{width:100%;border-collapse:collapse;margin-top:8px}
th{background:#374151;color:white;padding:5px;text-align:left;font-size:10px}
td{border-bottom:1px solid #e5e7eb;padding:4px;font-size:10px}
.total{font-weight:bold;background:#eef2ff}
.positive{color:#059669}.negative{color:#dc2626}
</style></head><body>
<h1>DRE - Demonstrativo de Resultado</h1>
<div style="font-size:9px;color:#666">Período: ${from} a ${to}</div>
<h2>Receitas</h2>
<table><tr><th>Descrição</th><th>Valor</th><th>Status</th></tr>
@for(Receivable r : receivables)
<tr><td>${r.getDescription()}</td><td class="positive">R$ ${r.getAmount()}</td><td>${r.getStatus()}</td></tr>
@endfor
</table>
<h2>Despesas</h2>
<table><tr><th>Descrição</th><th>Valor</th><th>Status</th></tr>
@for(Payable p : payables)
<tr><td>${p.getDescription()}</td><td class="negative">R$ ${p.getAmount()}</td><td>${p.getStatus()}</td></tr>
@endfor
</table>
</body></html>
