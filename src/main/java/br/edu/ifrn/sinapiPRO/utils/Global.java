package br.edu.ifrn.sinapiPRO.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Global {

	public String cSenha_Logado;
	
	public String cObrasRestritas = "";
	public String Si_Versao = "";
	public String Si_Nmusua = "";
	public String Si_Logotipo = "";
	public String cModuloDisponivel = "FINSUPESTCOMFATMAOORCGER";
	public String cMoedaNacional = "R$";
	public String cDataBase = "";
	public String cUsuario = "";
	public String cLogado = "";
	public String cSenha = "";
	public String cContasRestritas = "";
	public String cPeriodoAntecipacao = "";
	public String cCidade;
	public String cPath;
	public String cPathUpload;  
	public String cUsua1_Email;
	public String cUsua1_Smtp;
	public String cUsua1_Smtp_Login;
	public String cUsua1_Smtp_Senha;
	public String cUsua1_Smtp_Seguro;
	public String cUsua1_Smtp_SSL;
	public String cEmp1_Rec_Esp = "0";
	public String cEndBase = "";
	public String cCnpj_Logado = "";

	public String cNavegador = "";
	public String cNavegadorVersao = "";
	public boolean lMobile = false;
	public boolean lAndroid = false;
	public boolean lIOS = false;
	public String requestUrl = "";
	public boolean lPluginFlash = false;
	public int nPluginFlashVersao = 0;

	public Boolean lSepararDescricaoUnidade = false;
	public Boolean lSepararContaBancaria;
	public Boolean lSepararCentroCusto = false;
	public Boolean lGerandoExportacaoPorGrupo;
	public Boolean lAtivarNotificacao = true;
	public Boolean lUsuarioCliente = false;
	public Boolean lUsuarioCorretor = false;
	public Boolean lCliente = false;
	public Boolean lFornecedor = false;
	public Boolean lCorretor = false;
	public Boolean lImobiliaria = false;
	public Boolean lDllNfe = false;
	public Boolean lUsuarioRevenda = false;
	public Long nPess2_cod_logado = null; 

	public Long nCodigoUsuario;
	public Long nSequenciaContaContabil = 0l;
	public Long nNumeroBoxVenda = 35L;
	public Long nDecimalQuantidade = 3L;
	public Long nDecimalQuantidadeOrc = 3L;
	public Long nDecimalUnitario = 2L;
	public Long nDecimalUnitarioOrc = 2L;
	public Long nEmpresaSelecionada;
	public Long nEmpresa = 1l;

	public Long nUsua1_Por_Smtp;
	public Long nCodigoPessoa = 0L;

	public Double nAliquota_Irrf = 1.5;
	public Double nAliquota_Iss = 2.00;
	public Double nAliquota_Pis = 3.00;
	public Double nAliquota_Cofins = 0.65;
	public Double nAliquota_Csl = 1.00;
	public Double nAliquota_Ipi = 5.00;
	public Double nAliquota_Inss = 11.00;
	public Double nUltimoIndiceOrigem = 0d;
	public Double nUltimoIndiceDestino = 0d;
	public Double nUltimoValorOrigem = 0d;
	public Double nUltimoValorDestino = 0d;

	public Date dUltimaDataOrigem = new Date(0);
	public Date dUltimaDataDestino = new Date(0);
	public Date dControlaAssinatura = new Date(0);

	public Map<String, String[]> aRestricoes = new HashMap<String, String[]>();
	public Map<Long, Object[]> aConfiguracoes = new HashMap<Long, Object[]>();

	public List<Object[]> aClassificacao = new ArrayList<Object[]>();
	public List<Object[]> aContaContabil = new ArrayList<Object[]>();
	public List<Object[]> aContaIncluida = new ArrayList<Object[]>();
	public List<Object[]> aCentroCustosGrupo = new ArrayList<Object[]>();
	public List<String[]> aTipoControles = new ArrayList<String[]>();
	public List<Object[]> aTiposParcelas = new ArrayList<Object[]>();
	public Object[] aConfEmail;

	public boolean lAtividadeManha = false;
	public boolean lAtividadeTarde = false;
	public boolean lVerificarFtp = true;

	public boolean lMenuComandoSQL = false;

}
