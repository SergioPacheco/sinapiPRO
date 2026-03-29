<#include "base.ftl">
<@base title="Curva S - ${orcamento!''}">
<div class="header">
    <div class="titulo">CURVA S — AVANÇO ACUMULADO</div>
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
<h2>Gráfico Curva S</h2>
<table>
    <tr>
        <th style="width:80px;">Período</th>
        <th>Avanço Acumulado</th>
    </tr>
    <#list cronograma as cm>
    <tr>
        <td>${cm.periodo!""}</td>
        <td style="position:relative;">
            <div style="background-color:#337ab7;height:16px;width:${cm.percentual!"0"}%;color:#fff;font-size:8px;text-align:right;padding-right:3px;">
                ${cm.percentual!"0"}%
            </div>
            <#list [25, 50, 75, 100] as marco>
            <div style="position:absolute;top:0;left:${marco}%;width:1px;height:16px;background-color:#c00;opacity:0.4;"></div>
            </#list>
        </td>
    </tr>
    </#list>
</table>
<div style="margin-top:4px;font-size:8px;color:#999;">
    <span style="color:#c00;">|</span> Marcos: 25% / 50% / 75% / 100%
</div>
<div class="total-row" style="margin-top:8px;padding:4px 6px;border:1px solid #ccc;">
    Total Geral do Orçamento: R$ ${totalGeral!"0,00"}
</div>
</@base>
