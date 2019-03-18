/*
$(function() {

  $("input").maskMoney({
    allowNegative: true,
    thousands: '.',
    decimal: ',',
    affixesStay: false
  });

});
*/
// SINAPI_Preco_Ref_Insumos_RN_201802_NaoDesonerado.xls
//
var SinapiPRO = SinapiPRO || {};

SinapiPRO.MaskMoney = (function() {
	
	function MaskMoney() {
		this.decimal = $('.js-decimal');
		this.plain = $('.js-plain');
		this.coeficiente = $('.js-coeficiente');
	}
	
	MaskMoney.prototype.enable = function() {
		this.decimal.maskNumber({ 
			allowNegative: false, 
			decimal: ',', 
			thousands: '.',
			affixesStay: false
		});
		this.plain.maskNumber({ 
			allowNegative: false, 
			integer: true, 
			thousands: '.', 
			affixesStay: false
		});
		this.coeficiente.maskNumber({ 
			allowNegative: false, 
			precision: 7, 
			decimal: ',', 
			thousands: '.',
			affixesStay: false});
	}
	$('.money').mask("#,##0.00", {reverse: true});
	return MaskMoney;
	
}());

SinapiPRO.MaskPhoneNumber = (function() {
	
	function MaskPhoneNumber() {
		this.inputPhoneNumber = $('.js-phone-number');
	}
	
	MaskPhoneNumber.prototype.enable = function() {
		var maskBehavior = function (val) {
		  return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
		};
		
		var options = {
		  onKeyPress: function(val, e, field, options) {
		      field.mask(maskBehavior.apply({}, arguments), options);
		    }
		};
		
		this.inputPhoneNumber.mask(maskBehavior, options);
	}
	
	return MaskPhoneNumber;
	
}());

SinapiPRO.MaskCep = (function() {
	
	function MaskCep() {
		this.inputCep = $('.js-cep');
	}
	
	MaskCep.prototype.enable = function() {
		this.inputCep.mask('00.000-000');
	}
	
	return MaskCep;
	
}());

SinapiPRO.MaskDate = (function() {
	
	function MaskDate() {
		this.inputDate = $('.js-date');
	}
	
	MaskDate.prototype.enable = function() {
		this.inputDate.mask('00/00/0000');
		this.inputDate.datepicker({
			orientation: 'bottom',
			language: 'pt-BR',
			autoclose: true
		});
	}
	
	return MaskDate;
	
}());

SinapiPRO.Security = (function() {
	
	function Security() {
		this.token = $('input[name=_csrf]').val();
		this.header = $('input[name=_csrf_header]').val();
	}
	
	Security.prototype.enable = function() {
		$(document).ajaxSend(function(event, jqxhr, settings) {
			jqxhr.setRequestHeader(this.header, this.token);
		}.bind(this));
	}
	
	return Security;
	
}());

numeral.language('pt-br');

SinapiPRO.formatarMoeda = function(valor) {
	return numeral(valor).format('0,0.00');
}

SinapiPRO.recuperarValor = function(valorFormatado) {
	return numeral().unformat(valorFormatado);
}

$(function() {
	var maskMoney = new SinapiPRO.MaskMoney();
	maskMoney.enable();
	
	var maskPhoneNumber = new SinapiPRO.MaskPhoneNumber();
	maskPhoneNumber.enable();
	
	var maskCep = new SinapiPRO.MaskCep();
	maskCep.enable();
	
	var maskDate = new SinapiPRO.MaskDate();
	maskDate.enable();
	
	var security = new SinapiPRO.Security();
	security.enable();
	
});
