@import com.sinapipro.api.finance.domain.ReceivableInstallment
@import java.util.List
@import java.time.LocalDate
@param List<ReceivableInstallment> overdue
@param LocalDate date
<html xmlns="http://www.w3.org/1999/xhtml"><head><style>
body{font-family:Arial,sans-serif;font-size:11px;padding:20px}
h1{color:#1a56db;font-size:16px}
table{width:100%;border-collapse:collapse;margin-top:10px}
th{background:#d97706;color:white;padding:6px;text-align:left;font-size:10px}
td{border-bottom:1px solid #e5e7eb;padding:5px;font-size:10px}
</style></head><body>
<h1>Posição de Contas a Receber - Aging</h1>
<div style="font-size:9px;color:#666">Data base: ${date} | Total vencidos: ${overdue.size()}</div>
<table>
<tr><th>#</th><th>Vencimento</th><th>Valor</th><th>Status</th></tr>
@for(ReceivableInstallment r : overdue)
<tr><td>${r.getInstallmentNumber()}</td><td>${r.getDueDate()}</td><td>R$ ${r.getAmount()}</td><td>${r.getStatus()}</td></tr>
@endfor
</table>
</body></html>
