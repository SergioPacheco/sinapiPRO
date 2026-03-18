SinapiPRO = SinapiPRO || {};

SinapiPRO.PesquisaRapidaObra = (function() {
	
	function PesquisaRapidaObra() {
		this.pesquisaRapidaObrasModal = $('#pesquisaRapidaObras');
		this.nomeInput                   = $('#nomeObraModal');
		this.pesquisaRapidaBtn           = $('.js-pesquisa-rapida-obras-btn'); 
		this.containerTabelaPesquisa     = $('#containerTabelaPesquisaRapidaObras');
		this.htmlTabelaPesquisa          = $('#tabela-pesquisa-rapida-obra').html();
		this.template                    = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro                = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaObra.prototype.iniciar = function() {
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaObrasModal.on('shown.bs.modal', onModalShow.bind(this));

	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado(event) {
		event.preventDefault();
		
		$.ajax({
			url: this.pesquisaRapidaObrasModal.find('form').attr('action'),
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
		
		var tabelaObraPesquisaRapida = new SinapiPRO.TabelaObraPesquisaRapida(this.pesquisaRapidaObrasModal);
		tabelaObraPesquisaRapida.iniciar();
	} 
	
	function onErroPesquisa() {
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaObra;
	
}());

SinapiPRO.TabelaObraPesquisaRapida = (function() {
	
	function TabelaObraPesquisaRapida(modal) {
		this.modalObra = modal;
		this.obra = $('.js-obra-pesquisa-rapida');
	}
	
	TabelaObraPesquisaRapida.prototype.iniciar = function() {
		this.obra.on('click', onObraSelecionado.bind(this));
	}
	
	function onObraSelecionado(evento) {
		this.modalObra.modal('hide');
		
		var obraSelecionado = $(evento.currentTarget);
		$('#nomeObra').val(obraSelecionado.data('nome'));
		$('#codigoObra').val(obraSelecionado.data('codigo'));
	}
	
	return TabelaObraPesquisaRapida;
	
}());

$(function() {
	var pesquisaRapidaObra = new SinapiPRO.PesquisaRapidaObra();
	pesquisaRapidaObra.iniciar();
});