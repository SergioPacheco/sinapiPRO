var sinapiPRO = sinapiPRO || {};

sinapiPRO.EstiloCadastroRapido = (function() {
	
	function EtapaCadastroRapido() {
		this.modal = $('#modalCadastroRapidoEtapa');
		this.botaoSalvar = this.modal.find('.js-modal-cadastro-etapa-salvar-btn');
		this.form = this.modal.find('form');
		this.url = this.form.attr('action');
		this.inputNomeEtapa = $('#nomeEtapa');
		this.containerMensagemErro = $('.js-mensagem-cadastro-rapido-etapa');
	}
	
	EtapaCadastroRapido.prototype.iniciar = function() {
		this.form.on('submit', function(event) { event.preventDefault() });
		this.modal.on('shown.bs.modal', onModalShow.bind(this));
		this.modal.on('hide.bs.modal', onModalClose.bind(this))
		this.botaoSalvar.on('click', onBotaoSalvarClick.bind(this));
	}
	
	function onModalShow() {
		this.inputNomeEtapa.focus();
	}
	
	function onModalClose() {
		this.inputNomeEtapa.val('');
		this.containerMensagemErro.addClass('hidden');
		this.form.find('.form-group').removeClass('has-error');
	}
	
	function onBotaoSalvarClick() {
		var nomeEtapa = this.inputNomeEtapa.val().trim();
		$.ajax({
			url: this.url,
			method: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({ nome: nomeEtapa }),
			error: onErroSalvandoEtapa.bind(this),
			success: onEtapaSalvo.bind(this)
		});
	}
	
	function onErroSalvandoEtapa(obj) {
		var mensagemErro = obj.responseText;
		this.containerMensagemErro.removeClass('hidden');
		this.containerMensagemErro.html('<span>' + mensagemErro + '</span>');
		this.form.find('.form-group').addClass('has-error');
	}
	
	function onEtapaSalvo(etapa) {
		var comboEtapa = $('#etapa');
		comboEstilo.append('<option value=' + estilo.codigo + '>' + estilo.nome + '</option>');
		comboEstilo.val(estilo.codigo);
		this.modal.modal('hide');
	}
	return EtapaCadastroRapido;
	
}());

$(function() {
	var etapaCadastroRapido = new sinapiPRO.EtapaCadastroRapido();
	etapaCadastroRapido.iniciar();
});
