SinapiPRO = SinapiPRO || {};

SinapiPRO.AutocompleteInsumo = (function() {

	function AutocompleteInsumo() {
		
		this.inputInsumo       = $('.js-insumo-input');
		var htmlTemplateInsumo = $('#template-autocomplete-insumo').html();
		this.template          = Handlebars.compile(htmlTemplateInsumo);
		this.emitter           = $({});
		this.on                = this.emitter.on.bind(this.emitter);
	}
	
	AutocompleteInsumo.prototype.iniciar = function() {
		console.log('autocomplete INSUMO iniciou', '');
		var options = {
			url: function(porDescricao) {
				return this.inputInsumo.data('url') + '?porDescricao=' + porDescricao;
			}.bind(this),
			getValue: 'descricao',
			minCharNumber: 3,
			requestDelay: 300,
			ajaxSettings: {
				contentType: 'application/json'
			},
			template: {
				type: 'custom',
				method: template.bind(this)
			},
			list: {
				onChooseEvent: onItemSelecionado.bind(this)
			}
		};
		
		this.inputInsumo.easyAutocomplete(options);
	}
	
	function onItemSelecionado() {
		this.emitter.trigger('insumo-selecionado', this.inputInsumo.getSelectedItemData());
		this.inputInsumo.val('');
		this.inputInsumo.focus();
	}
	
	function template(descricao, insumo) {
		insumo.valorFormatado = SinapiPRO.formatarMoeda(insumo.precoPadrao);
		return this.template(insumo);
	}
	
	return AutocompleteInsumo
	
}());
