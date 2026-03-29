<#include "base.ftl">
<@base title="Composição - ${composicao.descricao!''}">
<div class="header">
    <div class="titulo">COMPOSIÇÃO DETALHADA</div>
    <div class="subtitulo">Emissão: ${emissao!""}</div>
</div>
<table style="margin-bottom:10px;">
    <tr><td><strong>Código:</strong> ${composicao.codigo!""}</td><td><strong>Descrição:</strong> ${composicao.descricao!""}</td></tr>
    <tr><td><strong>Unidade:</strong> ${composicao.unidade!""}</td><td><strong>Classe:</strong> ${composicao.classe!""}</td></tr>
</table>
<h2>Itens da Composição</h2>
<table>
    <thead>
        <tr>
            <th>Código</th>
            <th>Descrição</th>
            <th>Und</th>
            <th class="text-right">Coeficiente</th>
            <th class="text-right">Preço Unit.</th>
            <th class="text-right">Custo</th>
        </tr>
    </thead>
    <tbody>
        <#list itens as item>
        <tr>
            <td class="text-center">${item.codigo!""}</td>
            <td>${item.descricao!""}</td>
            <td class="text-center">${item.unidade!""}</td>
            <td class="text-right">${item.coeficiente!""}</td>
            <td class="text-right">${item.precoUnitario!""}</td>
            <td class="text-right">${item.custo!""}</td>
        </tr>
        </#list>
    </tbody>
    <tfoot>
        <tr class="total-row"><td colspan="5">CUSTO TOTAL</td><td class="text-right">${custoTotal!""}</td></tr>
    </tfoot>
</table>
</@base>
