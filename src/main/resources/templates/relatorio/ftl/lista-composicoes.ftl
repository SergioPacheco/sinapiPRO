<#include "base.ftl">
<@base title="Lista de Composições">
<div class="header">
    <div class="titulo">LISTA DE COMPOSIÇÕES</div>
    <div class="subtitulo">Base: ${basePreco!""} | Tipo: ${tipoRelatorio!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Código</th>
            <th>Descrição</th>
            <th>Unidade</th>
            <th>Classe</th>
            <th class="text-right">Custo Unit.</th>
        </tr>
    </thead>
    <tbody>
        <#list composicoes as c>
        <tr>
            <td class="text-center">${c.codigo!""}</td>
            <td>${c.descricao!""}</td>
            <td class="text-center">${c.unidade!""}</td>
            <td>${c.classe!""}</td>
            <td class="text-right">${c.custoUnitario!""}</td>
        </tr>
        <#if tipoRelatorio == "Analítico" && c.itens??>
        <#list c.itens as item>
        <tr style="background-color:#f0f8ff;">
            <td></td>
            <td style="padding-left:20px;">↳ ${item.descricao!""}</td>
            <td class="text-center">${item.unidade!""}</td>
            <td class="text-right">${item.coeficiente!""}</td>
            <td class="text-right">${item.custoUnitario!""}</td>
        </tr>
        </#list>
        </#if>
        </#list>
    </tbody>
</table>
<p><strong>Total de composições: ${composicoes?size}</strong></p>
</@base>
