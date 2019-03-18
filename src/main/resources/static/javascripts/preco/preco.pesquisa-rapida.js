SinapiPRO = SinapiPRO || {};

SinapiPRO.PesquisaRapidaPreco = (function() {
	
	function PesquisaRapidaPreco() {
		this.precosBtn         = $('.js-precos-btn')
		this.modal             = $('#pesquisaRapidaPrecos');
		this.containerTabela   = $('#containerTabelaPesquisaRapidaPrecos');
		this.htmlTabela        = $('#tabela-pesquisa-rapida-preco').html();
		this.template          = Handlebars.compile(this.htmlTabela);
		this.mensagemErro      = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaPreco.prototype.iniciar = function() {
		
		this.precosBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.modal.on('shown.bs.modal');
	}
	
	
	function onPesquisaRapidaClicado(event) {
		event.preventDefault();
		
		var botaoClicado = $(event.currentTarget);
		var url = botaoClicado.data('url');
		var codigo = botaoClicado.data('love');
		console.log('CODIGO DO INSUMO= ', codigo);
		
		$.ajax({
			url: '/insumos/precos',
			method: 'GET',
			contentType: 'application/json',
			data: { codigoInsumo: codigo}, 
			success: onPesquisaConcluida.bind(this),
			error: onErroPesquisa.bind(this)
		});
	}
	
	function onPesquisaConcluida(resultado) {
		
		console.log('resultado=', resultado);
		this.mensagemErro.addClass('hidden');
		var html = this.template(resultado);
		this.containerTabela.html(html);
	} 
	
	function onErroPesquisa() {
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaPreco;
	
}());


$(function() {
	var pesquisaRapidaPreco = new SinapiPRO.PesquisaRapidaPreco();
	pesquisaRapidaPreco.iniciar();
});


