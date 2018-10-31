SinapiPRO.Orcamento = (function() {
	 
	function Orcamento(tabelaItens) {
		this.tabelaItens            = tabelaItens;
		this.valorTotalBox          = $('.js-valor-total-box');
		this.valorMaoObraBox  		= $('.js-valor-mao-obra-box');
		this.valorMaterialBox		= $('.js-valor-material-box');
		this.valorEquipamentoBox	= $('.js-valor-equipamento-box');
		
		this.valorTotalItens        = this.tabelaItens.valorTotal();
		this.valorMaoObra           = this.tabelaItens.valorMaoObra();
		this.valorMaterial          = this.tabelaItens.valorMaterial();
		this.valorEquipamento       = this.tabelaItens.valorEquipamento();
		
		
	}
	
	Orcamento.prototype.iniciar = function() {
		this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
		this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
		
		onValoresAlterados.call(this);
	}
	
	function onTabelaItensAtualizada(evento, valorTotalItens, valorMaoObra, valorMaterial, valorEquipamento) {
		this.valorTotalItens  = valorTotalItens  == null ? 0 : valorTotalItens;
		this.valorMaoObra     = valorMaoObra     == null ? 0 : valotMaoObra; 
		this.valorMaterial    = valorMaterial    == null ? 0 : valorMaterial; 
		this.valorEquipamento = valorEquipamento == null ? 0 : valorEquipamento;  
	}
	
	function onValoresAlterados() {
		var valorTotal            = numeral(this.valorTotalItens);
		var valorTotalMaoObra     = numeral(this.valorMaoObra); 
		var valorTotalMaterial    = numeral(this.valorMaterial); 
		var valorTotalEquipamento = numeral(this.valorEquipamento); 
		
		this.valorTotalBox.html(SinapiPRO.formatarMoeda(valorTotal));
		this.valorMaoObraBox.html(SinapiPRO.formatarMoeda(valorTotalMaoObra));
		this.valorMaterialBox.html(SinapiPRO.formatarMoeda(valorTotalMaterial));
		this.valorEquipamentoBox.html(SinapiPRO.formatarMoeda(valorTotalEquipamento));
		
	}
	
	return Orcamento;
	
}());

$(function() {
	/*
	$('#form').submit(function (evt) {
	    evt.preventDefault();
	    window.history.back();
	});
	*/ 
	
	var autocompleteEtapa = new SinapiPRO.AutocompleteEtapa();
	autocompleteEtapa.iniciar();
	
	var autocomplete = new SinapiPRO.Autocomplete();
	autocomplete.iniciar();
	
	var autocompleteInsumo = new SinapiPRO.AutocompleteInsumo();
	autocompleteInsumo.iniciar();
	     
		
	var tabelaItens = new SinapiPRO.TabelaItens(autocompleteInsumo, autocompleteEtapa, autocomplete);
	tabelaItens.iniciar();
	
	var orcamento = new SinapiPRO.Orcamento(tabelaItens);
	orcamento.iniciar();
	
	
});