<#include "base.ftl">
<@base title="Planejamento Físico - ${orcamento!''}">
<div class="header">
    <div class="titulo">PLANEJAMENTO FÍSICO</div>
    <div class="subtitulo">${orcamento!""} | Emissão: ${emissao!""}</div>
</div>
<#list etapas as etapa>
<h2>${etapa.nome!""}</h2>
<table>
    <thead>
        <tr>
            <th>Item</th>
            <th>Descrição</th>
            <th class="text-center">Início</th>
            <th class="text-center">Fim</th>
            <th class="text-center">Meses</th>
            <th class="text-right">Valor (R$)</th>
            <th class="text-right">%</th>
        </tr>
    </thead>
    <tbody>
        <#list etapa.itens as item>
        <tr>
            <td>${item.itemizacao!""}</td>
            <td>${item.descricao!""}</td>
            <td class="text-center">${item.dataInicio!""}</td>
            <td class="text-center">${item.dataFim!""}</td>
            <td class="text-center">${item.duracaoMeses!""}</td>
            <td class="text-right">${item.valor!""}</td>
            <td class="text-right">${item.percentual!""}%</td>
        </tr>
        </#list>
        <tr class="total-row">
            <td colspan="5">Subtotal ${etapa.nome!""}</td>
            <td class="text-right">${etapa.subtotal!""}</td>
            <td class="text-right">${etapa.percentual!""}%</td>
        </tr>
    </tbody>
</table>
</#list>
<table>
    <tr class="total-row">
        <td>TOTAL GERAL</td>
        <td class="text-right">${totalGeral!""}</td>
        <td class="text-right">100%</td>
    </tr>
</table>
</@base>
