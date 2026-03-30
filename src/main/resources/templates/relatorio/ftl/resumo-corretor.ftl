<#include "base.ftl">
<@base title="Resumo por Corretor - ${obra!''}">
<div class="header">
    <div class="titulo">RESUMO DE COMISSÕES POR CORRETOR</div>
    <div class="subtitulo">${obra!""} | Emissão: ${emissao!""}</div>
</div>
<#list corretores as corretor>
<h2>${corretor.nome!""}</h2>
<table>
    <thead>
        <tr>
            <th>Unidade</th>
            <th>Cliente</th>
            <th>Data Venda</th>
            <th class="text-right">Valor Venda</th>
            <th class="text-right">% Comissão</th>
            <th class="text-right">Valor Comissão</th>
            <th>Situação</th>
        </tr>
    </thead>
    <tbody>
        <#list corretor.comissoes as c>
        <tr>
            <td>${c.unidade!""}</td>
            <td>${c.cliente!""}</td>
            <td>${c.dataVenda!""}</td>
            <td class="text-right">${c.valorVenda!""}</td>
            <td class="text-right">${c.percentual!""}%</td>
            <td class="text-right">${c.valorComissao!""}</td>
            <td>${c.situacao!""}</td>
        </tr>
        </#list>
        <tr class="total-row">
            <td colspan="5">Subtotal ${corretor.nome!""}</td>
            <td class="text-right">${corretor.totalComissoes!""}</td>
            <td></td>
        </tr>
    </tbody>
</table>
</#list>
<div class="total-row" style="margin-top:8px;padding:4px 6px;border:1px solid #ccc;">
    Total Geral de Comissões: R$ ${totalGeralComissoes!"0,00"}
</div>
</@base>
