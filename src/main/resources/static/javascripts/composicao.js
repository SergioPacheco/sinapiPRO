sinapiPRO.Composicao = (function(){
	
	function Composicao(tabelaItens){
		// this.tabelaItens            = tabelaItens;
		// this.valorTotalBox          = $('.js-valor-total-box');
		// this.valorTotalBoxContainer = $('.js-valor-total-box-container');
		
		// this.valorTotalItens = this.tabelaItens.valorTotal();
	}
	
	Composicao.prototype.iniciar = function (){
		// this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
		// this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
		// onValoresAlterados.call(this);
		
	}
	
	function onTabelaItensAtualizada(evento, valorTotalItens){
		// this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;
	}
	
	function onValoresAlterados(){
		// Numeral garante que tudo vai ser numero descartando hipotese de numeros como strings
		// var valorTotal = numeral(this.valorTotalItens);
		// this.valorTotalBox.html(sinapiPRO.formatarMoeda(valorTotal));
		// this.valorTotalBoxContainer.toggleClass('negativo', valorTotal < 0);
	}
	
	return Composicao
	
}());

$(function(){
	
	var autocomplete = new sinapiPRO.Autocomplete();
	//autocomplete.iniciar();
	
	var tabelaItens = new sinapiPRO.TabelaItens(autocomplete);
	//tabelaItens.iniciar();
	
	var composicao = new sinapiPRO.composicao(tabelaItens);
	//composicao.iniciar();
	
});