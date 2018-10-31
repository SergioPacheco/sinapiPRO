SinapiPRO.TabelaItens = (function() {
	 
	function TabelaItens(autocompleteInsumo, autocompleteEtapa, autocomplete,) {
		this.autocomplete        = autocomplete;
		this.autocompleteEtapa   = autocompleteEtapa;
		this.autocompleteInsumo  = autocompleteInsumo;
		this.tabelaItemContainer = $('.js-tabela-item-container');
		this.uuid                = $('#uuid').val();
		this.selecaoCheckbox     = $('.js-seleciona');
		this.etapaCheckBox       = $('#etapaCheckBox').val();
		this.emitter             = $({});
		this.on                  = this.emitter.on.bind(this.emitter);
		console.log("etapaCheckbox", this.etapaCheckBox); 
	}
	
	TabelaItens.prototype.iniciar = function() {
		this.autocomplete.on('composicao-selecionado', onComposicaoSelecionado.bind(this));
		this.autocompleteInsumo.on('insumo-selecionado',  onInsumoSelecionado.bind(this));
		this.autocompleteEtapa.on('etapa-selecionado', onEtapaSelecionado.bind(this));
		
		this.selecaoCheckbox.on('click', onSelecaoClicado.bind(this));
		
		bindQuantidade.call(this);
		bindTabelaItem.call(this);
	}
	       
	TabelaItens.prototype.valorTotal = function() {
		return this.tabelaItemContainer.data('valor-total');
	}
	TabelaItens.prototype.valorMaoObra = function() {
		return this.tabelaItemContainer.data('valor-mao-obra');
	}
	TabelaItens.prototype.valorMaterial = function() {
		return this.tabelaItemContainer.data('valor-material');
	}
	TabelaItens.prototype.valorEquipamento = function() {
		return this.tabelaItemContainer.data('valor-equipamento');
	}
	
	function onComposicaoSelecionado(evento, item) {
		
		console.log('Selecionado Composicao', item );
		if (this.etapaCheckBox == null) {
			swal('Selecione uma etapa', 'Use thick box', 'error');
		}
		
		this.etapaCheckBox = $('#etapaCheckBox').val();
		console.log('etapaCheckBox', this.etapaCheckBox);
		
		var resposta = $.ajax({
			url: 'composicao/',
			method: 'POST',
			data: {
				codigoEtapa: this.etapaCheckBox,
				codigoComposicao: item.codigo,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onInsumoSelecionado(evento, item) {
		console.log('Selecionado Insumo', item );
		this.etapaCheckBox = $('#etapaCheckBox').val();
		console.log('etapaCheckBox', this.etapaCheckBox);
		
		var resposta = $.ajax({
			url: 'insumo',
			method: 'POST',
			data: {
				codigoEtapa: this.etapaCheckBox,
				codigoInsumo: item.codigoInsumo,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onEtapaSelecionado(evento, item) {
		
		$('#etapaCheckBox').val(item.codigo);
		this.etapaCheckBox = $('#etapaCheckBox').val();
		console.log('etapaCheckBox', this.etapaCheckBox);
		console.log('Selecionado Etapa', item );
		
		var resposta = $.ajax({
			url: 'etapa',
			method: 'POST',
			data: {
				codigoEtapa: item.codigo,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onItemAtualizadoNoServidor(html) {
		this.tabelaItemContainer.html(html);
		
		bindQuantidade.call(this);
		
		var tabelaItem = bindTabelaItem.call(this); 
		this.emitter.trigger('tabela-itens-atualizada', 
				              tabelaItem.data('valor-total'), 
				              tabelaItem.data('valor-mao-obra'), 
				              tabelaItem.data('valor-material'),
				              tabelaItem.data('valor-equipamento'));
	}
	
	function onQuantidadeItemAlterado(evento) {
		
		var input = $(evento.target);
		var quantidade = input.val();
		var codigoItem = input.data('codigo-item');
		var etapa      = input.data('codigo-etapa');
		var tipo       = input.data('tipo');
		
		var strUrl = etapa+"/"+tipo+"/"+codigoItem; 
		console.log('strURL', strUrl);
		
		var resposta = $.ajax({
			url: strUrl,
			method: 'PUT',
			data: {
				uuid: this.uuid,
			    quantidade: quantidade
				
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onDoubleClick(evento) {
		$(this).toggleClass('solicitando-exclusao');
	}
	
	function onExclusaoItemClick(evento) {
		var codigoItem = $(evento.target).data('codigo-item');
		var resposta = $.ajax({
			url: 'item/' + this.uuid + '/' + codigoItem,
			method: 'DELETE'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function bindTabelaItem() {
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('dblclick', onDoubleClick);
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		return tabelaItem;
	}
	
	function onSelecaoClicado(evento) {
		var etapaSelecionada = $(evento.currentTarget);
		$('#etapaCheckBox').val(etapaSelecionada.data('codigo-etapa'));
	}
	
	function bindQuantidade() {
		var quantidadeItemInput = $('.js-tabela-quantidade-item');
		var timout = null; 
		console.log('Qtd', quantidadeItemInput);
		quantidadeItemInput.change = function (e) {
			clearTimeout(timeout);
			timeout = setTimeout(function () {
				console.log('Qtd', timeout );
				
		    }, 500);
		}
		quantidadeItemInput.on('change', onQuantidadeItemAlterado.bind(this));
		quantidadeItemInput.maskNumber({ integer: true, thousands: '' });
		
	}
	
	return TabelaItens;
	
}());
