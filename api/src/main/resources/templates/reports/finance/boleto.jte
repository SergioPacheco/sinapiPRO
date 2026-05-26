@import com.sinapipro.api.finance.domain.ReceivableInstallment
@param ReceivableInstallment installment
<html xmlns="http://www.w3.org/1999/xhtml"><head><style>
body{font-family:Arial,sans-serif;padding:30px}
.boleto{border:2px solid #000;padding:20px}
.bank{font-size:24px;font-weight:bold;border-bottom:2px solid #000;padding-bottom:10px}
.field{margin:8px 0}.field label{font-size:9px;color:#666;display:block}.field span{font-size:12px}
.barcode{margin-top:20px;font-family:monospace;font-size:14px;letter-spacing:2px}
</style></head><body>
<div class="boleto">
<div class="bank">SinapiPRO - Ficha de Compensação</div>
<div class="field"><label>Nosso Número</label><span>${installment.getOurNumber() != null ? installment.getOurNumber() : installment.getId().toString().substring(0,20)}</span></div>
<div class="field"><label>Vencimento</label><span>${installment.getDueDate()}</span></div>
<div class="field"><label>Valor</label><span>R$ ${installment.getAmount()}</span></div>
<div class="field"><label>Parcela</label><span>${installment.getInstallmentNumber()}</span></div>
<div class="barcode">${installment.getBoletoBarcode() != null ? installment.getBoletoBarcode() : "00000.00000 00000.000000 00000.000000 0 00000000000000"}</div>
</div>
</body></html>
