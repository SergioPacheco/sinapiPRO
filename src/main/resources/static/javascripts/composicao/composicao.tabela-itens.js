SinapiPRO.TabelaItens = (function(){
	
	function TabelaItens(autocomplete){
		this.autocomplete = autocomplete;  
		this.tabelaInsumosContainer = $('.js-tabela-insumos-container');
		this.uuid = $('#uuid').val();
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	// Chamado por composicao.autocomplete-itnes.js ao selecionar item
	TabelaItens.prototype.iniciar = function(){
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		bindQuantidade.call(this);
		bindTabelaItem.call(this);
	}
	
	TabelaItens.prototype.valorTotal = function(){
		return this.tabelaInsumosContainer.data('valor');
	}
	
	function onItemSelecionado(evento, item){
		var resposta = $.ajax({
			url: 'item',
			method: 'POST',
			data: {
				codigo: item.codigo,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onItemAtualizadoNoServidor(html){
		this.tabelaInsumosContainer.html(html);
		
		bindQuantidade.call(this);
		
		var tabelaItem = bindTabelaItem.call(this);
		
		this.emitter.trigger('tabela-itens-atualizada', tabelaItem.data('valor-total'));
		
	}
	
	function onQuantidadeItemAlterado(evento){
		
		var input = $(evento.target);
		var coeficiente = input.val();
		
		var codigoInsumo = input.data('codigo-insumo');
		
		if (coeficiente <= 0) {
			input.val(1);
			coeficiente = 1;
		}
		
		var resposta = $.ajax({
			url: 'item/' + codigoInsumo,
			method: 'PUT',
			data: {
				coeficiente: coeficiente,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onDoubleClick(evento){
		$(this).toggleClass('solicitando-exclusao');
	}
	
	function onExclusaoItemClick(evento){
		
		var codigoItem = $(evento.target).data('codigo-insumo');
		var resposta = $.ajax({
					url: 'item/' + this.uuid + '/' + codigoItem,
					method: 'DELETE'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function bindQuantidade(){
		var quantidadeItemInput = $('.js-tabela-insumo-coeficiente-item');
		quantidadeItemInput.on('change', onQuantidadeItemAlterado.bind(this));
		quantidadeItemInput.maskNumber({ integer: true, thousands: ''});
	}
	
	function bindTabelaItem(){
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('dblclick', onDoubleClick);
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		
		return tabelaItem;
	}
	
	return TabelaItens;
	
}());

