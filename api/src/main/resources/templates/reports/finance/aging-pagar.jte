@import com.sinapipro.api.finance.domain.PayableInstallment
@import java.util.List
@import java.time.LocalDate
@param List<PayableInstallment> overdue
@param LocalDate date
<html xmlns="http://www.w3.org/1999/xhtml"><head><style>
body{font-family:Arial,sans-serif;font-size:11px;padding:20px}
h1{color:#1a56db;font-size:16px}
table{width:100%;border-collapse:collapse;margin-top:10px}
th{background:#dc2626;color:white;padding:6px;text-align:left;font-size:10px}
td{border-bottom:1px solid #e5e7eb;padding:5px;font-size:10px}
.total{font-weight:bold;background:#fef2f2}
</style></head><body>
<h1>Posição de Contas a Pagar - Aging</h1>
<div style="font-size:9px;color:#666">Data base: ${date} | Total vencidos: ${overdue.size()}</div>
<table>
<tr><th>#</th><th>Vencimento</th><th>Valor</th><th>Status</th></tr>
@for(PayableInstallment p : overdue)
<tr><td>${p.getInstallmentNumber()}</td><td>${p.getDueDate()}</td><td>R$ ${p.getAmount()}</td><td>${p.getStatus()}</td></tr>
@endfor
</table>
</body></html>
