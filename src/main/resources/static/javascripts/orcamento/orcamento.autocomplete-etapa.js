SinapiPRO = SinapiPRO || {};

SinapiPRO.AutocompleteEtapa = (function() {

	function AutocompleteEtapa() {
		
		this.inputEtapa       = $('.js-etapa-input');
		var htmlTemplateEtapa = $('#template-autocomplete-etapa').html();
		this.template         = Handlebars.compile(htmlTemplateEtapa);
		this.emitter          = $({});
		this.on               = this.emitter.on.bind(this.emitter);
		this.containerMensagemErro = $('.js-mensagem-cadastro-rapido-etapa');
	}
	
	AutocompleteEtapa.prototype.iniciar = function() {
		console.log('Autocomplete ETAPA iniciou', '');
		
		console.log('URL', this.inputEtapa.data('url'));
		 
		var options = {
			url: function(nome) {
				  return 'etapas?nome=' + nome;
			}.bind(this),
			getValue: 'nome', minCharNumber: 2, requestDelay: 300,
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
		var codigoEtapa = this.inputEtapa.getSelectedItemData();
		console.log('getSelectedItemData()', codigoEtapa);;
		$.ajax({
			url: 'etapas/adicionarEtapa',
			method: 'POST',
			contentType: 'application/json',
			data: {
				codigo: 5
			}, 
			error: onErroSalvandoEtapa.bind(this),
			success: onEtapaSalva.bind(this)
		});
		
		this.inputEtapa.val('');
		this.inputEtapa.focus();
	}
	
	function onErroSalvandoEtapa(obj) {
		var mensagemErro = obj.responseText;
		this.containerMensagemErro.removeClass('hidden');
		this.containerMensagemErro.html('<span>' + mensagemErro + '</span>');
		console.log('ERRO ao adicionar etapa', etapa.codigo);;
	}
	
	function onEtapaSalva(etapa) {
		// var comboEtapa = $('#tapa');
		// comboEtapa.append('<option value=' + etapa.codigo + '>' + etapa.nome + '</option>');
		// comboEtapa.val(etapa.codigo);
		console.log('Salvando Etapa', etapa.codigo);
		this.modal.modal('hide');
	}
	
	
	function template(nome, etapa) {
		return this.template(etapa);
	}
	
	
	
	return AutocompleteEtapa
	
}());
$(function() {
	var autocompleteEtapa = new SinapiPRO.AutocompleteEtapa();
	autocompleteEtapa.iniciar();
});


