function openAjax() {

	var ajax = false;

	try {
		ajax = new XMLHttpRequest(); // XMLHttpRequest para Firefox, Safari,
		// dentre outros.
	} catch (ee) {
		try {
			ajax = new ActiveXObject("Msxml2.XMLHTTP"); // Para o Internet
			// Explorer
		} catch (e) {
			try {
				ajax = new ActiveXObject("Microsoft.XMLHTTP"); // Para o
				// Internet
				// Explorer
			} catch (E) {
				ajax = false;
			}
		}
	}

	return ajax;
}

function verificaNovoAttChat() {
	verificaNovoAtt();
}

var timeOutVerificaNovoAtt = null;

function verificaNovoAtt() {

	if (timeOutVerificaNovoAtt != null) {
		clearTimeout(timeOutVerificaNovoAtt);
	}

	var res = "teste";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}
	
	verificaPopAjaxDrop();

	var ajax = openAjax();
	ajax.open("POST", "/ajax/VerificaNovoAtt.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
				timeOutVerificaNovoAtt = setTimeout("verificaNovoAtt()", 30000);
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(null);
}

// Chama a funçaoo para notificar sobre novo atendimento e chat
function verificaPopAjax() {
	verificaPop();
}

var nProcessosProcessando = 0;
var timeoutProcessando = null;

function startProcessandoTimeout() {

	// nProcessosProcessando++;

	var pnProcessando = document.getElementById('pnProcessando');

	if (pnProcessando != null) {
		pnProcessando.style.height = document.body.scrollHeight;
		pnProcessando.style.width = document.body.scrollWidth;
		pnProcessando.style.display = '';
		pnProcessando.style.visibility = 'visible';
	}
}

function startProcessando() {

	nProcessosProcessando++;

	if (timeoutProcessando == null) {
		timeoutProcessando = setTimeout('startProcessandoTimeout()', 300);
	}

}

function stopProcessando() {

	nProcessosProcessando--;

	if (nProcessosProcessando < 0) {
		nProcessosProcessando = 0;
	}

	if (nProcessosProcessando == 0) {
		var pnProcessando = document.getElementById('pnProcessando');

		if (pnProcessando != null) {

			if (timeoutProcessando != null) {
				clearTimeout(timeoutProcessando);
			}
			timeoutProcessando = null;

			pnProcessando.style.height = "0px";
			pnProcessando.style.width = "0px";
			pnProcessando.style.display = 'none';
			pnProcessando.style.visibility = 'hidden';
		}
	}
}

var nProcessosPopAjax = 0;

function startPopAjax() {
	nProcessosPopAjax++;
	verificaPopAjax();
}

function stopPopAjax() {

	if (nProcessosPopAjax == 1) {
		verificaPopAjax();
	}

	nProcessosPopAjax--;

	if (nProcessosPopAjax < 0) {
		nProcessosPopAjax = 0;
	}
}

var timeOutVerificaPop = null;

