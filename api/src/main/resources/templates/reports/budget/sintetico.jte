@param com.sinapipro.api.budget.domain.Budget budget
@param java.util.List<com.sinapipro.api.budget.domain.BudgetItem> items
@param java.math.BigDecimal total
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<style>
@page { size: A4 landscape; margin: 15mm; }
body { font-family: Arial, Helvetica, sans-serif; font-size: 9px; color: #1a1a1a; margin: 0; }
.header { border-bottom: 2px solid #1a56db; padding-bottom: 8px; margin-bottom: 12px; }
.header h1 { font-size: 16px; color: #1a56db; margin: 0 0 4px 0; }
.header p { font-size: 10px; color: #555; margin: 2px 0; }
.meta { display: flex; gap: 20px; margin-bottom: 12px; font-size: 9px; }
.meta-item { background: #f3f4f6; padding: 6px 10px; border-radius: 4px; }
.meta-item strong { display: block; font-size: 8px; color: #666; text-transform: uppercase; margin-bottom: 2px; }
table { width: 100%; border-collapse: collapse; margin-top: 8px; }
th { background: #1a56db; color: white; padding: 5px 6px; text-align: left; font-size: 8px; text-transform: uppercase; }
th.num { text-align: right; }
td { padding: 4px 6px; border-bottom: 1px solid #e5e7eb; font-size: 9px; }
td.num { text-align: right; font-family: 'Courier New', monospace; }
td.code { font-family: 'Courier New', monospace; font-size: 8px; color: #555; }
tr.stage td { background: #fef2f2; color: #dc2626; font-weight: bold; font-size: 9px; border-bottom: 1px solid #fca5a5; }
tr:nth-child(even) td { background: #fafafa; }
.footer { margin-top: 16px; border-top: 1px solid #ddd; padding-top: 8px; font-size: 8px; color: #888; display: flex; justify-content: space-between; }
.total-row td { background: #1a56db !important; color: white !important; font-weight: bold; font-size: 10px; }
</style>
</head>
<body>
<div class="header">
    <h1>Planilha Or&#231;ament&#225;ria Sint&#233;tica</h1>
    <p><strong>${budget.getTitle()}</strong></p>
    <p>Cliente: ${budget.getCustomerName()} | C&#243;digo: ${budget.getCode()}</p>
</div>

<table>
    <thead>
        <tr>
            <th style="width:70px">Item</th>
            <th style="width:60px">C&#243;d.Ref</th>
            <th>Descri&#231;&#227;o</th>
            <th style="width:30px">Un</th>
            <th class="num" style="width:70px">Quantidade</th>
            <th class="num" style="width:80px">Valor Unit.</th>
            <th class="num" style="width:90px">Total</th>
        </tr>
    </thead>
    <tbody>
        @for(var item : items)
        <tr>
            <td class="code">${item.getCustomCode() != null ? item.getCustomCode() : ""}</td>
            <td class="code">${item.getComposition().getSinapiCode()}</td>
            <td>${item.getComposition().getDescription()}</td>
            <td>${item.getComposition().getUnit()}</td>
            <td class="num">${String.format("%,.4f", item.getQuantity())}</td>
            <td class="num">${String.format("%,.2f", item.getUnitCost())}</td>
            <td class="num">${String.format("%,.2f", item.getDirectCost())}</td>
        </tr>
        @endfor
        <tr class="total-row">
            <td colspan="6" style="text-align:right">TOTAL GERAL</td>
            <td class="num">${String.format("%,.2f", total)}</td>
        </tr>
    </tbody>
</table>

<div class="footer">
    <span>SinapiPRO v0.1.0</span>
    <span>Gerado em: ${java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</span>
</div>
</body>
</html>
