@import com.sinapipro.api.finance.domain.BankTransaction
@import java.util.List
@import java.time.LocalDate
@param List<BankTransaction> transactions
@param LocalDate from
@param LocalDate to
<html xmlns="http://www.w3.org/1999/xhtml"><head><style>
body{font-family:Arial,sans-serif;font-size:11px;padding:20px}
h1{color:#1a56db;font-size:16px}
table{width:100%;border-collapse:collapse;margin-top:10px}
th{background:#1a56db;color:white;padding:6px;text-align:left;font-size:10px}
td{border-bottom:1px solid #e5e7eb;padding:5px;font-size:10px}
.credit{color:#059669}.debit{color:#dc2626}
.meta{font-size:9px;color:#666;margin-bottom:10px}
</style></head><body>
<h1>Extrato de Conta Corrente</h1>
<div class="meta">Período: ${from} a ${to} | Total: ${transactions.size()} lançamentos</div>
<table>
<tr><th>Data</th><th>Tipo</th><th>Descrição</th><th>Valor</th><th>Conciliado</th></tr>
@for(BankTransaction t : transactions)
<tr>
<td>${t.getTransactionDate()}</td>
<td class="${"CREDIT".equals(t.getType()) ? "credit" : "debit"}">${t.getType()}</td>
<td>${t.getDescription()}</td>
<td class="${"CREDIT".equals(t.getType()) ? "credit" : "debit"}">R$ ${t.getAmount()}</td>
<td>${t.isReconciled() ? "Sim" : "Não"}</td>
</tr>
@endfor
</table>
</body></html>
