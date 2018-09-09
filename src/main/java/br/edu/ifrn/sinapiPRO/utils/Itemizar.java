package br.edu.ifrn.sinapiPRO.utils;

import java.sql.Connection;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Classe responsável por efetuar o recálculo da itemização do orçamento.
 * */
public class Itemizar {

	Long itoc1_cod;
	ItemizarOrcamentoUtil itemizador;

	public Itemizar(Long itoc1_cod, ItemizarOrcamentoUtil itemizador) {
		this.itoc1_cod = itoc1_cod;
		this.itemizador = itemizador;
	}

	/**
	 * Efetua o recálculo da itemização do orçamento a partir do item informado.
	 * */
	public HashMap<Long, String> doITM(Long itoc1_cod, IAcoesConv acoesConv) throws Exception {

		HashMap<Long, String> map = new HashMap<Long, String>();
		String itoc1_itm_etapa = "";

		if (!itemizador.caoc1_ite_aut)
			return map;

		String itoc1_itm = "";

		StringBuilder str = new StringBuilder();
		str.append("SELECT");
		str.append(" ITOC1_COD,");
		str.append(" ITOC1_COD_PAI,");
		str.append(" ITOC1_SEQ,");
		str.append(" ITOC1_ITM,");
		str.append(" (SELECT X.ITOC1_ITM FROM TS1_ITOC X WHERE X.ITOC1_COD = TS1_ITOC.ITOC1_COD_PAI) ITOC1_ITM_PAI, ");
		str.append(" ITOC1_NIV_NUM,");
		str.append(" ITOC1_TIP ");
		str.append("FROM");
		str.append(" TS1_ITOC ");
		str.append("WHERE");
		str.append(" ITOC1_COD_PAI = ").append(itoc1_cod);
		str.append(" ORDER BY ITOC1_SEQ ");

		Db_Sql sql = new Db_Sql(str.toString(), "", acoesConv.Global());

		while (!sql.Eof()) {

			String itoc1_tip = String.valueOf(sql.FIELDGET("ITOC1_TIP"));

			String mascara = itemizador.getMascara(Integer.parseInt(itoc1_tip));

			if (mascara == null) {
				// MethodosVO.ExecutaSqlSemVerificacao("UPDATE TS1_ITOC SET ITOC1_ITM = '' WHERE ITOC1_COD = " + String.valueOf(sql.FIELDGET("ITOC1_COD")), null, acoesConv.Global());
				sql.Skip(1L);
				continue;
			}

			if ("3".equals(itoc1_tip)) {
				itoc1_itm = getItemizacao(itoc1_itm_etapa.isEmpty() ? (String) sql.FIELDGET("ITOC1_ITM_PAI") : itoc1_itm_etapa, itoc1_itm_etapa.isEmpty() ? 2 : 0, mascara);
				itoc1_itm_etapa = itoc1_itm;
			} else {
				if (itoc1_itm.isEmpty())
					itoc1_itm = getItemizacao((String) sql.FIELDGET("ITOC1_ITM_PAI"), 2, mascara);
				else
					itoc1_itm = getItemizacao(itoc1_itm, 0, mascara);
			}
			MethodosVO.ExecutaSqlSemVerificacao("UPDATE TS1_ITOC SET ITOC1_ITM = '" + itoc1_itm.trim() + "' WHERE ITOC1_COD = " + String.valueOf(sql.FIELDGET("ITOC1_COD")), null, acoesConv.Global());

			map.putAll(doITM(((Number) sql.FIELDGET("ITOC1_COD")).longValue(), acoesConv));

			map.put((Long) sql.FIELDGET("ITOC1_COD"), itoc1_itm.trim());

			sql.Skip(1L);
		}

		return map;
	}

	/**
	 * Retorna a sequência da itemização.
	 * 
	 * @param itoc1_itm
	 *            itemização atual ou anterior
	 * @param tipoRetorno
	 *            tipo de sequência (0 - irmão, 1 - irmão do pai, 2 - filho)
	 * @param caoc1_sep_itm
	 *            Separador da itemização
	 * @param mascara
	 *            Máscara da itemização - a máscara aqui deve estar formatada de acordo com o tipo(ITOC1_TIP) do item que se quer obter a itemização.
	 *            Para maiores detalhes sobre a formatação de máscaras veja OrcamentoUtil.getMascara().
	 * */
	protected synchronized String getItemizacao(String itoc1_itm, int tipoRetorno, String mascara) {

		if (!itemizador.caoc1_ite_aut)
			return "";

		itoc1_itm = "".equals(itoc1_itm.trim()) ? "0" : itoc1_itm.trim();

		if (tipoRetorno == 1) {
			int size = itoc1_itm.lastIndexOf(itemizador.caoc1_sep_itm) - mascara.length();
			if (size > 0) {
				itoc1_itm = itoc1_itm.substring(0, size);
			}
		}

		String[] split = String.valueOf(itoc1_itm).split("[" + itemizador.caoc1_sep_itm + "]");
		int index = split.length - 1 < 0 ? 0 : tipoRetorno == 2 ? split.length : split.length - 1;

		String itemizacao = "";

		try {
			long n = Long.parseLong(split[split.length - 1]) + 1L;

			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			nf.setMaximumIntegerDigits(mascara.length());
			nf.setMinimumIntegerDigits(mascara.length());

			for (int i = 0; i < index; i++) {
				// itemizacao += nf.format(Long.parseLong(split[i])).replaceAll(",", "") + separador;
				itemizacao += split[i] + itemizador.caoc1_sep_itm;
			}

			if (tipoRetorno == 2) {
				itemizacao += nf.format(1L).replaceAll(",", "") + itemizador.caoc1_sep_itm;
			} else {
				itemizacao += nf.format(n).replaceAll(",", "") + itemizador.caoc1_sep_itm;
			}

		} catch (NumberFormatException nfe) {

		}

		return itemizacao;
	}

}
