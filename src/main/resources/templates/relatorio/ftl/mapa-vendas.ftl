<#include "base.ftl">
<@base title="Mapa de Vendas - ${obra!''}">
<div class="header">
    <div class="titulo">MAPA DE VENDAS</div>
    <div class="subtitulo">${obra!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Unidade</th>
            <th>Tipo</th>
            <th>Bloco</th>
            <th class="text-right">Área (m²)</th>
            <th class="text-right">Valor Base</th>
            <th>Situação</th>
            <th>Cliente</th>
            <th>Data Venda</th>
        </tr>
    </thead>
    <tbody>
        <#list unidades as u>
        <tr>
            <td>${u.identificacao!""}</td>
            <td>${u.tipo!""}</td>
            <td>${u.bloco!""}</td>
            <td class="text-right">${u.areaPrivativa!""}</td>
            <td class="text-right">${u.valorBase!""}</td>
            <td>${u.situacao!""}</td>
            <td>${u.cliente!""}</td>
            <td>${u.dataVenda!""}</td>
        </tr>
        </#list>
    </tbody>
</table>
<div class="total-row" style="margin-top:8px;padding:4px 6px;border:1px solid #ccc;">
    Total Unidades: ${totalUnidades!0} | Vendidas: ${totalVendidas!0} | Disponíveis: ${totalDisponiveis!0}
</div>
</@base>