function verificaPop() {

	if (nProcessosPopAjax < 1) {
		if (timeOutVerificaPop != null) {
			clearTimeout(timeOutVerificaPop);
		}
		return;
	}

	if (timeOutVerificaPop != null) {
		clearTimeout(timeOutVerificaPop);
	}

	try {
		if (document.getElementById('telacentral') == null) {
			return;
		}
	} catch (e) {
		return;
	}

	var idAba = document.getElementById('telacentral').parentNode.parentNode.parentNode;

	var id = getParameterUrl("idAba", idAba.URL);

	var res = "idAba=" + id;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/VerificaPop.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
				timeOutVerificaPop = setTimeout("verificaPop()", 750);
				verificaWinDropAcima();
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function respostaPop(resposta) {

	var idAba = document.getElementById('telacentral').parentNode.parentNode.parentNode;

	var id = getParameterUrl("idAba", idAba.URL);

	var res = "res=" + resposta + "&" + "idAba=" + id;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/RespostaPop.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {
		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function verificaPopAjaxDrop() {
	verificaWinDrop();
	// Força mais 2 verificações, pois o init da tela pode demorar para processar.
	try {
		setTimeout("verificaWinDrop()", 500);
		setTimeout("verificaWinDrop()", 1500);
	} catch (e) {
	}
}

function verificaWinDropAcima() {
	if (window != null && window.parent != null) {
		var obj = window.parent.document.getElementById("btVerificaWinDropAcima");
		if (obj != null) {
			obj.click();
		}
	}
}

var timeOutVerificaWin = null;

function verificaWinDrop() {

	if (timeOutVerificaWin != null) {
		clearTimeout(timeOutVerificaWin);
	}

	var res = "idAba=mdi";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	try {
		var ajax = openAjax();
		ajax.open("POST", "/ajax/VerificaWin.jsp" + cParam, true);
		ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
		ajax.setRequestHeader("Content-length", res.length);
		ajax.onreadystatechange = function() {
			if (ajax.readyState == 4) {
				if (ajax.status == 200) {
					eval(ajax.responseText);
				} else {
					// erro ajax
				}
				ajax = null;
			}
		};
		ajax.send(res);

	} catch (e) {
	}
}

function corrigeAlturaIframe() {

	if (document.getElementById('telacentral') == null) {
		return;
	}

	var idAba = document.getElementById('telacentral').parentNode.parentNode.parentNode;
	var id = getParameterUrl("idAba", idAba.URL);
	var nHeight = document.body.scrollHeight;
	var nWidth = document.body.scrollWidth;
	var oIframe = parent.document.getElementById(id + "iFrame");
	if (oIframe == null) {
		return;
	}
	var oBotaoMax = parent.document.getElementById(id + "Maximizar");

	parent.maximizarMdi(id, oBotaoMax, true);

	oIframe.parentNode.parentNode.style.display = "block";

	document.getElementById('btTesteFocus').focus();
}

function enviarAutoCompleteConsultaImovel(id, valor, cMap) {

	if (idFocus == null || id != idFocus) {
		return;
	}

	var cAux = "";
	if (cMap != null && cMap != '') {
		cAux = "&map=" + cMap;
	}

	var res = "id=" + id + "&valor=" + encodeURI(valor) + cAux;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/EditAutoCompleteConsultaImovel.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function enviarAutoComplete(id, valor, cIdAba) {

	valor = replaceAll(valor, '+', '%');
	
	if (idFocus == null || id != idFocus || cIdAba == null || cIdAba == '') {
		return;
	}

	var cAux = "&idAba=" + cIdAba;

	var res = "id=" + id + "&valor=" + encodeURI(valor) + cAux;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/EditAutoComplete.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function verificaMensagemMenu() {
	verificaMensagem(1);
}

var timeOutVerificaMensagem = null;

function verificaMensagem(contador) {

	if (timeOutVerificaMensagem != null) {
		clearTimeout(timeOutVerificaMensagem);
	}

	var res = "idAba=mdi";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/VerificaMensagem.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
				timeOutVerificaMensagem = setTimeout("verificaMensagem(1)", 30000);
			} else {
				if (contador == 1) {
					timeOutVerificaMensagem = setTimeout("verificaMensagem(2)", 30000);
				} else {
					// erro ajax
					invalidaSessao();
				}
			}
			ajax = null;
		}

	};

	ajax.send(res);

}

function invalidaSessao() {
	document.getElementById('edValidaSessaoSair').value = '0';
	window.parent.document.getElementById('pnFormInicial').innerHTML = "<table align='center'><tr><td style='padding-top: 50px; text-align: center;'>Ocorreu um problema de comunica��o com o servidor.</td></tr><tr><td style='text-align: center;'>Ser� necess�rio logar no sistema novamente.</td></tr><tr><td style='text-align: center;'>Se o problema persistir consulte o administrador do sistema.</td></tr></table>";
}

function selecionaMenuMensagem() {

	var res = "chave=selecionaMenuMensagem";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaAtualizarVersao() {

	var res = "chave=selecionaAtualizarVersao";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMostrarSobreVersao() {

	var res = "chave=selecionaMostrarSobreVersao";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMostrarSobre() {

	var res = "chave=selecionaMostrarSobre";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function atualizarModulos() {
	var res = "chave=selecionaAtualizarModulos";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function clickHelpDesk() {
	var res = "chave=selecionaHelpDesk";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMenuNotificacao() {

	var res = "chave=selecionaMenuNotificacao";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMenuSair() {
	window.parent.close();
}

function selecionaMenu(id) {

	var res = "chave=selecionaMenu&id=" + id;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	verificaPopAjaxDrop();

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {
		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				eval(ajax.responseText);
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMenuFerramenta(id) {

	var res = "chave=selecionaMenuFerramenta&id=" + id;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMenuCliente(id) {

	var res = "chave=selecionaMenuCliente&id=" + id;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMenuOculto() {

	var res = "chave=selecionaMenuOculto";

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function selecionaMenuMdi() {

	var edTelaMdi = document.getElementById('edTelaMdi').value;
	var edTelaMdiId = document.getElementById('edTelaMdiId').value;

	var res = "chave=selecionaMenuMdi&edTelaMdi=" + edTelaMdi + "&edTelaMdiId=" + edTelaMdiId;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				eval(ajax.responseText);
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function entrarLogin(cTipo, lProcessando) {

	if (lProcessando) {
		startProcessando();
	}

	var usuario = '';
	var senha = '';
	var empresa = '';

	if (cTipo == 'login') {
		usuario = document.getElementById('edUsuario').value;
		senha = document.getElementById('edSenha').value;
		var edEmpresa = document.getElementById('EscolheEmpresa');
		if (edEmpresa != null) {
			empresa = edEmpresa.value;
		}
	} else if (cTipo == 'loginEsqueciSenha') {
		usuario = document.getElementById('edUsuario').value;
	}

	var res = "tipo=" + cTipo + "&usuario=" + usuario + "&senha=" + senha + "&empresa=" + empresa;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/LoginAjax.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					if (lProcessando) {
						stopProcessando();
					}
					eval(resultado);
				}
			} else {
				// erro ajax
				if (lProcessando) {
					stopProcessando();
				}
			}

			ajax = null;

		}
	};
	ajax.send(res);
}

function selecionaNavegador(nome, versao, mobile, android, ios) {

	var res = "tipo=loginSelecionaNavegador&nome=" + nome + "&versao=" + versao + "&mobile=" + mobile + "&android=" + android + "&ios=" + ios;

	if (FlashDetect != null && FlashDetect != 'undefined' && FlashDetect.installed) {
		res += "&lFlash=true&nFlashVersao=" + FlashDetect.major;
	} else {
		res += "&lFlash=false&nFlashVersao=0";
	}

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/LoginAjax.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function pesquisaMenuAjax(text) {

	if (text == null) {
		text = '';
	}

	var res = "chave=pesquisaMenu&text=" + encodeURIComponent(text);

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=ISO-8859-1");
	// ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function marcaMensagemLida(cod) {

	var res = "chave=marcaMensagemLida&cod=" + cod;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/Menu.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function limpaAjax() {

	if (timeOutVerificaNovoAtt != null) {
		clearTimeout(timeOutVerificaNovoAtt);
		timeOutVerificaNovoAtt = null;
	}

	if (timeOutVerificaPop != null) {
		clearTimeout(timeOutVerificaPop);
		timeOutVerificaPop = null;
	}

	if (timeOutVerificaWin != null) {
		clearTimeout(timeOutVerificaWin);
		timeOutVerificaWin = null;
	}

	if (timeOutVerificaMensagem != null) {
		clearTimeout(timeOutVerificaMensagem);
		timeOutVerificaMensagem = null;
	}

	if (timeOutTempoAcesso != null) {
		clearTimeout(timeOutTempoAcesso);
		timeOutTempoAcesso = null;
	}

	for (var i = 0; i < aTimeOutAgendador.length; i++) {
		if (aTimeOutAgendador[i][1] != null) {
			clearTimeout(aTimeOutAgendador[i][1]);
		}
		aTimeOutAgendador[i][1] = null;
		break;
	}

}

function autoCompleteGridWeb(cMostrarStatus, idGrid, valueInput, idColuna, nLinha) {
	var cParametros = "idGrid=" + idGrid + "&valorCompInput=" + encodeURIComponent(valueInput) + "&idColuna=" + idColuna + "&linha=" + nLinha;
	acaoWindow('AutoCompleteGrid', '', cMostrarStatus, cParametros);
}

function SelectCalendarControl(cId, aDate) {

	var dates = "";
	var cVirgula = "";

	for (var i = 0; i < aDate.length; i++) {
		var nDate = aDate[i].getTime();
		dates += cVirgula + nDate;
		cVirgula = ",";

	}

	var cParametros = "idCalendarControl=" + cId + "&adate=" + dates;

	acaoWindow('SelectCalendarControl', '', '1', cParametros);
}

function SelectCalendarControlWeek(cId, nSemana, nMes, nAno, lCtrl) {

	var cParametros = "idCalendarControl=" + cId + "&cSemana=" + nSemana + "&cMes=" + nMes + "&cAno=" + nAno + "&ctrl=" + lCtrl;

	acaoWindow('SelectCalendarControlWeek', '', '1', cParametros);
}

function downloadGantt(cId, cType, nHeight, nWidth) {
	var cParametros = "idGantt=" + cId + "&type=" + cType + "&height=" + nHeight + "&width=" + nWidth;
	acaoWindow('DownloadGantt', '', '1', cParametros);
}

function blurWeb(cMostrarStatus, comp, idComp) {

	// Não chamar o onBlur quando a divAutoComplete estiver aberta.
	// Ocorre problema pois chama o onBlur sem o valor estar preenchido no campo de código.
	var divAutoComplete = document.getElementById("divAutoComplete");
	if (divAutoComplete != null && divAutoComplete.style.visibility == 'visible') {

		var retorno = true;

		var linhaAtual = divAutoComplete.firstChild.firstChild.firstChild;
		if (linhaAtual != null) {
			var objLinhaAtual = linhaAtual.firstChild.firstChild.firstChild.firstChild.firstChild.firstChild;
			var valor = objLinhaAtual.getAttribute('objValor');
			if (comp.value.toUpperCase() == valor.toUpperCase()) {
				retorno = false;
			}
		}

		if (retorno) {
			return;
		}

	}

	var cParametros = "idComp=" + idComp + "&valorComp=" + encodeURIComponent(comp.value);
	acaoWindow('OnBlur', '', cMostrarStatus, cParametros);
}

function focusWeb(cMostrarStatus, comp, idComp) {
	var cParametros = "idComp=" + idComp;
	acaoWindow('OnFocus', '', cMostrarStatus, cParametros);
}

function buttonClickWeb(cMostrarStatus, comp, idComp) {
	var cParametros = "idComp=" + idComp + "&valorChk=" + comp.checked;
	acaoWindow('ButtonClickCheckBox', '', '1', cParametros);
}

function buttonClickRadioWeb(cMostrarStatus, comp, idComp) {
	var cParametros = "idComp=" + idComp + "&valorItem=" + encodeURIComponent(comp.value);
	acaoWindow('ButtonClickRadio', '', cMostrarStatus, cParametros);
}

function selecionaRelatorio(valor) {
	var cParametros = "valorItem=" + valor;
	acaoWindow('SelecionaRelatorio', '', '0', cParametros);
}

function buttonClickListBoxWeb(cMostrarStatus, comp, idComp) {

	var cValores = "";
	var cVirgula = "";

	// .length
	var childs = comp.childNodes;
	for (var i = 0; i < childs.length; i++) {
		if (childs[i].selected) {
			cValores += cVirgula + childs[i].value;
			cVirgula = ",";
		}
	}

	var cParametros = "idComp=" + idComp + "&valorItem=" + encodeURIComponent(comp.value) + "&valores=" + encodeURIComponent(cValores);
	acaoWindow('ButtonClickListBox', '', cMostrarStatus, cParametros);
}

function dbClickStatus(status) {
	var cParametros = "status=" + status;
	acaoWindow('DbClickStatusDocumento', '', '1', cParametros);
}

function acaoExluirFotoImovel(cIdImagem) {
	var cParametros = "cIdImagem=" + cIdImagem;
	acaoWindow('BotaoExluirFoto', '', '1', cParametros);
}

function acaoUpload(cIdFile, cAcao) {
	var cParametros = "cIdFile=" + cIdFile + "&cAcao=" + cAcao;
	acaoWindow('BotaoUpload', '', '0', cParametros);
}

function acaoUploadAll(cAcao) {
	var cParametros = "cAcao=" + cAcao;
	acaoWindow('BotaoUploadComplete', '', '0', cParametros);
}

function acaoWindow(id, idAba, cMostrarStatus, cParametros) {

	var comp = document.getElementById(id);
	if (comp != null && (comp.getAttribute("type") == "image" || comp.getAttribute("type") == "IMAGE") && (comp.getAttribute("className") == "DESABILITA_OBJETO" || comp.getAttribute("class") == "DESABILITA_OBJETO")) {
		return false;
	}

	startPopAjax();

	if (idAba == null || idAba == '') {
		idAba = document.getElementById("edIdAba").value;
	}

	if (cMostrarStatus == '1') {
		startProcessando();
	}

	var cParams = "";
	if (cParametros != null && cParametros != '') {
		cParams = cParametros;
	}

	var res = "id=" + id + "&idAba=" + idAba + "&" + cParams;

	var compViewState = document.getElementById('javax.faces.ViewState');
	if (compViewState != null) {
		var viewState = compViewState.value;
		if (viewState != null && viewState != '') {
			res += "&viewState=" + viewState;
		}
	}

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/AcaoWindow.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=ISO-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
					stopPopAjax();
					if (cMostrarStatus == '1') {
						stopProcessando();
					}
				}
			} else {
				stopPopAjax();
				if (cMostrarStatus == '1') {
					stopProcessando();
				}
			}
			ajax = null;
		}
	};
	ajax.send(res);

	return false;
}

var timeOutAtivaAtualizacao = null;
var lContinuaAtivaAtualizacao = false;

function ativaAtualizacao() {

	lContinuaAtivaAtualizacao = true;

	startPopAjax();

	var idAba = document.getElementById("edIdAba").value;

	var res = "id=AtivaAtualizacao&idAba=" + idAba;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/AcaoWindow.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
					stopPopAjax();
				}
				if (lContinuaAtivaAtualizacao) {
					timeOutAtivaAtualizacao = setTimeout("ativaAtualizacao()", 1000);
				}
			} else {
				stopPopAjax();
				if (lContinuaAtivaAtualizacao) {
					timeOutAtivaAtualizacao = setTimeout("ativaAtualizacao()", 1000);
				}
			}
			ajax = null;
		}
	};
	ajax.send(res);

	return false;
}

function tempoAcesso() {

	var idAba = document.getElementById("edIdAba").value;
	var res = "idAba=" + idAba;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/AcaoAcesso.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
				ativaTempoAcesso(true);
			} else {
				ativaTempoAcesso(true);
			}
			ajax = null;
		}
	};
	ajax.send(res);

	return false;
}

var timeOutTempoAcesso = null;

function ativaTempoAcesso(lAcessa) {
	if (timeOutTempoAcesso == null || lAcessa) {
		timeOutTempoAcesso = setTimeout("tempoAcesso()", 5000);
	}
}

function salvarGoogleMaps(latitude, longitude, zoom, tipo, univ1_cod) {

	var res = "latitude=" + latitude + "&longitude=" + longitude + "&zoom=" + zoom + "&tipo=" + tipo + "&univ1_cod=" + univ1_cod;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/AcaoGoogleMaps.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
				ativaTempoAcesso(true);
			} else {
				ativaTempoAcesso(true);
			}
			ajax = null;
		}
	};
	ajax.send(res);

	return false;
}

