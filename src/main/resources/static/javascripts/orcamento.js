sinapiPRO.Orcamento = (function() {
	
	function Orcamento(tabelaItens) {
		this.tabelaItens = tabelaItens;
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');
		this.valorTotalBoxContainer = $('.js-valor-total-box-container');
		
		this.valorTotalItens = this.tabelaItens.valorTotal();
		this.valorFrete = this.valorFreteInput.data('valor');
		this.valorDesconto = this.valorDescontoInput.data('valor');
	}
	
	Orcamento.prototype.iniciar = function() {
		this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
		this.valorFreteInput.on('keyup', onValorFreteAlterado.bind(this));
		this.valorDescontoInput.on('keyup', onValorDescontoAlterado.bind(this));
		
		this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
		this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
		this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));
		
		onValoresAlterados.call(this);
	}
	
	function onTabelaItensAtualizada(evento, valorTotalItens) {
		this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;
	}
	
	function onValorFreteAlterado(evento) {
		this.valorFrete = sinapiPRO.recuperarValor($(evento.target).val());
	}
	
	function onValorDescontoAlterado(evento) {
		this.valorDesconto = sinapiPRO.recuperarValor($(evento.target).val());
	}
	
	function onValoresAlterados() {
		var valorTotal = numeral(this.valorTotalItens) + numeral(this.valorFrete) - numeral(this.valorDesconto);
		this.valorTotalBox.html(sinapiPRO.formatarMoeda(valorTotal));
		
		this.valorTotalBoxContainer.toggleClass('negativo', valorTotal < 0);
	}
	
	return Orcamento;
	
}());

$(function() {
	
	var autocomplete = new sinapiPRO.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new sinapiPRO.TabelaItens(autocomplete);
	tabelaItens.iniciar();
	
	var orcamento = new sinapiPRO.Orcamento(tabelaItens);
	orcamento.iniciar();
	
});