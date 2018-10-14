sinapiPRO = sinapiPRO || {};

sinapiPRO.Autocomplete = (function(){

	function Autocomplete(){
		this.skuOuDescricaoInput = $('.js-codigo-descricao-composicao-input');
		var htmlTemplateAutocomplete = $('#template-autocomplete-insumo').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	Autocomplete.prototype.iniciar = function(){
		var options = {
				url: function(skuOuDescricao){
					return this.skuOuDescricaoInput.data('url') + '?skuOuDescricao=' + skuOuDescricao;
				}.bind(this),
				getValue: 'descricao',
				minCharNumber: 3,
				requestDaley: 300,
				ajaxSettings: {
					contentType: 'application/json'
				},
				template:{
					type:'custom',
					method: template.bind(this)
				},
				list: {
					onChooseEvent: onItemSelecionado.bind(this)
				}
		};
		
		this.skuOuDescricaoInput.easyAutocomplete(options);
	}
	
	function onItemSelecionado(){
		this.emitter.trigger('item-selecionado', this.skuOuDescricaoInput.getSelectedItemData());
		this.skuOuDescricaoInput.val('');
		this.skuOuDescricaoInput.focus();
	}
	
	function template(nome, insumo){
		insumo.valorFormatado = sinapiPRO.formatarMoeda(insumo.preco);
		return this.template(insumo);
	}
	
	return Autocomplete
	
}());