var aTimeOutAgendador = new Array();
var qtdAgendador = -1;

function addAgendador(cId, cAcao, nTempo) {

	qtdAgendador++;

	var nAux = qtdAgendador;

	for (var i = 0; i < aTimeOutAgendador.length; i++) {
		if (aTimeOutAgendador[i][0] == cId) {
			removeAgendador(cId);
			nAux = i;
			break;
		}
	}

	aTimeOutAgendador[nAux] = new Array();
	aTimeOutAgendador[nAux][0] = cId;
	aTimeOutAgendador[nAux][1] = setTimeout("executaAgendador('" + cId + "', '" + cAcao + "', " + nTempo + ")", nTempo);

}

function executaAgendador(cId, cAcao, nTempo) {

	acaoWindow(cAcao, '', '0', '');

	for (var i = 0; i < aTimeOutAgendador.length; i++) {
		if (aTimeOutAgendador[i][0] == cId) {
			aTimeOutAgendador[i][1] = setTimeout("executaAgendador('" + cId + "', '" + cAcao + "', " + nTempo + ")", nTempo);
			break;
		}
	}

}

function removeAgendador(cId) {
	for (var i = 0; i < aTimeOutAgendador.length; i++) {
		if (aTimeOutAgendador[i][0] == cId) {
			if (aTimeOutAgendador[i][1] != null) {
				clearTimeout(aTimeOutAgendador[i][1]);
			}
			aTimeOutAgendador[i][1] = null;
			break;
		}
	}
}

