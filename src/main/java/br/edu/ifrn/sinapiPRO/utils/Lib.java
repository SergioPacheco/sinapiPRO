package br.edu.ifrn.sinapiPRO.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Lib {

	public static final String crlf = "\n";
	public static final String CRTL = Lib.CHR(13) + Lib.CHR(10);
	public static final Integer ASC_LF = 10;
	public static final Integer ASC_CR = 13;

	/**
	 * Retorna true se o parametro value for null ou vazio. Caso contrário retorna false.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean Empty(Object value) {

		if (value != null && !value.toString().trim().isEmpty()) {

			if (value instanceof Number) {

				if (((Number) value).doubleValue() != 0) {

					return false;

				} else {

					return true;

				}
			} else if (value instanceof List) {
				return ((List<?>) value).isEmpty();
			}

			return false;
		}

		return true;
	}

	/**
	 * Arredonda o valor limitando o numero de decimais conforme o informado
	 * 
	 * @param valor
	 * @param numero
	 * @return
	 */
	public static Double Round(Object objValor, Integer numero) {

		Double valor = 0.0;

		if (objValor instanceof Number) {
			valor = new Double(String.valueOf(objValor));
		}

		if (numero == null) {
			numero = 0;
		}

		BigDecimal bd = new BigDecimal(valor.toString());
		bd = bd.setScale(numero, BigDecimal.ROUND_HALF_UP);
		valor = bd.doubleValue();

		return valor;
	}

	/**
	 * Tira os espaços do campo
	 * 
	 * @param campo
	 * @return
	 */
	public static String Trim(Object campo) {

		if (campo == null) {
			return "";
		}

		return String.valueOf(campo).trim();
	}

	/**
	 * Retorna uma String com o Char correspondente numericamente.
	 * 
	 * @param numero
	 * @return
	 */
	public static String CHR(Integer numero) {

		if (numero == null) {
			return "";
		}

		char c = (char) numero.intValue();
		return String.valueOf(c);
	}

	/**
	 * retorna a o mes escrito por extenso, conforme a data informada.
	 * 
	 * @param data
	 * @return
	 */
	public static String MesExtenso(Date data) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		int mes = cal.get(Calendar.MONTH);
		mes++;

		return Lib.MesExtenso(mes);
	}

	/**
	 * Função utilizada nos relatórios
	 * */
	public static String MesExtenso(Integer mes) {

		String mesExtenso = "";

		switch (mes) {

		case 1:
			mesExtenso = "Janeiro";
			break;
		case 2:
			mesExtenso = "Fevereiro";
			break;
		case 3:
			mesExtenso = "Março";
			break;
		case 4:
			mesExtenso = "Abril";
			break;
		case 5:
			mesExtenso = "Maio";
			break;
		case 6:
			mesExtenso = "Junho";
			break;
		case 7:
			mesExtenso = "Julho";
			break;
		case 8:
			mesExtenso = "Agosto";
			break;
		case 9:
			mesExtenso = "Setembro";
			break;
		case 10:
			mesExtenso = "Outubro";
			break;
		case 11:
			mesExtenso = "Novembro";
			break;
		case 12:
			mesExtenso = "Dezembro";
			break;
		default:
			mesExtenso = "Mês não identificado";
			break;
		}

		return mesExtenso;
	}

	/**
	 * retorna uma String contendo o valor do objeto informado
	 * 
	 * @param objeto
	 * @return
	 * 
	 */
	public static String AsString(Object campo) {

		if (campo == null) {
			return null;
		}

		if (campo instanceof Date) {
			return Lib.DToC((Date) campo);
		}

		if (campo instanceof Double) {  
			return Lib.Str((Number) campo);
		}

		return String.valueOf(campo);
	}

	/**
	 * retorna o ano correspondente a data informada
	 * 
	 * @param data
	 * @return
	 */
	public static Integer Year(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * retorna uma data como String no formato "yyyymmdd"
	 * 
	 * @param data
	 * @return
	 */
	public static String DateAsString(Date data) {
		String str = Lib.StrZero(Lib.Year(data), 4) + Lib.StrZero(Lib.Month(data), 2) + Lib.StrZero(Lib.Day(data), 2);
		return str;
	}

	/**
	 * retorna a hora correspondente a data informada
	 * 
	 * @param data
	 * @return
	 */
	public static Integer Hour(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * retorna o minuto correspondente a data informada
	 * 
	 * @param data
	 * @return
	 */
	public static Integer Minute(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * retorna o segundo correspondente a data informada
	 * 
	 * @param data
	 * @return
	 */
	public static Integer Second(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.SECOND);
	}

	/**
	 * retorna o milisegundo correspondente a data informada
	 * 
	 * @param data
	 * @return
	 */
	public static Integer MilliSecond(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.MILLISECOND);
	}

	/**
	 * retorna uma data/hora como String no formato "yyyymmddHHMMSS"
	 * 
	 * @param data
	 * @return
	 */
	public static String DateTimeAsString(Date data) {
		String str = Lib.StrZero(Lib.Year(data), 4) + Lib.StrZero(Lib.Month(data), 2) + Lib.StrZero(Lib.Day(data), 2) + Lib.StrZero(Lib.Hour(data), 2) + Lib.StrZero(Lib.Minute(data), 2) + Lib.StrZero(Lib.Second(data), 2);
		return str;
	}

	/**
	 * Converte um numerico para uma String, definindo um tamanho total e decimais
	 * 
	 * @param valor
	 * @param length
	 * @param decimais
	 * @return
	 */
	public static String Str(Number valor, Integer length, Integer decimais) {

		if (valor == null) {
			return null;
		}

		StringBuilder retorno = new StringBuilder();
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		retorno.append(nf.format(valor));

		if (decimais != null) {

			StringBuilder cAux2 = new StringBuilder("0.");
			for (int i = 0; i < decimais; i++) {
				cAux2.append("0");
			}

			retorno = new StringBuilder(new DecimalFormat(cAux2.toString()).format(new BigDecimal(valor.toString()).setScale(decimais, BigDecimal.ROUND_HALF_UP)).replace(",", "."));

		} else if (new Double(0).equals(valor.doubleValue())) {
			retorno = new StringBuilder("0");
		} else {
			// retorno = Lib.Round(valor, 13).toString();
			retorno = new StringBuilder(new DecimalFormat("0.0000000000000").format(new BigDecimal(valor.toString()).setScale(13, BigDecimal.ROUND_HALF_UP)).replace(",", "."));

			String aRetorno[] = retorno.toString().split("\\.");
			retorno = new StringBuilder(aRetorno[0]);
			if (aRetorno.length > 1 && new BigInteger(aRetorno[1]).longValue() > 0L) {

				if ("0".equals(aRetorno[1].substring(0, 1))) {
					retorno.append(".");

					for (int i = 0; i < aRetorno[1].length(); i++) {
						if (aRetorno[1].charAt(i) == '0') {
							retorno.append("0");
						} else {
							break;
						}
					}

					retorno.append(new BigInteger(aRetorno[1]).toString());

				} else {
					retorno.append("." + new BigInteger(aRetorno[1]).toString());
				}

			}
		}

		if (length != null) {
			int tamanho = retorno.length();

			if (tamanho < length) {
				StringBuilder sRetorno = new StringBuilder();
				sRetorno.append(retorno);

				for (int i = tamanho; i < length; i++) {
					sRetorno.insert(0, " ");
				}

				if (sRetorno.charAt(sRetorno.length() - 1) == '.') {
					sRetorno.setCharAt(sRetorno.length() - 1, ' ');
				}

				return Lib.RTrim(sRetorno);
			}

			retorno = new StringBuilder(retorno.substring(0, length));

			if (retorno.charAt(retorno.length() - 1) == '.' || retorno.charAt(retorno.length() - 1) == ',') {
				retorno = new StringBuilder(retorno.substring(0, retorno.length() - 1));
			}
		}

		return retorno.toString();
	}

	/**
	 * Converte um numerico para uma String, definindo um tamanho total
	 * 
	 * @param valor
	 * @param inteiros
	 * @return
	 */
	public static String Str(Number valor, Integer length) {
		return Str(valor, length, null);
	}

	/**
	 * Converte um numerico para uma String
	 * 
	 * @param valor
	 * @return
	 */
	public static String Str(Number valor) {
		return Str(valor, null, null);
	}

	/**
	 * retorna a posicao da primeira ocorrencia encontrada no campo informado, conforme o parametro de busca
	 * 
	 * @param busca
	 * @param campo
	 * @return
	 */
	public static Integer At(String busca, String campo) {

		if (busca == null || campo == null || busca.isEmpty() || campo.isEmpty()) {
			return 0;
		}

		return campo.indexOf(busca) + 1;
	}

	/**
	 * retorna a posicao da primeira ocorrencia encontrada no campo informado, conforme o parametro de busca
	 * 
	 * @param busca
	 * @param campo
	 * @return
	 */
	public static Integer At2(String busca, String campo) {
		return campo.indexOf(busca) + 1;
	}

	/**
	 * retorna a posicao da primeira ocorrencia encontrada no campo informado, conforme o parametro de busca e posicao inicial
	 * 
	 * @param busca
	 * @param campo
	 * @param posicaoInicial
	 * @return
	 */
	public static Integer At3(String busca, String campo, Integer posicaoInicial) {
		return campo.indexOf(busca, posicaoInicial + 1) + 1;
	}

	/**
	 * retorna a posicao da primeira ocorrencia encontrada no campo informado, conforme o parametro de busca
	 * 
	 * @param campo
	 * @param busca
	 * @return
	 */
	public static Integer Search(String campo, String busca) {
		return At2(busca, campo);
	}

	/**
	 * retorna o mes de acordo com a data informada.
	 * 
	 * @param data
	 * @return
	 */
	public static Integer Month(Date data) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * retira todos os espaços em branco do inicio e fim da String
	 * 
	 * @param campo
	 * @return
	 */
	public static String AllTrim(Object campo) {
		return campo == null ? "" : String.valueOf(campo).trim();
	}

	/**
	 * retira os espaços em branco à direita da String
	 * 
	 * @param campo
	 * @return
	 */
	public static String NTrim(Object campo) {

		if (campo == null)
			return null;

		return String.valueOf(campo).replaceAll("\\s+$", "");
	}

	/**
	 * retira os espaços em branco à direita da String
	 * 
	 * @param campo
	 * @return
	 */
	public static String RTrim(Object campo) {

		if (campo == null) {
			return null;
		}

		return String.valueOf(campo).replaceAll("\\s+$", "");
	}

	/**
	 * retira os espaços em branco à esquerda da String
	 * 
	 * @param campo
	 * @return
	 */
	public static String LTrim(Object campo) {

		if (campo == null) {
			return null;
		}

		return String.valueOf(campo).replaceAll("^\\s+", "");
	}

	
	/**
	 * formata o campo e preenche com zeros a esquerda para atingir o tamanho
	 * 
	 * @param campo
	 * @param digitos
	 * @return
	 */
	public static String StrZero(Object campo, Integer length, Integer decimais) {

		if (!(campo instanceof Number)) {
			return null;
		}

		String retorno = "";
		NumberFormat nf = NumberFormat.getInstance(Locale.US);

		if (decimais != null && decimais.intValue() != 0) {

			if (campo instanceof Double) {
				campo = new BigDecimal(campo.toString()).setScale(decimais, BigDecimal.ROUND_HALF_UP);
			}
			length = length - decimais - 1;
			nf.setMaximumIntegerDigits(length); // -1 para desconsiderar o separador decimal
			nf.setMinimumIntegerDigits(length);
			nf.setMaximumFractionDigits(decimais);
			nf.setMinimumFractionDigits(decimais);
			retorno = nf.format(campo);
		} else {
			nf.setMaximumIntegerDigits(length);
			nf.setMinimumIntegerDigits(length);
			retorno = nf.format(campo);
		}

		if (retorno.charAt(retorno.length() - 1) == '.' || retorno.charAt(retorno.length() - 1) == ',') {
			retorno = retorno.substring(0, retorno.length() - 1);
		}

		retorno = retorno.replaceAll(",", "");

		return retorno;
	}

	/**
	 * formata o campo e preenche com zeros a esquerda para atingir o tamanho
	 * 
	 * @param campo
	 * @param length
	 * @return
	 */
	public static String StrZero(Object campo, Integer length) {

		if (!(campo instanceof Number)) {
			return null;
		}

		if (length == null || length == 0) {
			return String.valueOf(campo).replaceAll(",", "");
		}

		String retorno = String.valueOf(campo);

		length--;

		if (retorno.replaceAll("\\.", "").length() > length + 1) {
			return retorno.substring(0, length + 1);
		}

		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMaximumIntegerDigits(length + 1);
		nf.setMinimumIntegerDigits(length + 1);
		retorno = nf.format(campo);

		return retorno.replaceAll(",", "");

	}

	/**
	 * retorna uma subString do campo
	 * 
	 * @param cTarget
	 * @param nStart
	 * @param nCount
	 * @return
	 */
	public static String Substr(Object cTarget, Integer nStart, Integer nCount) {

		try {

			if (cTarget == null || nStart == null) {
				return "";
			}

			if (nStart > 0) {
				nStart--;
			}

			if (nStart < 0) {  
				nStart = String.valueOf(cTarget).length() + nStart; 
			}

			if (nCount == null || nCount <= 0) {
				return String.valueOf(cTarget).substring(nStart);
			}

			if (String.valueOf(cTarget).length() < nStart + nCount) {
				return String.valueOf(cTarget).substring(nStart);
				// nCount = String.valueOf(cTarget).length();
			}

			return String.valueOf(cTarget).substring(nStart, nStart + nCount);

		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
	}

	/**
	 * retorna uma subString do campo
	 * 
	 * @param cTarget
	 * @param nStart
	 * @return
	 */
	public static String Substr(Object cTarget, Integer nStart) {

		return Substr(cTarget, nStart, null);
	}

	/**
	 * retorna uma subString do campo
	 * 
	 * @param cTarget
	 * @param nStart
	 * @return
	 */
	public static String Substr2(Object cTarget, Integer nStart) {

		return Substr(cTarget, nStart, null);
	}

	/**
	 * retorna uma subString do campo
	 * 
	 * @param cTarget
	 * @param nStart
	 * @param nCount
	 * @return
	 */
	public static String Substr3(Object cTarget, Integer nStart, Integer nCount) {

		return Substr(cTarget, nStart, nCount);
	}

	/**
	 * retorna um Date conforme o campo informado
	 * 
	 * @param campo
	 * @return
	 */
	public static Date CToD(Object campo) {
		try {
			if (String.valueOf(campo).isEmpty()) {
				return null;
			} else {
				// return new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(campo));

				String[] data = String.valueOf(campo).split("/");
				if (data[2].length() == 2) {
					data[2] = "20" + data[2];
				}

				Calendar cal = Calendar.getInstance();
				cal.set(new Integer(data[2]), new Integer(data[1]) - 1, new Integer(data[0]));
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.getTime();
				cal.setLenient(false);
				return cal.getTime();
			}

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Retorna a data formatada para o banco de dados
	 * 
	 * @param data
	 * @return
	 */
	public static String SetaData(Date dData) {

		String cData = Lib.DToS(dData);
		if (Lib.Empty(cData)) {
			cData = "NULL";
		} else {
			cData = "'" + Lib.Substr(cData, 1, 4) + "-" + Lib.Substr(cData, 5, 2) + "-" + Lib.Substr(cData, 7, 2) + " 00:00:00'";
		}

		return cData;
	}

	public static String SetaDataHora(Date dData) {

		String cData = Lib.DToS(dData);
		if (Lib.Empty(cData)) {
			cData = "NULL";
		} else {
			cData = "'" + Lib.Substr(cData, 1, 4) + "-" + Lib.Substr(cData, 5, 2) + "-" + Lib.Substr(cData, 7, 2) + " " + Lib.HToC(dData) + "'";
		}

		return cData;

	}

	/**
	 * Metodo que executa a substituição de valores no campo informado.
	 * 
	 * @param cTarget
	 *            :String campo
	 * @param cSearch
	 *            : String para procurar
	 * @param cReplace
	 *            : String para substituir
	 * @param nStart
	 *            : posição de inicio
	 * @param nCount
	 *            : numero de ocorencias para substituir
	 * @return
	 */
	public static String StrTran(Object cTarget, String cSearch, String cReplace, Integer nStart, Integer nCount) {
		if (!(cTarget instanceof String)) {
			return "";
		}

		if (nCount == null) {
			return cTarget.toString().replace(cSearch, cReplace);
		}

		if (nStart == null || nStart == 0) {
			nStart = 1;
		}

		nStart--;
		String campo = String.valueOf(cTarget);
		String aux1 = campo.substring(0, nStart);
		String aux2 = campo.substring(nStart, campo.length());

		for (int i = 0; i < nCount; i++) {
			aux2 = aux2.replaceFirst(cSearch, cReplace);
		}

		return aux1 + aux2;
	}

	/**
	 * Metodo que executa a substituição de valores no campo informado.
	 * 
	 * @param cTarget
	 *            :String campo
	 * @param cSearch
	 *            : String para procurar
	 * @param cReplace
	 *            : String para substituir
	 * @return
	 */
	public static String StrTran(Object cTarget, String cSearch, String cReplace) {
		return StrTran(cTarget, cSearch, cReplace, null, null);
	}

	/**
	 * Retorna a data e hora atual
	 * 
	 * @return
	 */
	public static Date Today() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * Retorna a hora atual
	 * 
	 * @return
	 */
	public static Date Now() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, 0);
		return cal.getTime();
	}

	/**
	 * Retorna o dia do mes de acordo com a data informada
	 * 
	 * @param data
	 * @return
	 */
	public static Integer Day(Date data) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static Date AddMes(Date data, Integer nIntervalo) {
		return Lib.AddMes(data, nIntervalo < 0 ? "-" : "+", nIntervalo);
	}

	/**
	 * Adiciona ou retira um mês da data passada como parametro.
	 * 
	 * @param data
	 * @param cOper
	 * @return
	 */
	public static Date AddMes(Date data, String cOper) {

		Date dData = null;

		if (cOper.equals("+")) { // Acrescenta 1 mês
			dData = CToD(StrZero(1, 2) + "/" + StrZero(Month(data) == 12 ? 1 : Month(data) + 1, 2) + "/" + StrZero(Month(data) == 12 ? Year(data) + 1 : Year(data), 4));
		} else { // Descresce 1 mês
			dData = CToD(StrZero(1, 2) + "/" + StrZero(Month(data) == 1 ? 12 : Month(data) - 1, 2) + "/" + StrZero(Month(data) == 1 ? Year(data) - 1 : Year(data), 4));
		}

		if (Day(data) > diasmes(Month(dData), Year(dData))) {
			dData = CToD(StrZero(diasmes(Month(dData), Year(dData)), 2) + "/" + StrZero(Month(dData), 2) + "/" + StrZero(Year(dData), 4));
		} else {
			dData = CToD(StrZero(Day(data), 2) + "/" + StrZero(Month(dData), 2) + "/" + StrZero(Year(dData), 4));
		}

		return dData;
	}

	/**
	 * Adiciona o mes conforme a solicitação e numero de meses
	 * 
	 * @param data
	 * @param cOper
	 * @param nIntervalo
	 * @return
	 */
	public static Date AddMes(Date data, String cOper, Integer nIntervalo) {

		Date dData = null;
		Integer nQtdMes = null;
		Integer nDia = null;
		Integer nMes = null;
		Integer nAno = null;
		Integer nNumDat = null;

		if (nIntervalo == null) {
			nQtdMes = 1;
		} else {
			nQtdMes = nIntervalo;
		}

		nDia = Lib.Day(data);
		nAno = Lib.Year(data);

		if (cOper.equals("+")) {

			nMes = Month(data) + nQtdMes;

			if (nMes > 12) {

				nAno += nMes / 12;
				nMes = Mod(nMes, 12);

				if (nMes == 0) {
					nAno = nAno - 1;
					nMes = 12;
				}
			}

		} else {

			nNumDat = (12 * nAno) + Month(data) - nQtdMes;

			if (Mod(nNumDat, 12) == 0) {
				nMes = 12;
				nAno = (nNumDat / 12) - 1;
			} else {
				nMes = Mod(nNumDat, 12);
				nAno = nNumDat / 12;
			}

		}

		if (diasmes(nMes, nAno) < nDia) {
			nDia = diasmes(nMes, nAno);
		}

		dData = Lib.CToD(Lib.StrZero(nDia, 2) + "/" + Lib.StrZero(nMes, 2) + "/" + Lib.Str(nAno, 4));

		return dData;
	}

	public static Date AddAno(Date data, Integer nIntervalo) {
		return Lib.AddAno(data, nIntervalo < 0 ? "-" : "+", nIntervalo);
	}

	/**
	 * Adiciona um ano a data
	 * 
	 * @param Data
	 * @param cOper
	 * @param nPeriodo
	 * @return
	 */
	public static Date AddAno(Date Data, String cOper, Integer nPeriodo) {

		Date dData = null;
		Integer nDia = null;

		// Caso seja indicado para fazer o incremento menor de 12 meses
		if (nPeriodo != null && nPeriodo != 12) {
			dData = Lib.AddMes(Data, "+", nPeriodo);
			return dData;
		}

		if (Lib.Day(Data) > Lib.diasmes(Lib.Month(Data), Lib.Year(Data) + 1) || Lib.Day(Data) > Lib.diasmes(Lib.Month(Data), Lib.Year(Data) - 1)) {
			nDia = Lib.diasmes(Lib.Month(Data), Lib.Year(Data) + 1);
		} else {
			nDia = Lib.Day(Data);
		}

		if (cOper.equals("+")) { // Acrescenta 1 ano
			dData = Lib.CToD(Lib.StrZero(nDia, 2) + "/" + Lib.StrZero(Lib.Month(Data), 2) + "/" + Lib.StrZero(Lib.Year(Data) + 1, 4));
		} else { // Descresce 1 ano
			dData = Lib.CToD(Lib.StrZero(nDia, 2) + "/" + Lib.StrZero(Lib.Month(Data), 2) + "/" + Lib.StrZero(Lib.Year(Data) - 1, 4));
		}

		return dData;
	}

	/**
	 * Retorna o resto da divisão dos valores informados
	 * 
	 * @param nDividend
	 * @param nDivisor
	 * @return
	 */
	public static Integer Mod(Integer nDividend, Integer nDivisor) {

		return nDividend % nDivisor;
	}

	/**
	 * Determina o numero de dias do mes
	 * 
	 * @param mes
	 * @param ano
	 * @return
	 */
	public static Integer diasmes(Integer mes, Integer ano) {

		Calendar cal = Calendar.getInstance();
		cal.set(ano, mes - 1, 1, 0, 0, 0);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	/**
	 * Determina o numero de dias do mes
	 * 
	 * @param data
	 * @return
	 */
	public static Integer diasmes(Date data) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		Integer mes = cal.get(Calendar.MONTH) + 1;
		Integer ano = cal.get(Calendar.YEAR);

		return diasmes(mes, ano);
	}

	/**
	 * retorna a quantidade de registro da collection
	 * 
	 * @param lista
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Integer ALen(Object lista) {

		if (lista == null) {
			return 0;
		}

		if (lista instanceof Map) {

			return ((Map) lista).size();

		} else if (lista instanceof List) {

			return ((List) lista).size();

		} else if (lista instanceof Object[]) {

			return ((Object[]) lista).length;

		}
		return null;
	}

	/**
	 * retorna o tamanho da String
	 * 
	 * @param campo
	 * @return
	 */
	public static Integer SLen(String campo) {
		if (campo == null) {
			return 0;
		}
		return campo.length();
	}

	/**
	 * retorna um valor correspondente a String informada
	 * 
	 * @param campo
	 * @return
	 */
	public static Double Val(String campo) {

		if (campo == null || campo.trim().isEmpty()) {
			return 0d;
		}

		try {
			return Double.parseDouble(campo.replace(",", "."));
		} catch (NumberFormatException e) {
			return 0d;
		}

	}

	/**
	 * retorna o valor do campo em letras maiusculas
	 * 
	 * @param campo
	 * @return
	 */
	public static String Upper(String campo) {

		if (campo != null) {
			return campo.toUpperCase().trim();
		} else {
			return "";
		}

	}

	public static String UpperCase(String conteudo) {
		if (conteudo != null) {
			return conteudo.toUpperCase();
		} else {
			return "";
		}
	}

	/**
	 * retorna o valor absoluto do valor informado
	 * 
	 * @param valor
	 * @return
	 */
	public static Double Abs(Double valor) {

		return Math.abs(valor);
	}

	/**
	 * retorna o valor absoluto do valor informado
	 * 
	 * @param valor
	 * @return
	 */
	public static Long Abs(Long valor) {

		return Math.abs(valor);
	}

	/**
	 * retorna a fração do valor informado
	 * 
	 * @param valor
	 * @return
	 */
	public static Integer Frac(Double valor) {

		if (valor == null || valor.equals(0d)) {
			return 0;
		} else {
			return Integer.parseInt(String.valueOf(valor).substring(String.valueOf(valor).indexOf(".") + 1));
		}
	}

	/**
	 * retorna a quantidade de casas decimais do valor informado.
	 * 
	 * @param valor
	 * @return
	 */
	public static Integer qtdDecimais(Double valor) {

		if (valor == null || valor.equals(0d)) {
			return 2;
		} else {
			return String.valueOf(valor).substring(String.valueOf(valor).indexOf(".") + 1).length();
		}
	}

	/**
	 * retorna o tamanho do array ou string informados
	 * 
	 * @param valor
	 * @return
	 */
	public static Integer Len(Object valor) {

		if (valor == null) {
			return 0;
		} else if (valor instanceof String) {
			return String.valueOf(valor).length();
		} else if (valor instanceof List<?>) {
			return ((List<?>) valor).size();
		}
		return 0;
	}

	/**
	 * Escreve o valor informado por extenso
	 * 
	 * @param nNumero
	 * @param cIndice
	 * @return
	 */
	public static String Extenso(Double nNumero, String cIndice) {

		HashMap<Integer, String> aUnidade = null;
		HashMap<Integer, String> aDezenas = null;
		HashMap<Integer, String> aCentenas = null;
		HashMap<Integer, String> aEspeciais = null;
		String cCasas = "";
		String cValor = "";
		String cValorPar = "";
		String cInteiroPar = "";
		String cInteiro = "";
		String cFloat = "";
		Boolean lInteiroSing = false;
		Boolean lFloatSing = false;

		Boolean lZeroE = false;

		nNumero = Abs(Lib.Round(nNumero, 2));

		// Divide os inteiros dos decimais e converte para string
		cInteiro = Lib.AllTrim(Lib.Str(nNumero.intValue()));
		cFloat = Lib.Str(Frac(nNumero));

		if (cIndice.equals("E")) {

			if (At(",", cFloat) != 0) {

				if (Substr(cFloat, At(",", cFloat) + 1, 1).equals("0")) {
					lZeroE = true;
				}
			}
		}

		cFloat = Lib.StrTran(cFloat, ",", "");
		cFloat = Lib.AllTrim(Lib.Str(Val(cFloat)));

		// Carrega as arrays de constantes
		aUnidade = new HashMap<Integer, String>();
		aUnidade.put(1, "um");
		aUnidade.put(2, "dois");
		aUnidade.put(3, "três");
		aUnidade.put(4, "quatro");
		aUnidade.put(5, "cinco");
		aUnidade.put(6, "seis");
		aUnidade.put(7, "sete");
		aUnidade.put(8, "oito");
		aUnidade.put(9, "nove");

		aEspeciais = new HashMap<Integer, String>();
		aEspeciais.put(10, "dez");
		aEspeciais.put(11, "onze");
		aEspeciais.put(12, "doze");
		aEspeciais.put(13, "treze");
		aEspeciais.put(14, "quatorze");
		aEspeciais.put(15, "quinze");
		aEspeciais.put(16, "dezesseis");
		aEspeciais.put(17, "dezessete");
		aEspeciais.put(18, "dezoito");
		aEspeciais.put(19, "dezenove");

		aDezenas = new HashMap<Integer, String>();
		aDezenas.put(2, "vinte");
		aDezenas.put(3, "trinta");
		aDezenas.put(4, "quarenta");
		aDezenas.put(5, "cinquenta");
		aDezenas.put(6, "sessenta");
		aDezenas.put(7, "setenta");
		aDezenas.put(8, "oitenta");
		aDezenas.put(9, "noventa");

		aCentenas = new HashMap<Integer, String>();
		aCentenas.put(1, "cento");
		aCentenas.put(2, "duzentos");
		aCentenas.put(3, "trezentos");
		aCentenas.put(4, "quatrocentos");
		aCentenas.put(5, "quinhentos");
		aCentenas.put(6, "seiscentos");
		aCentenas.put(7, "setecentos");
		aCentenas.put(8, "oitocentos");
		aCentenas.put(9, "novecentos");

		cInteiroPar = "";
		cCasas = "";
		cValor = "";

		// Divide o numero inteiro de 3 em 3(da esquerda para direita) e
		// proocessa por bloco
		for (int i = Len(cInteiro); i > 0; i--) {

			cInteiroPar = Lib.Substr(cInteiro, i, 1) + cInteiroPar;

			if (Len(cInteiroPar) == 3) {
				cValorPar = "";
				cInteiroPar = Lib.AllTrim(Lib.Str(Val(cInteiroPar)));

				if (Len(cInteiroPar) == 3) {
					if (Val(cInteiroPar) == 100) {
						if (Len(cInteiro) > 3 && cCasas.isEmpty()) {
							cValorPar = "e cem";
						} else {
							cValorPar = "cem";
						}
					} else {

						cValorPar = aCentenas.get(Val(Lib.Substr(cInteiroPar, 1, 1)).intValue());

						if (Val(Lib.Substr(cInteiroPar, 2, 2)) >= 10 && Val(Lib.Substr(cInteiroPar, 2, 2)) <= 19) {
							cValorPar += " e " + aEspeciais.get(Val(Lib.Substr(cInteiroPar, 2, 2)).intValue());
						} else {
							if (Val(Lib.Substr(cInteiroPar, 2, 1)) != 0) {
								cValorPar += " e " + aDezenas.get(Val(Lib.Substr(cInteiroPar, 2, 1)).intValue());
							}

							if (Val(Lib.Substr(cInteiroPar, 3, 1)) != 0) {
								cValorPar += " e " + aUnidade.get(Val(Lib.Substr(cInteiroPar, 3, 1)).intValue());
							}
						}
					}

				} else if (Len(cInteiroPar) == 2) {
					if (Val(cInteiroPar) >= 10 && Val(cInteiroPar) <= 19) {
						cValorPar += " e " + aEspeciais.get(Val(Lib.Substr(cInteiroPar, 1, 2)).intValue());
					} else {
						cValorPar += " e " + aDezenas.get(Val(Lib.Substr(cInteiroPar, 1, 1)).intValue());
						if (Val(Lib.Substr(cInteiroPar, 2, 1)) != 0) {
							cValorPar += " e " + aUnidade.get(Val(Lib.Substr(cInteiroPar, 2, 1)).intValue());
						}
					}

				} else if (Len(cInteiroPar) == 1) {
					if (Val(cInteiroPar) != 0) {
						cValorPar += " e " + aUnidade.get(Val(Lib.Substr(cInteiroPar, 1, 1)).intValue());
					}
				}

				if (Val(cInteiroPar) != 0) {
					cValor = cValorPar + cCasas + cValor;
				} else {
					cValor = cValorPar + cValor;
				}

				cCasas = " " + Lib.AllTrim(Lib.Str(Val(cCasas) + 1)) + " ";
				cInteiroPar = "";
			}

		}

		// Se sobrou número para processar
		if (!cInteiroPar.isEmpty()) {

			cValorPar = "";
			cInteiroPar = Lib.AllTrim(Lib.Str(Val(cInteiroPar)));

			if (Len(cInteiroPar) == 2) {
				if (Val(cInteiroPar) >= 10 && Val(cInteiroPar) <= 19) {
					cValorPar += aEspeciais.get(Val(Lib.Substr(cInteiroPar, 1, 2)).intValue());
				} else {
					cValorPar += aDezenas.get(Val(Lib.Substr(cInteiroPar, 1, 1)).intValue());

					if (Val(Lib.Substr(cInteiroPar, 2, 1)) != 0) {
						cValorPar += " e " + aUnidade.get(Val(Lib.Substr(cInteiroPar, 2, 1)).intValue());
					}
				}

			} else if (Len(cInteiroPar) == 1) {
				if (Val(cInteiroPar) != 0) {

					cValorPar += aUnidade.get(Val(Lib.Substr(cInteiroPar, 1, 1)).intValue());

					if (Len(cInteiro) < 3 && Val(cInteiroPar) == 1) {
						lInteiroSing = true;
					}
				}
			}

			cInteiroPar = "";
			cValor = cValorPar + cCasas + cValor;
		}

		// Troca nos números descritos em cValor conforme sua posição na milhar
		cValor = Lib.StrTran(cValor, "1", "mil");
		cValor = Lib.StrTran(cValor, "2", "milhão");
		cValor = Lib.StrTran(cValor, "3", "bilhão");
		cValor = Lib.StrTran(cValor, "4", "trilhão");

		if (Upper(cIndice).equals("C") && Val(cInteiro) != 0) {
			if (lInteiroSing) {
				cValor += " real";
			} else {
				cValor += " reais";
			}

		} else if (Upper(cIndice).equals("O") && Val(cInteiro) != 0) {
			if (lInteiroSing) {
				cValor += " inteiro";
			} else {
				cValor += " inteiros";
			}
		}

		// Trata os decimais
		if (Val(cFloat) != 0) {

			if (cIndice.equals("E")) {

				cValorPar = (Val(cInteiro) != 0 ? " virgula " : "");

				if (lZeroE) {
					cValorPar += "zero ";
				}

				if (SLen(cFloat) == 2 && Lib.Substr(cFloat, 2, 1).equals("0")) {
					cFloat = Lib.Substr(cFloat, 1, 1);
				}

			} else {
				cValorPar = (Val(cInteiro) != 0 ? " e " : "");
			}

			if (SLen(cFloat) == 2) {
				if (Val(cFloat) >= 10 && Val(cFloat) <= 19) {
					cValorPar += aEspeciais.get(Val(Lib.Substr(cFloat, 1, 2)).intValue());

				} else {
					cValorPar += aDezenas.get(Val(Lib.Substr(cFloat, 1, 1)).intValue());

					if (Val(Lib.Substr(cFloat, 2, 1)) != 0) {
						cValorPar += " e " + aUnidade.get(Val(Lib.Substr(cFloat, 2, 1)).intValue());
					}
				}

			} else if (SLen(cFloat) == 1) {
				if (Val(cFloat) != 0) {

					cValorPar += aUnidade.get(Val(Lib.Substr(cFloat, 1, 1)).intValue());

					if (Val(cFloat) == 1) {
						lFloatSing = true;
					}
				}
			}

			if (Upper(cIndice).equals("C")) {
				if (lFloatSing) {
					cValorPar += " centavo";
				} else {
					cValorPar += " centavos";
				}

			} else if (Upper(cIndice).equals("O")) {
				if (lFloatSing) {
					cValorPar += " milesimo";
				} else {
					cValorPar += " milesimos";
				}
			}
			cValor += cValorPar;
		}

		return cValor;
	}

	/**
	 * Retorna um substring limitado pela posicão
	 * 
	 * @param campo
	 * @param posicao
	 * @return
	 */
	public static String Left(String campo, Integer posicao) {

		if (campo == null || campo.isEmpty()) {
			return "";
		}

		if (posicao <= campo.length()) {
			return campo.substring(0, posicao);
		} else {
			return campo.substring(0, campo.length());
		}

	}

	/**
	 * Formata o campo de acordo com a mascara informada
	 * 
	 * @param campo
	 * @param mascara
	 * @return
	 */
	public static String Transform(Object campo, String mascara) {

		if (mascara == null || campo == null) {
			return "";
		}

		if (campo instanceof String) {
			campo = Double.valueOf((String) campo);
		}

		String valor = null;

		if (campo instanceof Double) {
			valor = new BigDecimal(((Double) campo).doubleValue()).toString();
		} else {
			valor = String.valueOf(campo);
		}

		if (mascara.charAt(0) == '@') {

			if (mascara.charAt(1) == '!') {
				return valor.toUpperCase();

			} else if (mascara.charAt(1) == 'E') {

				mascara = mascara.substring(2).trim();

				String valorCampo = null;

				if (campo instanceof Double) {
					valorCampo = new BigDecimal(((Double) campo).doubleValue()).toString();
				} else {
					valorCampo = String.valueOf(campo);
				}

				int inteiroCampo = 0;
				int decimal = 0;

				int inteiroMascara = mascara.substring(0, mascara.indexOf(".")).replaceAll(",", "").length();

				if (valorCampo.contains(".")) {
					inteiroCampo = valorCampo.substring(0, valorCampo.indexOf(".")).replaceAll("-", "").length();
					decimal = mascara.substring(mascara.indexOf(".") + 1).length();
				} else {
					inteiroCampo = valorCampo.replaceAll("-", "").length();
				}

				if (decimal == 0 && campo instanceof Double) {
					decimal = mascara.substring(mascara.indexOf(".") + 1).length();
				}

				NumberFormat df = NumberFormat.getInstance();
				df.setMaximumFractionDigits(decimal);
				df.setMinimumFractionDigits(decimal);
				df.setMaximumIntegerDigits(inteiroMascara);
				df.setMinimumIntegerDigits(inteiroCampo);
				return df.format(campo);

			} else if (mascara.charAt(1) == 'D') {

				return SetaData(Today());

			} else if (mascara.charAt(1) == 'S') {

				mascara = mascara.substring(2).trim();

				String strCampo = null;

				if (campo instanceof Double) {
					strCampo = new BigDecimal(((Double) campo).doubleValue()).toString();
				} else {
					strCampo = String.valueOf(campo);
				}

				StringBuilder tamanho = new StringBuilder();

				char[] arr = mascara.trim().toCharArray();

				for (char c : arr) {

					if (!Character.isDigit(c)) {
						break;
					}

					tamanho.append(c);
				}

				strCampo = strCampo.substring(0, Integer.valueOf(tamanho.toString()));

				if (mascara.contains("!")) {
					strCampo = strCampo.toUpperCase();
				}

				return strCampo;
			}

		} else {

			mascara = mascara.trim();
			String valorCampo = null;

			if (campo instanceof Double) {
				valorCampo = new BigDecimal(((Double) campo).doubleValue()).toString();
			} else {
				valorCampo = String.valueOf(campo);
			}

			int inteiroMascara = mascara.length();
			int decimal = 0;
			if (mascara.contains(".")) {
				inteiroMascara = mascara.substring(0, mascara.indexOf(".")).replaceAll(",", "").length();
				decimal = mascara.substring(mascara.indexOf(".") + 1).length();
			}
			int inteiroCampo = valorCampo.length();
			if (valorCampo.contains(".")) {
				inteiroCampo = valorCampo.substring(0, valorCampo.indexOf(".")).length();
			}

			NumberFormat df = NumberFormat.getInstance();
			df.setMaximumFractionDigits(decimal);
			df.setMinimumFractionDigits(decimal);
			df.setMaximumIntegerDigits(inteiroMascara);
			df.setMinimumIntegerDigits(inteiroCampo);

			return df.format(campo);

		}

		return "";
	}

	/**
	 * retorna uma substring do final da string, conforme a posicao
	 */
	public static String Right(String campo, Integer posicao) {

		if (campo == null || campo.isEmpty()) {
			return "";
		}

		if (posicao <= campo.length()) {
			return campo.substring(campo.length() - posicao);
		} else {
			return "";
		}

	}

	/**
	 * Retorna uma string em letras minusculas
	 * 
	 * @param campo
	 * @return
	 */
	public static String Lower(String campo) {

		if (campo != null) {
			return campo.toLowerCase();
		} else {
			return "";
		}
	}

	/**
	 * Verifica se o valor esta entre os campos informados
	 * 
	 * @param campo
	 * @param valor1
	 * @param valor2
	 * @return
	 */
	public static boolean Between(Object campo, Object valor1, Object valor2) {

		if (campo instanceof Date) {

			if ((((Date) campo).after((Date) valor1) || ((Date) campo).getTime() == ((Date) valor1).getTime()) && (((Date) campo).before((Date) valor2) || ((Date) campo).getTime() == ((Date) valor2).getTime())) {
				return true;
			}

			return false;
		} else if (campo instanceof String) {

			if (((String) campo).compareToIgnoreCase((String) valor1) >= 0 && ((String) campo).compareToIgnoreCase((String) valor2) <= 0) {
				return true;
			}

			return false;
		} else if (campo instanceof Integer) {

			if ((Integer) campo >= (Integer) valor1 && (Integer) campo <= (Integer) valor2) {
				return true;
			}

			return false;
		} else if (campo instanceof Long) {

			if ((Long) campo >= (Long) valor1 && (Long) campo <= (Long) valor2) {
				return true;
			}

		} else if (campo instanceof Double) {

			if ((Double) campo >= (Double) valor1 && (Double) campo <= (Double) valor2) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Retorna a quantidade de espaços.
	 * 
	 * @param quantidade
	 * @return
	 */
	public static String Space(int quantidade) {
		StringBuilder space = new StringBuilder();

		for (int i = 0; i < quantidade; i++) {
			space.append(" ");
		}

		return space.toString();
	}

	/**
	 * Retorna a Data formatada para texto
	 * 
	 * @param data
	 * @return
	 */
	public static String DToC(Date data, String formato) {

		if (data == null) {
			return "";
		}

		if (formato == null) {
			formato = "dd/MM/yyyy";
		}

		SimpleDateFormat formatterToView = new SimpleDateFormat(formato);

		return formatterToView.format(data);
	}

	public static String DToC(Date data) {
		return DToC(data, "dd/MM/yyyy");
	}

	/**
	 * Retorna a Hora formatada para texto no formato HORA:MINUTOS:SEGUNDOS
	 * 
	 * @param data
	 * @return
	 */
	public static String HToC(Date data) {

		if (data == null) {
			return "";
		}

		SimpleDateFormat formatterToView = new SimpleDateFormat("HH:mm:ss");

		return formatterToView.format(data);
	}

	/**
	 * Retorna a Hora formatada para texto no formato HORA:MINUTOS
	 * 
	 * @param data
	 * @return
	 */
	public static String HToC2(Date data) {

		if (data == null) {
			return "";
		}

		SimpleDateFormat formatterToView = new SimpleDateFormat("HH:mm");

		return formatterToView.format(data);
	}

	public static String getHora(Date data) {

		if (data == null) {
			return "";
		}

		SimpleDateFormat formatterToView = new SimpleDateFormat("HH");

		return formatterToView.format(data);
	}

	public static String getMinuto(Date data) {

		if (data == null) {
			return "";
		}

		SimpleDateFormat formatterToView = new SimpleDateFormat("mm");

		return formatterToView.format(data);
	}

	/**
	 * Retorna a Data no formato yyyyMMdd
	 * 
	 * @param data
	 * @return
	 */
	public static String DToS(Date data) {

		if (data == null) {
			return "";
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(data);

	}

	/**
	 * Retorna a Data no formato yyyy-MM-dd HH:mm:ss
	 * 
	 * @param data
	 * @return
	 */
	public static String SetaDataHoraEmString(Date data) {

		if (data == null) {
			return "";
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "'" + dateFormat.format(data) + "'";

	}

	/**
	 * Retorna a Data no formato HH:mm:ss
	 * 
	 * @param data
	 * @return
	 */
	public static String SetaHoraEmString(Date data) {

		if (data == null) {
			return "";
		}

		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		return "'" + dateFormat.format(data) + "'";

	}

	/**
	 * Retorna a Data no formato yyyy-MM-dd
	 * 
	 * @param data
	 * @return
	 */
	public static String SetaDataEmString(Date data) {

		if (data == null) {
			return "";
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return "'" + dateFormat.format(data) + "'";

	}

	/**
	 * Retorna a Data no formato yyyy-MM-dd
	 * 
	 * @param data
	 * @return
	 */
	public static String SetaDataEmStringBr(Date data) {

		if (data == null) {
			return "";
		}

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		return dateFormat.format(data);

	}

	/**
	 * Testa a instancia do campo como String
	 * 
	 * @param campo
	 * @return
	 */
	public static boolean IsString(Object campo) {

		if (campo == null) {
			return false;
		}

		if (campo instanceof String) {
			return true;
		}

		return false;
	}

	/**
	 * Retorna a diferença de meses entre duas datas
	 * 
	 * @param Data1
	 * @param Data2
	 * @return
	 */
	public static Integer DifMes(Date Data1, Date Data2) {

		Date dt1 = Data1;
		Date dt2 = Data2;
		Integer nrmes = 0;

		if (Lib.Empty(Data1) || Lib.Empty(Data2) || Data1.after(Data2)) {
			return 0;
		}

		while (!Lib.Month(dt1).equals(Lib.Month(dt2)) || !Lib.Year(dt1).equals(Lib.Year(dt2))) {
			dt1 = Lib.CToD(Lib.StrZero(1, 2) + "/" + Lib.StrZero((Lib.Month(dt1) == 12 ? 1 : Lib.Month(dt1) + 1), 2) + "/" + Lib.StrZero((Lib.Month(dt1) == 12 ? Lib.Year(dt1) + 1 : Lib.Year(dt1)), 4));
			nrmes++;
		}

		return nrmes;
	}

	/**
	 * Retorna diferença de anos entre as duas datas.
	 * */
	public static Integer DifAnos(Date Data1, Date Data2) {

		if ((Data1.before(Data2)) || (Lib.Empty(Data1))) {
			return 0;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(Data1);
		int dt1 = cal.get(Calendar.YEAR);
		cal.clear();
		cal.setTime(Data2);
		int dt2 = cal.get(Calendar.YEAR);

		return dt1 - dt2;
	}

	/**
	 * Retorna a diferença de dias entre duas datas
	 * 
	 * @param Data1
	 * @param Data2
	 * @return
	 */
	public static Double DifDias(Date Data1, Date Data2) {

		if (Lib.Empty(Data1) || Lib.Empty(Data2)) {
			return 0d;
		}

		Calendar dt1 = Calendar.getInstance();
		dt1.setTime(Data1);
		dt1.set(Calendar.HOUR_OF_DAY, 0);
		dt1.set(Calendar.MINUTE, 0);
		dt1.set(Calendar.SECOND, 0);
		dt1.set(Calendar.MILLISECOND, 0);

		Calendar dt2 = Calendar.getInstance();
		dt2.setTime(Data2);
		dt2.set(Calendar.HOUR_OF_DAY, 0);
		dt2.set(Calendar.MINUTE, 0);
		dt2.set(Calendar.SECOND, 0);
		dt2.set(Calendar.MILLISECOND, 0);

		Double nrdias = 0.00;

		// diferença do time dividido por (24 * 60 * 60 * 1000)

		if (Data1.before(Data2)) {
			while (dt1.compareTo(dt2) != 0) {
				dt1.add(Calendar.DAY_OF_MONTH, 1);
				dt1.set(Calendar.HOUR_OF_DAY, 0);
				dt1.set(Calendar.MINUTE, 0);
				dt1.set(Calendar.SECOND, 0);
				dt1.set(Calendar.MILLISECOND, 0);

				nrdias++;
			}
		} else {
			while (dt2.compareTo(dt1) != 0) {
				dt2.add(Calendar.DAY_OF_MONTH, 1);
				dt2.set(Calendar.HOUR_OF_DAY, 0);
				dt2.set(Calendar.MINUTE, 0);
				dt2.set(Calendar.SECOND, 0);
				dt2.set(Calendar.MILLISECOND, 0);

				nrdias--;
			}
		}

		return nrdias;
	}

	/**
	 * Converte uma string para maiusculo
	 * 
	 * @param cControle
	 * @return
	 */
	public static String String2Symbol(String cControle) {

		if (cControle == null) {
			return "";
		}

		return cControle.toUpperCase();
	}

	/**
	 * verifica de o valor cTextdo é um numerico
	 * 
	 * @param campo
	 * @return
	 */
	public static boolean IsNumeric(Object campo) {

		if (campo == null) {
			return false;
		}

		return campo instanceof Number;
	}

	public static boolean IsDouble(Object campo) {

		if (campo == null) {
			return false;
		}

		return campo instanceof Double;
	}

	/**
	 * Testa a instancia do campo como Date
	 * 
	 * @param campo
	 * @return
	 */
	public static boolean IsDate(Object campo) {

		if (campo == null) {
			return false;
		}

		return campo instanceof Date;
	}

	/**
	 * Retorna o nome da maquina
	 * 
	 * @return
	 */
	public static String NetName() {

		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * retorna a hora
	 * 
	 * @return
	 */
	public static String Time() {

		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}
	

	/**
	 * Retorna o dia da semana
	 * 
	 * @param dDate
	 * @return
	 */
	public static Integer DoW(Date dDate) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(dDate);

		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Retorna a quantidade de occorencias da pesquisa numa determinada frase.
	 * 
	 * @param cSearch
	 * @param cTarget
	 * @param offSet
	 * @return
	 */
	public static Integer Occurs3(String cSearch, String cTarget, Integer offSet) {

		String aux = cTarget;
		Integer count = 0;

		if (offSet != null) {
			aux = cTarget.substring(offSet);
		}

		while (true) {

			if (aux.indexOf(cSearch) == -1) {
				break;
			}

			count++;
			aux = aux.substring(aux.indexOf(cSearch) + cSearch.length());
		}
		return count;
	}

	/**
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static Long calculaDiferencaDias(Date dataInicio, Date dataFim) {

		Calendar c1 = Calendar.getInstance();
		c1.setTime(dataInicio);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(dataFim);

		Long m1 = c1.getTimeInMillis();
		Long m2 = c2.getTimeInMillis();
		Long retorno = Math.round(((m2.doubleValue() - m1.doubleValue()) / (24d * 60d * 60d * 1000d)));

		return retorno;

	}

	/**
	 * Adiciona os dias conforme especificado.
	 * 
	 * @param data
	 * @param dias
	 * @return
	 */
	public static Date addDias(Date data, Integer dias) {

		Calendar cal = Calendar.getInstance();
		if (data != null) {
			cal.setTime(data);
		} else {
			cal.setTimeInMillis(0L);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_YEAR, dias);

		return cal.getTime();
	}

	/**
	 * Diminui os dias conforme especificado.
	 * 
	 * @param data
	 * @param dias
	 * @return
	 */
	public static Date rollDias(Date data, Integer dias) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.roll(Calendar.DAY_OF_YEAR, dias);

		return cal.getTime();
	}

	/**
	 * Retorna a posicao da ultima ocorencia informada
	 * 
	 * @param campo
	 * @param pesquisa
	 * @return
	 */
	public static Integer RAt(String pesquisa, String campo) {

		return campo.lastIndexOf(pesquisa);
	}

	/**
	 * Retorna a posicao da ultima ocorencia informada
	 * 
	 * @param campo
	 * @param pesquisa
	 * @return
	 */
	public static Integer RAt2(String pesquisa, String campo) {

		return RAt(pesquisa, campo);
	}

	/**
	 * Retorna a posicao da ultima ocorencia informada, iniciando da posicao desejada
	 * 
	 * @param campo
	 * @param pesquisa
	 * @param inicio
	 * @return
	 */
	public static Integer RAt3(String pesquisa, String campo, Integer inicio) {

		return campo.lastIndexOf(pesquisa, inicio);
	}

	/**
	 * converte uma String data em um Date
	 * 
	 * @param campo
	 * @return
	 */
	public static Date SToD(String campo) {

		if (campo.length() < 8) {
			return null;
		}

		campo = campo.substring(0, 8);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(campo.substring(6, 8)));
		cal.set(Calendar.MONTH, Integer.valueOf(campo.substring(4, 6)) - 1);
		cal.set(Calendar.YEAR, Integer.valueOf(campo.substring(0, 4)));

		return cal.getTime();
	}


	/**
	 * Função que converte mes em extenso para um inteiro janeiro = 1 e assim por diante
	 * */
	public static Integer MesExtensoParaNumero(String value) {

		Integer cReturn = 0;
		value = Lib.Upper(value);

		if ("JANEIRO".equalsIgnoreCase(value)) {
			cReturn = 1;
		} else if ("FEVEREIRO".equalsIgnoreCase(value)) {
			cReturn = 2;
		} else if ("MAR�O".equalsIgnoreCase(value)) {
			cReturn = 3;
		} else if ("ABRIL".equalsIgnoreCase(value)) {
			cReturn = 4;
		} else if ("MAIO".equalsIgnoreCase(value)) {
			cReturn = 5;
		} else if ("JUNHO".equalsIgnoreCase(value)) {
			cReturn = 6;
		} else if ("JULHO".equalsIgnoreCase(value)) {
			cReturn = 7;
		} else if ("AGOSTO".equalsIgnoreCase(value)) {
			cReturn = 8;
		} else if ("SETEMBRO".equalsIgnoreCase(value)) {
			cReturn = 9;
		} else if ("OUTUBRO".equalsIgnoreCase(value)) {
			cReturn = 10;
		} else if ("NOVEMBRO".equalsIgnoreCase(value)) {
			cReturn = 11;
		} else if ("DEZEMBRO".equalsIgnoreCase(value)) {
			cReturn = 12;

		} else {
			cReturn = 1;

		}
		return cReturn;
	}

	

	/**
	 * 
	 * @param diretorio
	 *            aonde se encontra o arquivo.
	 * @param arquivo
	 *            que vai ser lido.
	 * @return retorna uma string com o conteudo do arquivo.
	 * @throws IOException
	 */
	public static String lerArquivo(File diretorio, String arquivo) throws IOException {

		BufferedReader leitor = null;
		StringBuilder retorno = new StringBuilder();

		try {

			File cArquivo = new File(diretorio, arquivo);
			leitor = new BufferedReader(new FileReader(cArquivo));

			while (leitor.ready()) {
				retorno.append(leitor.readLine().replace("</br>", Lib.CRTL));
			}

		} finally {
			leitor.close();
		}

		return retorno.toString();

	}

	

	/**
	 * 
	 * @param arquivo
	 * @return string com o tamanho do arquivo
	 * @author
	 */
	public static String TamanhoArquivo(File arquivo) {
		long tamanho = arquivo.length();

		int B = 1; // Byte
		int KB = 1024 * B; // Kilobyte
		int MB = 1024 * KB; // Megabyte
		int GB = 1024 * MB; // Gigabyte

		String cTamanho = "0";

		if (tamanho > GB) {
			cTamanho = Lib.Str(new Double((double) tamanho / GB), 12, 2) + " GB";
		} else if (tamanho > MB) {
			cTamanho = Lib.Str(new Double((double) tamanho / MB), 12, 2) + " MB";
		} else if (tamanho > KB) {
			cTamanho = Lib.Str(new Double((double) tamanho / KB), 12, 2) + " KB";
		} else {
			cTamanho = Lib.Str(new Double((double) tamanho), 12, 0) + " bytes";
		}

		return cTamanho;

	}

	/**
	 * 
	 * @param arquivo
	 * @return data da última modificão do arquivo
	 * @author Donizete
	 */

	public static Date DataModificacaoArquivo(File arquivo) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(arquivo.lastModified());
		return cal.getTime();
	}

	public static boolean IsLogic(Object object) {

		if (object == null) {
			return false;
		}

		return object instanceof Boolean;
	}

	

	/**
	 * <blockquote>
	 * <p>
	 * Retorna o valor passado como parametro formatado seguindo a seguinte regra.
	 * <p>
	 * Exemplo:
	 * <p>
	 * Valor: 1.850,2000000 => Resultado: 1,850,20.
	 * <p>
	 * Valor: 1.850,2001000 => Resultado: 1,850,2001.
	 * <p>
	 * Valor: 1.850,2220000 => Resultado: 1,850,222.</blockquote>
	 * 
	 * @param valor
	 *            - Valor a ser formatado.
	 * 
	 * @return
	 */

	public static String retornaValorFormatado(Number valor) {

		String valor2 = "";

		valor2 = Lib.Substr(valor, 0, Lib.At(".", valor.toString()) - 1);

		if (valor.floatValue() == ((Double) Double.parseDouble(valor2)).floatValue()) {

			DecimalFormat dfm = new DecimalFormat("###,###,###");
			return dfm.format(Long.parseLong(valor2));
		} else {

			DecimalFormat dfm = new DecimalFormat("###,###,##0.00######");
			return dfm.format(valor);
		}

	}


	/**
	 * 
	 * @param valor
	 * @return
	 */
	public static Double StringToD(String valor) {
		return Double.parseDouble(valor.replaceAll("\\.", "").replaceAll(",", "\\."));
	}

	public static String formataNumero(Long decimal, Double numero) {

		numero = Lib.Round(numero, 8);

		NumberFormat df = NumberFormat.getInstance();
		df.setMaximumFractionDigits(decimal.intValue());
		df.setMinimumFractionDigits(decimal.intValue());
		df.setMaximumIntegerDigits(12);
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(numero);

	}

	public static int getArredondamento(char arr) {
		switch (arr) {
		case 'E':
			return BigDecimal.ROUND_DOWN;
		case 'G':
			return BigDecimal.ROUND_HALF_UP;
		case 'C':
			return BigDecimal.ROUND_HALF_UP;
		case 'T':
			return BigDecimal.ROUND_DOWN;
		}
		return -1;
	}

	public static BigDecimal getArredondamento(BigDecimal val, Long decimal, char arredondamento) {

		// val = BigDecimal.valueOf(val.doubleValue());

		switch (arredondamento) {
		case 'E':
			// O banco de dados aceita no máximo 38 caracteres numéricos
			Integer nInteiros = val.intValue();
			val = val.setScale(38 - nInteiros.toString().length(), BigDecimal.ROUND_DOWN);
			break;
		case 'T':
			val = val.setScale(decimal.intValue(), BigDecimal.ROUND_DOWN);
			break;
		case 'G':
			val = val.setScale(decimal.intValue(), BigDecimal.ROUND_HALF_UP);
			break;
		case 'C':
			val = val.setScale(decimal.intValue(), BigDecimal.ROUND_HALF_UP);
			break;
		}
		return val;
	}

	public static Double getArredondamento(Double val, Long decimal, char arredondamento) {

		val = Lib.Round(val, 8);

		BigDecimal bd = BigDecimal.valueOf(val);

		switch (arredondamento) {
		case 'E':
			// O banco de dados aceita no máximo 38 caracteres numéricos
			Integer nInteiros = bd.intValue();
			val = bd.setScale(38 - nInteiros.toString().length(), BigDecimal.ROUND_DOWN).doubleValue();
			break;
		case 'T':
			val = bd.setScale(decimal.intValue(), BigDecimal.ROUND_DOWN).doubleValue();
			break;
		case 'G':
			val = bd.setScale(decimal.intValue(), BigDecimal.ROUND_HALF_UP).doubleValue();
			break;
		case 'C':
			val = bd.setScale(decimal.intValue(), BigDecimal.ROUND_HALF_UP).doubleValue();
			break;
		}
		return val;
	}
 

	/**
	 * Retorna a data do primeiro dia da semana. Semana incia no domingo e termina no s�bado.
	 * 
	 * @param date
	 * @return
	 */
	public static Date DiaInicioSemana(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return calendar.getTime();
		}

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return Lib.DiaInicioSemana(calendar.getTime());

	}

	/**
	 * Retorna a data do último dia da semana. Semana incia no domingo e termina no sábado.
	 * 
	 * @param date
	 * @return
	 */
	public static Date DiaFimSemana(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			return calendar.getTime();
		}

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return Lib.DiaFimSemana(calendar.getTime());

	}

	public static String RetiraAcentuacao(String cText) {

		if (cText == null) {
			return "";
		}

		cText = Normalizer.normalize(cText, Normalizer.Form.NFD);
		return cText.replaceAll("[^\\p{ASCII}]", "");
	}

	private static Map<Long, String> mapTipoDado = null;

	public static String RetTipoDado(Long nTipo) {

		if (Lib.mapTipoDado == null) {

			Lib.mapTipoDado = new HashMap<Long, String>();
			Lib.mapTipoDado.put(1L, "Char");
			Lib.mapTipoDado.put(2L, "Float");
			Lib.mapTipoDado.put(3L, "Int");
			Lib.mapTipoDado.put(4L, "Real");
			Lib.mapTipoDado.put(5L, "Numeric");
			Lib.mapTipoDado.put(6L, "DateTime");
			Lib.mapTipoDado.put(7L, "TimeStamp");
			Lib.mapTipoDado.put(8L, "Text");
			Lib.mapTipoDado.put(9L, "Decimal");
			Lib.mapTipoDado.put(10L, "Varchar");

		}

		return Lib.mapTipoDado.get(nTipo);

	}

	/**
	 * Retira todos zeros a esquerda de um texto.
	 * 
	 * @param cTexto
	 * @return Texto sem zeros a esquerda.
	 */
	public static String retiraZeroEsquerda(String cTexto) {

		if (!Empty(cTexto)) {
			cTexto = cTexto.replaceAll("^0*", "");
		}

		return cTexto;
	}

	/**
	 * Retorna o dia da semana com base na data informada.
	 * 
	 * @param data
	 * @return String
	 */
	public static String retornaDiaSemanaFormatada(Date data) {
		String nRetorno = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		int nDiaSemana = calendar.get(Calendar.DAY_OF_WEEK);

		switch (nDiaSemana) {
		case Calendar.SUNDAY:
			nRetorno = "Domingo";
			break;
		case Calendar.MONDAY:
			nRetorno = "Segunda-feira";
			break;
		case Calendar.TUESDAY:
			nRetorno = "Terça-feira";
			break;
		case Calendar.WEDNESDAY:
			nRetorno = "Quarta-feira";
			break;
		case Calendar.THURSDAY:
			nRetorno = "Quinta-feira";
			break;
		case Calendar.FRIDAY:
			nRetorno = "Sexta-feira";
			break;
		case Calendar.SATURDAY:
			nRetorno = "Sábado";
			break;
		}

		return nRetorno;

	}

	/**
	 * Retorna a sigla do dia da semana com base na data informada.
	 * 
	 * @param data
	 * @return String
	 */
	public static String retornaDiaSemanaSigla(Date data) {
		String nRetorno = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		int nDiaSemana = calendar.get(Calendar.DAY_OF_WEEK);

		switch (nDiaSemana) {
		case Calendar.SUNDAY:
			nRetorno = "D";
			break;
		case Calendar.MONDAY:
			nRetorno = "S";
			break;
		case Calendar.TUESDAY:
			nRetorno = "T";
			break;
		case Calendar.WEDNESDAY:
			nRetorno = "Q";
			break;
		case Calendar.THURSDAY:
			nRetorno = "Q";
			break;
		case Calendar.FRIDAY:
			nRetorno = "S";
			break;
		case Calendar.SATURDAY:
			nRetorno = "S";
			break;
		}

		return nRetorno;

	}
	

	/**
	 * Método converte um data do tipo texto para um objeto tipo Date de acordo com os parâmetros passados.
	 * 
	 * @param data
	 *            - é a data em string que será convertido
	 * @param formato
	 *            - tipo da formatação do parametro data
	 * @return retorna um objeto Date
	 * @throws Exception
	 */
	public static Date getData(String data, String formato) throws Exception {

		if (Empty(data)) {
			return null;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(formato);
		return formatter.parse(data);

	}

	/**
	 * Retorna somente a data, a hora, minuto, segundo e milissegundos ser�o zerados
	 * 
	 * @param data
	 * @return data com time zerado.
	 */
	public static Date getDataTempoZerado(Date data) {

		if (data == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();

	}

	/**
	 * Retorna somente a data, a hora, minuto, segundo e milissegundos serão zerados
	 * 
	 * @param data
	 * @return Calendar com time zerado.
	 */
	public static Calendar getCalendarTempoZerado(Date data) {

		if (data == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;

	}


	public static void limparDiretorio(File diretorio) {

		if (diretorio.isDirectory()) {

			for (File file : diretorio.listFiles()) {

				if (file.isDirectory()) {
					limparDiretorio(file);
				}

				file.delete();

			}

		}

	}

	
		
}
