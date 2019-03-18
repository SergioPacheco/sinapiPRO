SinapiPRO = SinapiPRO || {};

SinapiPRO.PesquisaRapidaEtapa = (function() {
	
	function PesquisaRapidaEtapa() {
		
		this.pesquisaRapidaEtapasModal = $('#pesquisaRapidaEtapas');
		this.nomeInput                 = $('#nomeEtapaModal');
		this.pesquisaRapidaBtn         = $('.js-pesquisa-rapida-etapas-btn'); 
		this.containerTabelaPesquisa   = $('#containerTabelaPesquisaRapidaEtapas');
		this.htmlTabelaPesquisa        = $('#tabela-pesquisa-rapida-etapas').html();
		this.template                  = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro              = $('.js-mensagem-erro');
		
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
		
	}
	
	PesquisaRapidaEtapa.prototype.iniciar = function() {
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaEtapasModal.on('shown.bs.modal', onModalShow.bind(this));

	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado(event) {
		event.preventDefault();
		
		$.ajax({
			url: this.pesquisaRapidaEtapasModal.find('form').attr('action'),
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
		
		var tabelaEtapaPesquisaRapida = new SinapiPRO.TabelaEtapaPesquisaRapida(this.pesquisaRapidaEtapasModal);
		tabelaEtapaPesquisaRapida.iniciar();
	} 
	
	function onErroPesquisa() {
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaEtapa; 
	
}());

SinapiPRO.TabelaEtapaPesquisaRapida = (function() {
	
	function TabelaEtapaPesquisaRapida(modal) {
		this.modalEtapa = modal;
		this.etapa = $('.js-etapa-pesquisa-rapida');
	}
	
	TabelaEtapaPesquisaRapida.prototype.iniciar = function() {
		this.etapa.on('click', onEtapaSelecionado.bind(this));
	}
	
	function onEtapaSelecionado(evento) {
		this.modalEtapa.modal('hide');
		
		var etapaSelecionado = $(evento.currentTarget);
		var formData = {
    			nome   : etapaSelecionado.data('nome'),
    			codigo : etapaSelecionado.data('codigo')
		}
	}
	
	return TabelaEtapaPesquisaRapida;
	
}());

$(function() {
	var pesquisaRapidaEtapa = new SinapiPRO.PesquisaRapidaEtapa();
	pesquisaRapidaEtapa.iniciar();
});