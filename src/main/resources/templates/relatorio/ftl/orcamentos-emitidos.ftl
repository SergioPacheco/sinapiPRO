<#include "base.ftl">
<@base title="Orçamentos Emitidos">
<div class="header">
    <div class="titulo">ORÇAMENTOS EMITIDOS</div>
    <div class="subtitulo">Período: ${dataInicio!""} a ${dataFim!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Código</th>
            <th>Nome</th>
            <th>Tipo</th>
            <th>Obra</th>
            <th>Cliente</th>
            <th>Situação</th>
            <th class="text-right">Valor Total</th>
        </tr>
    </thead>
    <tbody>
        <#list orcamentos as o>
        <tr>
            <td class="text-center">${o.codigo!""}</td>
            <td>${o.nome!""}</td>
            <td class="text-center">${o.tipoOrcamento!""}</td>
            <td>${o.obra!""}</td>
            <td>${o.cliente!""}</td>
            <td class="text-center">${o.situacao!""}</td>
            <td class="text-right">${o.valorTotal!""}</td>
        </tr>
        </#list>
    </tbody>
</table>
<p><strong>Total de orçamentos: ${orcamentos?size}</strong></p>
</@base>
