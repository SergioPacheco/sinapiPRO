package br.edu.ifrn.sinapiPRO.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Esta classe é responsável por indicar se possui redundância 
 * entre os filhos da composição passada por parâmetro.
 * */
public class VerificaComposicaoRedundante {

	/** Mapa para armazenar o código de cada subcomposição filha. */
	private static HashMap<Long, Long> mapaEstruturaComposicao = new HashMap<Long, Long>();

	/**
	 * Método responsável por verificar se existe redundância na composição.
	 * 
	 * 
	 * */
	public static boolean verificaComposicaoRedundante(Long nInsu1Cod_composicao, Long nInsu1_cod_ori, Long nBase1_cod, Global global, Connection conn) throws Exception {

		ConnectionDB connection = null;
		try {

			connection = new ConnectionDB(conn, global);

			mapaEstruturaComposicao.clear();
			mapaEstruturaComposicao.put(nInsu1Cod_composicao, nInsu1Cod_composicao);
			mapaEstruturaComposicao.put(nInsu1_cod_ori, nInsu1_cod_ori);

			if (verificaRedundancia(nInsu1_cod_ori, nBase1_cod, global, connection.conn)) {
				return true;
			}

			return false;

		} finally {

			if (connection != null) {
				connection.Close();
			}
		}
	}

	private static boolean verificaRedundancia(long nInsu1_cod, Global global, Connection conn) throws Exception {
		return verificaRedundancia(nInsu1_cod, null, global, conn);
	}

	/**
	 * Para cada filho do tipo composição, armazena-se no HashMap seu respectivo código. <br>
	 * Após preencher o HashMap verifica se o código da composição corrente está entre eles. <br>
	 * Se estiver indica que há redundância entre os filhos desta composição.
	 * */
	private static boolean verificaRedundancia(Long nInsu1_cod, Long base1_cod, Global global, Connection conn) throws Exception {
		StringBuilder sb = new StringBuilder();
		ConnectionDB connection = null;
		try {

			connection = new ConnectionDB(conn, global);

			sb.append("SELECT");
			sb.append(" INSU1_COD");
			sb.append(",INSU1_COD_ORI");
			sb.append(",(SELECT X.INSU1_COM FROM TS1_INSU X WHERE X.INSU1_COD = TS1_COMP.INSU1_COD) INSU1_COM ");
			sb.append("FROM");
			sb.append(" TS1_COMP ");
			sb.append("WHERE");
			sb.append(" INSU1_COD_ORI = ").append(nInsu1_cod);
			if (base1_cod != null) {
				sb.append(" AND BASE1_COD = ").append(base1_cod);
			}

			Db_Sql oSql_aux = new Db_Sql(sb.toString(), "", global, connection.conn);

			while (!oSql_aux.Eof()) {

				// retorna se encontrar uma das sub-composições que compoem a estrutura
				if (mapaEstruturaComposicao.containsKey(oSql_aux.FIELDGET("INSU1_COD"))) {
					return true;
				}
				if ("1".equals(oSql_aux.FIELDGET("INSU1_COM"))) {
					mapaEstruturaComposicao.put(((Number) oSql_aux.FIELDGET("INSU1_COD")).longValue(), ((Number) oSql_aux.FIELDGET("INSU1_COD")).longValue());
					if (verificaRedundancia(((Number) oSql_aux.FIELDGET("INSU1_COD")).longValue(), base1_cod, global, connection.conn)) {
						return true;
					}
				}

				oSql_aux.Skip(1L);
			}

			// precisa remover o item ao final, pois deve-se armazenar apenas os cabeçaalhos que compoem a estrutura de baixo para cima
			mapaEstruturaComposicao.remove(nInsu1_cod);

			return false;

		} finally {

			if (connection != null) {
				connection.Close();
			}
		}
	}

	/**
	 * Verifica se existe redundância na composição da planilha de orçamento.
	 * 
	 * @param object
	 * */
	public static boolean verificaComposicaoRedundanteOrcamento(Long insu1_codComposicao, 
			Long insu1_cod_pai, 
			Long itoc1_cod_pai, 
			Long base1_cod, 
			Global global, Connection conn) throws Exception {

		ConnectionDB connection = null;
		try {

			connection = new ConnectionDB(conn, global);

			mapaEstruturaComposicao.clear();

			mapaEstruturaComposicao.put(insu1_codComposicao, insu1_codComposicao);
			mapaEstruturaComposicao.put(insu1_cod_pai, insu1_cod_pai);

			// primeiro verifica se a composição é redundante
			if (verificaRedundancia(insu1_codComposicao, base1_cod, global, connection.conn)) {
				return true;
			}

			// caso a composição não for redundante, é preciso verificar se haverá redundancia na estrutura do orçamento.
			mapaEstruturaComposicao.clear();
			mapaEstruturaComposicao.put(insu1_codComposicao, insu1_codComposicao);

			// pega a estrutura de cabeçalhos da composção que pretende-se adicionar ao orçamento.
			pegaEstruturaComposicao(mapaEstruturaComposicao, insu1_codComposicao, base1_cod, global, connection.conn);

			if (insu1_codComposicao.equals(insu1_cod_pai)) {
				return true;
			}

			if (verificaRedundanciaOrcamento(itoc1_cod_pai, global, connection.conn)) {
				return true;
			}

			return false;

		} finally {

			if (connection != null) {
				connection.Close();
			}
		}
	}

