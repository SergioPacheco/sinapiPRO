package br.edu.ifrn.sinapiPRO.utils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;


/**
 * Utilitário para manipular a itemização do orçamento.
 * */
public class ItemizarOrcamentoUtil {

	private boolean ite_automatica;
	private String sep_itm;
	private String mac_itm;

	public ItemizarOrcamentoUtil(boolean caoc1_ite_aut, String caoca1_sep_itm, String caoc1_mac_itm) {
		this.ite_automatica = caoc1_ite_aut;
		this.sep_itm = caoca1_sep_itm;
		this.mac_itm = caoc1_mac_itm;
	}

	/**
	 * Itemização automática do orçamento.
	 * */
	public boolean isItemizar() {
		return ite_automatica;
	}

	/**
	 * Itemização orçamento. Faz validação de acordo com a máscara do tipo de item informado.
	 * Caso a itemização estiver ativa porém a máscara referente ao tipo de item não estiver configurada para itemizar automaticamente
	 * o usuário poderá informar sua própria itemização.
	 * */
	public boolean isItemizar(int itoc1_tip) {
		return ite_automatica && getMascara(itoc1_tip) != null;
	}

	public void setItemizar(boolean caoc1_ite_aut) {
		this.ite_automatica = caoc1_ite_aut;
	}

	/**
	 * Retorna a sequência da itemização.
	 * 
	 * @param itoc1_itm
	 *            itemização atual ou anterior
	 * @param tipoRetorno
	 *            tipo de sequência (0 - irmão, 1 - irmão do pai, 2 - filho)
	 * @param sep_itm
	 *            Separador da itemização
	 * @param mascara
	 *            Máscara da itemização - a máscara aqui deve estar formatada de acordo com o tipo(ITOC1_TIP) do item que se quer obter a itemização.
	 *            Para maiores detalhes sobre a formatação de máscaras veja OrcamentoUtil.getMascara().
	 * */
	public String getItemizacao(String itoc1_itm, int tipoRetorno, String mascara) {

		if (!ite_automatica)
			return "";

		itoc1_itm = "".equals(itoc1_itm.trim()) ? "0" : itoc1_itm.trim();

		if (tipoRetorno == 1) {
			int size = itoc1_itm.lastIndexOf(sep_itm) - mascara.length();
			if (size > 0) {
				itoc1_itm = itoc1_itm.substring(0, size);
			}
		}

		String[] split = String.valueOf(itoc1_itm).split("[" + sep_itm + "]");
		int index = split.length - 1 < 0 ? 0 : tipoRetorno == 2 ? split.length : split.length - 1;

		String itemizacao = "";

		try {
			long n = Long.parseLong(split[split.length - 1]) + 1L;

			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			nf.setMaximumIntegerDigits(mascara.length());
			nf.setMinimumIntegerDigits(mascara.length());

			for (int i = 0; i < index; i++) {
				// itemizacao += nf.format(Long.parseLong(split[i])).replaceAll(",", "") + separador;
				itemizacao += split[i] + sep_itm;
			}

			if (tipoRetorno == 2) {
				itemizacao += nf.format(1L).replaceAll(",", "") + sep_itm;
			} else {
				itemizacao += nf.format(n).replaceAll(",", "") + sep_itm;
			}

		} catch (NumberFormatException nfe) {

		}

		return itemizacao;
	}

