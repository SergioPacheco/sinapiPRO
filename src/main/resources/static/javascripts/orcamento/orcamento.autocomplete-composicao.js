SinapiPRO = SinapiPRO || {};

SinapiPRO.Autocomplete = (function() {

	function Autocomplete() {
		
		this.inputComposicao       = $('.js-composicao-input');
		var htmlTemplateComposicao = $('#template-autocomplete-composicao').html();
		this.template              = Handlebars.compile(htmlTemplateComposicao);
		this.emitter               = $({});
		this.on                    = this.emitter.on.bind(this.emitter);
	}
	
	Autocomplete.prototype.iniciar = function() {
		console.log('autocomplete COMPOSICAO inicou', '');
		
		var options = {
			url: function(porDescricao) {
				return this.inputComposicao.data('url') + '?porDescricao=' + porDescricao;
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
		
		this.inputComposicao.easyAutocomplete(options);
	}
	
	function onItemSelecionado() {
		this.emitter.trigger('composicao-selecionado', this.inputComposicao.getSelectedItemData());
		this.inputComposicao.val('');
		this.inputComposicao.focus();
	}
	
	function template(nome, composicao) {
		composicao.valorFormatado = SinapiPRO.formatarMoeda(composicao.custoTotal);
		return this.template(composicao);
	}
	
	return Autocomplete
	
}());
