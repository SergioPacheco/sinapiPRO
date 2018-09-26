package br.edu.ifrn.sinapiPRO.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Funcoes {

	/**
	 * Passa para o próximo dia do mês ou do mês anterior
	 * cOper + ou - 
	 * 
	 */
	public static Date AddDia(Date data, Integer nDia_Vencto, String cOper) {

		Date dData = null;
		Integer nDia = null;
		Integer nMes = null;
		Integer nAno = null;

		nDia = Lib.Day(data);
		nMes = Lib.Month(data);
		nAno = Lib.Year(data);

		// Caso seja informado o dia a ser atribuido
		if (nDia_Vencto != 0) {

			nDia = nDia_Vencto;

		} else {

			if (cOper.equals("+")) {

				if (nMes == 12) {
					nAno++;
					nMes = 1;
				} else {
					nMes++;
				}

			} else {

				if (nMes == 1) {
					nAno--;
					nMes = 12;
				} else {
					nMes--;
				}
			}
		}

		if (nDia > Lib.diasmes(nMes, nAno)) {
			nDia = Lib.diasmes(nMes, nAno);
		}

		dData = Lib.CToD(Lib.StrZero(nDia, 2) + "/" + Lib.StrZero(nMes, 2) + "/" + Lib.Str(nAno, 4));

		return dData;
	}

	/**
	 * Alinha
	 * 
	 */
	public static String Alinha(String cPalavra, Integer nEspaco, String cAlinhamento) {

		String cResPalavra = "";

		cResPalavra = cPalavra;

		while (Lib.Len(cResPalavra) < nEspaco) {

			if (cAlinhamento.equals("ESQUERDA")) {
				cResPalavra = cResPalavra + " ";
			} else if (cAlinhamento.equals("DIREITA")) {
				cResPalavra = " " + cResPalavra;
			} else if (cAlinhamento.equals("CENTRO")) {
				cResPalavra = " " + cResPalavra + " ";
			}
		}

		cResPalavra = Lib.Substr(cResPalavra, 1, nEspaco);

		return cResPalavra;
	}

	/**
	 * Arredondar Valores
	 * 
	 */
	public static Double ArredondarValores(Double nValorOrigem, String cColuna, String cTipoArredondamento, String cItoc1_Tip, Global global) {

		Double nValorConvertido = null;
		Long nCasaDecimal = null;

		nValorConvertido = nValorOrigem;

		if (Lib.Upper(cColuna).equals("QUANTIDADE")) {
			nCasaDecimal = global.nDecimalQuantidadeOrc;

		} else {
			nCasaDecimal = global.nDecimalUnitarioOrc;

		}

		if (cTipoArredondamento.equals("G") || (cTipoArredondamento.equals("C") && cItoc1_Tip.equals("1"))) {
			nValorConvertido = Lib.Round(nValorOrigem, nCasaDecimal.intValue());

		} else if (cTipoArredondamento.equals("T")) {
			nValorConvertido = Funcoes.Truncar(nValorOrigem, nCasaDecimal.intValue());

		}

		return nValorConvertido;
	}

	
	/**
	 * Extenso Ordinal
	 * 
	 */
	public static String ExtensoOrdinal(Number nNumero) {

		String cNumero = "";
		String cEspaco = "";
		StringBuilder cRet = new StringBuilder();
		List<Object[]> aOrdinal = null;

		cNumero = Lib.StrZero(Lib.Val(Lib.AsString(nNumero)), 3);

		cRet.append(Lib.NTrim(Lib.Val(cNumero)) + "$(");

		aOrdinal = new ArrayList<Object[]>();

		aOrdinal.add(new Object[] { "centésimo", "ducentésimo", "trecentésimo", "quadrigentésimo", "quingentésimo", "sexcentésimo", "setigentésimo", "octigentésimo", "nongentésimo" });
		aOrdinal.add(new Object[] { "décimo", "vigésimo", "trigésimo", "quadragésimo", "quinquagésimo", "sexagésimo", "septuagésimo", "octogésimo", "nonagésimo" });
		aOrdinal.add(new Object[] { "primeiro", "segundo", "terceiro", "quarto", "quinto", "sexto", "sétimo", "oitávo", "nono" });

		for (int i = 0; i < 3; i++) {

			if (Lib.Substr(cNumero, i, i + 1) != "0") {

				cRet.append(cEspaco + aOrdinal.get(i)[Lib.Val(Lib.Substr(cNumero, i, i + 1)).intValue()]);
				cEspaco = " ";
			}
		}

		cRet.append(")");
		return cRet.toString();
	}

	
	/**
	 * Retorna data no primeiro dia do mês
	 * 
	 */
	public static Date PrimeiraDataMes(Date dData) {
		return Lib.CToD("01/" + Lib.StrZero(Lib.Month(dData), 2) + "/" + Lib.StrZero(Lib.Year(dData), 4));
	}


	/**
	 * Substitui Caracter
	 * 
	 */
	public static String SubstituirCaracter(String cCaracter, String cNovoValor, String cValor) {
		String cRetorno = "";
		Integer nPos = null;
		Integer i = null;

		cRetorno = Lib.AllTrim(cValor);

		// d Substitui o caracter das posições da string com o novo valor
		nPos = Lib.At(cCaracter, Lib.Upper(Lib.AllTrim(cValor)));
		if (nPos > 0) {

			for (i = 0; i < Lib.SLen(Lib.AllTrim(cValor)); i++) {
				if (!Lib.Upper(Lib.Substr(Lib.AllTrim(cValor), i, 1)).equalsIgnoreCase(cCaracter)) {
					break;
				}
			}

			cRetorno = Lib.AllTrim(Lib.Substr(Lib.AllTrim(cValor), 1, (nPos - 1)) + Lib.Substr(Lib.AllTrim(cNovoValor), 1, (i - nPos)) + Lib.Substr(Lib.AllTrim(cValor), i));
		}

		if (Lib.Right(cRetorno, 1).equals(".")) {
			cRetorno = Lib.Substr(cRetorno, 1, Lib.Len(cRetorno) - 1);
		}

		return cRetorno;
	}


	/**
	 * Texto Para Numero
	 * 
	 */
	public static Double TextoParaNumero(String cValor) {
		Double nValor = null;

		cValor = Lib.StrTran(cValor, ".", "");
		cValor = Lib.StrTran(cValor, ",", ".");

		nValor = Lib.Val(cValor);

		return nValor;
	}

	/**
	 * Tira Char
	 * 
	 */
	public static String TiraChar(String cValor, Integer nTamanho, boolean lCaracter) {
		String cTexto = "";
		StringBuilder cTexto_Aux = new StringBuilder();

		if (!Lib.Empty(cValor)) {
			cTexto = Lib.StrTran(cValor, "/", "");
			cTexto = Lib.StrTran(cTexto, "-", "");
			cTexto = Lib.StrTran(cTexto, ".", "");
		}

		if (nTamanho != null && nTamanho >= Lib.Len(Lib.AllTrim(cTexto))) {
			cTexto = Lib.AllTrim(cTexto) + Lib.Space(nTamanho - Lib.Len(Lib.AllTrim(cTexto)));
		}

		// Retira caracteres alfabéticos
		if (lCaracter) {
			for (int i = 0; i < Lib.SLen(cTexto); i++) {
				if (Lib.At(Lib.Substr(cTexto, i, 1), "0123456789") != 0) {
					cTexto_Aux.append(Lib.Substr(cTexto, i, 1));
				}
			}
			cTexto = cTexto_Aux.toString();
		}

		return cTexto;
	}

	/**
	 * Truncar
	 * 
	 */
	public static Double Truncar(Double nValor, Integer nCasas) {

		String cValor;
		Integer nPosicao;

		cValor = Lib.Str(nValor);
		nPosicao = Lib.At(",", cValor);
		cValor = Lib.Substr(cValor, 1, nPosicao + nCasas);

		return Lib.Val(cValor);
	}

	/**
	 * Retorna data no ultimo dia do mês
	 * 
	 * @param dData
	 * @return
	 */
	public static Date UltimaDataMes(Date dData) {

		return Lib.CToD(Lib.StrZero(Lib.diasmes(Lib.Month(dData), Lib.Year(dData)), 2) + "/" + Lib.StrZero(Lib.Month(dData), 2) + "/" + Lib.StrZero(Lib.Year(dData), 4));
	}

	/**
	 * Valida Cnpj Cpf
	 * 
	 * @param cCgcCpf
	 * @param lCgc
	 * @param lRequerido
	 * @return
	 */
	public static boolean ValidaCnpjCpf(String cCgcCpf, Boolean lCgc, Boolean lRequerido) {
		Integer cCol1 = null;
		Integer cCol2 = null;
		Integer cCol3 = null;
		Integer cCol4 = null;
		Integer cCol5 = null;
		Integer cCol6 = null;
		Integer cCol7 = null;
		Integer cCol8 = null;
		Integer cCol9 = null;
		Integer cCol10 = null;
		Integer cCol11 = null;
		Integer cCol12 = null;
		Integer cCol13 = null;
		Integer nTot1 = null;
		Integer nTot2 = null;
		Integer nDiv1 = null;
		Integer nDiv2 = null;
		Integer nMul1 = null;
		Integer nMul2 = null;
		Integer nDig1 = null;
		Integer nDig2 = null;
		Integer RV28Res = null;
		Integer RV27Tot = null;
		Integer RV24Pos = null;
		Integer RV26Pes = null;
		Integer RV22Dig1 = null;
		Integer RV23Dig2 = null;
		Integer RV25Num = null;
		String RV19CGCCPF = "";
		String cCgcCpf_Aux = "";
		String nPont1 = "";
		String nPont2 = "";
		String nTrac1 = "";
		String nBarr1 = "";

		if (lRequerido && Lib.Empty(cCgcCpf))
			return false;

		if (!Lib.Empty(Lib.Substr(cCgcCpf, 1, 2)) && !".".equals(Lib.AllTrim(Lib.Substr(cCgcCpf, 1, 2)))) {

			cCgcCpf_Aux = Lib.StrTran(cCgcCpf, ".", "");
			cCgcCpf_Aux = Lib.StrTran(cCgcCpf_Aux, "/", "");
			cCgcCpf_Aux = Lib.StrTran(cCgcCpf_Aux, "-", "");

			if (Lib.SLen(Lib.AllTrim(cCgcCpf_Aux)) == 0) {
				// Se for lRequerido = TRUE o campo é obrigatório ser informado
				if (lRequerido) {
					return false;
				}
				return true;
			}

			// Calculo do CGC
			if (lCgc) {

				// Para CGC padr�o do sistema
				if (Lib.Substr(cCgcCpf, 1, 18).equals("11.111.111/1111-11")) {
					return true;
				}

				nTot1 = 0;
				nTot2 = 0;
				cCgcCpf_Aux = Lib.Substr(cCgcCpf, 1, 2) + Lib.Substr(cCgcCpf, 4, 3) + Lib.Substr(cCgcCpf, 8, 3) + Lib.Substr(cCgcCpf, 12, 4);

				if (Lib.Val(cCgcCpf_Aux) == 0) {
					return false;
				}

				cCol1 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 1, 1)).intValue();
				cCol2 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 2, 1)).intValue();
				cCol3 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 3, 1)).intValue();
				cCol4 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 4, 1)).intValue();
				cCol5 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 5, 1)).intValue();
				cCol6 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 6, 1)).intValue();
				cCol7 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 7, 1)).intValue();
				cCol8 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 8, 1)).intValue();
				cCol9 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 9, 1)).intValue();
				cCol10 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 10, 1)).intValue();
				cCol11 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 11, 1)).intValue();
				cCol12 = Lib.Val(Lib.Substr(cCgcCpf_Aux, 12, 1)).intValue();

				nTot1 = nTot1 + (cCol1 * 5);
				nTot1 = nTot1 + (cCol2 * 4);
				nTot1 = nTot1 + (cCol3 * 3);
				nTot1 = nTot1 + (cCol4 * 2);
				nTot1 = nTot1 + (cCol5 * 9);
				nTot1 = nTot1 + (cCol6 * 8);
				nTot1 = nTot1 + (cCol7 * 7);
				nTot1 = nTot1 + (cCol8 * 6);
				nTot1 = nTot1 + (cCol9 * 5);
				nTot1 = nTot1 + (cCol10 * 4);
				nTot1 = nTot1 + (cCol11 * 3);
				nTot1 = nTot1 + (cCol12 * 2);

				nDiv1 = (nTot1 / 11);
				nMul1 = nTot1 - (nDiv1 * 11);

				if (nMul1 == 0) {
					nDig1 = 0;
				} else {
					nDig1 = 11 - nMul1;
				}

				if (nDig1 == 10) {
					nDig1 = 0;
				}

				cCol13 = nDig1;
				nTot2 = nTot2 + (cCol1 * 6);
				nTot2 = nTot2 + (cCol2 * 5);
				nTot2 = nTot2 + (cCol3 * 4);
				nTot2 = nTot2 + (cCol4 * 3);
				nTot2 = nTot2 + (cCol5 * 2);
				nTot2 = nTot2 + (cCol6 * 9);
				nTot2 = nTot2 + (cCol7 * 8);
				nTot2 = nTot2 + (cCol8 * 7);
				nTot2 = nTot2 + (cCol9 * 6);
				nTot2 = nTot2 + (cCol10 * 5);
				nTot2 = nTot2 + (cCol11 * 4);
				nTot2 = nTot2 + (cCol12 * 3);
				nTot2 = nTot2 + (cCol13 * 2);

				nDiv2 = (nTot2 / 11);
				nMul2 = nTot2 - (nDiv2 * 11);

				if (nMul2 == 0) {
					nDig2 = 0;
				} else {
					nDig2 = 11 - nMul2;
				}

				if (nDig2 == 10) {
					nDig2 = 0;
				}

				nPont1 = Lib.Substr(cCgcCpf, 3, 1);
				nPont2 = Lib.Substr(cCgcCpf, 7, 1);
				nBarr1 = Lib.Substr(cCgcCpf, 11, 1);
				nTrac1 = Lib.Substr(cCgcCpf, 16, 1);

				if (!nPont1.equals(".") || !nPont2.equals(".") || !nBarr1.equals("/") || !nTrac1.equals("-")) {
					return false;
				}

				if (Lib.Val(Lib.Substr(cCgcCpf, 17, 1)).intValue() == nDig1 && Lib.Val(Lib.Substr(cCgcCpf, 18, 1)).intValue() == nDig2) {
					return true;
				}

			} else {

				nTot1 = 0;
				nTot2 = 0;

				if (Lib.Val(cCgcCpf_Aux) == 0) {
					return false;
				}

				RV19CGCCPF = Lib.Space(03) + cCgcCpf_Aux;
				RV27Tot = 0;
				RV24Pos = 4;
				RV26Pes = 10;
				RV22Dig1 = 0;
				RV23Dig2 = 0;

				while (RV24Pos <= 12) {

					RV25Num = Lib.Val(Lib.Substr(RV19CGCCPF, RV24Pos, 1)).intValue() * RV26Pes;
					RV27Tot = RV27Tot + RV25Num;
					RV26Pes = RV26Pes - 1;
					RV24Pos = RV24Pos + 1;

					if (RV26Pes < 2) {
						RV26Pes = 9;
					}

				}

				RV28Res = RV27Tot - (RV27Tot / 11) * 11;

				if (RV28Res == 0 || RV28Res == 1) {
					RV22Dig1 = 0;
				} else {
					RV22Dig1 = (11 - RV28Res);
				}

				RV27Tot = 0;
				RV26Pes = 11;
				RV24Pos = 4;

				while (RV24Pos <= 13) {

					RV25Num = Lib.Val(Lib.Substr(RV19CGCCPF, RV24Pos, 1)).intValue() * RV26Pes;
					RV27Tot = RV27Tot + RV25Num;
					RV26Pes = RV26Pes - 1;
					RV24Pos = RV24Pos + 1;

					if (RV26Pes < 2) {
						RV26Pes = 9;
					}

				}

				RV28Res = RV27Tot - (RV27Tot / 11) * 11;

				if (RV28Res == 0 || RV28Res == 1) {
					RV23Dig2 = 0;
				} else {
					RV23Dig2 = (11 - RV28Res);
				}

				if (Lib.Val(Lib.Substr(cCgcCpf, 13, 1)).intValue() == RV22Dig1 && Lib.Val(Lib.Substr(cCgcCpf, 14, 1)).intValue() == RV23Dig2) {
					return true;
				}

			}

		} else {
			return true;
		}

		return false;
	}

	

	/**
	 * Valida CEP
	 * 
	 */
	public static boolean ValidaCEP(String c) {

		// Verifica se está vazio
		if (Lib.Empty(c)) {
			return true;
		}

		c = Lib.AllTrim(c);

		// Verifica se contém somente o hifen
		if (Lib.SLen(c) == 1 && Lib.Substr(c, 1, 1).equals("-")) {
			return true;
		}

		// Verifica se todos os caracteres digitados estão corretos
		for (int i = 0; i < c.length(); i++) {
			if (i == 6) {
				if (!"-".equals(Lib.Substr(c, i, 1))) {
					return false;
				}
			} else {

				try {
					Integer.parseInt(Lib.Substr(c, i, 1));
				} catch (NumberFormatException e) {
					return false;
				}

			}
		}

		// Verifica se está completamente preenchido
		if (Lib.SLen(c) == 9) {
			return true;
		}

		return false;

	}

	/**
	 * Valida Time
	 * 
	 */
	public static String ValidaTime(String cTime, Boolean lVazio) {

		Double uVal = null;

		// Horas
		uVal = Lib.Val(Lib.Left(cTime, 2));

		if (uVal < 0 || uVal > 24) {
			return "";
		}

		// Minutos
		uVal = Lib.Val(Lib.Substr(cTime, 4, 2));

		if (uVal < 0 || uVal > 59) {
			return "";
		}

		// Segundos
		uVal = Lib.Val(Lib.Substr(cTime, 7, 2));

		if (uVal < 0 || uVal > 59) {
			return "";
		}
		return cTime;
	}

	

	/**
	 * Conta a ocorrência de busca em texto
	 * 
	 */
	public static Long ocorrencias(String busca, String texto) {
		int pos = -1;
		long contagem = 0;
		while (true) {
			pos = texto.indexOf(busca, pos + 1);
			if (pos < 0) {
				break;
			}
			contagem++;
		}
		return contagem;
	}

	

}