	/**
	 * Gera a itemização do orçamento.
	 * 
	 * @param caoc1_cod
	 *            Orçamento para itemizar
	 * @param itoc1_seq
	 *            Gera a partir da sequência informada (itens com sequênciar maior)
	 * @param itoc1_niv_num
	 *            Gera somente de um nível específico
	 * */
	protected HashMap<Long, String> gerarItemizacao(Long caoc1_cod, String itoc1_seq, Long itoc1_niv_num, IAcoesConv acoesConv) throws Exception {

		HashMap<Long, String> map = new HashMap<Long, String>();

		if (!this.ite_automatica)
			return map;

		StringBuilder str = new StringBuilder();
		str.append("SELECT");
		str.append(" ITOC1_COD,");
		str.append(" ITOC1_COD_PAI,");
		str.append(" ITOC1_SEQ,");
		str.append(" ITOC1_ITM,");
		str.append(" ITOC1_NIV_NUM,");
		str.append(" (SELECT X.ITOC1_ITM FROM TS1_ITOC X WHERE X.ITOC1_COD = TS1_ITOC.ITOC1_COD_PAI) ITOC1_ITM_PAI,");
		str.append(" (SELECT TOP 1 X.ITOC1_ITM FROM TS1_ITOC X WHERE X.ITOC1_COD_PAI = TS1_ITOC.ITOC1_COD_PAI AND X.ITOC1_COD <> TS1_ITOC.ITOC1_COD /*AND X.ITOC1_TIP = TS1_ITOC.ITOC1_TIP */ AND X.ITOC1_SEQ < TS1_ITOC.ITOC1_SEQ ORDER BY X.ITOC1_ITM DESC) ITOC1_ITM_AUX,");
		str.append(" ITOC1_TIP ");
		str.append("FROM");
		str.append(" TS1_ITOC ");
		str.append("WHERE");
		str.append(" CAOC1_COD = ").append(caoc1_cod);
		if (!Lib.Empty(itoc1_seq))
			str.append(" AND ITOC1_SEQ >= '").append(itoc1_seq).append("'");
		if (!Lib.Empty(itoc1_niv_num))
			str.append(" AND ITOC1_NIV_NUM = ").append(itoc1_niv_num);

		str.append(" ORDER BY ITOC1_SEQ ");

		// Db_Sql sql = new Db_Sql(str.toString(), "", acoesConv.Global());

		String itoc1_itm = "";
		Long itoc1_cod_anterior = 0L;

		long indiceTabela = 0l;

		while (!sql.Eof()) {

			sql.Execute();
			sql.Goto(indiceTabela);

			String itoc1_tip = String.valueOf(sql.FIELDGET("ITOC1_TIP"));

			String mascara_aux = getMascara(Integer.parseInt(itoc1_tip));
			if (mascara_aux == null) {
				// MethodosVO.ExecutaSqlSemVerificacao("UPDATE TS1_ITOC SET ITOC1_ITM = '' WHERE ITOC1_COD = " + String.valueOf(sql.FIELDGET("ITOC1_COD")), null, acoesConv.Global());
				sql.Skip(1L);
				itoc1_cod_anterior = ((Number) sql.FIELDGET("ITOC1_COD")).longValue();
				indiceTabela++;
				continue;
			}

			int tipoRetorno = 0;

			if ("2".equals(itoc1_tip)) {
				// formatar a itemização de acordo com a máscara
				itoc1_itm = getItemizacao(String.valueOf((Long) sql.FIELDGET("ITOC1_NIV_NUM") - 1L), 0, mascara_aux);
			} else {

				// 0 = irmão, 1 = irmão do pai, 2 = filho
				if (itoc1_cod_anterior.equals(((Number) sql.FIELDGET("ITOC1_COD_PAI")).longValue())) {
					tipoRetorno = 2;
				} else {
					tipoRetorno = 0;
				}

				if (sql.FIELDGET("ITOC1_ITM_AUX") != null && !"".equals(String.valueOf(sql.FIELDGET("ITOC1_ITM_AUX")))) {
					itoc1_itm = getItemizacao(String.valueOf(sql.FIELDGET("ITOC1_ITM_AUX")), 0, mascara_aux);
				} else if (sql.FIELDGET("ITOC1_ITM_PAI") != null && !"".equals(String.valueOf(sql.FIELDGET("ITOC1_ITM_PAI")))) {
					itoc1_itm = getItemizacao(String.valueOf(sql.FIELDGET("ITOC1_ITM_PAI")), 2, mascara_aux);
				} else {
					itoc1_itm = getItemizacao(itoc1_itm, tipoRetorno, mascara_aux);
				}
			}

			map.put((Long) sql.FIELDGET("ITOC1_COD"), itoc1_itm);

			MethodosVO.ExecutaSqlSemVerificacao("UPDATE TS1_ITOC SET ITOC1_ITM = '" + itoc1_itm.trim() + "' WHERE ITOC1_COD = " + String.valueOf(sql.FIELDGET("ITOC1_COD")), null, acoesConv.Global());
			itoc1_cod_anterior = ((Number) sql.FIELDGET("ITOC1_COD")).longValue();
			sql.Skip(1L);
			indiceTabela++;
		}

		return map;
	}

	/**
	 * Retorna a máscara de itemização de acordo com o tipo do item (ITOC1_TIP). 
	 * Retorna null caso o item esteja configurado para não gerar itemização.
	 * */
	protected String getMascara(int itoc1_tip) {

		String mask = mac_itm;

		switch (itoc1_tip) {
		case 2:// nível
			mask = mac_itm.split("[" + sep_itm + "]")[0];
			break;
		case 3: // subnível
			mask = mac_itm.split("[" + sep_itm + "]")[1];
			break;
		case 0: // insumo solto
			mask = mac_itm.split("[" + sep_itm + "]")[2];
			break;
		case 1: // composição
			mask = mac_itm.split("[" + sep_itm + "]")[2];
			break;
		case 4: // item da composição
			mask = mac_itm.split("[" + sep_itm + "]")[3];
			break;
		case 5: // item da composição
			mask = mac_itm.split("[" + sep_itm + "]")[3];
			break;
		default:
			break;
		}

		if (mask.toLowerCase().contains("x"))
			return null;
		return mask;
	}

}
