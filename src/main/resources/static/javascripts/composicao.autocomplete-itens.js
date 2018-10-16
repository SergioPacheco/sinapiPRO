sinapiPRO = sinapiPRO || {};

sinapiPRO.Autocomplete = (function(){

	function Autocomplete(){           
		this.codigoOuDescricaoInput = $('.js-codigo-descricao-composicao-input');
		var htmlTemplateAutocomplete = $('#template-autocomplete-item').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	Autocomplete.prototype.iniciar = function(){
		var options = {
				url: function(codigoOuDescricao){
					return this.codigoOuDescricaoInput.data('url') + '?codigoOuDescricao=' + codigoOuDescricao;
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
		
		this.codigoOuDescricaoInput.easyAutocomplete(options);
	}
	
	function onItemSelecionado(){
		this.emitter.trigger('item-selecionado', this.codigoOuDescricaoInput.getSelectedItemData());
		this.codigoOuDescricaoInput.val('');
		this.codigoOuDescricaoInput.focus();
	}
	
	function template(nome, item){
		insumo.valorFormatado = sinapiPRO.formatarMoeda(item.preco);
		return this.template(item);
	}
	
	return Autocomplete
	
}());
