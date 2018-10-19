{
	var browserInfo;
	function newBrowserInfo() {
		obj = new Object;
		obj.isFF = false;
		obj.isOP = false;
		obj.isIE = false;
		obj.isChrome = false;
		obj.isSafari = false;

		obj.init = function() {
			var val = navigator.userAgent.toLowerCase();
			if (val.indexOf("firefox") > -1) {
				browserInfo.isFF = true;
			} else if (val.indexOf("opera") > -1) {
				browserInfo.isOP = true;
			} else if (val.indexOf("msie") > -1) {
				browserInfo.isIE = true;
			} else if (val.indexOf("chrome") > -1) {
				browserInfo.isChrome = true;
			} else if (val.indexOf("safari") > -1) {
				browserInfo.isSafari = true;
			}
		};

		obj.toString = function() {
			if (browserInfo.isFF) {
				return "isFF";
			} else if (browserInfo.isOP) {
				return "isOP";
			} else if (browserInfo.isIE) {
				return "isIE";
			} else if (browserInfo.isChrome) {
				return "isChrome";
			} else if (browserInfo.isSafari) {
				return "isSafari";
			} else {
				return "<desconhecido>";
			}
		};

		return obj;
	}

	// Construtor do objeto que guardará informações dos browsers (para poder usar ao inves de ficar vendo quando é IE se document.all)
	var browserInfo = newBrowserInfo();
	browserInfo.init();

	var lClickLoginOculto = false;

	function blocking(nr, caminhoCss) {
		if (document.layers) {
			current = (document.layers[nr].display == 'none') ? '' : 'none';
			document.layers[nr].display = current;
		} else if (browserInfo.isIE) {
			current = (document.all[nr].style.display == 'none') ? '' : 'none';
			document.all[nr].style.display = current;
		} else if (document.getElementById) {
			vista = (document.getElementById(nr).style.display == 'none') ? '' : 'none';
			document.getElementById(nr).style.display = vista;
		}

		current = (document.getElementById("frmPesquisaMenu").style.display == "none") ? "" : "none";
		document.getElementById("frmPesquisaMenu").style.display = current;
		current = (document.getElementById("frmCollapseTree").style.display == "none") ? "" : "none";
		document.getElementById("frmCollapseTree").style.display = current;

		var compImg = document.getElementById('imgEsconderMenu');
		if (compImg.className.indexOf("FECHAR_MENU") == -1) {
			compImg.className = "FECHAR_MENU";
		} else {
			compImg.className = "ABRIR_MENU";
		}

		redimensionarToolBar();

	}

	function redimensionarToolBar() {

		var toolBar = document.getElementById('MDIToolBar');
		var menuFerramentas = document.getElementById('DivMenuFerramenta');
		var menuFerramentasSub = document.getElementById('DivMenuFerramentaSubSub');

		toolBar.style.width = "50px";
		if (menuFerramentas != null) {
			menuFerramentas.style.width = "50px";
			if (menuFerramentasSub != null) {
				menuFerramentasSub.style.width = menuFerramentasSub.getElementsByTagName("div").length * 30;
			}
		}

		toolBar.style.width = toolBar.parentNode.offsetWidth + "px";
		if (menuFerramentas != null) {
			menuFerramentas.style.width = menuFerramentas.parentNode.offsetWidth + "px";

			if (parseInt(menuFerramentasSub.style.width) > parseInt(menuFerramentas.style.width)) {
				document.getElementById('DivMenuFerramentaVoltar').className = 'VOLTAR_ABA_FER';
				document.getElementById('DivMenuFerramentaAvancar').className = 'AVANCAR_ABA_FER';
			} else {
				document.getElementById('DivMenuFerramentaVoltar').className = 'VOLTAR_ABA_FER_ESC';
				document.getElementById('DivMenuFerramentaAvancar').className = 'AVANCAR_ABA_FER_ESC';
			}

		}

	}

	function MouseOverFecharMenu(button) {
		if (button.className == "FECHAR_MENU")
			button.className = "FECHAR_MENU_SELECIONADO";
		else if (button.className == "ABRIR_MENU")
			button.className = "ABRIR_MENU_SELECIONADO";
	}

	function MouseOutFecharMenu(button) {
		if (button.className == "FECHAR_MENU_SELECIONADO")
			button.className = "FECHAR_MENU";
		else if (button.className == "ABRIR_MENU_SELECIONADO")
			button.className = "ABRIR_MENU";
	}

	function replaceAll(str, de, para) {
		var pos = str.indexOf(de);
		while (pos > -1) {
			str = str.replace(de, para);
			pos = str.indexOf(de);
		}
		return (str);
	}

	function tabenter(evt) {

		evt = getEvent(evt);

		if (getKeyCode(evt) == 13) {
			// if (getKeyCode(evt) == 13 || (!evt.shiftKey && getKeyCode(evt) ==
			// 9)) {

			var elementAtivo = getTarget(evt);

			var nextcomp = getNextElementByName(elementAtivo);
			if (nextcomp != null) {
				try {
					nextcomp.focus();
				} catch (e) {
					// Não dispara um erro mno IE
				}
			} else {
				if (elementAtivo != null) {
					elementAtivo.blur();
				}
			}
			return false;

		}
		return true;
	}

	// Específico para combo box
	function tabenterTab(evt) {

		evt = getEvent(evt);

		if (getKeyCode(evt) == 13 || getKeyCode(evt) == 9) {
			// if (getKeyCode(evt) == 13 || (!evt.shiftKey && getKeyCode(evt) == 9)) {

			var elementAtivo = getTarget(evt);

			var nextcomp = getNextElementByName(elementAtivo);
			if (nextcomp != null) {
				try {
					nextcomp.focus();
				} catch (e) {
					// Não dispara um erro mno IE
				}
			} else {
				if (elementAtivo != null) {
					elementAtivo.blur();
				}
			}
			return false;

		}
		return true;
	}

	function tabenter_explorador(evt) {

		evt = getEvent(evt);

		if (getKeyCode(evt) == 13) {

			var elementAtivo = getTarget(evt);

			document.getElementById('btTBarLocalizar').click();

			return false;

		}
		return true;
	}

	// Recupera o evento do form
	function getEvent(evt) {
		if (!evt)
			evt = window.event; // Internet Explorer
		return evt;
	}

	// Recupera o elemento que está com o foco
	function getTarget(evt) {
		var target = null;

		if (evt.srcElement)
			target = evt.srcElement;
		else if (evt.target)
			target = evt.target;

		return target;
	}

	// Recupera o código da tecla que foi pressionado
	function getKeyCode(evt) {
		var code;

		if (typeof (evt.keyCode) == 'number')
			code = evt.keyCode;
		else if (typeof (evt.which) == 'number')
			code = evt.which;
		else if (typeof (evt.charCode) == 'number')
			code = evt.charCode;
		else
			return 0;

		return code;
	}
	
	function isValidaDataSQLServer(nrAno){
		return nrAno == null || nrAno == undefined ? false:
				!(Number(nrAno) < 1753 || Number(nrAno) > 9999)
	}

	function data_onkeyup(oData) {
		if (oData.value != "") {
			var sData = oData.value.toString();
			var nLen = oData.value.length;
			if (nLen == 2) {
				var sDia = oData.value.toString();
				var nDia = sDia.substring(0, 2);
				if (nDia < 1 || nDia > 31) {
					oData.value = "";
					nLen = 0;
				}
			}
			if (nLen == 2 || nLen == 5) {
				oData.value += "/";
			}
			if (nLen == 4) {
				if (sData.substring(3, 4) == "/") {
					oData.value = sData.substring(0, 3);
				}
			}
			if (nLen == 5) {
				var sMes = oData.value.toString();
				var nMes = 0;
				var sDia = "";
				var nDia = 0;
				nMes = sMes.substring(3, 5);
				if (nMes < 1 || nMes > 12) {
					sDia = oData.value.toString();
					nDia = sMes.substring(0, 3);
					oData.value = nDia;
					alert("Mês : " + sMes.substring(3, 5) + " inválido.");
				}
			}
			if (nLen == 7) {
				var sAno = "";
				var sAno1 = "";
				sAno = oData.value.toString();
				sAno1 = sAno.substring(6);
				if (sData.substring(6, 7) == "/") {
					oData.value = sData.substring(0, 6);
				}
				if (sAno1 == 1) {
					oData.value += "9";
				}
				if (sAno1 == 9) {
					oData.value = sAno.substring(0, 6);
					oData.value += "199";
				}
				if (sAno1 == 0) {
					oData.value = sAno.substring(0, 6);
					oData.value += "200";
				}
			}

			if((nLen > 10) 
					|| ( nLen == 10 && !isValidaDataSQLServer(oData.value.toString().substring(6,10)))){
					oData.value = "01/01/1900";
			}
		}
	}

	function bloqueiaTextArea(qtdMax, comp) {
		if ((qtdMax - comp.value.length) <= 0) {
			comp.value = comp.value.substring(0, qtdMax);
		}
	}

	/* Função Pai de Mascaras */
	function Mascara(o, f) {
		v_obj = o;
		v_fun = f;
		setTimeout("execmascara()", 1);
	}

	/* Função que Executa os objetos */
	function execmascara() {
		v_obj.value = v_fun(v_obj.value);
	}

	/* Função que permite apenas numeros */
	function Integer(v) {
		return v.replace(/\D/g, "");
	}

	/* Função que padroniza telefone (11) 4184-1241 */
	function Telefone(v) {
		v = v.replace(/\D/g, "");
		v = v.replace(/^(\d\d)(\d)/g, "($1) $2");
		v = v.replace(/(\d{4})(\d)/, "$1-$2");
		return v;
	}

	/* Função que padroniza CPF */
	function Cpf(v) {
		v = v.replace(/\D/g, "");
		v = v.replace(/(\d{3})(\d)/, "$1.$2");
		v = v.replace(/(\d{3})(\d)/, "$1.$2");

		v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");

		if (v.length > 14)
			v = v.substring(0, 14);

		return v;
	}

	/* Função que padroniza CEP */
	function Cep(v) {
		v = v.replace(/\D/g, ""); // Remove tudo o que não é dígito
		v = v.replace(/^(\d{5})(\d)/, "$1-$2");
		return v;
	}

	/* Função que padroniza CNPJ */
	function Cnpj(v) {
		v = v.replace(/\D/g, "");
		v = v.replace(/^(\d{2})(\d)/, "$1.$2");
		v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");
		v = v.replace(/\.(\d{3})(\d)/, ".$1/$2");
		v = v.replace(/(\d{4})(\d)/, "$1-$2");
		if (v.length > 18)
			v = v.substring(0, 18);
		return v;
	}

	/* Função que padroniza o Site */
	function Site(v) {
		v = v.replace(/^http:\/\/?/, "");
		dominio = v;
		caminho = "";
		if (v.indexOf("/") > -1)
			dominio = v.split("/")[0];
		caminho = v.replace(/[^\/]*/, "");
		dominio = dominio.replace(/[^\w\.\+-:@]/g, "");
		caminho = caminho.replace(/[^\w\d\+-@:\?&=%\(\)\.]/g, "");
		caminho = caminho.replace(/([\?&])=/, "$1");
		if (caminho != "")
			dominio = dominio.replace(/\.+$/, "");
		v = "http://" + dominio + caminho;
		return v;
	}

	/* Função que padroniza DATA */
	function Data(v) {
		v = v.replace(/\D/g, "");
		v = v.replace(/(\d{2})(\d)/, "$1/$2");
		v = v.replace(/(\d{2})(\d)/, "$1/$2");
		return v;
	}

	/* Função que padroniza DATA */
	function Hora(v) {
		v = v.replace(/\D/g, "");
		v = v.replace(/(\d{2})(\d)/, "$1:$2");
		return v;
	}

	/* Função que padroniza valor monétario */
	function Valor(v) {
		v = v.replace(/\D/g, ""); // Remove tudo o que não é dígito
		v = v.replace(/^([0-9]{3}\.?){3}-[0-9]{2}$/, "$1,$2");
		v = v.replace(/(\d)(\d{2})$/, "$1,$2"); // Coloca ponto antes dos 2
		// últimos digitos
		return v;
	}

	/* Função que padroniza Area */
	function Area(v) {
		v = v.replace(/\D/g, "");
		v = v.replace(/(\d)(\d{2})$/, "$1.$2");
		return v;

	}

	function mascara_valor(fld, decSep, nrdec) {
		v_o = fld;
		v_d = decSep;
		v_n = nrdec;
		setTimeout("execmascaravalor()", 1);
	}

	/* Função que Executa os objetos */
	function execmascaravalor() {
		v_o.value = FormataReais(v_o.value, v_d, v_n);
	}

	function FormataReais(valor, decSep, nrdec) {
		var i = j = 0;
		var len = len2 = 0;
		var aux = aux2 = '';
		var auxSep = (decSep == ',') ? '.' : ',';
		valor = valor.replace(/\D/g, "");
		aux = valor;
		len = aux.length;
		if (len == 0) {
			valor = '';
		} else if (len <= nrdec) {
			valor = '0';
			if (nrdec > 0) {
				valor += decSep;
			}
			for (var ii = 0; ii < (nrdec - len); ii++) {
				valor += '0';
			}
			valor += aux;
		} else if (len > nrdec) {
			for (var jj = 0; jj < (aux.length - (nrdec + 1)); jj++) {
				if (aux.charAt(jj) == '0') {
					aux = aux.substring(1, aux.length);
				} else {
					break;
				}
			}
			len = aux.length;
			aux2 = '';
			for (j = 0, i = len - (nrdec + 1); i >= 0; i--) {
				if (j > 2 && j % 3 == 0)
					aux2 += auxSep;
				aux2 += aux.charAt(i);
				j++;
			}
			valor = '';
			len2 = aux2.length;
			for (i = len2 - 1; i >= 0; i--)
				valor += aux2.charAt(i);
			if (nrdec > 0) {
				valor += decSep;
			}
			valor += aux.substr(len - nrdec, len);
		}
		return valor;
	}

	function selecionaRel(comp, doc, value) {

		var ChamaImpressaoEmail = doc.getElementById('ChamaImpressaoEmail');
		if (ChamaImpressaoEmail != null) {
			doc.getElementById('ChamaImpressaoEmail').className = 'FORMATO_RELATORIO';
		}

		var ChamaImpressaoDownload = doc.getElementById('ChamaImpressaoDownload');
		if (ChamaImpressaoDownload != null) {
			doc.getElementById('ChamaImpressaoDownload').className = 'FORMATO_RELATORIO';
		}

		doc.getElementById('ChamaImpressaoXls').className = 'FORMATO_RELATORIO';
		doc.getElementById('ChamaImpressaoRtf').className = 'FORMATO_RELATORIO';
		doc.getElementById('ChamaImpressaoPdf').className = 'FORMATO_RELATORIO';
		doc.getElementById('ChamaImpressaoCsv').className = 'FORMATO_RELATORIO';

		// doc.getElementById('relSelecionado').value = value;

		comp.className = 'FORMATO_RELATORIO_SELECIONADO';

		selecionaRelatorio(value);
	}

	function selecionaRelCadastro(comp, doc) {

		doc.getElementById('BotaoXls').className = 'FORMATO_RELATORIO';
		doc.getElementById('BotaoRtf').className = 'FORMATO_RELATORIO';
		doc.getElementById('BotaoPdf').className = 'FORMATO_RELATORIO';

		comp.className = 'FORMATO_RELATORIO_SELECIONADO';

	}

	function checkKey(event, comp) {
		if (event.keyCode == 13) {
			comp.blur();
			document.getElementById('btLogin').click();
		}
	}

	function getFirstElement(form, id) {
		var passou = false;

		if (form != null && form.elements.length > 1) {
			for (var i = 0; form.elements.length; i++) {
				var el = form.elements[i];
				if (el == null) {
					return null;
				}
				if (id == null || el.id == id || passou) {
					passou = true;
					// Encontrou o elemento atual
					var x = i + 1;
					var elnx = form.elements[x];

					if (elnx) {
						switch (elnx.type) {
						case "text":
						case "select-one":
						case "checkbox":
						case "image":
						case "password":
						case "radio":
						case "reset":
						case "submit":
						case "textarea":
							if (elnx.disabled || elnx.id == 'inputDateGridInputDate' || elnx.style.display == 'none' || elnx.style.visibility == 'hidden') {
								continue;
							}

							break;
						default:
							continue;
							break;
						}
						return elnx;
					}
				}
			}
		}
		return null;
	}

	// Recupera o próximo elemento de acordo com o nome
	function getNextElementByName(elementAtivo) {
		return getFirstElement(elementAtivo.form, elementAtivo.id);
	}

	function trim(value) {
		return value.replace(/^\s+|\s+$/g, '');
	}

	function Ferramenta(cAction, cParameter, cAgrupar) {
		document.formulario.EXECUTAR.value = cParameter;
		document.formulario.AGRUPAR.value = cAgrupar;
		document.formulario.action = "../Scripts/" + cAction + ".exe";
		document.formulario.submit();
	}

	function load() {
		Ferramenta(getParameter('cAction'), getParameter('cParameter'), getParameter('cAgrupar'));
	}

	function Ferramenta(cAction, cParameter, cAgrupar, cPess2_cod) {
		document.formulario.EXECUTAR.value = cParameter;
		document.formulario.AGRUPAR.value = cAgrupar;
		document.formulario.CPESS2_COD.value = cPess2_cod;
		document.formulario.action = "../Scripts/" + cAction + ".exe";
		document.formulario.submit();
	}

	function load_cliente() {
		Ferramenta(getParameter('cAction'), getParameter('cParameter'), getParameter('cAgrupar'), getParameter('CPESS2_COD'));
	}

	function getParameter(name) {
		try {
			name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
			var regexS = "[\\?&]" + name + "=([^&#]*)";
			var regex = new RegExp(regexS);
			var results = regex.exec(window.location.href);
			if (results == null)
				return "";
			else
				return results[1];

		} catch (e) {
		}
	}

	function getParameterUrl(name, url) {
		name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
		var regexS = "[\\?&]" + name + "=([^&#]*)";
		var regex = new RegExp(regexS);
		var results = regex.exec(url);
		if (results == null)
			return "";
		else
			return results[1];
	}

	function Cadastro(cServerStratoWeb, cCampo, cOperacao, cArquivo, cTitulo, nheight, nWidth, nTop, nLeft, cUsuario) {
		window.open(cServerStratoWeb + "/" + cArquivo + ".asp?FIELDRET=" + cCampo + "&OPERACAO=" + cOperacao + "&TITULO=" + cTitulo + "&CUSUA1_COD=" + cUsuario, cOperacao, "height=" + nheight + ",width=" + nWidth + ",status=no,toolbar=no,menubar=no,location=no,top=" + nTop + ",left=" + nLeft + ",scrollbars=no");
	}

	function CadastroScroll(cServerStratoWeb, cStratoBaseDados, cCampo, cOperacao, cArquivo, cTitulo, nheight, nWidth, nTop, nLeft, cUsuario) {
		window.open(cServerStratoWeb + "/" + cArquivo + ".asp?STRA_BASEDADOS=" + cStratoBaseDados + "&FIELDRET=" + cCampo + "&OPERACAO=" + cOperacao + "&TITULO=" + cTitulo + "&CUSUA1_COD=" + cUsuario, cOperacao + cCampo, "height=" + nheight + ",width=" + nWidth + ",status=yes,toolbar=no,menubar=no,location=no,top=" + nTop + ",left=" + nLeft + ",scrollbars=yes,resizable=yes");
	}

	function CadastroPopScroll(cServerStratoWeb, cStratoBaseDados, cCampo, cOperacao, cArquivo, cTitulo, nheight, nWidth, nTop, nLeft, cUsuario) {
		return cServerStratoWeb + "/" + cArquivo + ".asp?STRA_BASEDADOS=" + cStratoBaseDados + "&FIELDRET=" + cCampo + "&OPERACAO=" + cOperacao + "&TITULO=" + cTitulo + "&CUSUA1_COD=" + cUsuario, cOperacao + cCampo, "height=" + nheight + ",width=" + nWidth + ",status=yes,toolbar=no,menubar=no,location=no,top=" + nTop + ",left=" + nLeft + ",scrollbars=yes,resizable=yes";
	}

	function AbrirTela(evento, id) {
		var oDiv = document.getElementById("DIV_OCULTA_TELA");

		document.getElementById("ihIdTextArea").value = id;

		document.getElementById("taObs").value = document.getElementById(id).value;

		// Posiciona a DIV.

		var dimensaoComponente = parseInt(evento.clientX + oDiv.offsetWidth);

		if (dimensaoComponente > oDiv.parentNode.scrollWidth) {
			oDiv.style.left = evento.clientX + (oDiv.parentNode.scrollWidth - dimensaoComponente) + "px";
		} else {
			oDiv.style.left = evento.clientX + "px";
		}

		oDiv.style.top = evento.clientY + document.body.scrollTop + "px";

		// Deixa a DIV visivel.
		oDiv.style.display = "block";

	}

	function googlemaps(obj) {

		var latitude = '';
		var longitude = '';
		var zoom = '';
		var tipo = '';
		var univ1_cod = document.getElementById('edUniv1_cod').value;

		if (obj != null) {
			latitude = obj.getCenter().lat();
			longitude = obj.getCenter().lng();
			zoom = obj.getZoom();
			tipo = obj.mapTypeId;
		}

		salvarGoogleMaps(latitude, longitude, zoom, tipo, univ1_cod);

	}

	// ////////////////////////////////////////////////////////////////////
	// Inicio dos metodos de formatação de maskara de valores numericos //
	// ////////////////////////////////////////////////////////////////////

	function edicaoValor(campo, event, inteiros, decimais) {

		if (decimais > 0) {

			var cCampo = campo.value;

			var indice = cCampo.indexOf(',');

			if (indice > -1) {// se tiver virgula, prenche no formato correto

				var aux = cCampo.substring(indice + 1, cCampo.length);
				if (aux.length > decimais) {

					aux = aux.substring(0, decimais);
					cCampo = cCampo.substring(0, indice + 1) + aux;

					campo.value = cCampo;

				}

			}

		}

	}

	// edita o valor apos sair do campo
	function editValorOnBlur(campo, event, inteiros, decimais, maxValue) {

		// retira todos os espaços vazios
		campo.value = trim(campo.value);
		if (campo.value == null || campo.value == "") {
			campo.value = 0;
		}

		// verifica se o valor possui mais de uma virgila
		var count = 0;
		if (campo.value.length > 0) {

			for (var i = 0; i <= campo.value.length; i++) {

				if (campo.value.charCodeAt(i) == 44) {
					count++;
				}
			}

			if (count > 1) {
				campo.value = "";
				alert("O valor informado está incorreto !");
				return false;
			}
		}

		var lNegativo = false;

		var cCampo = "";
		// retira os digitos incorretos do campo
		if (campo.value.length > 0) {

			if (campo.value.charAt(0) == '-') {
				lNegativo = true;
			}

			for (var i = 0; i <= campo.value.length; i++) {

				if (!isNaN(campo.value.charAt(i)) || campo.value.charCodeAt(i) == 44) {
					cCampo += campo.value.charAt(i);
				}

			}

			cCampo = retirarZeroEsquerda(cCampo);

		}

		if (cCampo == null || cCampo == '') {
			cCampo = '0';
		}

		// valida a parte correspondente aos decimais
		if (decimais > 0) {

			var indice = cCampo.indexOf(',');

			if (indice > -1) {// se tiver virgula, prenche no formato correto

				var aux = cCampo.substring(indice + 1, cCampo.length);
				if (aux.length > decimais) {

					aux = aux.substring(0, decimais);
					cCampo = cCampo.substring(0, indice + 1) + aux;
				} else if (aux.length < decimais) {

					var qtde = decimais - aux.length;
					for (var j = 0; j < qtde; j++) {
						aux += "0";
					}
					cCampo = cCampo.substring(0, indice + 1) + aux;
				}

			} else { // se não tiver virgula, prenche no formato correto

				cCampo += ",";
				for (var j = 0; j < decimais; j++) {
					cCampo += '0';
				}
			}

			// verifica se existe numeros antes da virgula
			indice = cCampo.indexOf(',');
			var aux = cCampo.substring(0, indice);

			if (aux.length == 0) {
				cCampo = "0" + cCampo.substring(0, cCampo.length);
			}
		}

		// valida a parte correspondente aos inteiros
		if (inteiros > 0) {

			var indice = cCampo.indexOf(',');

			if (indice > -1) { // retira os digitos que ultrapassam o limite estabelecido pelo parametro

				var aux = cCampo.substring(0, indice);
				if (aux.length > inteiros) {

					cCampo = aux.substring(indice - inteiros, indice) + cCampo.substring(indice, cCampo.length);
				}

			} else {// retira os digitos que ultrapassam o limite estabelecido pelo parametro

				if (cCampo.length > inteiros) {

					cCampo = cCampo.substring(cCampo.length - inteiros, cCampo.length);
				}
			}
		}

		// coloca pontos para formatar os milhares
		var indice = cCampo.indexOf(',');

		if (indice > -1) {

			var camposMilhar = cCampo.substring(0, indice);

			var aux = "";
			var count = camposMilhar.length / 3;

			var i = camposMilhar.length - 3;

			while (count > 0) {
				aux = "." + camposMilhar.substring(i, i + 3) + aux;
				i = i - 3;
				count--;
			}

			if (aux.charCodeAt(0) == 46)
				aux = aux.replace(".", "");

			cCampo = aux + cCampo.substring(indice, cCampo.length);

		} else {

			var aux = "";
			var count = cCampo.length / 3;

			i = cCampo.length - 3;

			while (count > 0) {
				aux = "." + cCampo.substring(i, i + 3) + aux;
				i = i - 3;
				count--;
			}

			if (aux.charCodeAt(0) == 46)
				aux = aux.replace(".", "");

			cCampo = aux;
		}

		if (lNegativo) {
			cCampo = '-' + cCampo;
		}

		if (maxValue != null && maxValue != undefined && maxValue != '' && maxValue != 'null') {
			var valorAux = parseFloat(replaceAll(cCampo, ".", "").replace(",", "."));
			maxValue = parseFloat(maxValue);

			if (valorAux > maxValue) {

				maxValue = maxValue.toString().replace(".", ",");

				cCampo = maxValue.toString();
				campo.value = cCampo;
				editValorOnBlur(campo, event, inteiros, decimais, null);
				return;

			}

		}

		campo.value = cCampo;
	}

	function retirarZeroEsquerda(valor) {

		if (valor) {

			for (var i = 0; i <= valor.length; i++) {

				if (valor.charAt(i) == '0') {
					return retirarZeroEsquerda(valor.substring(i + 1));
				} else {
					return valor;
				}

			}
		}

	}

	// bloqueia os valores que não podem fazer parte de um numerico
	function editValorOnKeyPress(campo, event) {

		if (validaKeyEdit(event, campo) == false) {
			return true;
		}

		if (validaNumerico(event)) {
			return false;
		}
		return true;

	}

	// valida os campos que compoem um valor inteiro ou decimal
	function validaNumerico(event) {

		var key;
		if (browserInfo.isIE) { // Internet Explorer
			key = event.keyCode;
		} else { // Nestcape
			key = event.which;
		}

		var character = String.fromCharCode(key);

		if (key != 44 && key != 46) {// testa se naum eh virgula ou ponto
			if (isNaN(character)) {
				return true;
			}
		}
		return false;
	}

	// valida a tecla pressionada, sem os controles de posição existentesd na validação do cep
	function validaKeyEdit(e, Campo) {

		var key;
		if (browserInfo.isIE) { // Internet Explorer
			key = e.keyCode;
		} else { // Nestcape
			key = e.which;
		}

		// algumas teclas no firefox possuem o codigo 0
		if (key == 0) {
			return false;
		}

		// Não deve executar o script quando estiver movendo com as setas.
		// if ((key == 37) || (key == 38)) {
		// return false;
		// }
		// Não deve executar o script quando estiver movendo com as setas.
		// if ((key == 39) || (key == 40)) {
		// return false;
		// }

		// Quando precionar Shift/CRTL/TAB nao deve executar o script.
		if ((key == 16) || (key == 17) || (key == 9)) {
			return false;
		}

		// Quando precionar Home/End nao deve executar o script.
		if ((key == 35) || (key == 36)) {
			return false;
		}

		// Quando precionar DEL nao deve executar o script.
		if ((key == 46)) {
			return false;
		}

		// Backsppace
		if (key == 8) {
			return false;
		}

		// Sinal de negativo (-)
		if (key == 45) {
			return false;
		}

		return true;
	}

	// ////////////////////////////////////////////////////////////////////
	// /// Fim dos metodos de formatação de maskara de valores numericos //
	// ////////////////////////////////////////////////////////////////////

	// functions para ampliar a imagem da tela de resumo do cadastro de imovel

	function ocultaDivImagem(evento) {

		var oTelaCom = document.getElementById("DIV_OCULTA_TELA");

		oTelaCom.style.display = "none";
	}

	function apresentaDivImagem(evento, img) {

		var oDiv = document.getElementById("DIV_OCULTA_TELA");

		// Posiciona a DIV.
		oDiv.style.left = evento.clientX;
		oDiv.style.top = evento.clientY + document.body.scrollTop;
		document.getElementById("imagemAmpliada").src = img.src;
		oDiv.style.display = "block";
	}

	// função que atualiza as paginas do atendeWeb no Java
	function atualizaPaginaAtendeWeb() {

		var frameMDI = document.getElementById("framemdi");
		var frameNodes = frameMDI.getElementsByTagName("iframe");

		for (var i = 0; i < frameNodes.length; i++) {
			var url = frameNodes[i].getAttribute("src");

			var indice = url.indexOf('=') + 1;
			var aux = url.substring(indice, indice + 9);

			if (aux.toUpperCase() == 'ATENDEWEB') {

				var nodePai = frameNodes[i].parentNode;

				if (nodePai.style.display == "block" || nodePai.style.display == '') {
					frameNodes[i].setAttribute("src", url);
				}
			}
		}
	}

	function hideModalPanelPop(seq) {
		if (seq == 1) {
			respostaPop(-1);
		}
		var pnMensagemAviso = document.getElementById('pnMensagemPop');
		pnMensagemAviso.innerHTML = '';
		pnMensagemAviso.style.display = 'none';
		pnMensagemAviso.style.top = '-1000px';
		pnMensagemAviso.style.left = '-1000px';

		var divDesabilitaMensagemPop = document.getElementById('divDesabilitaMensagemPop');

		if (divDesabilitaMensagemPop != null) {
			divDesabilitaMensagemPop.style.height = "0px";
			divDesabilitaMensagemPop.style.width = "0px";
			divDesabilitaMensagemPop.style.display = 'none';
		}

	}

	function clickBtPop(name, tempo, value) {

		if (document.getElementById(name) == null) {
			return null;
		}

		if (tempo == null) {
			document.getElementById(name).value = value;
		} else {
			
			var tmp = tempo - 1;
			document.getElementById(name).value = value + ' (' + tmp + ')';
			if (tmp < 1) {
				document.getElementById(name).click();
			} else {
				setTimeout("clickBtPop('" + name + "', " + tmp + ", '" + value + "')", 1000);
			}
		}
	}

	function onBlurPess2_nomToPess2_raz(campoNome, idCampoRaz) {
		var campoRaz = document.getElementById(idCampoRaz);
		if (campoRaz.value == '') {
			campoRaz.value = campoNome.value;
		}
	}

	function extrairArquivo(caminho) {

		caminho = caminho.replace(/\\/g, "/");
		var arquivo = caminho.substring(caminho.lastIndexOf('/') + 1);
		var extensao = arquivo.substring(arquivo.lastIndexOf('.') + 1);

		return arquivo;

	}

	function abrirAnexo(arquivo, comp) {

		var form = document.createElement("form");

		var cParam = getParameter("idAcesso");
		if (cParam != null && cParam != '') {
			cParam = "?idAcesso=" + cParam;
		} else {
			cParam = "";
		}

		form.setAttribute("action", "/ajax/darquivo.jsp" + cParam);
		form.setAttribute("method", "post");
		form.setAttribute("accept-charset", "UTF-8");
		form.setAttribute("style", "display: none;");

		var inId = document.createElement("input");
		inId.setAttribute("value", arquivo);
		inId.setAttribute("name", "carquivo");
		inId.setAttribute("id", "carquivo");

		form.appendChild(inId);

		comp.appendChild(form);

		form.submit();

		comp.removeChild(form);

	}

	function validaCodigoAcessoForm() {
		var valor = document.getElementById('edCodigoAcesso').value;
		if (valor == null || valor == '') {
			document.getElementById('edCodigoAcesso').value = document.getElementById('edCodigoAcessoInput').value;
		}
	}

	/*
	 * Inicio Controle de tempo de sessão nas telas
	 */

	// var nTempoLimite = 3600000; // 60 minutos.
	var nTempoLimite = 28800000; // 8 horas - ATT 53209.
	// var nTempoLimite = 30000; // 30 segundos.
	var tempoInicial = new Date(); // Define a data e Hora inicial da sessão.
	// Apenas usada para exibir na tela o tempo
	// de duração da sessão.

	// Função que é chamada para finalizar a sessão.
	function finalizaSessao() {
		// Finaliza o timeout da sessão.
		timeOutSessao = 0;
		acaoWindow('AcaoTempoSessao', '', '0');
	}

	// Função que atualiza o tempo do TimeOut.
	function atualizaTempoSessao() {
		if (timeOutSessao != 0) {
			// Zerar o timeOut.
			clearTimeout(timeOutSessao);

			// Seta tempo inicial da sessão. Apenas para visualização.
			tempoInicial = new Date();

			// Monta o timeout novamente para previnir erros.
			timeOutSessao = setTimeout('finalizaSessao()', nTempoLimite);
		}
	}

	// Função que exibe o tempo para expirar a sessão.
	function exibeTempoExpirarSessao() {
		var tempoAtual = new Date();
		var nDiferenca = tempoAtual.getTime() - tempoInicial.getTime();

		tempoAtual.setTime(nDiferenca);

		var exibirTempoAtualizacao = document.getElementById("ExibirTempoAtualizacao");

		if (exibirTempoAtualizacao != null) {
			exibirTempoAtualizacao.innerHTML = "Tempo da sessão: " + strZero(tempoAtual.getMinutes(), 2) + ":" + strZero(tempoAtual.getSeconds(), 2);
		}

		// So deve continuar exibindo o contador até a sessão terminar.
		if (timeOutSessao != 0) {
			timeOutTempo = setTimeout("exibeTempoExpirarSessao()", 100);
		} else {
			document.getElementById("ExibirTempoAtualizacao").innerHTML += "<br>Terminou a sessão!";
		}
	}

	// Função que preenche com "0" a esquerda da string.
	function strZero(string, nCasas) {
		string += "";
		var x = nCasas - string.length;
		for (var i = 0; i < x; i++) {
			string = "0" + string;
		}
		return string;
	}

	/*
	 * Fim Controle de tempo de sessão nas telas
	 */

	function esconderGridAutoComplete() {
		document.getElementById("divGridAutoComplete").style.visibility = "hidden";
		document.getElementById("divGridAutoComplete").style.top = "-8000px";
		document.getElementById("divGridAutoComplete").style.left = "-8000px";
		document.getElementById("divGridAutoComplete_ContentArea").innerHTML = "";
	}

	/*
	 * Inicio auto complete
	 */
	function clickBody(evento) {

		// Tratar o esconder do autocomplete
		var oDiv = document.getElementById('divAutoComplete');
		if (oDiv != null && oDiv.style.visibility == "visible") {

			var left = parseInt(oDiv.style.left.replace("px", ""));
			if (left == null || isNaN(left)) {
				left = 0;
			}

			var top = parseInt(oDiv.style.top.replace("px", ""));
			if (top == null || isNaN(top)) {
				top = 0;
			}

			if (evento.clientX < left || (evento.clientX > (left + oDiv.offsetWidth)) || evento.clientY < top || (evento.clientY > (top + oDiv.offsetHeight))) {
				oDiv.style.visibility = "hidden";
				oDiv.style.top = -500;
				oDiv.style.left = -500;
			}
		}

		// Tuschinski 03/08/2011 - Tratar o esconder do gridautocomplete
		var oDivGrid = document.getElementById('divGridAutoComplete');
		if (oDivGrid != null && oDivGrid.style.visibility == "visible") {

			var left = parseInt(oDivGrid.style.left.replace("px", ""));
			if (left == null || isNaN(left)) {
				left = 0;
			}

			var top = parseInt(oDivGrid.style.top.replace("px", ""));
			if (top == null || isNaN(top)) {
				top = 0;
			}

			// Ver se clicou fora da area
			if ((evento.clientX < left || (evento.clientX > (left + oDivGrid.offsetWidth))) || // verifica
			// na horizontal
			(evento.clientY < top || (evento.clientY > (top + oDivGrid.offsetHeight)))) { // verifica
				// na vertical
				esconderGridAutoComplete();
			}
		}

		// Força para clicar na aba e mudar de aba ativa na tela.
		var objIdAba = document.getElementById("edIdAba");
		if (objIdAba != null) {
			if (window.parent != null && window.parent.document != null && window.parent.document.getElementById(objIdAba.value + "Barra") != null) {
				window.parent.document.getElementById(objIdAba.value + "Barra").onmousedown();
			}
		}

	}

	var descAutoComplete = '';

	var timerAutoComplete = null;
	var idFocus = null;

	function focusAutoComplete(comp) {
		descAutoComplete = comp.value;
		idFocus = comp.id;
		return false;
	}

	var descAutoCompleteEmail = '';

	function focusAutoCompleteEmail(comp) {

		var desc = comp.value.split(";");
		descAutoCompleteEmail = desc[desc.length - 1];

		idFocus = comp.id;

		return false;
	}

	function blurAutoComplete(comp) {

		idFocus = null;

		if (comp.id.toUpperCase().indexOf('CEP') > -1) {
			// Nao faz nada
		} else if (comp.id.toUpperCase().indexOf('IDE') == -1) {

			var valor = comp.value;
			valor = parseInt(valor);

			if (isNaN(valor)) {
				comp.value = 0;
			} else if (valor < 0) {
				comp.value = 0;
			}
		}
	}

	function blurAutoCompleteCep(comp) {
		idFocus = null;
	}

	function autoCompleteDown(evento) {

		var keynum = getKeyCode(evento);

		var oDiv = document.getElementById('divAutoComplete');
		if (oDiv == null) {
			return false;
		}

		// enter
		if (keynum == 13 || keynum == 9) {
			if (oDiv.style.visibility == 'hidden') {
				return tabenter(evento);
			} else {
				return false;
			}
		}
	}

	function autoComplete(comp, evento, cMap) {
		return autoComplete(comp, evento, cMap, false);
	}

	function autoComplete(comp, evento, cMap, lDataWindow, cIdAba) {

		if (idFocus == null || comp.id != idFocus) {
			return;
		}

		var keynum = getKeyCode(evento);

		var oDiv = document.getElementById('divAutoComplete');
		if (oDiv == null) {
			return;
		}

		// Se teclar ESC, fecha a caixa de pesquisa
		if (keynum == 27) {
			oDiv.style.visibility = 'hidden';
			return false;
		} else if (keynum == 38) {
			// seta para cima

			var table = oDiv.firstChild;

			if (table != null) {

				var nLinhas = oDiv.firstChild.firstChild.childNodes.length;

				if (oDiv.firstChild.firstChild.firstChild.className == 'LINHA_AUTO_COMPLETE_SELECIONADA') {
					return false;
				}

				var linhaAtual = oDiv.firstChild.firstChild.lastChild;

				var proxima = false;

				for (var i = nLinhas; i > 0; i++) {

					if (proxima) {
						linhaAtual.className = 'LINHA_AUTO_COMPLETE_SELECIONADA';
						break;
					}

					if (linhaAtual.className == 'LINHA_AUTO_COMPLETE_SELECIONADA') {
						proxima = true;
						linhaAtual.className = 'LINHA_AUTO_COMPLETE';
					}

					linhaAtual = linhaAtual.previousSibling;

					if (linhaAtual == null) {
						break;
					}
				}
			}
			return false;
		} else if (keynum == 40) {
			// seta para baixo

			var table = oDiv.firstChild;

			if (table != null) {

				var nLinhas = oDiv.firstChild.firstChild.childNodes.length;

				if (oDiv.firstChild.firstChild.lastChild.className == 'LINHA_AUTO_COMPLETE_SELECIONADA') {
					return false;
				}

				var linhaAtual = oDiv.firstChild.firstChild.firstChild;

				var proxima = false;

				for (i = 1; i <= nLinhas; i++) {

					if (proxima) {
						linhaAtual.className = 'LINHA_AUTO_COMPLETE_SELECIONADA';
						break;
					}

					if (linhaAtual.className == 'LINHA_AUTO_COMPLETE_SELECIONADA') {
						proxima = true;
						linhaAtual.className = 'LINHA_AUTO_COMPLETE';
					}

					linhaAtual = linhaAtual.nextSibling;

					if (linhaAtual == null) {
						break;
					}
				}
			}
			return false;
		} else if (keynum == 13 || keynum == 9) {

			if (oDiv.style.visibility == 'visible') {

				var nLinhas = oDiv.firstChild.firstChild.childNodes.length;

				var linhaAtual = oDiv.firstChild.firstChild.firstChild;

				for (i = 1; i <= nLinhas; i++) {
					if (linhaAtual.className == 'LINHA_AUTO_COMPLETE_SELECIONADA') {
						linhaAtual.firstChild.firstChild.firstChild.firstChild.firstChild.firstChild.onclick();
						break;
					}

					linhaAtual = linhaAtual.nextSibling;

					if (linhaAtual == null) {
						break;
					}
				}
			}

			return true;
		}

		if (comp.value == null || comp.value == '') {
			oDiv.style.visibility = 'hidden';
			return false;
		}

		if (descAutoComplete == comp.value) {
			return false;
		}

		var dimensaoComponente = parseInt(evento.clientX + oDiv.offsetWidth);

		if (dimensaoComponente > oDiv.parentNode.scrollWidth) {
			oDiv.style.left = findPosX(comp) + (oDiv.parentNode.scrollWidth - dimensaoComponente) + "px";
		} else {
			oDiv.style.left = findPosX(comp) + "px";
		}

		var posTop = getInfoElemento(comp).top + comp.scrollHeight;

		if (browserInfo.isIE) {
			posTop = posTop + 6;
		} else {
			posTop = posTop + 2;
		}

		oDiv.style.top = posTop + "px";

		if (timerAutoComplete != null) {
			clearTimeout(timerAutoComplete);
		}

		if (lDataWindow) {
			timerAutoComplete = setTimeout(function() {
				enviarAutoComplete(comp.id, comp.value, cIdAba);
			}, 350);
		} else {

			var desc = comp.value.split(";");
			var descAux = desc[desc.length - 1];

			if (descAutoComplete == descAux) {
				return false;
			}

			timerAutoComplete = setTimeout(function() {
				enviarAutoCompleteConsultaImovel(comp.id, comp.value, cMap);
			}, 350);
		}
	}

	function clickAutoComplete(descComp, idComp, cod, idCompCod) {

		var oDiv = document.getElementById('divAutoComplete');

		if (idComp != null && idComp != '') {
			var oCompNom = document.getElementById(idComp);
			if (oCompNom != null) {
				oCompNom.value = descComp;
			}
		}

		if (cod != null && cod != '' && idCompCod != null && idCompCod != '') {
			var oCompCod = document.getElementById(idCompCod);
			if (oCompCod != null) {
				oCompCod.value = cod;
			}
		}

		oDiv.style.visibility = 'hidden';

		if (idCompCod != null && idCompCod != '') {
			var oCompCod = document.getElementById(idCompCod);
			if (oCompCod != null && !oCompCod.disabled) {
				oCompCod.focus();
				oCompCod.blur();
				oCompCod.focus();
			}
		}

		if (idComp != null && idComp != '') {
			var oCompNom = document.getElementById(idComp);
			if (oCompNom != null && !oCompNom.disabled) {
				oCompNom.focus();
				oCompNom.blur();
				oCompNom.focus();
			}
		}

	}

	function clickAutoCompleteCep(aLista) {

		var oDiv = document.getElementById('divAutoComplete');
		oDiv.style.visibility = 'hidden';

		for (var i = 0; i < aLista.length; i++) {

			var idComp = aLista[i][0];
			var descComp = aLista[i][1];

			if (idComp != null && idComp != '') {
				var oCompNom = document.getElementById(idComp);
				if (oCompNom != null && (oCompNom.value == null || oCompNom.value == '' || idComp.toUpperCase().indexOf("CEP") > 0 || (idComp.toUpperCase().indexOf("CIDA2_COD") > 0 && oCompNom.value == '0'))) {
					oCompNom.value = descComp;
					if (i == 0) {
						// seta o foco no campo do cep
						oCompNom.focus();
						oCompNom.blur();
						oCompNom.focus();
					}
				}
			}
		}
	}

	function clickAutoCompleteEmail(descComp, idComp) {

		var oDiv = document.getElementById('divAutoComplete');
		oDiv.style.visibility = 'hidden';

		if (idComp != null && idComp != '') {
			var oCompNom = document.getElementById(idComp);
			if (oCompNom != null) {

				var descValue = '';

				var desc = oCompNom.value.split(";");
				for (var i = 1; i < desc.length; i++) {
					descValue = descValue + desc[i - 1] + ";";
				}

				descValue = descValue + descComp + ";";

				oCompNom.value = descValue;
				oCompNom.focus();
				oCompNom.blur();
				oCompNom.focus();

			}
		}
	}

	function findPosX(obj) {
		var curleft = 0;
		if (obj.offsetParent)
			while (1) {
				curleft += obj.offsetLeft;
				if (!obj.offsetParent)
					break;
				obj = obj.offsetParent;
			}
		else if (obj.x)
			curleft += obj.x;

		return curleft;
	}

	function findPosY(obj) {
		var curtop = 0;
		if (obj.offsetParent)
			while (1) {
				curtop += obj.offsetTop;
				if (!obj.offsetParent)
					break;
				obj = obj.offsetParent;
			}
		else if (obj.y)
			curtop += obj.y;

		return curtop;
	}
	/*
	 * Fim auto complete
	 */

	function chamaImpressaoJsp(id, name) {

		var janela = window.open('about:blank', '_blank', 'width=1,height=1,top=0,left=0');
		if (janela == null) {
			alert('As janelas popup estão bloqueadas.\nFavor desbloquear para poder executar esta ação.');
		} else {
			janela.close();
			var cParam = getParameter("idAcesso");
			if (cParam != null && cParam != '') {
				cParam = "&idAcesso=" + cParam;
			} else {
				cParam = "";
			}
			window.open("/Relatorio.jsp?relatorio=" + name + "&carquivo=" + id + cParam, id + "", "");
		}

	}
	
	function apresentaImpressaoJsp(id) {

		var janela = window.open('about:blank', '_blank', 'width=1,height=1,top=0,left=0');
		if (janela == null) {
			alert('As janelas popup estão bloqueadas.\nFavor desbloquear para poder executar esta ação.');
		} else {
			janela.close();
			window.open("/Apresentacao.jsp?id=" + id);
		}

	}

	function clickGrupoMenu(idGrupo) {

		var comp = document.getElementById(idGrupo);

		var lAbrir = comp.getAttribute("abrir") == 'true';
		if (!lAbrir) {
			lAbrir = comp.getAttribute("abrir") == true;
		}
		comp.setAttribute("abrir", !lAbrir);

		var compImg = document.getElementById(idGrupo + 'img');
		if (lAbrir) {
			compImg.src = '/layout/imagens/upGrupoMenu.gif';
		} else {
			compImg.src = '/layout/imagens/downGrupoMenu.gif';
		}

		var childs = comp.childNodes;
		for (var i = 0; i < childs.length; i++) {

			var compChild = childs[i];
			if (compChild.tagName == 'DIV' || compChild.tagName == 'div') {

				if (lAbrir) {
					compChild.style.display = '';
				} else {
					compChild.style.display = 'none';
				}
			}
		}
	}

	function expandirRecolherMenu(lExpandir, pnDiv) {

		var cDisplay = '';
		if (!lExpandir) {
			cDisplay = 'none';
		}

		var pnMenu = null;
		if (pnDiv == null) {
			pnMenu = document.getElementById('pnMenu');
		} else {
			pnMenu = pnDiv;
		}

		var childs = pnMenu.childNodes;
		for (var i = 0; i < childs.length; i++) {

			var compChild = childs[i];
			if (compChild.tagName == 'DIV' || compChild.tagName == 'div') {

				if (pnDiv != null) {
					compChild.style.display = cDisplay;

					if (pnDiv.id != null && pnDiv.id != '') {

						if (cDisplay == '') {
							pnDiv.setAttribute("abrir", false);
						} else {
							pnDiv.setAttribute("abrir", true);
						}

						var pnImg = document.getElementById(pnDiv.id + 'img');
						if (pnImg != null) {
							if (cDisplay == '') {
								pnImg.src = '/layout/imagens/upGrupoMenu.gif';
							} else {
								pnImg.src = '/layout/imagens/downGrupoMenu.gif';
							}
						}
					}
				}
				expandirRecolherMenu(lExpandir, compChild);
			}
		}
	}

	var isIe = false;
	/* Define se usuario esta usando o Internet Explorer. */
	if (navigator.appName == "Microsoft Internet Explorer") {
		isIe = true;
	} else {
		isIe = false;
	}

	function fecharAbaMenuAntigo(obj) {

		var id = obj.getAttribute('cId');

		redimensionarToolBar();

		var compFrame = document.getElementById('mdi' + id + 'iFrame');
		compFrame.src = '';
		compFrame = null;

		var compAba = document.getElementById('mdi' + id);
		compAba.innerHTML = '';
		var compBot = document.getElementById('bot' + id);
		compBot.innerHTML = '';

		compBot.parentNode.removeChild(compBot);
		compBot = null;
		compAba.parentNode.removeChild(compAba);
		compAba = null;

		return false;
	}

	function fecharAbaMenu(obj) {

		var id = obj.getAttribute('cId');

		var compBot = document.getElementById('bot' + id);
		compBot.innerHTML = '';
		var objMaeAba = document.getElementById('objMaeAba');
		objMaeAba.removeChild(compBot);
		compBot = null;

		var compAba = document.getElementById('mdi' + id);
		compAba.style.display = 'none';

		redimensionarToolBar();
		ativaMainWindow();
		fecharAba('mdi' + id);

		var compFrame = document.getElementById('mdi' + id + 'iFrame');
		compFrame.src = '';
		compFrame = null;

		compAba.innerHTML = '';
		compAba.parentNode.removeChild(compAba);
		compAba = null;

		return false;
	}

	function alteraMdiMenu(obj) {
		var id = obj.id;
		alteraMdi('mdi' + obj.getAttribute('cId'), false, true);
		return false;
	}

	function startMoveJanelaMdiMenu(obj, e) {
		var id = obj.id;
		if (!podeFecharMdi('mdi' + obj.getAttribute('cId'))) {
			return false;
		}
		startMoveJanelaMdi(e, 'mdi' + obj.getAttribute('cId'));
		return false;
	}

	function stopMoveJanelaMdiMenu(obj) {
		var id = obj.id;
		stopMoveJanelaMdi();
		return false;
	}

	function maximizarMdiMenu(obj) {
		var id = obj.id;
		maximizarMdi('mdi' + obj.getAttribute('cId'), obj);
		return false;
	}

	function procuraFecharMenu(obj) {
		var id = obj.id;
		procuraFechar('mdi' + obj.getAttribute('cId'));
		return false;
	}

	function startResizeJanelaMdiMenu(obj, e) {
		var id = obj.id;
		if (!podeFecharMdi('mdi' + obj.getAttribute('cId'))) {
			return false;
		}
		startResizeJanelaMdi(obj, e);
		return false;
	}

	function ClickMDIMenu(obj) {
		ClickMDI(obj);
		return false;
	}

	function MouseOverMDIMenu(obj) {
		MouseOverMDI(obj);
	}

	function MouseOutMDIMenu(obj) {
		MouseOutMDI(obj);
	}

	function MouseOverCloseButtonMDIMenu(obj) {
		MouseOverCloseButtonMDI(obj);
	}

	function MouseOutCloseButtonMDIMenu(obj) {
		MouseOutCloseButtonMDI(obj);
	}

	function resetaCssBotaoMenu(idAtual) {

		var objMaeAba = document.getElementById('objMaeAba');
		var childs = objMaeAba.childNodes;
		for (var i = 0; i < childs.length; i++) {

			var compChild = childs[i];
			if ((compChild.tagName == 'DIV' || compChild.tagName == 'div') && compChild.id != idAtual) {

				var id = compChild.id;

				document.getElementById(id + 'left').className = 'colLeft';
				document.getElementById(id + 'center').className = 'colCenter';
				document.getElementById(id + 'right').className = 'colRight';
				document.getElementById(id).className = 'BOTAOABA';

			}

		}

	}

	var timerPesquisaMenu = null;

	function pesquisaMenu(text) {

		if (timerPesquisaMenu != null) {
			clearTimeout(timerPesquisaMenu);
		}

		timerPesquisaMenu = setTimeout(function() {
			pesquisaMenuAjax(text);
		}, 1000);

	}

	function habilitaTelaPai() {
		var divDesabilitaTelaPai = document.getElementById('divDesabilitaTelaPai');
		if (divDesabilitaTelaPai != null) {
			divDesabilitaTelaPai.style.height = "0px";
			divDesabilitaTelaPai.style.width = "0px";
			// divDesabilitaTelaPai.style.display = 'none';
			divDesabilitaTelaPai.style.visibility = 'hidden';
			divDesabilitaTelaPai.style.zIndex = '0';
		}
	}

	function desabilitaTelaPai() {
		var divDesabilitaTelaPai = document.getElementById('divDesabilitaTelaPai');
		if (divDesabilitaTelaPai != null) {
			divDesabilitaTelaPai.style.height = document.body.scrollHeight;
			divDesabilitaTelaPai.style.width = document.body.scrollWidth;
			// divDesabilitaTelaPai.style.display = '';
			divDesabilitaTelaPai.style.visibility = 'visible';
			divDesabilitaTelaPai.style.zIndex = '4';
		}
	}

	function sairFecharAbas() {

		var objMaeAba = document.getElementById("framemdi");

		var oChilds = objMaeAba.childNodes;
		var i = 0;
		for (i = 0; i < oChilds.length; i++) {

			var oDiv = oChilds[0];
			if (oDiv != null && oDiv.innerHTML != null) {

				id = oDiv.id.substring(3);

				var compFrame = document.getElementById('mdi' + id + 'iFrame');
				compFrame.src = '';
				compFrame = null;

				var compAba = document.getElementById('mdi' + id);
				compAba.innerHTML = '';
				var compBot = document.getElementById('bot' + id);
				compBot.innerHTML = '';

				compBot.parentNode.removeChild(compBot);
				compBot = null;
				compAba.parentNode.removeChild(compAba);
				compAba = null;
			}
		}
	}

	function editCalencarOnBlur(campo) {

		if (campo.value == null || campo.value == '') {
			return true;
		}

		if (campo.value.length > 10) {
			campo.value = campo.value.substring(0, 10);
		} else if (campo.value.length < 10
				|| !isValidaDataSQLServer(campo.value.toString().substring(6,10))) {
			campo.value = '';
			return false;
		}

		var cData = campo.value;

		var matches = cData.match(/^\b(\d+)\D(\d+)\D(\d+)\b\b(?:\s+(\d{1,2})\D(\d{2})\D*((\d{2})?))?\b$/);
		if (matches == null) {
			campo.value = '';
			return false;
		}
		var dia = matches[1];
		if (dia == null) {
			campo.value = '';
			return false;
		}
		var mes = matches[2];
		if (mes == null) {
			campo.value = '';
			return false;
		}
		var ano = matches[3];
		if (ano == null) {
			campo.value = '';
			return false;
		}

		var patternValidaData = /^(((0[1-9]|[12][0-9]|3[01])([-.\/])(0[13578]|10|12)([-.\/])(\d{4}))|(([0][1-9]|[12][0-9]|30)([-.\/])(0[469]|11)([-.\/])(\d{4}))|((0[1-9]|1[0-9]|2[0-8])([-.\/])(02)([-.\/])(\d{4}))|((29)(\.|-|\/)(02)([-.\/])([02468][048]00))|((29)([-.\/])(02)([-.\/])([13579][26]00))|((29)([-.\/])(02)([-.\/])([0-9][0-9][0][48]))|((29)([-.\/])(02)([-.\/])([0-9][0-9][2468][048]))|((29)([-.\/])(02)([-.\/])([0-9][0-9][13579][26])))$/;

		if (!patternValidaData.test(cData)) {
			campo.value = '';
			return false;
		}

		var dateRegExp = /^(19|20)\d\d-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$/;
		if (!dateRegExp.test(ano + "-" + mes + "-" + dia))
			return false; // formato inválido
		if (dia == 31 && (/^0?[469]$/.test(mes) || mes == 11)) {
			return false; // dia 31 de um mes de 30 dias
		} else if (dia >= 30 && mes == 2) {
			return false; // mais de 29 dias em fevereiro
		} else if (mes == 2 && dia == 29 && !(ano % 4 == 0 && (ano % 100 != 0 || ano % 400 == 0))) {
			return false; // dia 29 de fevereiro de um ano não bissexto
		} else {
			return true; // Data válida
		}

		return true;

	}

	function blurMascaraCep(comp) {

		var cValue = comp.value;
		if (cValue != null && cValue != '') {

			cValue = trim(cValue);

			if (cValue.length == 8 && !isNaN(cValue)) {

				var cIni = cValue.substring(0, 5);
				var cFim = cValue.substring(5);

				cValue = cIni + '-' + cFim;
				comp.value = cValue;

			}

		}

	}

	/*
	 * Marca as fotos que serão enviadas por e-mail na ficha do imovel Jonathan - 17/01/2011
	 */
	function marcaFoto(campo) {

		// document.getElementById('txtHidden').value = campo.id;
		// document.getElementById('btnHidden').click();

		var cParametros = "idImagem=" + campo.id;
		acaoWindow('ClickImagemEnviarEmail', '', '0', cParametros);

		if (campo.style.borderColor.indexOf('red') == -1) {
			campo.style.borderColor = 'red';
		} else {
			campo.style.borderColor = 'silver';
		}
	}

	var nQtdFotosAnexarFoto = 0;

	function marcaFotoAnexarFoto(campo) {

		if (nQtdFotosAnexarFoto < 3) {

			if (campo.style.borderColor.indexOf('red') == -1) {
				campo.style.borderColor = 'red';
				nQtdFotosAnexarFoto++;
			} else {
				campo.style.borderColor = 'silver';
				nQtdFotosAnexarFoto--;
			}

			var cParametros = "idImagem=" + campo.id;
			acaoWindow('ClickImagemEnviarEmail', '', '0', cParametros);

		} else {

			if (campo.style.borderColor.indexOf('red') != -1) {
				campo.style.borderColor = 'silver';
				nQtdFotosAnexarFoto--;

				var cParametros = "idImagem=" + campo.id;
				acaoWindow('ClickImagemEnviarEmail', '', '0', cParametros);

			}

		}

	}

	/*
	 * Início máscara para os campos de telefone do sistema
	 */
	function doGetCaretPosition(ctrl) {

		var CaretPos = 0;
		// IE Support
		if (document.selection) {

			// ctrl.focus();
			var Sel = document.selection.createRange();
			var SelLength = document.selection.createRange().text.length;
			Sel.moveStart('character', -ctrl.value.length);
			CaretPos = Sel.text.length - SelLength;
		}
		// Firefox support
		else if (ctrl.selectionstart || ctrl.selectionstart == '0') {
			CaretPos = ctrl.selectionstart;
		}

		return (CaretPos);

	}

	function setPositionMascara(Campo, posicaoCursor) {

		if (Campo.selectionStart || Campo.selectionStart == '0') {
			// Campo.focus();
			// Campo.setSelectionRange(posicaoCursor, posicaoCursor);
		} else if (document.selection) {
			var range = Campo.createTextRange();
			range.collapse(true);
			range.moveEnd('character', posicaoCursor);
			range.moveStart('character', posicaoCursor);
			range.select();
		}

	}

	/* Função que padroniza telefone (11) 4184-1241 */
	function MascaraTelefone(comp) {

		var nPos = doGetCaretPosition(comp);

		var v = comp.value;

		// v=v.replace(/\D/g,"")
		v = v.replace(/^(\w\w)(\w)/g, "($1) $2");
		// v=v.replace(/(\d{4})(\d)/,"$1-$2")

		comp.value = v;

		if (v.length != 6) {
			setPositionMascara(comp, nPos);
		}

	}

	function blurMascaraTelefone(comp) {

		var cValue = comp.value;
		if (cValue != null && cValue != '') {

			cValue = trim(cValue);

			if (cValue.length >= 10) {

				var cDdd = cValue.substring(0, 5);

				cValue = cValue.substring(5);

				if (cValue.indexOf("-") > 0) {

					var cIni = cValue.substring(0, cValue.indexOf("-"));

					var cFim = cValue.substring(cValue.indexOf("-") + 1);

					if (cFim.length > 4) {
						cFim = cFim.substring(0, 4);
					}

					cValue = cDdd + cIni + '-' + cFim;
					comp.value = cValue;

				} else {

					var cIni = cValue.substring(0, cValue.length - 4);
					var cFim = cValue.substring(cValue.length - 4);

					cValue = cDdd + cIni + '-' + cFim;
					comp.value = cValue;

				}
			}

		}

	}

	/*
	 * Fim máscara para os campos de telefone do sistema
	 */

	var lIe = false;

	if (navigator.appName == "Microsoft Internet Explorer") {
		lIe = true;
	} else {
		lIe = false;
	}

	function clickMenuAtende(obj, e) {

		e = getEvento(e);
		var aPosicoes = getInfoElemento(obj);

		var nTop = 0;
		var nLeft = 0;

		// Define posição do menu.
		if (e.button == 2) {
			nTop = e.clientY + document.body.scrollTop - 5;
			nLeft = e.clientX + document.body.scrollLeft - 3;
		} else {
			nTop = aPosicoes.top;
			nLeft = aPosicoes.left;
		}

		var nWidthTela = 0;

		if (browserInfo.isIE) {
			nWidthTela = document.body.clientWidth;
		} else {
			nWidthTela = window.innerWidth;
		}

		if (nLeft + 150 > nWidthTela) {
			nLeft = nLeft - 130;
		}

		oAtendeMenu = document.getElementById('ATENDE_MENU');

		oAtendeMenu.style.left = nLeft;
		oAtendeMenu.style.top = nTop + 24;
		oAtendeMenu.style.visibility = 'visible';

	}

	var timeMenuAtende = null;

	function escondeMenuAtendeTime() {
		timeMenuAtende = setTimeout('escondeMenuAtende()', 500);
	}

	function escondeMenuAtende() {

		oAtendeMenu = document.getElementById('ATENDE_MENU');
		oAtendeMenu.style.visibility = 'hidden';

	}

	function mostrarMenuAtende() {

		if (timeMenuAtende != null) {
			clearTimeout(timeMenuAtende);
			timeMenuAtende = null;
		}

		oAtendeMenu = document.getElementById('ATENDE_MENU');
		oAtendeMenu.style.visibility = 'visible';

	}

	function clickHelp(cLink) {
		window.open(cLink, "Help", "");
	}

	function respostaMensagemMenu(cod) {
		var pnMensagemAviso = document.getElementById('pnMensagemAviso');
		if (pnMensagemAviso != null) {
			marcaMensagemLida(cod);
			pnMensagemAviso.style.display = 'none';
			pnMensagemAviso.innerHTML = '';
		}
	}

	function fecharMensagemMenu() {
		var pnMensagemAviso = document.getElementById('pnMensagemAviso');
		if (pnMensagemAviso != null) {
			pnMensagemAviso.style.display = 'none';
			pnMensagemAviso.innerHTML = '';
		}
	}

	function clickCheckBoxExplorador(cComp) {

		var comp = document.getElementById(cComp);
		if (comp.checked) {

			desabilitaInputsExplorador(false);

			if (cComp == 'chkQuadraLote') {

				document.getElementById('chkQuadraLoteAux').checked = true;
				document.getElementById('chkProponentesAux').checked = false;

				if (document.getElementById('chkProponentes') != null) {
					document.getElementById('chkProponentes').checked = false;
					document.getElementById('mPROPONENTES_PESS').value = '';
					document.getElementById('mPROPONENTES_PESS').disabled = true;
					document.getElementById('mPROPONENTES_PESS').className = 'EDIT_DISABLED';
				}

				document.getElementById('mQuadra').value = '';
				document.getElementById('mQuadra').disabled = false;
				document.getElementById('mQuadra').className = 'EDIT';
				document.getElementById('mLote').value = '';
				document.getElementById('mLote').disabled = false;
				document.getElementById('mLote').className = 'EDIT';
				document.getElementById('mOBRA2_COD_QUA').value = '';
				document.getElementById('mOBRA2_COD_QUA').disabled = false;
				document.getElementById('mOBRA2_COD_QUA').className = 'EDIT';

				document.getElementById('DesativaCheckProponentes').click();
				document.getElementById('AtivaCheckQuadraLote').click();

			} else {

				document.getElementById('chkProponentesAux').checked = true;
				document.getElementById('chkQuadraLoteAux').checked = false;

				document.getElementById('mPROPONENTES_PESS').value = '';
				document.getElementById('mPROPONENTES_PESS').disabled = false;
				document.getElementById('mPROPONENTES_PESS').className = 'EDIT';

				if (document.getElementById('chkQuadraLote') != null) {
					document.getElementById('chkQuadraLote').checked = false;
					document.getElementById('mQuadra').value = '';
					document.getElementById('mQuadra').disabled = true;
					document.getElementById('mQuadra').className = 'EDIT_DISABLED';
					document.getElementById('mLote').value = '';
					document.getElementById('mLote').disabled = true;
					document.getElementById('mLote').className = 'EDIT_DISABLED';
					document.getElementById('mOBRA2_COD_QUA').value = '';
					document.getElementById('mOBRA2_COD_QUA').disabled = true;
					document.getElementById('mOBRA2_COD_QUA').className = 'EDIT_DISABLED';
				}

				document.getElementById('DesativaCheckQuadraLote').click();
				document.getElementById('AtivaCheckProponentes').click();

			}

		} else {

			document.getElementById('chkQuadraLoteAux').checked = false;
			document.getElementById('chkProponentesAux').checked = false;

			desabilitaInputsExplorador(true);

			if (document.getElementById('chkQuadraLote') != null) {
				document.getElementById('mQuadra').value = '';
				document.getElementById('mQuadra').disabled = true;
				document.getElementById('mQuadra').className = 'EDIT_DISABLED';
				document.getElementById('mLote').value = '';
				document.getElementById('mLote').disabled = true;
				document.getElementById('mLote').className = 'EDIT_DISABLED';
				document.getElementById('mOBRA2_COD_QUA').value = '';
				document.getElementById('mOBRA2_COD_QUA').disabled = true;
				document.getElementById('mOBRA2_COD_QUA').className = 'EDIT_DISABLED';
				document.getElementById('DesativaCheckQuadraLote').click();
			}

			if (document.getElementById('chkProponentes') != null) {
				document.getElementById('mPROPONENTES_PESS').value = '';
				document.getElementById('mPROPONENTES_PESS').disabled = true;
				document.getElementById('mPROPONENTES_PESS').className = 'EDIT_DISABLED';
				document.getElementById('DesativaCheckProponentes').click();
			}

		}

	}

	function desabilitaInputsExplorador(lHabilita) {

		var inputs = document.getElementsByTagName('input');
		for (var i = 0; i < inputs.length; i++) {

			var compTela = inputs[i];
			if (compTela.type == 'text' && compTela.id != 'inputDateGridInputDate') {
				compTela.value = '';
				compTela.disabled = !lHabilita;
				compTela.className = (lHabilita ? 'EDIT' : 'EDIT_DISABLED');
			}

		}

	}

	function selecionaImagemPrincipal(comp) {
		var compPrincipal = document.getElementById("mediaOutputFotoPrincipal");
		compPrincipal.src = comp.src;
		compPrincipal.title = comp.title;
	}

	var aListaMenuSub = new Array();
	var aListaMenuSubTimeout = new Array();

	function abrirMenuImprimir(e, obj, id) {

		if (obj != null && (obj.getAttribute("type") == "image" || obj.getAttribute("type") == "IMAGE") && (obj.getAttribute("className") == "DESABILITA_OBJETO" || obj.getAttribute("class") == "DESABILITA_OBJETO")) {
			return false;
		}

		e = getEvento(e);
		var aPosicoes = getInfoElemento(obj);

		var nTop = 0;
		var nLeft = 0;

		// Define posição do menu.
		if (e.button == 2) {
			nTop = e.clientY + document.body.scrollTop - 5;
			nLeft = e.clientX + document.body.scrollLeft - 3;
		} else {
			nTop = aPosicoes.top;
			nLeft = aPosicoes.left;
		}

		var nWidthTela = 0;

		if (browserInfo.isIE) {
			nWidthTela = document.body.clientWidth;
		} else {
			nWidthTela = window.innerWidth;
		}

		if (nLeft + 150 > nWidthTela) {
			nLeft = nLeft - 130;
		}

		var menuImprimir = document.getElementById(id + '_menu');
		menuImprimir.style.display = '';
		menuImprimir.style.left = nLeft;
		menuImprimir.style.top = nTop + 24;

	}

	function menuSubInicio(id) {

		var lIncluir = true;

		for (var i = 0; i < aListaMenuSub.length; i++) {
			if (aListaMenuSub[i] == id) {
				lIncluir = false;
			}
		}

		if (lIncluir) {
			aListaMenuSub[aListaMenuSub.length] = id;
		}

	}

	function escondeMenuImprimirTime(id) {

		for (var i = 0; i < aListaMenuSub.length; i++) {

			if (aListaMenuSub[i] == id) {

				if (aListaMenuSubTimeout[i] != null) {
					clearTimeout(aListaMenuSubTimeout[i]);
				}
				aListaMenuSubTimeout[i] = setTimeout('escondeMenuImprimir("' + id + '")', 500);

				break;

			}
		}

	}

	function escondeMenuImprimir(id) {

		var menuImprimir = document.getElementById(id + '_menu');
		menuImprimir.style.display = 'none';

	}

	function mostrarMenuImprimir(id) {

		for (var i = 0; i < aListaMenuSub.length; i++) {
			if (aListaMenuSub[i] == id) {

				if (aListaMenuSubTimeout[i] != null) {
					clearTimeout(aListaMenuSubTimeout[i]);
					aListaMenuSubTimeout[i] = null;
				}

				break;

			}
		}

		var menuImprimir = document.getElementById(id + '_menu');
		menuImprimir.style.display = '';

	}

	function clickMenuImprimir(id) {

		for (var i = 0; i < aListaMenuSub.length; i++) {
			if (aListaMenuSub[i] == id) {

				if (aListaMenuSubTimeout[i] != null) {
					clearTimeout(aListaMenuSubTimeout[i]);
					aListaMenuSubTimeout[i] = null;
				}

				break;

			}
		}

		var menuImprimir = document.getElementById(id + '_menu');
		menuImprimir.style.display = 'none';
	}

	function itemMenuImprimirSelected(obj) {
		obj.className = 'MENU_LIST_MENU_ITEM MENU_LIST_MENU_ITEM_HOVER LABEL';
	}

	function itemMenuImprimirUnSelected(obj) {
		obj.className = 'MENU_LIST_MENU_ITEM MENU_LIST_MENU_ITEM_ENABLED LABEL';
	}

	function tabenterEfetuaPagamentoCopa2ValPgt(evt) {
		evt = getEvent(evt);
		if (getKeyCode(evt) == 13 || (!evt.shiftKey && getKeyCode(evt) == 9)) {
			document.getElementById('mCopa2_Val_Pgt').blur();
			return false;
		}
		return true;
	}

	var aListaTab = new Array();

	function carregaTab(idTab, idTabPanel, lAtiva, cLabel, cTitle, lAjax, lDisabled, cWidthTab, cWidthText) {

		var lIncluir = true;
		for (var i = 0; i < aListaTab.length; i++) {
			if (aListaTab[i] == idTab) {
				lIncluir = false;
			}
		}

		if (lIncluir) {
			aListaTab[aListaTab.length] = idTab;
		}

		// tpCadastroUsuarioTrTab
		var trTab = document.getElementById(idTabPanel + 'TrTab');

		var tdCell = document.createElement('td');
		tdCell.id = idTab + "_cell";
		if (lAtiva) {
			tdCell.className = "TAB_CELL_ATIVADA";
		} else {
			tdCell.className = "TAB_CELL_DESATIVADA";
		}
		tdCell.style.height = "100%";
		tdCell.style.width = cWidthTab;
		tdCell.style.verticalAlign = "bottom";

		var tableShifted = document.createElement('table');
		tableShifted.id = idTab + "_shifted";
		tableShifted.cellSpacing = "0";
		tableShifted.cellPadding = "0";
		tableShifted.border = "0";
		// tableShifted.style.position = "relative";
		tableShifted.style.top = "1px";
		tableShifted.style.height = "100%";
		tableShifted.style.width = "100%";
		tableShifted.style.position = "relative";
		tableShifted.onclick = function() {
			clickTabPanel(idTab, idTabPanel, lAjax);
		};

		var tbodyShifted = document.createElement('tbody');

		var trShifted = document.createElement('tr');

		// Inicio TD1

		var tdShifted1 = document.createElement('td');
		tdShifted1.className = "TABHDR_SIDE_BORDER";

		var imgTdShifted1 = document.createElement('img');
		imgTdShifted1.height = "1";
		imgTdShifted1.width = "1";
		imgTdShifted1.style.height = "1px";
		imgTdShifted1.style.width = "1px";
		imgTdShifted1.style.border = "0pt none";
		imgTdShifted1.src = "/layout/imagens/blank.gif";

		tdShifted1.appendChild(imgTdShifted1);

		trShifted.appendChild(tdShifted1);

		// Fim TD1

		// Inicio TD2

		var tdShifted2 = document.createElement('td');
		tdShifted2.className = "TABHDR_SIDE_BORDER_2";

		var tdTableShifted2 = document.createElement('table');
		tdTableShifted2.cellSpacing = "0";
		tdTableShifted2.cellPadding = "0";
		tdTableShifted2.border = "0";
		tdTableShifted2.style.height = "100%";
		tdTableShifted2.style.width = "100%";

		var tdTableTbodyShifted2 = document.createElement('tbody');

		var tdTableTbodyTrShifted2 = document.createElement('tr');

		var tdTableTbodyTrTdShifted2 = document.createElement('td');
		tdTableTbodyTrTdShifted2.style.width = cWidthText;
		tdTableTbodyTrTdShifted2.id = idTab + "_lbl";
		if (lDisabled) {
			tdTableTbodyTrTdShifted2.className = "TAB_PANEL TAB_DESABILITADA TAB_HEADER";
		} else if (lAtiva) {
			tdTableTbodyTrTdShifted2.className = "TAB_PANEL TAB_ATIVADA TAB_HEADER";
		} else {
			tdTableTbodyTrTdShifted2.className = "TAB_PANEL TAB_DESATIVADA TAB_HEADER";
		}

		tdTableTbodyTrTdShifted2.innerHTML = cLabel;
		tdTableTbodyTrTdShifted2.title = cTitle;

		tdTableTbodyTrShifted2.appendChild(tdTableTbodyTrTdShifted2);

		tdTableTbodyShifted2.appendChild(tdTableTbodyTrShifted2);

		tdTableShifted2.appendChild(tdTableTbodyShifted2);

		tdShifted2.appendChild(tdTableShifted2);

		trShifted.appendChild(tdShifted2);

		// Fim TD2

		// Inicio TD3

		var tdShifted3 = document.createElement('td');
		tdShifted3.className = "TABHDR_SIDE_BORDER";

		var imgTdShifted3 = document.createElement('img');
		imgTdShifted3.height = "1";
		imgTdShifted3.width = "1";
		imgTdShifted3.style.height = "1px";
		imgTdShifted3.style.width = "1px";
		imgTdShifted3.style.border = "0pt none";
		imgTdShifted3.src = "/layout/imagens/blank.gif";

		tdShifted3.appendChild(imgTdShifted3);

		trShifted.appendChild(tdShifted3);

		// Fim TD3

		tbodyShifted.appendChild(trShifted);

		tableShifted.appendChild(tbodyShifted);

		tdCell.appendChild(tableShifted);

		trTab.appendChild(tdCell);

	}

	function ajusteVerticalTabPanelAux() {

		for (var i = 0; i < aListaTab.length; i++) {

			var obj = document.getElementById(aListaTab[i] + "_lbl");
			if (obj.className.indexOf('TAB_ATIVADA') > -1) {
				ajusteVerticalTabPanel(aListaTab[i]);
			}

		}

	}

	function ajusteVerticalTabPanel(id) {

		var objAtendimentoCliente = document.getElementById('tdGeralFrAtendimentoCliente');

		var oPanel = document.getElementById(id + '_div');

		if (eval(oPanel.getAttribute("ajusteVertical")) == false) {
			return;
		}

		var aDivs = oPanel.getElementsByTagName("div");

		for (var i = 0; i < aDivs.length; i++) {
			if ((aDivs[i].id.indexOf('STRATOGRID') > -1 && eval(aDivs[i].getAttribute("lajustevertical"))) || aDivs[i].id.indexOf('divOrcamento') > -1) {
				document.getElementById(id + "_div").className = 'TAB_BODY_DIV TAB_BODY_BORDA';
				return;
			}
		}

		var oUltElem = document.getElementById("limitadorGrid");

		var oBody = document.body;

		oPanel.style.height = 100;
		oPanel.style.width = 1;

		var info = getInfoElemento(oUltElem);

		nAjusteVertical = 100 + (oBody.clientHeight - (info.top + info.height));
		nAjusteVertical -= 27;

		if (browserInfo.isFF) {
			nAjusteVertical += 8;
		} else if (browserInfo.isChrome) {
			nAjusteVertical += 3;
		}

		document.getElementById(id + "TAB_BODY").style.position = 'absolute';

		if (objAtendimentoCliente != null) {
			nAjusteVertical = 400;
		}

		if (nAjusteVertical < 0) {
			return;
		}

		oPanel.style.height = nAjusteVertical;
		if (browserInfo.isIE) {
			document.getElementById(id + "TAB_BODY").style.height = nAjusteVertical;
		} else {
			document.getElementById(id + "TAB_BODY").style.height = nAjusteVertical - 20;
		}
		document.getElementById(id + "TAB_BODY").childNodes[0].style.height = parseInt(document.getElementById(id + "TAB_BODY").style.height) - 20;

		if (browserInfo.isIE) {
			oPanel.style.width = oPanel.parentNode.clientWidth - 20;
			document.getElementById(id + "TAB_BODY").style.width = oPanel.parentNode.clientWidth;
		} else {
			oPanel.style.width = oPanel.parentNode.clientWidth;
			document.getElementById(id + "TAB_BODY").style.width = oPanel.parentNode.clientWidth - 2 - 20;
		}
		document.getElementById(id + "TAB_BODY").className = 'TAB_BODY_SUB TAB_BODY_BORDA';

	}

	function clickTabPanel(id, idTabPanel, lAjax, lDisabled) {

		var objTab = document.getElementById(id + '_lbl');
		if (objTab.className == 'TAB_PANEL TAB_ATIVADA TAB_HEADER' || objTab.className == 'TAB_PANEL TAB_DESABILITADA TAB_HEADER') {
			return;
		} else if (objTab.className == 'TAB_PANEL TAB_DESATIVADA TAB_HEADER') {

			var cParametros = "idTab=" + id;
			acaoWindow('ClickAbaTabPanel', '', '0', cParametros);

			if (lAjax) {
				acaoWindow(id, '', '1');
			}

			var trTab = document.getElementById(idTabPanel + 'TrTab');
			var tds = trTab.childNodes;
			for (var i = 0; i < tds.length; i++) {

				if (tds[i].className == 'TAB_CELL_ATIVADA') {

					tds[i].className = 'TAB_CELL_DESATIVADA';

					var idAux = tds[i].id;
					idAux = idAux.substring(0, idAux.indexOf('_cell'));
					document.getElementById(idAux + '_lbl').className = 'TAB_PANEL TAB_DESATIVADA TAB_HEADER';
					document.getElementById(idAux).style.display = 'none';

					break;
				}

			}

			document.getElementById(id + '_lbl').className = 'TAB_PANEL TAB_ATIVADA TAB_HEADER';
			document.getElementById(id + '_cell').className = 'TAB_CELL_ATIVADA';
			document.getElementById(id).style.display = '';

			ajusteVerticalTabPanel(id);

		}

		var oPanel = document.getElementById(id + '_div');
		var aDivs = oPanel.getElementsByTagName("div");

		for (var i = 0; i < aDivs.length; i++) {
			if (aDivs[i].id.indexOf('STRATOGRID') > -1) {
				// setTimeout("gridResize(true, '" + aDivs[i].id + "');", 1);
				gridResize(true, aDivs[i].id);
			}
		}

	}

	function TrocaCharEsp(cTexto) {
		var aArray_Texto = new Array("«", "»", "<", ">");
		var aArray_Codigo = new Array("&laquo;", "&raquo;", "&lt;", "&gt;");
		var x = 0;
		for (var i = 0; i < aArray_Codigo.length; i++) {
			while (x > -1) {
				cTexto = cTexto.replace(aArray_Codigo[i], aArray_Texto[i]);
				x = cTexto.indexOf(aArray_Codigo[i]);
			}
			x = 0;
		}
		return cTexto;
	}

	function ReverterTrocaCharEsp(cTexto) {
		var aArray_Codigo = new Array("«", "»", "<", ">");
		var aArray_Texto = new Array("&laquo;", "&raquo;", "&lt;", "&gt;");
		var x = 0;
		for (var i = 0; i < aArray_Codigo.length; i++) {
			while (x > -1) {
				cTexto = cTexto.replace(aArray_Codigo[i], aArray_Texto[i]);
				x = cTexto.indexOf(aArray_Codigo[i]);
			}
			x = 0;
		}
		return cTexto;
	}

	function clickToggle(comp) {

		var id = comp.id;
		var idOri = id.substring(0, id.indexOf('Label'));

		if (document.getElementById(idOri + 'On').style.display == 'none') {
			document.getElementById(idOri + 'On').style.display = 'block';
			document.getElementById(idOri + 'Off').style.display = 'none';

			document.getElementById(idOri + 'Body').style.display = 'block';

		} else {
			document.getElementById(idOri + 'On').style.display = 'none';
			document.getElementById(idOri + 'Off').style.display = 'block';

			document.getElementById(idOri + 'Body').style.display = 'none';

		}

		gridResize(true);

	}

	function keyDownDataWindow(e) {
		e = getEvento(e);
		var obj = getElementoEvent(e);
		// 27 - Esc
		if (getKeyCode(e) == 27) {
			// Tuschinski 30/08/2011 - Tratar o esconder do gridautocomplete
			var oDivGrid = document.getElementById('divGridAutoComplete');
			if (oDivGrid != null && oDivGrid.style.visibility == "visible") {
				esconderGridAutoComplete();
			}
			return false;
		} else if (obj == null || obj == undefined || obj.type != 'textarea') {
			if (gridVerificaShift(e) && getKeyCode(e) == 38) {
				return false;
			} else if (gridVerificaShift(e) && getKeyCode(e) == 40) {
				return false;
			} else if (gridVerificaCtrl(e) && getKeyCode(e) == 65) {
				return false;
			}
		}

		return true;
	}

	var cIdImagem = null;

	function excluirImagemImovel(obj, e) {

		e = getEvento(e);

		if (e.button != 2) {
			return true;
		}

		var aPosicoes = getInfoElemento(obj);

		var nTop = 0;
		var nLeft = 0;

		// Define posição do menu.
		if (e.button == 2) {
			nTop = e.clientY + document.body.scrollTop - 5;
			nLeft = e.clientX + document.body.scrollLeft - 3;
		} else {
			nTop = aPosicoes.top;
			nLeft = aPosicoes.left;
		}

		var nWidthTela = 0;

		if (browserInfo.isIE) {
			nWidthTela = document.body.clientWidth;
		} else {
			nWidthTela = window.innerWidth;
		}

		if (nLeft + 150 > nWidthTela) {
			nLeft = nLeft - 130;
		}

		var oDivExcluirImagem = document.getElementById('divExcluirImagem');

		oDivExcluirImagem.style.left = nLeft;
		oDivExcluirImagem.style.top = nTop;
		oDivExcluirImagem.style.visibility = "visible";

		// Define evento.
		oDivExcluirImagem.onmouseout = function() {
			oDivExcluirImagem.style.visibility = "hidden";
		};

		oDivExcluirImagem.onmouseover = function() {
			oDivExcluirImagem.style.visibility = "visible";
		};

		cIdImagem = obj.getAttribute('fileName');

		oDivExcluirImagem.onclick = botaoEexcluirImagemImovel;

		return false;

	}

	function botaoEexcluirImagemImovel() {
		acaoExluirFotoImovel(cIdImagem);
	}

	var cFocoAtual = "";

	function selectTextboxContent(textbox) {
		cFocoAtual = textbox.id;
		setTimeout(function() {
			if (cFocoAtual == textbox.id) {
				textbox.select();
			}
		}, 1);
	}

	function ajusteVerticalPanelTextAux() {

		var compPanelText = document.getElementById('xAite1_itemPANELTEXT');

		if (compPanelText != null) {

			compPanelText.style.height = 1;
			compPanelText.style.width = 1;

			compPanelText.style.top = 0;

			compPanelText.parentNode.style.height = 1;

		}

	}

	function ajusteVerticalPanelText() {

		var compPanelText = document.getElementById('xAite1_itemPANELTEXT');

		if (compPanelText != null) {

			var infoLargura = getInfoElemento(compPanelText.parentNode.parentNode);
			var info = getInfoElemento(document.getElementById('panelbody'));

			var infoTd1 = getInfoElemento(document.getElementById('td1Atendimento'));
			var infoTd2 = getInfoElemento(document.getElementById('td2Atendimento'));

			var infoTool = getInfoElemento(document.getElementById('pgToolbarDataWindow'));

			var nAux = 0;

			if (browserInfo.isIE) {
				nAux = 15;
			} else if (browserInfo.isChrome) {
				nAux = 18;
			} else if (browserInfo.isFF) {
				nAux = 18;
			} else if (browserInfo.isSafari) {
				nAux = 18;
			}

			var nHeight = info.height - infoTd1.height - infoTd2.height - infoTool.height - nAux;

			if (nHeight < 1) {
				nHeight = 100;
			}

			if (nHeight > 0) {

				compPanelText.style.height = nHeight;
				compPanelText.parentNode.style.height = nHeight;
			}

			if (infoLargura.width > 0) {
				compPanelText.style.width = infoLargura.width;
			}

			compPanelText.style.top = '';

			compPanelText.style.display = 'block';
			compPanelText.parentNode.style.display = 'block';

		} else {

			compPanelText = document.getElementById('TextCorpoEmailPANELTEXT');

			if (compPanelText != null) {

				var infoLargura = getInfoElemento(compPanelText.parentNode.parentNode);
				var oBody = document.body;

				if (infoLargura.width > 0) {
					compPanelText.style.width = infoLargura.width;
				}

				var nAux = 0;

				if (browserInfo.isIE) {
					nAux = 15;
				} else if (browserInfo.isChrome) {
					nAux = 18;
				} else if (browserInfo.isFF) {
					nAux = 18;
				} else if (browserInfo.isSafari) {
					nAux = 18;
				}

				var nHeight = oBody.clientHeight - nAux - 40;

				if (nHeight < 1) {
					nHeight = 100;
				}

				if (nHeight > 0) {

					compPanelText.style.height = nHeight;
					compPanelText.parentNode.style.height = nHeight;
				}

				compPanelText.style.top = '';

				compPanelText.style.display = 'block';
				compPanelText.parentNode.style.display = 'block';

			}

		}

	}

	function getTimeoutNotificacao(tipoNotificacao) {

		var timeoutNotificacaoCliente = null;
		var nQtdNotificacaoCliente = 0;
		var nQtdAuxNotificacaoCliente = 0;
		var nQtdSegundosNotificacaoCliente = 4000; // 4 Segundos
		var nQtdTempoTimeoutNotificacaoCliente = 25;
		var nQtdVezesNotificacaoCliente = nQtdSegundosNotificacaoCliente / nQtdTempoTimeoutNotificacaoCliente;
		var nQtdExecucaoNotificacaoCliente = 0;
		var cTextoNotificacaoCliente = "";
		var nAuxFontSizeNotificacaoCliente = 1;
		var nFontSizeNotificacaoCliente = 0;
		var nQtdFontSizeNotificacaoCliente = 1;
		var lAux = false;
		var color = null;

		if (tipoNotificacao == 1) {
			color = "#30AD23";
		} else if (tipoNotificacao == 2) {
			color = "#EF9B0F";
		}

		return {
			timeoutNotificacaoCliente : timeoutNotificacaoCliente,
			nQtdNotificacaoCliente : nQtdNotificacaoCliente,
			nQtdAuxNotificacaoCliente : nQtdAuxNotificacaoCliente,
			nQtdSegundosNotificacaoCliente : nQtdSegundosNotificacaoCliente,
			nQtdTempoTimeoutNotificacaoCliente : nQtdTempoTimeoutNotificacaoCliente,
			nQtdVezesNotificacaoCliente : nQtdVezesNotificacaoCliente,
			nQtdExecucaoNotificacaoCliente : nQtdExecucaoNotificacaoCliente,
			cTextoNotificacaoCliente : cTextoNotificacaoCliente,
			nAuxFontSizeNotificacaoCliente : nAuxFontSizeNotificacaoCliente,
			nFontSizeNotificacaoCliente : nFontSizeNotificacaoCliente,
			nQtdFontSizeNotificacaoCliente : nQtdFontSizeNotificacaoCliente,
			lAux : lAux,
			color : color
		};

	}

	var objNotificacao = getTimeoutNotificacao(1);
	var objNotificacaoCliente = getTimeoutNotificacao(2);

	function iniciaAlertaNotificacaoCliente(tipoNotificacao) {

		var obj = null;
		if (tipoNotificacao == 1) {
			obj = objNotificacao;
		} else if (tipoNotificacao == 2) {
			obj = objNotificacaoCliente;
		}

		if (obj.timeoutNotificacaoCliente == null) {
			obj.nQtdNotificacaoCliente = 0;
			obj.nQtdAuxNotificacaoCliente = 0;
			obj.nAuxFontSizeNotificacaoCliente = 1;
			obj.nFontSizeNotificacaoCliente = 0;
			obj.nQtdFontSizeNotificacaoCliente = 1;
			alertaNotificacaoCliente(tipoNotificacao);
		}
	}

	function alertaNotificacaoCliente(tipoNotificacao) {

		var comp = null;
		var compDiv = null;

		var obj = null;
		if (tipoNotificacao == 1) {
			obj = objNotificacao;
			comp = document.getElementById('AtendimentoNotificacao');
			compDiv = document.getElementById('DivAtendimentoNotificacao');
		} else if (tipoNotificacao == 2) {
			obj = objNotificacaoCliente;
			comp = document.getElementById('AtendimentoNotificacaoCliente');
			compDiv = document.getElementById('DivAtendimentoNotificacaoCliente');
		}

		if (obj.timeoutNotificacaoCliente != null) {
			clearTimeout(obj.timeoutNotificacaoCliente);
			obj.timeoutNotificacaoCliente = null;
		}

		if (obj.nQtdAuxNotificacaoCliente <= obj.nQtdVezesNotificacaoCliente) {

			var nOpac = 0;
			if (obj.nQtdNotificacaoCliente == 0) {
				nOpac = 0;
				comp.style.fontSize = 11;
				comp.style.color = obj.color;
			} else {
				nOpac = obj.nQtdNotificacaoCliente / (obj.nQtdVezesNotificacaoCliente / 2);

				var nAux = parseInt((obj.nQtdAuxNotificacaoCliente / obj.nQtdVezesNotificacaoCliente) * 12);

				if (obj.nFontSizeNotificacaoCliente != nAux) {

					if (obj.lAux) {
						obj.lAux = false;
						comp.style.color = obj.color;
					} else {
						obj.lAux = true;
						comp.style.color = "#000000";
					}

					obj.nFontSizeNotificacaoCliente = nAux;

					if (obj.nAuxFontSizeNotificacaoCliente == 1 && obj.nQtdFontSizeNotificacaoCliente <= 2) {
						comp.style.fontSize = parseInt(comp.style.fontSize) - 1;
						obj.nQtdFontSizeNotificacaoCliente++;
						if (obj.nQtdFontSizeNotificacaoCliente == 3) {
							obj.nAuxFontSizeNotificacaoCliente++;
						}
					} else if (obj.nAuxFontSizeNotificacaoCliente == 2 && obj.nQtdFontSizeNotificacaoCliente <= 5) {
						comp.style.fontSize = parseInt(comp.style.fontSize) + 1;
						obj.nQtdFontSizeNotificacaoCliente++;
						if (obj.nQtdFontSizeNotificacaoCliente == 6) {
							obj.nAuxFontSizeNotificacaoCliente++;
						}
					} else if (obj.nAuxFontSizeNotificacaoCliente == 3 && obj.nQtdFontSizeNotificacaoCliente <= 6) {
						comp.style.fontSize = parseInt(comp.style.fontSize) - 1;
						obj.nQtdFontSizeNotificacaoCliente++;
						if (obj.nQtdFontSizeNotificacaoCliente == 7) {
							obj.nAuxFontSizeNotificacaoCliente = 1;
							obj.nQtdFontSizeNotificacaoCliente = 1;
						}
					}
				}

			}

			if (obj.nQtdAuxNotificacaoCliente > (obj.nQtdVezesNotificacaoCliente / 2)) {
				obj.nQtdNotificacaoCliente--;
			} else {
				obj.nQtdNotificacaoCliente++;
			}

			obj.nQtdAuxNotificacaoCliente++;

			if (browserInfo.isIE) {
				compDiv.style.filter = "alpha(opacity = " + (nOpac * 100) + ")";
			} else {
				compDiv.style.opacity = nOpac;
			}

			obj.timeoutNotificacaoCliente = setTimeout('alertaNotificacaoCliente(' + tipoNotificacao + ')', obj.nQtdTempoTimeoutNotificacaoCliente);

		} else {
			if (browserInfo.isIE) {
				compDiv.style.filter = "alpha(opacity = 0)";
			} else {
				compDiv.style.opacity = 0;
			}
			comp.style.fontSize = 11;
			comp.style.color = obj.color;

			if (obj.nQtdExecucaoNotificacaoCliente < 2) {
				obj.nQtdExecucaoNotificacaoCliente++;
			} else {
				obj.nQtdExecucaoNotificacaoCliente = 0;
			}

		}

	}

	var lAuxEventos = false;

	function removeAllChild(comp) {

		while (comp.hasChildNodes()) {
			comp.removeChild(comp.lastChild);
		}

	}

	// var aListaMenuSubTimeout = new Array();
	var divAuxTimeout = null;

	function mostrarDivAux(e, obj, id) {

		e = getEvento(e);
		var aPosicoes = getInfoElemento(obj);

		var nTop = 0;
		var nLeft = 0;

		// Define posição do menu.
		if (e.button == 2) {
			nTop = e.clientY + document.body.scrollTop - 5;
			nLeft = e.clientX + document.body.scrollLeft - 3;
		} else {
			nTop = aPosicoes.top;
			nLeft = aPosicoes.left;
		}

		var nWidthTela = 0;

		if (browserInfo.isIE) {
			nWidthTela = document.body.clientWidth;
		} else {
			nWidthTela = window.innerWidth;
		}

		var divAux = document.getElementById(id);

		var leftAux = getInfoElemento(divAux).width;

		if (nLeft + leftAux > nWidthTela) {
			nLeft = nLeft - leftAux;
		}

		divAux.style.visibility = 'visible';
		divAux.style.left = nLeft;
		divAux.style.top = nTop + obj.clientHeight;

		if (divAuxTimeout != null) {
			clearTimeout(divAuxTimeout);
		}
		divAuxTimeout = null;

	}

	function escondeDivAuxTime(id) {

		if (divAuxTimeout != null) {
			clearTimeout(divAuxTimeout);
		}
		divAuxTimeout = setTimeout('escondeDivAux("' + id + '")', 300);

	}

	function escondeDivAux(id) {

		var divAux = document.getElementById(id);
		divAux.style.visibility = 'hidden';
		divAux.style.left = -50;
		divAux.style.top = -50;

	}

	function mostrarDivAuxAux(id) {

		if (divAuxTimeout != null) {
			clearTimeout(divAuxTimeout);
		}
		divAuxTimeout = null;

	}

	function clickAbaOrcamento() {

		var oGrid = document.getElementById('mItensOrcamentos');

		if (oGrid.parentNode.id == 'divOrcamentoOrcamento') {
			return;
		}

		var oOrcamento = document.getElementById('divOrcamentoOrcamento');
		var oBdi = document.getElementById('divOrcamentoBdi');

		oBdi.removeChild(oGrid);
		oOrcamento.appendChild(oGrid);

	}

	function clickAbaOrcamentoBdi() {

		var oGrid = document.getElementById('mItensOrcamentos');

		if (oGrid.parentNode.id == 'divOrcamentoBdi') {
			return;
		}

		var oOrcamento = document.getElementById('divOrcamentoOrcamento');
		var oBdi = document.getElementById('divOrcamentoBdi');

		oOrcamento.removeChild(oGrid);
		oBdi.appendChild(oGrid);

	}

	function clickListaAtendimentoCliente(e) {

		e = getEvento(e);

		var comp = document.getElementById('tpEmpresaLabel');
		var compObservacao = document.getElementById('mObservacao');

		var id = comp.id;
		var idOri = id.substring(0, id.indexOf('Label'));

		var infoMenu = getInfoElemento(comp);

		var left = infoMenu.left;
		var top = infoMenu.top;

		if (document.getElementById(idOri + 'On').style.display != 'none') {

			if (e.clientX < left || (e.clientX > (left + comp.offsetWidth)) || e.clientY < top || (e.clientY > (top + comp.offsetHeight))) {

				infoMenu = getInfoElemento(compObservacao);

				left = infoMenu.left;
				top = infoMenu.top;

				if (e.clientX < left || (e.clientX > (left + compObservacao.offsetWidth)) || e.clientY < top || (e.clientY > (top + compObservacao.offsetHeight))) {
					clickToggle(comp);
				}

			}

		}

	}

	function clickDayWeek(comp, e) {
		var idCalendar = comp.getAttribute('idCalendar');
		if (idCalendar == null || idCalendar == 'null') {
			return;
		}
		var compMes = document.getElementById('idMes' + idCalendar);

		var nSemana = comp.getAttribute('semana');
		var nMes = compMes.getAttribute('mes');
		var nAno = compMes.getAttribute('ano');

		var lCtrl = false;
		if (e != null) {
			lCtrl = gridVerificaCtrl(e);
		}
		SelectCalendarControlWeek(idCalendar.replace('Container', ''), nSemana, nMes, nAno, lCtrl);
	}

	function setDisableCombobox(id, disabled) {
		var comp = document.getElementById('strCmbEdt' + id);
		if (comp != null) {
			if (disabled == 'true') {
				comp.className = 'STR_EDIT_DISABLED_COMBOBOX';
				comp.disabled = 'disabled';
			} else {
				comp.className = 'STR_EDIT_COMBOBOX';
				comp.disabled = '';
			}
		}
	}

	function setValueCombobox(id, valueText, valueObj, valueOculto, valueText) {
		var comp = document.getElementById('strCmbEdt' + id);
		if (comp != null) {
			comp.value = valueText;
			comp.setAttribute('valueObj', valueObj);
			comp.setAttribute('valueOculto', valueOculto);
			comp.setAttribute('textOculto', valueText);
		}
	}

	function setRenderedCombobox(id, display) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.style.display = display;
		}
	}

	function setValueCalendar(id, value) {
		var comp = document.getElementById(id + "InputDate");
		if (comp != null) {
			comp.value = value;
		}
	}

	function setDisabledCalendar(id, disabled) {
		var comp = document.getElementById(id + "InputDate");
		if (comp != null) {
			comp.disabled = disabled;
			var compBt = document.getElementById(id + "ButtonDate");
			if (compBt != null) {
				compBt.disabled = disabled;
			}

			if (disabled) {
				comp.className = "EDIT_DISABLED";
			} else {
				comp.className = "EDIT";
			}
		}
	}

	function setRenderedCalendar(id, rendered) {
		var comp = document.getElementById(id + "InputDate");
		if (comp != null) {

			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}

			var compBt = document.getElementById(id + "ButtonDate");
			if (compBt != null) {
				if (rendered) {
					compBt.style.display = '';
				} else {
					compBt.style.display = 'none';
				}
			}
		}
	}

	function setValueInputtext(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.value = value;
		}
	}

	function setDisabledInputtext(id, disabled) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.disabled = disabled;
			if (disabled) {
				comp.className = "EDIT_DISABLED";
			} else {
				comp.className = "EDIT";
			}
		}
	}

	function setReadOnlyInputtext(id, readonly) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.readonly = readonly;
			if (readonly) {
				comp.className = "EDIT_DISABLED";
			} else {
				comp.className = "EDIT";
			}
		}
	}

	function setRenderedInputtext(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {

			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setValueInputtextarea(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.value = value;
		}
	}

	function setDisabledInputtextarea(id, disabled) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.disabled = disabled;
			if (disabled) {
				comp.className = "MEMO_DISABLED";
			} else {
				comp.className = "MEMO";
			}
		}
	}

	function setReadOnlyInputtextarea(id, readonly) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.readonly = readonly;
			comp.className = "MEMO";
		}
	}

	function setRenderedInputtextarea(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {

			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setValuePanelText(id, value) {
		var comp = document.getElementById(id + "PANELTEXT");
		if (comp != null) {
			// value = replaceAll(value, " ", "&nbsp;");
			// value = value.replace(" ", "&nbsp;");
			// comp.innerHTML = value;

			// value = value.replace("<br />", "\n");
			value = replaceAll(value, "<br />", "\n");
			comp.innerHTML = "<pre class='PANEL_TEXT_PRE'>" + value + "</pre>";
		}
	}

	function setFocusComp(id) {
		var comp = document.getElementById(id);
		if (comp != null && !comp.disabled && comp.style.display != 'none') {
			try {
				comp.blur();
				comp.focus();
			} catch (e) {
			}
		}
	}

	function setFocusCompSelect(id) {
		var comp = document.getElementById(id);
		if (comp != null && !comp.disabled && comp.style.display != 'none') {
			try {
				comp.blur();
				comp.focus();
				comp.select();
			} catch (e) {
			}
		}
	}

	function acaoAlteraComboBox(id, cAuxRetorno) {
		var compList = document.getElementById('strCmbLis' + id);
		if (compList != null) {
			compList.parentNode.removeChild(compList);
		}

		var comp = document.getElementById(id);

		if (comp != null) {
			comp.parentNode.innerHTML = cAuxRetorno;
		}
	}

	function setTitlePushButton(id, title) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.title = title;
		}
	}

	function setValuePushButton(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.value = value;
		}
	}

	function setDisabledPushButton(id, disabled) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.disabled = disabled;
		}
	}

	function setRenderedPushButton(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.style.display = "";
			} else {
				comp.style.display = "none";
			}
		}

		if (id.toUpperCase().indexOf("CONSULTA") > -1) {

			var compConsulta = document.getElementById(id + "Consulta");
			if (compConsulta != null) {

				if (comp != null) {
					comp.style.display = "none";
				}

				if (rendered) {
					compConsulta.style.display = "";
				} else {
					compConsulta.style.display = "none";
				}

			}

			var compCadastroConsulta = document.getElementById(id + "CadastroConsulta");
			if (compCadastroConsulta != null) {

				if (rendered) {
					compCadastroConsulta.style.display = "";
				} else {
					compCadastroConsulta.style.display = "none";
				}

			}

		}
	}

	function setInnerHtmlGrid(id, innerHtml) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.innerHTML = innerHtml;
			document.getElementById('divGridAutoComplete').style.visibility = 'visible';
		}
	}

	function setAtualizaGoogleMaps(innerHtml) {
		var compGmap = document.getElementById('spanGoogleMaps');
		if (compGmap != null && (compGmap.innerHTML == null || compGmap.innerHTML == '')) {
			compGmap.innerHTML = innerHtml;
		}
	}

	function setAtualizaGoogleMapsEndereco(innerHtml) {
		var compGmap = document.getElementById('spanGoogleMaps');
		if (compGmap != null) {
			compGmap.innerHTML = innerHtml;
		}
	}

	function setDownloadArquivo(cArquivo) {
		var btDownload = document.getElementById('btDownload');
		btDownload.value = cArquivo;
		btDownload.focus();
	}

	function setToolbarRenameItem(id, text, icon) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.childNodes[1].innerHTML = text;
			comp.childNodes[0].childNodes[0].src = icon;
		}
	}

	function setToolbarIcon(id, title, icon) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.src = icon;
			setToolbarTitle(id, title);
		}
	}
	
	function setToolbarRemove(id) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.parentNode.removeChild(comp);
		}
	}

	function setToolbarDisabled(id, readonly, className) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.readonly = readonly;
			comp.className = className;
		}
	}

	function setToolbarTitle(id, title) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.title = title;
		}
	}

	function setToolbarShowHide(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setToolbarClicked(id, clicked) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (clicked) {
				comp.parentNode.className = 'BOTAO_NAVEGADOR_CELL_SELECIONADO';
			} else {
				comp.parentNode.className = 'BOTAO_NAVEGADOR_CELL';
			}
		}
	}

	function setValueInputSecret(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.value = value;
		}
	}

	function setDisabledInputSecret(id, disabled) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.disabled = disabled;
			if (disabled) {
				comp.className = 'EDIT_DISABLED';
			} else {
				comp.className = 'EDIT';
			}
		}
	}

	function setReadonlyInputSecret(id, readonly) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.readonly = readonly;
			if (readonly) {
				comp.className = 'EDIT_DISABLED';
			} else {
				comp.className = 'EDIT';
			}
		}
	}

	function setRenderedImage(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setValueOutputText(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.innerHTML = value;
		}
	}

	function setRenderedOutputText(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setValueInputHidden(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.value = value;
		}
	}

	function setValueCheckBox(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.checked = value;
		}
	}

	function setTitleCheckBox(id, title) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.title = title;
		}
	}

	function setDisabledCheckBox(id, disabled) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.disabled = disabled;
		}
	}

	function setRenderedCheckBox(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setCaptionFieldSet(id, value) {
		var comp = document.getElementById(id);
		if (comp != null && comp.getElementsByTagName('legend').length > 0) {
			comp.getElementsByTagName('legend')[0].innerHTML = value;
		}
	}
	
	function setBorderFieldSet(id, value) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (value == 'true') {
				comp.styleClass = 'LABEL';
			} else {
				comp.styleClass = 'LABEL_SEM_BORDA';
			}
		}
	}

	function setRenderedFieldSet(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setDisabledTab(id, disabled) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (disabled) {
				comp.className = 'TAB_PANEL TAB_DESABILITADA TAB_HEADER';
			} else {
				comp.className = 'TAB_PANEL TAB_DESATIVADA TAB_HEADER';
			}
		}
	}

	function setRenderedTab(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.className = 'TAB_PANEL TAB_DESATIVADA TAB_HEADER';
			} else {
				comp.className = 'TAB_PANEL TAB_DESABILITADA TAB_HEADER';
			}
		}
	}

	function setActivateTab(id) {
		var comp = document.getElementById(id);
		if (comp != null) {
			comp.onclick();
		}
	}

	function setRenderedGrid(id, rendered) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (rendered) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function setDisabledGrid(id, disabled) {
		var comp = document.getElementById(id);
		if (comp != null) {
			if (disabled) {
				comp.style.display = '';
			} else {
				comp.style.display = 'none';
			}
		}
	}

	function criarAbaGeralDataWindow(id, cWidth, cHeight, cTop, cLeft, cCaption, sTela) {

		try {

			var framemdi = document.getElementById('framemdi');
			var divAba = document.createElement('div');
			divAba.id = id;
			divAba.className = 'janela';
			divAba.style.display = 'none';
			divAba.style.width = cWidth;
			divAba.style.height = cHeight;
			divAba.style.top = cTop;
			divAba.style.left = cLeft;

			var divAbaTable = document.createElement('table');
			divAbaTable.cellSpacing = '0';
			divAbaTable.cellPadding = '0';
			divAbaTable.style.width = '100%';
			divAbaTable.style.height = '100%';

			var divAbaBarraTableTbody = document.createElement('tbody');

			var divAbaBarraTableTbodyTr1 = document.createElement('tr');
			var divAbaBarraTableTbodyTr1Td = document.createElement('td');
			divAbaBarraTableTbodyTr1Td.style.height = '1px';

			var divAbaBarraTableTbodyTr2 = document.createElement('tr');
			var divAbaBarraTableTbodyTr2Td = document.createElement('td');
			divAbaBarraTableTbodyTr2Td.style.height = '100%';

			var divAbaBarraTableTbodyTr3 = document.createElement('tr');
			var divAbaBarraTableTbodyTr3Td = document.createElement('td');
			divAbaBarraTableTbodyTr3Td.style.height = '1px';

			var divAbaBarra = document.createElement('div');
			divAbaBarra.id = id + 'Barra';
			divAbaBarra.className = 'barra';
			divAbaBarra.setAttribute('cId', id.substring(3));
			divAbaBarra.onmousedown = function() {
				alteraMdiMenu(this);
			};
			var divAbaBarraTable = document.createElement('table');
			divAbaBarraTable.width = '100%';
			divAbaBarraTable.cellPadding = '0';
			divAbaBarraTable.cellSpacing = '0';
			divAbaBarraTable.style.tableLayout = 'fixed';
			var divAbaBarraTableTBody = document.createElement('tbody');
			var divAbaBarraTableTr = document.createElement('tr');

			var divAbaBarraTableTrTd1 = document.createElement('td');
			divAbaBarraTableTrTd1.style.textAlign = 'right';
			divAbaBarraTableTrTd1.style.border = '0';
			divAbaBarraTableTrTd1.style.padding = '0px';
			divAbaBarraTableTrTd1.style.cursor = 'move';
			divAbaBarraTableTrTd1.style.width = '31px';
			divAbaBarraTableTrTd1.setAttribute('cId', id.substring(3));
			divAbaBarraTableTrTd1.onmousedown = function(event) {
				startMoveJanelaMdiMenu(this, event);
			};
			divAbaBarraTableTrTd1.onmouseup = function() {
				stopMoveJanelaMdiMenu(this);
			};
			var divAbaBarraTableTrTd1Img = document.createElement('img');
			divAbaBarraTableTrTd1Img.src = '/layout/imagens/tela/4_2.png';
			divAbaBarraTableTrTd1Img.height = '19';
			divAbaBarraTableTrTd1Img.width = '31';

			var divAbaBarraTableTrTd2 = document.createElement('td');
			divAbaBarraTableTrTd2.className = 'MENU_7';
			divAbaBarraTableTrTd2.style.cursor = 'move';
			divAbaBarraTableTrTd2.style.whiteSpace = 'nowrap';
			divAbaBarraTableTrTd2.style.overflow = 'hidden';
			divAbaBarraTableTrTd2.setAttribute('cId', id.substring(3));
			divAbaBarraTableTrTd2.onmousedown = function(event) {
				startMoveJanelaMdiMenu(this, event);
			};
			divAbaBarraTableTrTd2.onmouseup = function() {
				stopMoveJanelaMdiMenu(this);
			};
			var divAbaBarraTableTrTd2Div = document.createElement('div');
			divAbaBarraTableTrTd2Div.id = id + "divTitulo";
			divAbaBarraTableTrTd2Div.style.width = '300%';
			var divAbaBarraTableTrTd2Span = document.createElement('span');
			divAbaBarraTableTrTd2Span.style.cursor = 'move';
			divAbaBarraTableTrTd2Span.className = 'PANEL_HEADER_TEMPLATE';
			divAbaBarraTableTrTd2Span.id = id + "tituloMdi";
			divAbaBarraTableTrTd2Span.innerHTML = cCaption;
			divAbaBarraTableTrTd2Span.style.whiteSpace = 'nowrap';

			var divAbaBarraTableTrTd3 = document.createElement('td');
			divAbaBarraTableTrTd3.className = 'MENU_7';
			divAbaBarraTableTrTd3.style.width = '20px';
			var divAbaBarraTableTrTd3Div = document.createElement('div');
			divAbaBarraTableTrTd3Div.id = id + "Maximizar";
			divAbaBarraTableTrTd3Div.title = 'Maximizar';
			divAbaBarraTableTrTd3Div.className = 'botao_maximizar';
			divAbaBarraTableTrTd3Div.setAttribute('cId', id.substring(3));
			divAbaBarraTableTrTd3Div.onmouseup = function() {
				maximizarMdiMenu(this);
			};
			divAbaBarraTableTrTd3Div.innerHTML = '&nbsp;';

			var divAbaBarraTableTrTd4 = document.createElement('td');
			divAbaBarraTableTrTd4.className = 'MENU_7';
			divAbaBarraTableTrTd4.style.width = '20px';
			var divAbaBarraTableTrTd4Div = document.createElement('div');
			divAbaBarraTableTrTd4Div.title = 'Fechar';
			divAbaBarraTableTrTd4Div.className = 'botao_fechar';
			divAbaBarraTableTrTd4Div.setAttribute('cId', id.substring(3));
			divAbaBarraTableTrTd4Div.onclick = function() {
				procuraFecharMenu(this);
			};
			divAbaBarraTableTrTd4Div.innerHTML = '&nbsp;';

			var divAbaBarraTableTrTd5 = document.createElement('td');
			divAbaBarraTableTrTd5.style.textAlign = 'right';
			divAbaBarraTableTrTd5.style.border = '0';
			divAbaBarraTableTrTd5.style.padding = '0px';
			divAbaBarraTableTrTd5.style.cursor = 'move';
			divAbaBarraTableTrTd5.style.width = '33px';
			divAbaBarraTableTrTd5.setAttribute('cId', id.substring(3));
			divAbaBarraTableTrTd5.onmousedown = function(event) {
				startMoveJanelaMdiMenu(this, event);
			};
			divAbaBarraTableTrTd5.onmouseup = function() {
				stopMoveJanelaMdiMenu(this);
			};
			var divAbaBarraTableTrTd5Img = document.createElement('img');
			divAbaBarraTableTrTd5Img.src = '/layout/imagens/tela/3_2.png';
			divAbaBarraTableTrTd5Img.height = '19';
			divAbaBarraTableTrTd5Img.width = '33';

			var divAbaBarraTableTrTd6 = document.createElement('td');
			divAbaBarraTableTrTd6.className = 'MENU_8';
			divAbaBarraTableTrTd6.style.cursor = 'move';
			divAbaBarraTableTrTd6.setAttribute('cId', id.substring(3));
			divAbaBarraTableTrTd6.onmousedown = function(event) {
				startMoveJanelaMdiMenu(this, event);
			};
			divAbaBarraTableTrTd6.onmouseup = function() {
				stopMoveJanelaMdiMenu(this);
			};

			divAbaBarraTableTrTd1.appendChild(divAbaBarraTableTrTd1Img);
			divAbaBarraTableTr.appendChild(divAbaBarraTableTrTd1);
			divAbaBarraTableTrTd2.appendChild(divAbaBarraTableTrTd2Div);
			divAbaBarraTableTrTd2Div.appendChild(divAbaBarraTableTrTd2Span);
			divAbaBarraTableTr.appendChild(divAbaBarraTableTrTd2);
			divAbaBarraTableTrTd3.appendChild(divAbaBarraTableTrTd3Div);
			divAbaBarraTableTr.appendChild(divAbaBarraTableTrTd3);
			divAbaBarraTableTrTd4.appendChild(divAbaBarraTableTrTd4Div);
			divAbaBarraTableTr.appendChild(divAbaBarraTableTrTd4);
			divAbaBarraTableTrTd5.appendChild(divAbaBarraTableTrTd5Img);
			divAbaBarraTableTr.appendChild(divAbaBarraTableTrTd5);
			divAbaBarraTableTr.appendChild(divAbaBarraTableTrTd6);
			divAbaBarraTable.appendChild(divAbaBarraTableTBody);
			divAbaBarraTableTBody.appendChild(divAbaBarraTableTr);
			divAbaBarra.appendChild(divAbaBarraTable);

			var divAbaFrame = document.createElement('div');
			divAbaFrame.id = id + "conteudo";
			divAbaFrame.className = 'conteudo';
			divAbaFrame.style.height = '100%';
			var divFrame = document.createElement('iframe');
			divFrame.id = id + "iFrame";
			divFrame.allowTransparency = 'true';
			divFrame.frameBorder = 'no';
			divFrame.scrolling = 'auto';
			divFrame.style.height = '100%';
			divFrame.style.width = '100%';
			divFrame.style.position = 'static';
			divFrame.style.display = 'block';
			divFrame.style.overflow = 'auto';
			divFrame.src = sTela;
			divAbaFrame.appendChild(divFrame);

			var divAbaStatus = document.createElement('div');
			divAbaStatus.id = id + "statusBar";
			divAbaStatus.className = 'statusBarDrop';

			var divAbaStatusTable = document.createElement('table');
			divAbaStatusTable.width = '100%';
			divAbaStatusTable.height = '100%';
			divAbaStatusTable.cellPadding = '0';
			divAbaStatusTable.cellSpacing = '0';
			divAbaStatusTable.border = '0';
			divAbaStatusTable.className = 'statusbar';

			var divAbaStatusTableTBody = document.createElement('tbody');

			var divAbaStatusTableTr = document.createElement('tr');
			var divAbaStatusTableTrTd1 = document.createElement('td');
			divAbaStatusTableTrTd1.innerHTML = '&nbsp;';
			var divAbaStatusTableTrTd2 = document.createElement('td');
			divAbaStatusTableTrTd2.innerHTML = '&nbsp;';
			divAbaStatusTableTrTd2.className = 'statusbarResize';
			divAbaStatusTableTrTd2.id = id + "statusBarResize";
			divAbaStatusTableTrTd2.setAttribute('cId', id.substring(3));
			divAbaStatusTableTrTd2.onmousedown = function(event) {
				startResizeJanelaMdiMenu(this, event);
			};

			divAbaStatusTableTr.appendChild(divAbaStatusTableTrTd1);
			divAbaStatusTableTr.appendChild(divAbaStatusTableTrTd2);
			divAbaStatusTableTBody.appendChild(divAbaStatusTableTr);
			divAbaStatusTable.appendChild(divAbaStatusTableTBody);
			divAbaStatus.appendChild(divAbaStatusTable);

			divAbaBarraTableTbodyTr1Td.appendChild(divAbaBarra);
			divAbaBarraTableTbodyTr2Td.appendChild(divAbaFrame);
			divAbaBarraTableTbodyTr3Td.appendChild(divAbaStatus);

			divAbaBarraTableTbodyTr1.appendChild(divAbaBarraTableTbodyTr1Td);
			divAbaBarraTableTbodyTr2.appendChild(divAbaBarraTableTbodyTr2Td);
			divAbaBarraTableTbodyTr3.appendChild(divAbaBarraTableTbodyTr3Td);

			divAbaBarraTableTbody.appendChild(divAbaBarraTableTbodyTr1);
			divAbaBarraTableTbody.appendChild(divAbaBarraTableTbodyTr2);
			divAbaBarraTableTbody.appendChild(divAbaBarraTableTbodyTr3);
			divAbaTable.appendChild(divAbaBarraTableTbody);
			divAba.appendChild(divAbaTable);

			framemdi.appendChild(divAba);

		} finally {

			divAba = null;
			divAbaBarra = null;
			divAbaTable = null;
			divAbaBarraTableTbody = null;
			divAbaBarraTableTbodyTr1 = null;
			divAbaBarraTableTbodyTr1Td = null;
			divAbaBarraTableTbodyTr2 = null;
			divAbaBarraTableTbodyTr2Td = null;
			divAbaBarraTableTbodyTr3 = null;
			divAbaBarraTableTbodyTr3Td = null;
			divAbaBarra = null;
			divAbaBarraTable = null;
			divAbaBarraTableTBody = null;
			divAbaBarraTableTr = null;
			divAbaBarraTableTrTd1 = null;
			divAbaBarraTableTrTd1Img = null;
			divAbaBarraTableTrTd2 = null;
			divAbaBarraTableTrTd2Span = null;
			divAbaBarraTableTrTd3 = null;
			divAbaBarraTableTrTd3Div = null;
			divAbaBarraTableTrTd4 = null;
			divAbaBarraTableTrTd4Div = null;
			divAbaBarraTableTrTd5 = null;
			divAbaBarraTableTrTd5Img = null;
			divAbaBarraTableTrTd6 = null;
			divAbaFrame = null;
			divFrame = null;
			divAbaStatus = null;
			divAbaStatusTable = null;
			divAbaStatusTableTBody = null;
			divAbaStatusTableTr = null;
			divAbaStatusTableTrTd1 = null;
			divAbaStatusTableTrTd2 = null;

		}

	}

	function criarAbaGeral(id, sTela) {

		try {

			var framemdi = document.getElementById('framemdi');
			var divAba = document.createElement('div');
			divAba.id = id;
			divAba.className = 'framemdi heightdivframe';
			var divFrame = document.createElement('iframe');
			divFrame.id = id + "iFrame";
			divFrame.allowTransparency = 'true';
			divFrame.frameBorder = 'no';
			divFrame.className = 'heightframe';
			divFrame.scrolling = 'auto';
			divFrame.style.border = '0px none';
			divFrame.style.width = '100%';
			divFrame.src = sTela;

			divAba.appendChild(divFrame);
			framemdi.appendChild(divAba);

		} finally {

			divAba = null;
			divFrame = null;

		}

		oAbaAtual = null;
		ArrumaClass('DIV', id);

	}

	function criaBotao(id, idAba, lDataWindow, value) {

		try {

			var objMaeAba = document.getElementById('objMaeAba');
			var divBotao = document.createElement('div');
			divBotao.id = id;
			divBotao.style.styleFloat = 'left';
			divBotao.style.cssFloat = 'left';
			divBotao.className = 'BOTAOABAPRESS';
			divBotao.onmouseover = function() {
				MouseOverMDIMenu(this);
			};
			divBotao.onmouseout = function() {
				MouseOutMDIMenu(this);
			};
			var divTable = document.createElement('table');
			divTable.cellSpacing = '0';
			divTable.cellPadding = '0';
			divTable.border = '0';
			divTable.style.margin = '1px 0pt 0pt 1px';
			var divTBody = document.createElement('tbody');
			var divTr = document.createElement('tr');
			var divTd1 = document.createElement('td');
			divTd1.id = id + "left";
			divTd1.className = 'rich-table-cell colLeftPress';
			var divTd2 = document.createElement('td');
			divTd2.id = id + "center";
			divTd2.className = 'rich-table-cell colCenterPress';
			var divSpan = document.createElement('span');
			var divInput1 = document.createElement('input');
			divInput1.id = 'mdi' + idAba + 'titMdi';
			divInput1.type = 'button';
			divInput1.className = 'BOTAOTEXTO';
			divInput1.title = value;
			divInput1.value = value;
			divInput1.style.width = '85px';
			divInput1.onclick = function() {
				ClickMDIMenu(this);
			};

			var divInput2 = document.createElement('input');
			divInput2.id = 'close' + idAba;
			divInput2.type = 'button';
			divInput2.className = 'BTNCLOSESELECIONADO';
			divInput2.title = 'Fechar';
			divInput2.style.border = '0px none';
			divInput2.style.width = '14px';
			divInput2.style.height = '14px';
			divInput2.style.verticalAlign = 'middle';
			divInput2.style.cursor = 'default';
			divInput2.style.paddingLeft = '5px';
			divInput2.onmouseover = function() {
				MouseOverCloseButtonMDIMenu(this);
			};
			divInput2.onmouseout = function() {
				MouseOutCloseButtonMDIMenu(this);
			};
			divInput2.setAttribute('cId', idAba);

			var divInput3 = document.createElement('input');
			divInput3.id = 'closeAux' + idAba;
			divInput3.setAttribute('cId', idAba);
			divInput3.type = 'button';
			divInput3.style.display = 'none';

			if (lDataWindow) {
				divInput2.onclick = function() {
					procuraFecharMenu(this);
				};
				divInput3.onclick = function() {
					fecharAbaMenu(this);
				};
			} else {
				divInput2.onclick = function() {
					fecharAbaMenuAntigo(this);
				};
				divInput3.onclick = function() {
					return false;
				};
			}

			var divTd3 = document.createElement('td');
			divTd3.id = id + "right";
			divTd3.className = 'rich-table-cell colRightPress';
			divTr.appendChild(divTd1);
			divSpan.appendChild(divInput1);
			divSpan.appendChild(divInput2);
			divSpan.appendChild(divInput3);
			divTd2.appendChild(divSpan);
			divTr.appendChild(divTd2);
			divTr.appendChild(divTd3);
			divTBody.appendChild(divTr);
			divTable.appendChild(divTBody);
			divBotao.appendChild(divTable);
			objMaeAba.appendChild(divBotao);

		} finally {

			divBotao = null;
			divTable = null;
			divTBody = null;
			divTr = null;
			divTd1 = null;
			divTd2 = null;
			divSpan = null;
			divInput1 = null;
			divInput2 = null;
			divInput3 = null;
			divTd3 = null;

		}

		objMaeAba.style.width = ((objMaeAba.childNodes.length) * 120) + 'px';

		resetaCssBotaoMenu(id);

	}

	function menuMouseDown(e) {

		e = getEvento(e);

		var compImg = document.getElementById('imgEsconderMenu');
		if (compImg.className.indexOf("FECHAR_MENU") == -1) {
			return;
		}

		lLateralMenuPress = true;

		var oMenuCentralDiv = document.getElementById("MENU_CENTRAL_DIV");
		oMenuCentralDiv.style.display = "";
		oMenuCentralDiv.style.height = 4000;
		oMenuCentralDiv.style.width = 4000;

		var oBarraMenuLateral = document.getElementById("BARRA_MENU_LATERAL");

		var oBarraMenuLateralOculto = document.getElementById("BARRA_MENU_LATERAL_OCULTO");
		oBarraMenuLateralOculto.style.display = "";

		var obj = getInfoElemento(oBarraMenuLateral);
		oBarraMenuLateralOculto.style.top = obj.top;
		oBarraMenuLateralOculto.style.height = obj.height;

	}

	function menuMouseUp(e) {

		var oMenuCentralDiv = document.getElementById("MENU_CENTRAL_DIV");
		oMenuCentralDiv.style.display = "none";
		oMenuCentralDiv.style.height = 0;
		oMenuCentralDiv.style.width = 0;

		var pos = e.clientX;
		if (e.clientX < 150) {
			pos = 150;
		}

		var oBarraMenuLateral = document.getElementById("pnMenu");
		oBarraMenuLateral.style.width = pos;

		var oBarraMenuLateralOculto = document.getElementById("BARRA_MENU_LATERAL_OCULTO");
		oBarraMenuLateralOculto.style.display = "none";

		setTimeout("salvarResizeMenu(" + parseInt(pos) + ");", 1);

	}

	var oTimeoutMouseMove = null;

	function menuMouseMoveTimeout(pos) {

		var oBarraMenuLateralOculto = document.getElementById("BARRA_MENU_LATERAL_OCULTO");
		oBarraMenuLateralOculto.style.width = pos;

		var oMenuCentralDiv = document.getElementById("MENU_CENTRAL_DIV");
		oMenuCentralDiv.style.display = "";

	}

	function menuMouseMove(e) {

		if (oTimeoutMouseMove != null) {
			clearTimeout(oTimeoutMouseMove);
		}

		e = getEvento(e);

		var pos = e.clientX;
		if (e.clientX < 150) {
			pos = 150;
		}

		oTimeoutMouseMove = setTimeout("menuMouseMoveTimeout(" + pos + ");", 1);

	}

	/**
	 * Força a localização de ítens na tela (_ComporOrcamento e _ListarOrcamento)
	 * 
	 * @param e
	 * @param comp
	 */
	function localizarComporOrcamento(e, comp) {

		e = getEvento(e);
		var keyCode = e.keyCode;

		if (keyCode == 13) {
			blurWeb('0', comp, 'odcmLocalizar');
			document.getElementById('BotaoLocalizar').click();
		}

	}


	function gridVerificaCtrl(e) {

		if (e == null) {
			return false;
		}

		e = getEvento(e);
		return e.ctrlKey;
	}

	function gridVerificaShift(e) {

		if (e == null) {
			return false;
		}

		e = getEvento(e);
		return e.shiftKey;
	}

}
