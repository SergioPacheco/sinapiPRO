SinapiPRO = SinapiPRO || {};

SinapiPRO.Autocomplete = (function(){

	function Autocomplete(){           
		this.porDescricaoInput = $('.js-codigo-descricao-insumo-input');
		var htmlTemplateAutocomplete = $('#template-autocomplete-insumo').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	Autocomplete.prototype.iniciar = function(){
		
		console.log('autocomplete INSUMO iniciou', '');
		var base = this.porDescricaoInput.data('base'); 
		console.log('base: ', base);
		
		var options = {
				url: function(porDescricao){
					 			return this.porDescricaoInput.data('url') 
					 			+ '?porDescricao=' + porDescricao;
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
		
		this.porDescricaoInput.easyAutocomplete(options);
	}
	
	function onItemSelecionado(){
		this.emitter.trigger('item-selecionado', this.porDescricaoInput.getSelectedItemData());
		this.porDescricaoInput.val('');
		this.porDescricaoInput.focus();
	}
	
	function template(nome, insumo){
		insumo.valorFormatado = SinapiPRO.formatarMoeda(insumo.precoPadrao);
		return this.template(insumo);
	}
	
	return Autocomplete
	
}());
