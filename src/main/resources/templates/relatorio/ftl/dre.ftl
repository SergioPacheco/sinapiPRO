<#include "base.ftl">
<@base title="DRE - ${periodo!''}">
<div class="header">
    <div class="titulo">DEMONSTRAÇÃO DO RESULTADO DO EXERCÍCIO</div>
    <div class="subtitulo">Período: ${periodo!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <tbody>
        <tr><td colspan="2" style="font-weight:bold;background:#e8e8e8;">RECEITAS</td></tr>
        <#list receitas as r>
        <tr><td style="padding-left:20px;">${r.descricao!""}</td><td class="text-right">${r.valor!""}</td></tr>
        </#list>
        <tr class="total-row"><td>Total Receitas</td><td class="text-right">${totalReceitas!""}</td></tr>
        <tr><td colspan="2" style="font-weight:bold;background:#e8e8e8;">DESPESAS</td></tr>
        <#list despesas as d>
        <tr><td style="padding-left:20px;">${d.descricao!""}</td><td class="text-right">${d.valor!""}</td></tr>
        </#list>
        <tr class="total-row"><td>Total Despesas</td><td class="text-right">${totalDespesas!""}</td></tr>
        <tr class="total-row" style="font-size:12px;"><td>RESULTADO LÍQUIDO</td><td class="text-right">${resultado!""}</td></tr>
    </tbody>
</table>
</@base>
