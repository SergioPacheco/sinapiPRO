var SinapiPRO = SinapiPRO || {};

SinapiPRO.ComboClasse = (function() {
	
	function ComboClasse() {
		this.combo = $('#classe');
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	ComboClasse.prototype.iniciar = function() {
		this.combo.on('change', onClasseAlterado.bind(this));
	}
	
	function onClasseAlterado() {
		this.emitter.trigger('alterado', this.combo.val());
	}
	
	return ComboClasse;
	
}());

SinapiPRO.ComboGrupo = (function() {
	
	function ComboGrupo(comboClasse) {
		this.comboClasse = comboClasse;
		this.combo = $('#grupo');
		this.imgLoading = $('.js-img-loading');
		this.inputHiddenGrupoSelecionado = $('#inputHiddenGrupoSelecionado');
	}
	
	ComboGrupo.prototype.iniciar = function() {
		reset.call(this);
		this.comboClasse.on('alterado', onClasseAlterado.bind(this));
		var codigoClasse = this.comboClasse.combo.val();
		inicializarGrupos.call(this, codigoClasse);
	}
	
	function onClasseAlterado(evento, codigoClasse) {
		this.inputHiddenGrupoSelecionado.val('');
		inicializarGrupos.call(this, codigoClasse);
	}
	
	function inicializarGrupos(codigoComposicaoClasse) {
		console.log('GRUPO Inicializado', codigoComposicaoClasse);
		if (codigoComposicaoClasse) {
			var resposta = $.ajax({
				url: this.combo.data('url'),
				method: 'GET',
				contentType: 'application/json',
				data: { 'classe': codigoComposicaoClasse }, 
				beforeSend: iniciarRequisicao.bind(this),
				complete: finalizarRequisicao.bind(this)
			});
			resposta.done(onBuscarGruposFinalizado.bind(this));
		} else {
			reset.call(this);
		}
	}
	
	function onBuscarGruposFinalizado(grupos) {
		console.log('GRUPO Finalizado', grupos);
		var options = [];
		grupos.forEach(function(grupos) {
			options.push('<option value="' + grupos.codigo + '">' + grupos.nome + '</option>');
		});
		
		this.combo.html(options.join(''));
		this.combo.removeAttr('disabled');
		
		var codigoGrupoSelecionado = this.inputHiddenGrupoSelecionado.val();
		if (codigoGrupoSelecionado) {
			this.combo.val(codigoGrupoSelecionado);
		}
	}
	
	function reset() {
		this.combo.html('<option value="">Selecione o Grupo</option>');
		this.combo.val('');
		this.combo.attr('disabled', 'disabled');
	}
	
	function iniciarRequisicao() {
		reset.call(this);
		this.imgLoading.show();
	}
	
	function finalizarRequisicao() {
		this.imgLoading.hide();
	}
	
	return ComboGrupo;
	
}());

$(function() {
	
	var comboClasse = new SinapiPRO.ComboClasse();
	comboClasse.iniciar();
	
	var comboGrupo = new SinapiPRO.ComboGrupo(comboClasse);
	comboGrupo.iniciar();
	
});

