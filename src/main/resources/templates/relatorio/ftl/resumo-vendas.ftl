<#include "base.ftl">
<@base title="Resumo de Vendas - ${obra!''}">
<div class="header">
    <div class="titulo">RESUMO DE VENDAS</div>
    <div class="subtitulo">${obra!""} | Período: ${periodo!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Unidade</th>
            <th>Cliente</th>
            <th>Data Venda</th>
            <th class="text-right">Valor Venda</th>
            <th>Situação</th>
            <th>Parcelas</th>
        </tr>
    </thead>
    <tbody>
        <#list vendas as v>
        <tr>
            <td>${v.unidade!""}</td>
            <td>${v.cliente!""}</td>
            <td>${v.dataVenda!""}</td>
            <td class="text-right">${v.valorVenda!""}</td>
            <td>${v.situacao!""}</td>
            <td class="text-center">${v.totalParcelas!0}</td>
        </tr>
        </#list>
    </tbody>
    <tfoot>
        <tr class="total-row">
            <td colspan="3">TOTAL</td>
            <td class="text-right">${totalVendas!""}</td>
            <td colspan="2"></td>
        </tr>
    </tfoot>
</table>
</@base>
