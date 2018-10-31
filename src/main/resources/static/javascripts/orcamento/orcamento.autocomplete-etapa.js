SinapiPRO = SinapiPRO || {};

SinapiPRO.AutocompleteEtapa = (function() {

	function AutocompleteEtapa() {
		
		this.inputEtapa       = $('.js-etapa-input');
		var htmlTemplateEtapa = $('#template-autocomplete-etapa').html();
		this.template         = Handlebars.compile(htmlTemplateEtapa);
		this.emitter          = $({});
		this.on               = this.emitter.on.bind(this.emitter);
	}
	
	AutocompleteEtapa.prototype.iniciar = function() {
		console.log('Autocomplete ETAPA iniciou', '');
		 
		var options = {
			url: function(codigoOuNome) {
				return this.inputEtapa.data('url') + '?codigoOuNome=' + codigoOuNome;
			}.bind(this),
			getValue: 'nome',
			minCharNumber: 2,
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
		 
		
		this.inputEtapa.easyAutocomplete(options);
	}
	
	function onItemSelecionado() {
		this.emitter.trigger('etapa-selecionado', this.inputEtapa.getSelectedItemData());
		this.inputEtapa.val('');
		this.inputEtapa.focus();
	}
	
	function template(nome, etapa) {
		return this.template(etapa);
	}
	
	return AutocompleteEtapa
	
}());
