<#include "base.ftl">
<@base title="Balancete - ${periodo!''}">
<div class="header">
    <div class="titulo">BALANCETE</div>
    <div class="subtitulo">Período: ${periodo!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr><th>Conta</th><th>Descrição</th><th class="text-right">Débito</th><th class="text-right">Crédito</th><th class="text-right">Saldo</th></tr>
    </thead>
    <tbody>
        <#list contas as c>
        <tr>
            <td>${c.numero!""}</td>
            <td>${c.descricao!""}</td>
            <td class="text-right">${c.debito!""}</td>
            <td class="text-right">${c.credito!""}</td>
            <td class="text-right">${c.saldo!""}</td>
        </tr>
        </#list>
    </tbody>
    <tfoot>
        <tr class="total-row">
            <td colspan="2">TOTAL</td>
            <td class="text-right">${totalDebito!""}</td>
            <td class="text-right">${totalCredito!""}</td>
            <td class="text-right">${saldoGeral!""}</td>
        </tr>
    </tfoot>
</table>
</@base>
