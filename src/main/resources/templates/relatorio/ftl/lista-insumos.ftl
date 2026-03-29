<#include "base.ftl">
<@base title="Lista de Insumos">
<div class="header">
    <div class="titulo">LISTA DE INSUMOS</div>
    <div class="subtitulo">Base: ${baseInsumo!""} | ${filtroEspecie!""} | Emissão: ${emissao!""}</div>
</div>
<table>
    <thead>
        <tr>
            <th>Código</th>
            <th>Descrição</th>
            <th>Unidade</th>
            <th>Espécie</th>
            <th class="text-right">Preço</th>
        </tr>
    </thead>
    <tbody>
        <#list insumos as i>
        <tr>
            <td class="text-center">${i.codigo!""}</td>
            <td>${i.descricao!""}</td>
            <td class="text-center">${i.unidade!""}</td>
            <td class="text-center">${i.especie!""}</td>
            <td class="text-right">${i.preco!""}</td>
        </tr>
        </#list>
    </tbody>
</table>
<p><strong>Total de insumos: ${insumos?size}</strong></p>
</@base>