function salvarResizeMenu(width) {

	var res = "width=" + width;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var ajax = openAjax();
	ajax.open("POST", "/ajax/MenuResize.jsp" + cParam, true);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					eval(resultado);
				}
			} else {
				// erro ajax
			}
			ajax = null;
		}
	};
	ajax.send(res);
}

function validaPosicaoElementoCheque(banc1_cod, coch1_pad, insere, posicaoComponentes) {

	var res = "banc1_cod=" + banc1_cod + "&coch1_pad=" + coch1_pad + "&insere=" + insere + "&posicaoComponentes=" + posicaoComponentes;

	var cParam = getParameter("idAcesso");
	if (cParam != null && cParam != '') {
		cParam = "?idAcesso=" + cParam;
	} else {
		cParam = "";
	}

	var result = "";

	var ajax = openAjax();
	ajax.open("POST", "/ajax/AcaoCheque.jsp" + cParam, false);
	ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=iso-8859-1");
	ajax.setRequestHeader("Content-length", res.length);
	ajax.onreadystatechange = function() {

		if (ajax.readyState == 4) {
			if (ajax.status == 200) {
				var resultado = ajax.responseText;
				if (resultado != '') {
					result = resultado;
				}
			}
			ajax = null;
		}
	};
	ajax.send(res);

	return result;
}