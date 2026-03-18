
package br.edu.ifrn.sinapiPRO.utils.report;

 
public class ReportUtil  {
/*
	private static final int RELPDF = 1;
	private static final int RELHTML = 2;
	private static final int RELXLS = 3;
	private static final int RELRTF = 4;
	private static final int RELCSV = 5;

	private static void imprimirPdf(String name, Map<String, Object> params, String nomeArquivo, String event, String cRetEscolhido, String cBase, Object bean) throws Exception {
		imprimirRelatorio(name, "application/pdf", params, nomeArquivo, event, cRetEscolhido, cBase, bean);
	}

	private static void imprimirHtml(String name, Map<String, Object> params, String nomeArquivo, String  event, String cRetEscolhido, String cBase, Object bean) throws Exception {
		imprimirRelatorio(name, "text/html", params, nomeArquivo, event, cRetEscolhido, cBase, bean);
	}

	private static void imprimirExcel(String name, Map<String, Object> params, String nomeArquivo, String event, String cRetEscolhido, String cBase, Object bean) throws Exception {
		imprimirRelatorio(name, "application/xls", params, nomeArquivo, event, cRetEscolhido, cBase, bean);
	}

	private static void imprimirCsv(String name, Map<String, Object> params, String nomeArquivo, String event, String cRetEscolhido, String cBase, Object bean) throws Exception {
		imprimirRelatorio(name, "application/csv", params, nomeArquivo, event, cRetEscolhido, cBase, bean);
	}

	private static void imprimirRtf(String name, Map<String, Object> params, String nomeArquivo, String event, String cRetEscolhido, String cBase, Object bean) throws Exception {
		imprimirRelatorio(name, "application/rtf", params, nomeArquivo, event, cRetEscolhido, cBase, bean);
	}

	private static void imprimirRelatorio(String name, String type, Map<String, Object> params, String nomeArquivo, String event, String cRetEscolhido, String cBase, Object bean) throws Exception {

		int tipoRelatorio = RELPDF;
		if ("text/html".equals(type)) {
			tipoRelatorio = RELHTML;
		} else if ("application/xls".equals(type)) {
			tipoRelatorio = RELXLS;
		} else if ("application/rtf".equals(type)) {
			tipoRelatorio = RELRTF;
		} else if ("application/csv".equals(type)) {
			tipoRelatorio = RELCSV;
		}

		ImprimirReport imprimirReport = new ImprimirReport();
		imprimirReport.setTipoRelatorio(tipoRelatorio);
		imprimirReport.setName(name);
		imprimirReport.setNomeArquivo(nomeArquivo);
		imprimirReport.setParams(params);
		imprimirReport.setCBase(cBase);
		imprimirReport.setNumero(11L); //Lib.RetornaNumeroAleatorio());
		imprimirReport.setCRetEscolhido(event.cRetEscolhido);
		imprimirReport.setBean(bean);
		imprimirReport.setParamsReport(event.reportParam);


		File fRelatorio = new File(HibernateUtil.getRelatorio() + "\\" + ReportUtil.getCRelatorio(name, null));
		if (fRelatorio == null || !fRelatorio.isFile()) {
			imprimirReport.setExiste(false);
		} else {
			// Se o usuário cancelar um pedido de parâmetro, o processo de impressão é abortado.
			boolean lAbort = ReportUtil.verificaParametrosRelatorio(fRelatorio, params, event);
			if (lAbort) {
				return;
			}
			imprimirReport.setExiste(true);
		}

	}

	private static boolean verificaParametrosRelatorio(File fReport, Map<String, Object> params, String dataWindow) throws Exception {

		JRParameter[] paramametros = ((JasperReport) JRLoader.loadObject(fReport)).getParameters();
		for (JRParameter jrParameter : paramametros) {

			if (jrParameter.getName().contains("_Parameter")) {

				if (!params.containsKey(jrParameter.getName()) && jrParameter.isForPrompting()) {

					String cParametro = jrParameter.getPropertiesMap().getProperty("cParametro");
					String cValor = jrParameter.getPropertiesMap().getProperty("cValor");

					if (!Lib.Empty(cParametro) && !Lib.Empty(cValor)) {

						Object param = params.get(cParametro);
						if (param != null && !cValor.equals(param)) {
							continue;
						}

					}

					Object[] objParam = new Object[4];
					objParam[0] = jrParameter.getName();
					objParam[1] = jrParameter.getDefaultValueExpression().getText();
					objParam[2] = jrParameter.getValueClassName();
					objParam[3] = jrParameter.getDescription();

					_ParametroRelatorio parametroRelatorio = (_ParametroRelatorio) dataWindow.getInstance(_ParametroRelatorio.class.getSimpleName(), objParam, null, dataWindow);
					parametroRelatorio.Show();

					if (parametroRelatorio.lAbort) {
						return true;
					}

					if (!Lib.Empty(parametroRelatorio.valorRetorno)) {
						params.put(jrParameter.getName(), parametroRelatorio.valorRetorno);
					}

				}

			}

		}

		return false;

	}

	public StringBuffer getHTMLReport(String name, Map<String, Object> params, String cRetEscolhido, String cBase, String cNomeArquivo, Object bean) throws Exception {

		String cRelatorio = getCRelatorio(name, cRetEscolhido);
		JasperPrint jasperPrint = null;
		jasperPrint = gerarJasperPrint(params, cBase, cRelatorio, bean);

		if (jasperPrint != null) {

			String cLocalRelatorio = HibernateUtil.getRelatorio();

			File fRelatorio = new File(cLocalRelatorio);
			if (fRelatorio.isDirectory()) {

				File fRelatorioTemp = new File(cLocalRelatorio + "/Temp");
				if (!fRelatorioTemp.isDirectory()) {
					fRelatorioTemp.mkdir();
				}

				if (fRelatorioTemp.isDirectory()) {

					String cNomeDiretorio = "TEMP_RET_" + new Date().getTime();

					File fRelatorioNome = new File(fRelatorioTemp.getAbsolutePath() + "/" + cNomeDiretorio);
					if (fRelatorioNome.mkdir()) {

						String cExtensao = null;

						cExtensao = "HTML";

						if (cExtensao != null) {

							String cNomeArquivoTemp = fRelatorioNome.getAbsolutePath() + "/" + cNomeArquivo + "." + cExtensao;
							cNomeArquivoTemp = cNomeArquivoTemp.replace("\\", "/");
							try {

								// JasperExportManager.exportReportToHtmlFile(jasperPrint, cNomeArquivoTemp);

								StringBuffer sb = new StringBuffer();

								JRHtmlExporter exporter = new JRHtmlExporter();
								exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
								exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, sb);

								exporter.getParameter(JRExporterParameter.IGNORE_PAGE_MARGINS);

								Map<Object, Object> hashMap = new HashMap<Object, Object>();
								request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
								request.getSession().setAttribute("IMAGES_MAP", hashMap);
								exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, hashMap);
								exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, request.getRequestURL().toString().replace(request.getServletPath(), "/image?image="));

								exporter.exportReport();

								return sb;
							} catch (JRException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		return null;
	}

	private static String getCRelatorio(String name, String cRetEscolhido) {

		String cNomeRET = name;
		String cRelatorio = name;

		if (Lib.Empty(cRetEscolhido)) {

			// PRC00300_JAVA.jasper

			File file = new File(HibernateUtil.getRelatorio() + "\\" + cNomeRET.substring(0, 3) + "AL" + cNomeRET.substring(5));

			if (file.isFile()) {

				cRelatorio = cNomeRET.substring(0, 3) + "AL" + cNomeRET.substring(5);

			} else {

				File fileAux = new File(HibernateUtil.getRelatorio() + "\\" + cNomeRET);

				if (!fileAux.isFile()) {

					// d BO - Boleto bancário
					// d CHE - Cheque emitido
					// d CHT - Cheque transferencia

					if ("BO".equalsIgnoreCase(cNomeRET.substring(0, 2)) || "CHE".equalsIgnoreCase(cNomeRET.substring(0, 3)) || "CHT".equalsIgnoreCase(cNomeRET.substring(0, 3))) {

						if ("BO".equalsIgnoreCase(cNomeRET.substring(0, 2))) {

							if (new File(HibernateUtil.getRelatorio() + "\\" + cNomeRET.substring(1, 1) + "A" + cNomeRET.substring(5, 8) + "_JAVA.jasper").isFile()) {
								cRelatorio = HibernateUtil.getRelatorio() + "\\" + cNomeRET.substring(1, 1) + "A" + cNomeRET.substring(5, 8) + "_JAVA.jasper";
							} else {

								if (new File(HibernateUtil.getRelatorio() + "\\" + cNomeRET.substring(0, 3) + "AL999_JAVA.jasper").isFile()) {
									cRelatorio = HibernateUtil.getRelatorio() + "\\" + cNomeRET.substring(0, 3) + "AL999_JAVA.jasper";
								} else {
									cRelatorio = cNomeRET.substring(0, 5) + "999_JAVA.jasper";
								}

							}

						} else {

							cRelatorio = cNomeRET.substring(0, 5) + "999_JAVA.jasper";

						}

					}

				}

			}

		} else {

			cRelatorio = cRetEscolhido;

		}

		return cRelatorio;

	}

	private static JasperPrint gerarJasperPrint(Map<String, Object> params, String cBase, String cRelatorio, HttpSession session, Object bean, HttpServletRequest request) throws Exception {

		JasperPrint jasperPrint = null;

		Object objParameter3 = params.get("_Parameter3");
		if (objParameter3 != null) {
			String _Parameter3 = objParameter3.toString();
			_Parameter3 += " [" + cRelatorio.substring(0, 8) + "]";
			_Parameter3 += " [ NOME DO USUARIO]";
			_Parameter3 += " Hora Emissão: " + new SimpleDateFormat("HH:mm:ss").format(new Date());
			params.remove("_Parameter3");
			params.put("_Parameter3", _Parameter3);
		}


		try {

			if (bean == null) {

				if (Lib.Empty(cBase)) {
					connectionDB = new ConnectionDB(null, GlobalForm.getGlobal(session, request));
				} else {
					connectionDB = new ConnectionDB(cBase, HibernateUtil.usuario, HibernateUtil.senha);
				}

				if ("MS-SQL".equals(GlobalStrato.cSGBD)) {
					sqlStatement = connectionDB.conn.createStatement();
					sqlStatement.execute("SET DATEFORMAT ymd;");
				}

			}

			params.put("SUBREPORT_DIR", HibernateUtil.getRelatorio() + "/");
			params.put("REPORT_LOCALE", new Locale("pt", "BR"));

			String cKeyLogo = null;

			for (String cKey : params.keySet()) {

				Object obj = params.get(cKey);
				if (obj != null && obj instanceof String && obj.toString().contains("LOGO") && obj.toString().toUpperCase().contains(".BMP")) {

					File file = new File(obj.toString());
					if (!file.isFile()) {
						cKeyLogo = cKey;
						break;
					}

				}

			}

			 
			if (bean == null) {
				jasperPrint = JasperFillManager.fillReport(HibernateUtil.getRelatorio() + "/" + cRelatorio, params, connectionDB.conn);
			} else {

				List<Object> list = null;

				if (bean instanceof List) {
					list = (List<Object>) bean;
				} else {
					list = new ArrayList<Object>();
					list.add(bean);
				}

				JRDataSource jrds = new JRBeanArrayDataSource(list.toArray());
				jasperPrint = JasperFillManager. .fillReport(HibernateUtil.getRelatorio() + "/" + cRelatorio, params, jrds);

			}

	 
		return jasperPrint;
	}

	public void imprimirRelatorio(int tipoRelatorio, String name, Map<String, Object> params, String nomeArquivo, DataWindow event, String cRetEscolhido, String cBase) throws Exception {
		this.imprimirRelatorio(tipoRelatorio, name, params, nomeArquivo, event, cRetEscolhido, cBase, null);
	}

	public void imprimirRelatorio(int tipoRelatorio, String name, Map<String, Object> params, String nomeArquivo, DataWindow event, String cRetEscolhido, String cBase, Object bean) throws Exception {

		// Verifica se o relatório está homologado pela Esquematika.
		BloqueioRelatorio.verifyReportOpen(getCRelatorio(nomeArquivo + "_JAVA.jrxml", null));

		if (ReportUtil.RELPDF == tipoRelatorio) {
			ReportUtil.imprimirPdf(name, params, nomeArquivo, event, cRetEscolhido, cBase, bean);
		} else if (ReportUtil.RELHTML == tipoRelatorio) {
			ReportUtil.imprimirHtml(name, params, nomeArquivo, event, cRetEscolhido, cBase, bean);
		} else if (ReportUtil.RELXLS == tipoRelatorio) {
			ReportUtil.imprimirExcel(name, params, nomeArquivo, event, cRetEscolhido, cBase, bean);
		} else if (ReportUtil.RELCSV == tipoRelatorio) {
			ReportUtil.imprimirCsv(name, params, nomeArquivo, event, cRetEscolhido, cBase, bean);
		} else if (ReportUtil.RELRTF == tipoRelatorio) {
			ReportUtil.imprimirRtf(name, params, nomeArquivo, event, cRetEscolhido, cBase, bean);
		} else if (ReportUtil.RELEMAIL == tipoRelatorio) {

			String report = this.geraRelatorio(RELPDF, name, params, cRetEscolhido, cBase, nomeArquivo, bean);
			_EnviarRelatorioEmail oEnviarRelatorioEmail = (_EnviarRelatorioEmail) event.getInstance(_EnviarRelatorioEmail.class.getSimpleName(), report, null, this);
			oEnviarRelatorioEmail.Show();
		}

	}

	public String geraRelatorio(int tipoRelatorio, 
			                    String name, Map<String, Object> params, 
			                    String cRetEscolhido, 
			                    String cBase, 
			                    String cNomeArquivo, Object bean) throws Exception {


		String cRelatorio = getCRelatorio(name, cRetEscolhido);
		JasperPrint jasperPrint = null;

		HttpSession session = null;
		HttpServletRequest request = null;
		if (FacesContext.getCurrentInstance() != null && SessionStrato.getCurrentInstance() == null) {
			session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		} else {
			session = ((FrSessionStrato) SessionStrato.getCurrentInstance()).getSession();
			request = ((FrSessionStrato) SessionStrato.getCurrentInstance()).getRequest();
		}

		jasperPrint = gerarJasperPrint(params, cBase, cRelatorio, session, bean, request);

		if (jasperPrint != null) {

			String cLocalRelatorio = HibernateUtil.getRelatorio();

			File fRelatorio = new File(cLocalRelatorio);
			if (fRelatorio.isDirectory()) {

				File fRelatorioTemp = new File(cLocalRelatorio + "/Temp");
				if (!fRelatorioTemp.isDirectory()) {
					fRelatorioTemp.mkdir();
				}

				if (fRelatorioTemp.isDirectory()) {

					String cNomeDiretorio = "TEMP_RET_" + new Date().getTime();

					File fRelatorioNome = new File(fRelatorioTemp.getAbsolutePath() + "/" + cNomeDiretorio);
					if (fRelatorioNome.mkdir()) {

						String cExtensao = null;

						if (ReportUtil.RELPDF == tipoRelatorio) {
							cExtensao = "PDF";
						} else if (ReportUtil.RELHTML == tipoRelatorio) {
							cExtensao = "HTML";
						} else if (ReportUtil.RELXLS == tipoRelatorio) {
							cExtensao = "XLS";
						} else if (ReportUtil.RELRTF == tipoRelatorio) {
							cExtensao = "RTF";
						} else if (ReportUtil.RELCSV == tipoRelatorio) {
							cExtensao = "TXT";
						}

						if (cExtensao != null) {

							String cNomeArquivoTemp = fRelatorioNome.getAbsolutePath() + "/" + cNomeArquivo + "." + cExtensao;
							cNomeArquivoTemp = cNomeArquivoTemp.replace("\\", "/");
							try {

								if (ReportUtil.RELPDF == tipoRelatorio) {
									JasperExportManager.exportReportToPdfFile(jasperPrint, cNomeArquivoTemp);
								} else if (ReportUtil.RELHTML == tipoRelatorio) {
									JasperExportManager.exportReportToHtmlFile(jasperPrint, cNomeArquivoTemp);
								} else if (ReportUtil.RELXLS == tipoRelatorio) {
									JRXlsExporter exporterXLS = new JRXlsExporter();
									exporterXLS.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
									exporterXLS.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, cNomeArquivoTemp);
									exporterXLS.setParameter(JExcelApiExporterParameter.IS_FONT_SIZE_FIX_ENABLED, true);
									exporterXLS.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
									exporterXLS.setParameter(JExcelApiExporterParameter.MAXIMUM_ROWS_PER_SHEET, 32767);
									exporterXLS.exportReport();
								} else if (ReportUtil.RELRTF == tipoRelatorio) {
									JRRtfExporter exporterRTF = new JRRtfExporter();
									exporterRTF.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
									exporterRTF.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, cNomeArquivoTemp);
									exporterRTF.exportReport();
								} else if (ReportUtil.RELCSV == tipoRelatorio) {
									JRCsvExporter exporterCSV = new JRCsvExporter();
									exporterCSV.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
									exporterCSV.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, cNomeArquivoTemp);
									exporterCSV.exportReport();
								}

								return cNomeArquivoTemp;

							} catch (JRException e) {
								e.printStackTrace();
							}

						}

					}

				}

			}

		}

		return null;
	}

	public static void impressao(String name, String cRetEscolhido, Map<String, Object> params, String cBase, int tipoRelatorio, String nomeArquivo, HttpServletResponse response, HttpServletRequest request, HttpSession session, Object bean, ReportParam paramsReport) {

		String type = "";
		if (ReportUtil.RELPDF == tipoRelatorio) {
			type = "application/pdf";
		} else if (ReportUtil.RELHTML == tipoRelatorio) {
			type = "text/html";
		} else if (ReportUtil.RELXLS == tipoRelatorio) {
			type = "application/xls";
		} else if (ReportUtil.RELRTF == tipoRelatorio) {
			type = "application/rtf";
		} else if (ReportUtil.RELCSV == tipoRelatorio) {
			type = "application/csv";
		}

		String cRelatorio = getCRelatorio(name, cRetEscolhido);

		JasperPrint jasperPrint = null;
		Exception eErro = null;
		try {

			jasperPrint = gerarJasperPrint(params, cBase, cRelatorio, session, bean, request);

		} catch (RuntimeException e) {
			eErro = e;
		} catch (Exception e) {

			if (e.getCause() instanceof SQLException) {
				try {
					JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(HibernateUtil.getRelatorio() + "/" + cRelatorio);
					String sql = jasperReport.getQuery().getText();

					for (String param : params.keySet()) {
						String paramCompleto = "$P!{" + param + "}";
						if (sql.contains(paramCompleto)) {
							Object obj = params.get(param);
							if (obj == null) {
								obj = "";
							}
							sql = sql.replace(paramCompleto, obj.toString());
						}
					}

					try {
						response.getWriter().println("<html><body>Erro ao executar relatório:<pre>" + sql + "</pre></body></html>");
					} catch (RuntimeException e1) {
						eErro = e1;
					} catch (Exception e1) {
						eErro = e1;
					}

					return;

				} catch (JRException e1) {
					eErro = e1;
				}
			} else {
				eErro = e;
			}
		} finally {

			if (eErro != null) {

				try {
					response.getWriter().println("<html><body>Erro ao executar relatório.<br>Consulte o administrador de sistemas.<br><br>" + eErro.toString() + "</body></html>");
				} catch (RuntimeException e1) {
					throw e1;
				} catch (Exception e1) {
					throw new FacesException(e1);
				}

				eErro.printStackTrace();

				return;

			}

		}

		JRExporter exporter = null;
		try {
			response.setContentType(type);
			ServletOutputStream outputStream = response.getOutputStream();
			if ("application/pdf".equals(type)) {
				exporter = new JRPdfExporter();
				// response.addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo + ".PDF");
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
			} else if ("text/html".equals(type)) {
				exporter = new JRHtmlExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, response.getWriter());

				exporter.getParameter(JRExporterParameter.IGNORE_PAGE_MARGINS);

				Map<Object, Object> hashMap = new HashMap<Object, Object>();
				request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
				request.getSession().setAttribute("IMAGES_MAP", hashMap);
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, hashMap);
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, request.getRequestURL().toString().replace(request.getServletPath(), "/image?image="));

			} else if ("application/xls".equals(type)) {
				exporter = new JRXlsExporter();

				exporter.setParameter(JExcelApiExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, outputStream);
				exporter.setParameter(JExcelApiExporterParameter.IS_FONT_SIZE_FIX_ENABLED, true);
				exporter.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				exporter.setParameter(JExcelApiExporterParameter.MAXIMUM_ROWS_PER_SHEET, 32767);

				response.addHeader("Content-Type", "application/force-download");
				response.addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo + ".xls");

			} else if ("application/csv".equals(type)) {
				exporter = new JRCsvExporter();

				exporter.setParameter(JExcelApiExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, outputStream);

				response.addHeader("Content-Type", "application/force-download");
				response.addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo + ".txt");

			} else if ("application/rtf".equals(type)) {
				exporter = new JRRtfExporter();

				exporter.setParameter(JExcelApiExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, outputStream);

				response.addHeader("Content-Type", "application/force-download");
				response.addHeader("Content-Disposition", "attachment; filename=" + nomeArquivo + ".rtf");

			}

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new FacesException(e);
		}

		try {
			exporter.exportReport();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new FacesException(e);
		}

	}

	public static void compilarRelatorios() {

		long ini = System.currentTimeMillis();

		System.out.println("Compilando relatórios");

		String cRelatorio = HibernateUtil.getRelatorio();
		File fileRelatorio = new File(cRelatorio);
		if (fileRelatorio.isDirectory()) {

			String[] lista = fileRelatorio.list(new FilenameFilter() {

				@Override
				public boolean accept(File file, String nome) {

					if (nome.contains("jrxml") || nome.contains("JRXML")) {
						return true;
					}

					return false;
				}
			});

			for (String nomeOrigem : lista) {
				try {
					String nomeDestino = nomeOrigem.replace(".jrxml", ".jasper").replace(".JRXML", ".jasper");

					System.out.println(nomeOrigem);

					nomeOrigem = cRelatorio + "\\" + nomeOrigem;
					nomeDestino = cRelatorio + "\\" + nomeDestino;

					JasperCompileManager.compileReportToFile(nomeOrigem, nomeDestino);
				} catch (JRException e) {
					e.printStackTrace();
				}
			}

		}

		long fim = System.currentTimeMillis();

		System.out.println("Término compilação de relatórios (" + (fim - ini) + ")");

	}

	public static void main(String[] args) {
		HibernateUtil.setRelatorio("C:\\eclipse\\workspacePerformance\\strato-report\\RELATO");
		compilarRelatorios();
	}

	public static void maina(String[] args) throws JRException {

		File fileDir = new File("C:\\eclipse\\workspaceAux\\strato-report\\RELATO");
		File[] list = fileDir.listFiles();

		int i = 0;

		for (File file : list) {

			if (file.isFile()) {

				String cName = file.getName();
				String cExt = cName;
				if (cExt.length() > 6) {

					cExt = cExt.substring(cExt.length() - 5);
					if ("jrxml".equalsIgnoreCase(cExt)) {
						System.out.println(file.getAbsolutePath() + " - " + file.getAbsolutePath().replace(cExt, "jasper"));
						JasperCompileManager.compileReportToFile(file.getAbsolutePath(), file.getAbsolutePath().replace(cExt, "jasper"));
						i++;
					}

				}

			}

		}

		System.out.println(i);

	}
*/
}
	 
