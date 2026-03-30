<#include "base.ftl">
<@base title="Posição de Estoque - ${obra!''}">
<div class="header">
    <div class="titulo">POSIÇÃO DE ESTOQUE</div>
    <div class="subtitulo">${obra!""} | Data: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Insumo</th>
            <th class="text-right">Qtd Atual</th>
            <th class="text-right">Qtd Mínima</th>
            <th class="text-right">Custo Médio (R$)</th>
            <th class="text-right">Valor Total (R$)</th>
            <th class="text-center">Status</th>
        </tr>
    </thead>
    <tbody>
        <#list itens as item>
        <tr style="${(item.status == 'CRITICO')?then('background-color:#fdd;','')}${(item.status == 'ZERADO')?then('background-color:#eee;','')}">
            <td>${item.insumo!""}</td>
            <td class="text-right">${item.qtdAtual!""}</td>
            <td class="text-right">${item.qtdMinima!""}</td>
            <td class="text-right">${item.custoMedio!""}</td>
            <td class="text-right">${item.valorTotal!""}</td>
            <td class="text-center">${item.status!""}</td>
        </tr>
        </#list>
    </tbody>
    <tfoot>
        <tr class="total-row">
            <td colspan="4">VALOR TOTAL EM ESTOQUE</td>
            <td class="text-right">${valorTotal!""}</td>
            <td></td>
        </tr>
    </tfoot>
</table>
</@base>