	/**
	 * Adiciona no mapa todas as sub-composições que fazem parte da estrutura da composição informada.
	 * */
	private static void pegaEstruturaComposicao(HashMap<Long, Long> mapaEstruturaComposicao2, 
			Long insu1_cod, Long base1_cod, Global global, Connection conn) throws Exception {
		StringBuilder sb = new StringBuilder();
		ConnectionDB connection = null;
		try {

			connection = new ConnectionDB(conn, global);

			sb.append("SELECT");
			sb.append(" INSU1_COD");
			sb.append(",INSU1_COD_ORI");
			sb.append(",(SELECT X.INSU1_COM FROM TS1_INSU X WHERE X.INSU1_COD = TS1_COMP.INSU1_COD) INSU1_COM ");
			sb.append("FROM");
			sb.append(" TS1_COMP ");
			sb.append("WHERE");
			sb.append(" INSU1_COD_ORI = ").append(insu1_cod);
			sb.append(" AND BASE1_COD = ").append(base1_cod);

			Db_Sql oSql_aux = new Db_Sql(sb.toString(), "", global, connection.conn);

			while (!oSql_aux.Eof()) {

				if ("1".equals(oSql_aux.FIELDGET("INSU1_COM")) && !mapaEstruturaComposicao.containsKey(oSql_aux.FIELDGET("INSU1_COD"))) {
					mapaEstruturaComposicao.put(((Number) oSql_aux.FIELDGET("INSU1_COD")).longValue(), ((Number) oSql_aux.FIELDGET("INSU1_COD")).longValue());
				}

				oSql_aux.Skip(1L);
			}

		} finally {

			if (connection != null) {
				connection.Close();
			}
		}
	}

	/**
	 * Verifica os pais do tipo composição e sub-composição comparando-os aos dados contidos no mapa.
	 * Se um dos pais já estiver no mapa indica que haverá redundância entre os filhos desta composição.
	 * */
	private static boolean verificaRedundanciaOrcamento(long nInsu1_cod, Global global, Connection conn) throws Exception {
		StringBuilder sb = new StringBuilder();
		ConnectionDB connection = null;
		try {

			connection = new ConnectionDB(conn, global);

			sb.append("SELECT");
			sb.append(" ITOC1_COD");
			sb.append(",ITOC1_COD_PAI");
			sb.append(",INSU1_COD");
			sb.append(",ITOC1_SUB_COM");
			sb.append(",ITOC1_TIP ");
			sb.append("FROM");
			sb.append(" TS1_ITOC ");
			sb.append("WHERE");
			sb.append(" ITOC1_COD = ").append(nInsu1_cod);

			Db_Sql oSql_aux = new Db_Sql(sb.toString(), "", global, connection.conn);

			while (!oSql_aux.Eof()) {

				if ("0,2,3".contains((String) oSql_aux.FIELDGET("ITOC1_TIP"))) {
					return false;
				}

				// retorna se encontrar uma das sub-composições que compoem a estrutura
				if (mapaEstruturaComposicao.containsKey(oSql_aux.FIELDGET("INSU1_COD"))) {
					return true;
				}

				boolean composicao = "1".equals(oSql_aux.FIELDGET("ITOC1_TIP")) || ("4,5".contains((String) oSql_aux.FIELDGET("ITOC1_TIP")) && "1".equals(oSql_aux.FIELDGET("ITOC1_SUB_COM")));

				if (composicao) {
					mapaEstruturaComposicao.put(((Number) oSql_aux.FIELDGET("INSU1_COD")).longValue(), ((Number) oSql_aux.FIELDGET("INSU1_COD")).longValue());
					if (verificaRedundanciaOrcamento(((Number) oSql_aux.FIELDGET("ITOC1_COD_PAI")).longValue(), global, connection.conn)) {
						return true;
					}
				}

				oSql_aux.Skip(1L);
			}

			return false;

		} finally {

			if (connection != null) {
				connection.Close();
			}
		}
	}
	
	/* como usar recursivo 
	new FileTraversal() {
	    public void onFile( final File f ) {
	        System.out.println(f);
	    }
	}.traverse(new File("somedir"));
	*/
	
	public class FileTraversal {
		public final void traverse( final File f ) throws IOException {
        if (f.isDirectory()) {
           onDirectory(f);
           final File[] childs = f.listFiles();
           for (File child : childs ) {
               traverse(child);
           }
           return;
           }
           onFile(f);
        }

        public void onDirectory( final File d ) {
        }

        public void onFile( final File f ) {
        }
	}

}
