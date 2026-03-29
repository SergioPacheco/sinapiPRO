<#include "base.ftl">
<@base title="Orçamento - ${orcamento.nome!''}">
<div class="header">
    <div class="titulo">ORÇAMENTO</div>
    <div class="subtitulo">${orcamento.nome!""} | ${orcamento.tipoOrcamento!""} | Emissão: ${emissao!""}</div>
</div>
<table style="margin-bottom:10px;">
    <tr><td><strong>Obra:</strong> ${orcamento.obra!""}</td><td><strong>Cliente:</strong> ${orcamento.cliente!""}</td></tr>
    <tr><td><strong>Estado:</strong> ${orcamento.estado!""}</td><td><strong>Situação:</strong> ${orcamento.situacao!""}</td></tr>
</table>
<table>
    <thead>
        <tr>
            <th>Item</th>
            <th>Descrição</th>
            <th>Und</th>
            <th class="text-right">Qtd</th>
            <th class="text-right">Vl.Unit.</th>
            <th class="text-right">MO</th>
            <th class="text-right">Material</th>
            <th class="text-right">Equip.</th>
            <th class="text-right">Total</th>
        </tr>
    </thead>
    <tbody>
        <#list itens as item>
        <tr>
            <td>${item.itemizacao!""}</td>
            <td>${item.descricao!""}</td>
            <td class="text-center">${item.unidade!""}</td>
            <td class="text-right">${item.quantidade!""}</td>
            <td class="text-right">${item.valorUnitario!""}</td>
            <td class="text-right">${item.valorMaoObra!""}</td>
            <td class="text-right">${item.valorMaterial!""}</td>
            <td class="text-right">${item.valorEquipamento!""}</td>
            <td class="text-right">${item.valorTotal!""}</td>
        </tr>
        </#list>
    </tbody>
    <tfoot>
        <tr class="total-row"><td colspan="5">Sub-Total</td><td class="text-right">${totalMaoObra!""}</td><td class="text-right">${totalMaterial!""}</td><td class="text-right">${totalEquipamento!""}</td><td class="text-right">${subTotal!""}</td></tr>
        <tr><td colspan="8">Leis Sociais (${percLeisSociais!""}%)</td><td class="text-right">${valorLeisSociais!""}</td></tr>
        <tr><td colspan="8">BDI (${percBdi!""}%)</td><td class="text-right">${valorBdi!""}</td></tr>
        <tr><td colspan="8">Taxa Adm. (${percTaxaAdm!""}%)</td><td class="text-right">${valorTaxaAdm!""}</td></tr>
        <tr class="total-row"><td colspan="8">TOTAL GERAL</td><td class="text-right">${totalGeral!""}</td></tr>
    </tfoot>
</table>
</@base>
