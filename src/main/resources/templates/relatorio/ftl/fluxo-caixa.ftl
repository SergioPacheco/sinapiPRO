<#include "base.ftl">
<@base title="Fluxo de Caixa - ${periodo!''}">
<div class="header">
    <div class="titulo">FLUXO DE CAIXA</div>
    <div class="subtitulo">Período: ${periodo!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr><th>Data</th><th>Descrição</th><th>Tipo</th><th class="text-right">Valor</th><th class="text-right">Saldo</th></tr>
    </thead>
    <tbody>
        <#list lancamentos as l>
        <tr>
            <td>${l.data!""}</td>
            <td>${l.descricao!""}</td>
            <td>${l.tipo!""}</td>
            <td class="text-right" style="${l.tipo == 'DEBITO'} ? 'color:red;' : 'color:green;'">${l.valor!""}</td>
            <td class="text-right">${l.saldo!""}</td>
        </tr>
        </#list>
    </tbody>
</table>
<div class="total-row" style="margin-top:8px;padding:4px 6px;border:1px solid #ccc;">
    Saldo Final: R$ ${saldoFinal!"0,00"}
</div>
</@base>
