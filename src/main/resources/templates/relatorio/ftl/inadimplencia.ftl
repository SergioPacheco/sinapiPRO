<#include "base.ftl">
<@base title="Inadimplência de Parcelas - ${obra!'Todas as Obras'}">
<div class="header">
    <div class="titulo">RELATÓRIO DE INADIMPLÊNCIA</div>
    <div class="subtitulo">${obra!""} | Data: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Unidade</th>
            <th>Cliente</th>
            <th>Parcela #</th>
            <th>Vencimento</th>
            <th class="text-right">Valor (R$)</th>
            <th class="text-center">Dias Atraso</th>
        </tr>
    </thead>
    <tbody>
        <#list parcelas as p>
        <tr>
            <td>${p.unidade!""}</td>
            <td>${p.cliente!""}</td>
            <td class="text-center">${p.numeroParcela!""}</td>
            <td>${p.vencimento!""}</td>
            <td class="text-right">${p.valor!""}</td>
            <td class="text-center" style="${(p.diasAtraso?number > 30)?then('color:red;font-weight:bold;','')}">
                ${p.diasAtraso!""}
            </td>
        </tr>
        </#list>
    </tbody>
    <tfoot>
        <tr class="total-row">
            <td colspan="4">TOTAL INADIMPLENTE</td>
            <td class="text-right">${totalInadimplente!""}</td>
            <td class="text-center">${totalParcelas!0} parcela(s)</td>
        </tr>
    </tfoot>
</table>
</@base>
