sinapiPRO = sinapiPRO || {};

sinapiPRO.PesquisaRapidaBasePrecos = (function() {
	
	function PesquisaRapidaBasePreco() {
		this.pesquisaRapidaBasePrecoModal = $('#pesquisaRapidaBasePrecos');
		this.nomeInput = $('#nomeBasePrecoModal');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-basePrecos-btn'); 
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaBasePrecos');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-basePrecos').html();
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaBasePreco.prototype.iniciar = function() {
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaBasePrecosModal.on('shown.bs.modal', onModalShow.bind(this));

	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado(event) {
		event.preventDefault();
		
		$.ajax({
			url: this.pesquisaRapidaBasePrecosModal.find('form').attr('action'),
			method: 'GET',
			contentType: 'application/json',
			data: {
				nome: this.nomeInput.val()
			}, 
			success: onPesquisaConcluida.bind(this),
			error: onErroPesquisa.bind(this)
		});
	}
	
	function onPesquisaConcluida(resultado) {
		this.mensagemErro.addClass('hidden');
		
		var html = this.template(resultado);
		this.containerTabelaPesquisa.html(html);
		
		var tabelaBasePrecoPesquisaRapida = new sinapiPRO.TabelaBasePrecoPesquisaRapida(this.pesquisaRapidaBasePrecosModal);
		tabelaBasePrecoPesquisaRapida.iniciar();
	} 
	
	function onErroPesquisa() {
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaBasePreco;
	
}());

sinapiPRO.TabelaBasePrecoPesquisaRapida = (function() {
	
	function TabelaBasePrecoPesquisaRapida(modal) {
		this.modalBasePreco = modal;
		this.basePreco = $('.js-basePreco-pesquisa-rapida');
	}
	
	TabelaBasePrecoPesquisaRapida.prototype.iniciar = function() {
		this.basePreco.on('click', onBasePrecoSelecionado.bind(this));
	}
	
	function onBasePrecoSelecionado(evento) {
		this.modalBasePreco.modal('hide');
		
		var basePrecoSelecionado = $(evento.currentTarget);
		$('#nomeBasePreco').val(basePrecoSelecionado.data('nome'));
		$('#codigoBasePreco').val(basePrecoSelecionado.data('codigo'));
	}
	
	return TabelaBasePrecoPesquisaRapida;
	
}());

$(function() {
	var pesquisaRapidaBasePreco = new sinapiPRO.PesquisaRapidaBasePreco();
	pesquisaRapidaBasePreco.iniciar();
});