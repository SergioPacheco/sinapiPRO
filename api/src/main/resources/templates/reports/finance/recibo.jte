@import com.sinapipro.api.finance.domain.PayableInstallment
@param PayableInstallment installment
<html xmlns="http://www.w3.org/1999/xhtml"><head><style>
body{font-family:Arial,sans-serif;padding:40px}
h1{color:#1a56db;border-bottom:2px solid #1a56db;padding-bottom:8px}
.field{margin:10px 0}.field label{font-weight:bold;display:inline-block;width:120px}
.signature{margin-top:60px;border-top:1px solid #000;width:300px;text-align:center;padding-top:5px}
</style></head><body>
<h1>Recibo de Pagamento</h1>
<div class="field"><label>Parcela:</label>${installment.getInstallmentNumber()}</div>
<div class="field"><label>Vencimento:</label>${installment.getDueDate()}</div>
<div class="field"><label>Valor:</label>R$ ${installment.getAmount()}</div>
<div class="field"><label>Valor Pago:</label>R$ ${installment.getPaidAmount()}</div>
<div class="field"><label>Data Pgto:</label>${installment.getPaidDate()}</div>
<div class="field"><label>Forma:</label>${installment.getPaymentMethod() != null ? installment.getPaymentMethod() : "-"}</div>
<div class="signature">Assinatura</div>
</body></html>
