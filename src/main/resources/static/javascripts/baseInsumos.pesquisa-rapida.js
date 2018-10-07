sinapiPRO = sinapiPRO || {};

sinapiPRO.PesquisaRapidaBaseInsumoo = (function() {
	
	function PesquisaRapidaBasePreco() {
		this.pesquisaRapidaBaseInsumoModal = $('#pesquisaRapidaBaseInsumos');
		this.nomeInput = $('#nomeBaseInsumoModal');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-baseInsumos-btn'); 
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaBaseInsumos');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-baseInsumo').html();
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaBaseInsumo.prototype.iniciar = function() {
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaBaseInsumosModal.on('shown.bs.modal', onModalShow.bind(this));

	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado(event) {
		event.preventDefault();
		
		$.ajax({
			url: this.pesquisaRapidaBaseInsumosInsumosModal.find('form').attr('action'),
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
		
		var tabelaBaseInsumoPesquisaRapida = new sinapiPRO.TabelaBaseInsumoPesquisaRapida(this.pesquisaRapidaBaseInsumosModal);
		tabelaInsumoPesquisaRapida.iniciar();
	} 
	
	function onErroPesquisa() {
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaBaseInsumo;
	
}());

sinapiPRO.TabelaBaseInsumoPesquisaRapida = (function() {
	
	function TabelaBaseInsumoPesquisaRapida(modal) {
		this.modalBaseInsumo = modal;
		this.baseInsumo = $('.js-baseInsumo-pesquisa-rapida');
	}
	
	TabelaBaseInsumoPesquisaRapida.prototype.iniciar = function() {
		this.baseInsumo.on('click', onBaseInsumoSelecionado.bind(this));
	}
	
	function onBaseInsumoSelecionado(evento) {
		this.modalBaseInsumo.modal('hide');
		
		var baseInsumoSelecionado = $(evento.currentTarget);
		$('#nomeBaseInsumo').val(baseInsumoSelecionado.data('nome'));
		$('#codigoBaseInsumo').val(baseInsumoSelecionado.data('codigo'));
	}
	
	return TabelaBaseInsumoPesquisaRapida;
	
}());

$(function() {
	var pesquisaRapidaBaseInsumo = new sinapiPRO.PesquisaRapidaBaseInsumo();
	pesquisaRapidaBaseInsumo.iniciar();
});