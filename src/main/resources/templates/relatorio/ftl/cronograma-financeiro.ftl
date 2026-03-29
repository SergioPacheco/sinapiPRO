<#include "base.ftl">
<@base title="Cronograma Financeiro - ${orcamento!''}">
<div class="header">
    <div class="titulo">CRONOGRAMA FINANCEIRO</div>
    <div class="subtitulo">${orcamento!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Período</th>
            <th class="text-right">Valor Planejado</th>
            <th class="text-right">Valor Acumulado</th>
            <th class="text-right">% Acumulado</th>
        </tr>
    </thead>
    <tbody>
        <#list cronograma as cm>
        <tr>
            <td>${cm.periodo!""}</td>
            <td class="text-right">${cm.valorPlanejado!""}</td>
            <td class="text-right">${cm.valorAcumulado!""}</td>
            <td class="text-right">${cm.percentual!""}%</td>
        </tr>
        </#list>
    </tbody>
</table>
<h2>Curva S</h2>
<table>
    <tr>
        <th style="width:80px;">Período</th>
        <th>Avanço Acumulado</th>
    </tr>
    <#list cronograma as cm>
    <tr>
        <td>${cm.periodo!""}</td>
        <td>
            <div style="background-color:#337ab7;height:16px;width:${cm.percentual!"0"}%;color:#fff;font-size:8px;text-align:right;padding-right:3px;">
                ${cm.percentual!"0"}%
            </div>
        </td>
    </tr>
    </#list>
</table>
</@base>
