package br.edu.ifrn.sinapiPRO.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.edu.ifrn.sinapiPRO.model.BaseKey;
import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.Classe;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.ItemBasePreco;
import br.edu.ifrn.sinapiPRO.model.ItemComposicao;
import br.edu.ifrn.sinapiPRO.model.TipoComposicao;
import br.edu.ifrn.sinapiPRO.repository.BasePrecos;
import br.edu.ifrn.sinapiPRO.repository.Classes;
import br.edu.ifrn.sinapiPRO.repository.Insumos;
import br.edu.ifrn.sinapiPRO.repository.ItemBasePrecoRepository;
import br.edu.ifrn.sinapiPRO.repository.TipoComposicaoRepository;
import br.edu.ifrn.sinapiPRO.service.CadastroComposicaoService;
import br.edu.ifrn.sinapiPRO.service.CadastroInsumoService;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;
import br.edu.ifrn.sinapiPRO.utils.Lib;

@Controller     
@RequestMapping(path="/sinapi")  
public class SinapiController {
	
	@Autowired  
	private Insumos insumoRepository;
	
	@Autowired
	private CadastroInsumoService cadastroInsumoService;
	
	@Autowired
	private BasePrecos basePrecoRepository;
	
	@Autowired
	private ItemBasePrecoRepository itemBasePrecoRepository;
				
	@Autowired  
	private CadastroComposicaoService composicaoService;
	
	@Autowired  
	private TipoComposicaoRepository tipoComposicaoRepository;
	
	@Autowired  
	private Classes classeRepository;
	
	private Double valueNumeric; 
	private String valueString;
	private List<ItemComposicao> itens = new ArrayList<>();

	// Codigo = Base da Precos
	@GetMapping(path="/insumo/{codigo}")  
	public @ResponseBody String importaInsumos (@PathVariable Long codigo) {
		
		//TODO: Fazer aqui o download do arquivo 
		//TODO: Checar se o arquivo existe
	 
	
		if(!basePrecoRepository.existsById(codigo)) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Precos");
        }
		
		BasePreco basePreco = basePrecoRepository.findById(codigo).get();
		BaseKey baseKey = new BaseKey(); 
		
		String uf = basePreco.getEstado().getSigla();
	
		Date df = Lib.asDate(basePreco.getDataReferencia());
		
		String ano = Integer.toString(Lib.Year(df)); 
		String mes = Lib.StrZero(Lib.Month(df), 2);
		
		// String oneracao = basePreco.getDesonerado().getDescricao(); 
		String oneracao =	"Desonerado";	 
		String fileName = "";
		// /home/sergio/sinapi-download/RN/2018/01/Desonerado/SINAPI_Preco_Ref_Insumos_RN_201801_Desonerado.xls
		// /home/sergio/sinapi-download/RN/2018/01/Desonerado/SINAPI_Preco_Ref_Insumos_RN_012018_Desonerado.XLS
		if (mes.equals("01")) {
			fileName = "/home/sergio/sinapi-download/"+uf+"/"+ano+"/"+mes+"/"+oneracao+"/SINAPI_Preco_Ref_Insumos_"+uf+"_"+ano+mes+"_"+oneracao+".xls";
		} else {
			fileName = "/home/sergio/sinapi-download/"+uf+"/"+ano+"/"+mes+"/"+oneracao+"/SINAPI_Preco_Ref_Insumos_"+uf+"_"+mes+ano+"_"+oneracao+".XLS";
		}	
		Double n = null; 
		Long   l = 0L;
		String s = null;
		
		System.out.println(fileName);
		   
		try (HSSFWorkbook wb = SinapiController.readFile(fileName)) {
			
			System.out.println("Data dump:\n");
			
			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				
				HSSFSheet sheet = wb.getSheetAt(k);
				int rows = sheet.getPhysicalNumberOfRows();
				// System.out.println("Planilha " + k + " \"" + wb.getSheetName(k) + "\" tem " + rows + " linha(s).");
				
				for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					if (row == null)  {
						continue;
					}
					if ( row.getPhysicalNumberOfCells() < 5 ) {
						continue;
					}
					
					if (row.getRowNum() ==6) { 
						continue; 
					}
					
					// System.out.println("\nROW " + row.getRowNum() + " has " + row.getPhysicalNumberOfCells() + " cell(s).");
					
					Insumo insumo = new Insumo(); 
					
					for (int c = 0; c < row.getLastCellNum(); c++) {
						HSSFCell cell = row.getCell(c);
						String value = null; 
						s=null;
						n=null; 
				 
						if (cell != null) {
							switch (cell.getCellTypeEnum()) {
								case FORMULA:
									value = "FORMULA value=" + cell.getCellFormula();
									break;
									
								case NUMERIC:
									value = "NUMERIC value=" + cell.getNumericCellValue();
									valueNumeric = cell.getNumericCellValue();
									break;

								case STRING:
									value = "STRING value=" + cell.getStringCellValue();
									valueString = cell.getStringCellValue(); 
									break;

								case BLANK:
									value = "<BLANK>";
									break;

								case BOOLEAN:
									value = "BOOLEAN value-" + cell.getBooleanCellValue();
									break;
								case ERROR:
									value = "ERROR value=" + cell.getErrorCellValue();
									break;

								default:
									value = "UNKNOWN value of type " + cell.getCellTypeEnum();
							}
							
							// System.out.println("CELL col=" + cell.getColumnIndex() + " VALUE="+ value);
							
							
							switch (cell.getColumnIndex()) {
								case 0:
									insumo.setCodigoInsumo( Lib.Round(valueNumeric,0).longValue() ); 
									break;
								case 1:
									insumo.setDescricao(valueString.trim()); 
									break;
								case 2:
									insumo.setUnidade(valueString.trim());
									break;
								case 3:
									// insumo.setOrigem(valueString.trim());
									break;
								case 4:
									insumo.setPrecoPadrao(StrToBig(valueString, 2)); 
									break;
								default:
									insumo = null; 
							}
						}
					} // end for
					
					if (insumo==null) {
						continue;
					}
					
					Optional<Insumo> insumoExistente = insumoRepository.findByCodigoInsumo(insumo.getCodigoInsumo());
					
					if(!insumoExistente.isPresent()){
						// Novo Insumo
						
						insumo.setBasePreco(basePreco);
						if (insumo.getCodigoInsumo() !=null &&
							insumo.getDescricao()    !=null &&
							insumo.getUnidade()      !=null &&
							insumo.getPrecoPadrao() !=null) {
							
							cadastroInsumoService.salvar(insumo);
							// insumoRepository.saveAndFlush(insumo);
							// System.out.println("Novo Insumo "+insumo.getDescricao());
						} else {
							// System.out.println("insumo NOVO NULLO");
							insumo=null;
							continue;
						}
					} else { 
						// Atualiza Insumo
						if (insumo.getCodigoInsumo() !=null &&
							insumo.getDescricao()    !=null &&
							insumo.getUnidade()      !=null &&
							insumo.getPrecoPadrao()!=null) {
							
							Insumo insumoEdita = insumoExistente.get(); 
							
							insumoEdita.setUnidade(insumo.getUnidade());
							insumoEdita.setDescricao(insumo.getDescricao());
							insumoEdita.setPrecoPadrao(insumo.getPrecoPadrao());
							insumoEdita.setBasePreco(basePreco);
							
							cadastroInsumoService.salvar(insumoEdita);
							// insumoRepository.saveAndFlush(insumo);
							System.out.println("Atualiza Insumo "+insumoEdita.getCodigo());
						} else {
							System.out.println("insumo ATUALIZA NULLO");
							insumo=null;
							continue;
						}
							
					}
					
					// Salva preco  
					
					baseKey = new BaseKey(); 
					baseKey.setBasePrecoID(basePreco.getCodigo());
					baseKey.setInsumoID(insumo.getCodigoInsumo());
					
					Optional<ItemBasePreco> item = itemBasePrecoRepository.findById(baseKey);
					
					if(!item.isPresent()){
						
						ItemBasePreco novoItem = new ItemBasePreco();
						novoItem.setBaseKey(baseKey);
						novoItem.setPreco(insumo.getPrecoPadrao());
						novoItem.setBasePreco(basePreco);
						novoItem.setAnoMes(ano+"/"+mes);
						itemBasePrecoRepository.saveAndFlush(novoItem);
												
					} else { 
						
						ItemBasePreco editaItem = item.get();
						editaItem.setPreco(insumo.getPrecoPadrao()); 
					 
						editaItem.setAnoMes(ano+"/"+mes);
						itemBasePrecoRepository.saveAndFlush(editaItem);
					
					}
					
					baseKey = null; 
					insumo = null; 
					
					
				} // for row
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
	 				 
		return "Insumos Importados com Sucesso!";
	}
	
	
	/*  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_01a062018.zip (6 arquivos .zip)
	 * 
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_072018_NaoDesonerado.zip
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_082018_NaoDesonerado.zip
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_072018_Desonerado.zip
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_082018_Desonerado.zip
	 *  
	 */
	
	/**
	 * 
	 * @param codigo - codigo da base de preços 
	 * @return
	 */
	@GetMapping(path="/composicao/{codigo}")
	public @ResponseBody String importaComposicoes(@PathVariable Long codigo) {
		
		// TODO: Importar o Arquivo zip e descompactar no diretorio  da aplicação 

		BasePreco basePreco = basePrecoRepository.findById(codigo).get();
		
		if(!basePrecoRepository.existsById(codigo)) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Precos");
        }
				
				
		String uf = basePreco.getEstado().getSigla();
	
		Date df = Lib.asDate(basePreco.getDataReferencia());
		
		String ano = Integer.toString(Lib.Year(df)); 
		String mes = Lib.StrZero(Lib.Month(df), 2);
		
		// String oneracao = basePreco.getDesonerado().getDescricao(); 
		String oneracao = "Desonerado";
	
		String fileName = "/home/sergio/sinapi-download/"+uf+"/"+ano+"/"+mes+"/Desonerado/SINAPI_Custo_Ref_Composicoes_Analitico_"+uf+"_"+mes+ano+"_"+oneracao+".XLS";
		if (mes.equals("01")) {
			fileName = "/home/sergio/sinapi-download/"+uf+"/"+ano+"/"+mes+"/Desonerado/SINAPI_Custo_Ref_Composicoes_Analitico_"+uf+"_"+ano+mes+"_"+oneracao+".xls";
		}
		
		
		Long L=0L;
		String s = null;
		Double n = null; 
		Boolean comeco = true; 
		
		String campo0Atual        = null;
		String campo1Atual        = null;
		String campo2Atual        = null;
		Double campo3Atual        = null;
		String campo6Atual        = null;
		String campo18Atual       = null; 
		
		String siglaAnterior      = "ASTU";
		String siglaAtual         = "ASTU"; 
		String composicaoAnterior = "97141";
		int i = 0; 
		Object o;
		
		Composicao composicao = new Composicao(); 
		ItemComposicao itemComposicao = new ItemComposicao(); 
		Classe classe = new Classe();
		TipoComposicao tipoComposicao = new TipoComposicao(); 
		List<ItemComposicao> itens = new ArrayList<>();
		 
		
		try (HSSFWorkbook wb = SinapiController.readFile(fileName)) {
			
			System.out.println("Data dump:\n");
			
			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				
				HSSFSheet sheet = wb.getSheetAt(k);
				int rows = sheet.getPhysicalNumberOfRows();
				System.out.println("Planilha " + k + " \"" + wb.getSheetName(k) + "\" tem " + rows + " linha(s).");
				
				for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					if (row == null)  {
						continue;
					}
					
					if (row.getRowNum() < 8) { 
						continue; 
					}
					
					System.out.println("\nROW " + row.getRowNum() + " has " + row.getPhysicalNumberOfCells() + " cell(s).");
					
					
					for (int c = 0; c < row.getLastCellNum(); c++) {
						HSSFCell cell = row.getCell(c);
						String value;
						if (cell != null) {
							s=null;
							n=null; 
							switch (cell.getCellTypeEnum()) {
								case FORMULA:
									value = "FORMULA value=" + cell.getCellFormula();
									continue;
									
								case BLANK:
									value = "<BLANK>";
									continue;
	
								case BOOLEAN:
									value = "BOOLEAN value-" + cell.getBooleanCellValue();
									continue;
									
								case ERROR:
									value = "ERROR value=" + cell.getErrorCellValue();
									continue;
									
								case NUMERIC:
									value = "NUMERIC value=" + cell.getNumericCellValue();
									n = cell.getNumericCellValue();
									break;

								case STRING:
									value = "STRING value=" + cell.getStringCellValue();
									s = cell.getStringCellValue(); 
									break;

								default:
									value = "UNKNOWN value of type " + cell.getCellTypeEnum();
									continue;
							}
							
							System.out.println("CELL col=" + cell.getColumnIndex() + " VALUE="+ value);
							
							 
							s=Lib.RTrim(s);
							s=Lib.LTrim(s);
															
							 
							switch (cell.getColumnIndex()) {
							
								case 0: // Nome da Clase
									campo0Atual = s; 
									break;
									
								case 1: // Sigla da Classe
									campo1Atual = s; 
									break;
									
								case 2: // Nome do Tipo 
									
									campo2Atual = s;
									break;
									
								case 3: // codigo do Tipo
									
									campo3Atual = n;
									break;
									
								case 4: // pula codigo agrupado 

									break;
									
								case 5: // pula descricao do agrupador 
									 
									break;	
									
								case 6: // Código da composição 
									campo6Atual = s;
									
									if (comeco) { /* Primeira vez */ 
										comeco=false;
										composicaoAnterior=campo6Atual;
										
										o=campo6Atual;
										L=Long.parseLong(  Lib.StrTran(o, "/", "0")  );
										composicao.setCodigo(L);
										
										System.out.println("primeira vez. compo ="+composicao.getCodigo());
										//
										
										//if( !campo6Atual.equals(L.toString() ))  {
								        //    throw new ResourceNotFoundException("Erro conversao"+campo6Atual+" "+L);
								        // }
										
										Optional<Classe> classeExistente = classeRepository.findBySiglaIgnoreCase(campo1Atual);
										
										if (classeExistente.isPresent()) {
											composicao.setClasse(classeExistente.get()); 
										} else { 
											classe =new Classe();
											classe.setNome(campo0Atual);
											classe.setSigla(campo1Atual);
											
											classeRepository.save(classe);
											composicao.setClasse(classeRepository.findBySiglaIgnoreCase(campo1Atual).get());
										}
										
										Optional<TipoComposicao> tipoExistente = tipoComposicaoRepository.findById( campo3Atual.longValue() );
										
										if (tipoExistente.isPresent()) {
											composicao.setTipoComposicao(tipoExistente.get()); 
										} else {
											tipoComposicao =new TipoComposicao();
											tipoComposicao.setCodigo(campo3Atual.longValue() ); 
											tipoComposicao.setNome(campo2Atual);
											
											tipoComposicaoRepository.save(tipoComposicao); 
											
											TipoComposicao novoTipo = tipoComposicaoRepository.findById( campo3Atual.longValue() ).get();
											composicao.setTipoComposicao(novoTipo);
										}
										
									}
									
																	
									if (composicaoAnterior.compareTo(campo6Atual) != 0) {    // 0 são idênticas   diferentes -1  +1
										
										composicaoAnterior = campo6Atual;
										
										System.out.println("Quebra ="+campo6Atual+" Anterior="+composicaoAnterior+" DENTRO="+composicaoAnterior.compareTo(campo6Atual));
										
										composicao.setBasePreco(basePreco);
										composicao.setDataCriacao(Lib.asLocalDateTime(Lib.Today()));
										composicao.adicionarItens(itens);
										
										System.out.println("salva composicao="+composicao.getCodigo());
										for (ItemComposicao t: itens) {
											System.out.println("Item codigo="+t.getCodigoItem()); 
										}
										
										composicaoService.salvar(composicao);
										
										itens.clear();
										
										composicao = new Composicao(); 
									    itemComposicao = new ItemComposicao();
									    
									    // Nova composicao
									    
									    o=campo6Atual;
										L=Long.parseLong(  Lib.StrTran(o, "/", "0")  );
										
										composicao.setCodigo(L);
										
										//if( !campo6Atual.equals(L.toString() ))  {
								        //    throw new ResourceNotFoundException("Erro conversao"+campo6Atual+" "+L);
								        //}
																			
											
										Optional<Classe> classeExistente = classeRepository.findBySiglaIgnoreCase(campo1Atual);
										
										if (classeExistente.isPresent()) {
											composicao.setClasse(classeExistente.get()); 
										} else { 
											classe =new Classe();
											classe.setNome(campo0Atual);
											classe.setSigla(campo1Atual);
											
											classeRepository.save(classe);
											composicao.setClasse(classeRepository.findBySiglaIgnoreCase(campo1Atual).get());
										}
										
										Optional<TipoComposicao> tipoExistente = tipoComposicaoRepository.findById( campo3Atual.longValue() );
										
										if (tipoExistente.isPresent()) {
											composicao.setTipoComposicao(tipoExistente.get()); 
										} else {
											tipoComposicao =new TipoComposicao();
											tipoComposicao.setCodigo(campo3Atual.longValue() ); 
											tipoComposicao.setNome(campo2Atual);
											
											tipoComposicaoRepository.save(tipoComposicao); 
											
											TipoComposicao novoTipo = tipoComposicaoRepository.findById( campo3Atual.longValue() ).get();
											composicao.setTipoComposicao(novoTipo);
										}
									    
									}
									break;	
									
								case 7:
									composicao.setDescricao(s); 
									break;	
									
								case 8:
									composicao.setUnidade(s); 
									break;
									
								case 9: // pula Origem do Preco
									break;	
									
								case 10: // Valor da Composição
									if (!Lib.Empty(s)) {
										composicao.setCustoTotal(StrToBig(s, 2)); 
									}	
									break;	
									
								case 11: // tipo do item - {COMPOSICAO INSUMO}
									 
									if (!Lib.Empty(s)) {
										itemComposicao = new ItemComposicao(); 
										itemComposicao.setTipo(s); 
									}
									break;	
									
								case 12: // codigo do item
									
									if (!Lib.Empty(s)) {
										o=s;
										L=Long.parseLong(  Lib.StrTran(o, "/", "0")  );
										
										itemComposicao.setCodigoItem(L);
									}
									
									break;	
									
								case 13: // Descricao do Item 
									if (!Lib.Empty(s)) {
										itemComposicao.setDescricao(s); 
									}
									break;	
									
								case 14: // Unidade do Item
									if (!Lib.Empty(s)) {
										itemComposicao.setUnidade(s);
									}
									break;	
									
								case 15: // pula origem do preco 
									break;	
									
								case 16: // coeficiente do item
									if (!Lib.Empty(s)) {
										itemComposicao.setCoeficiente(StrToBig(s, 7)); 
									}	
									break;	
									
								case 17: // preco unitario do item 
									if (!Lib.Empty(s)) {
										itemComposicao.setPrecoUnitario(StrToBig(s, 2)); 
									}
									break;	
									
								case 18: // Custo Total do Item 
									campo18Atual = s;
									if (!Lib.Empty(s)) {
										itemComposicao.setCustoTotal(StrToBig(s, 2)); 
									
									}
									break;	
									
								case 19: // Custo Mão de Obra 
									if (!Lib.Empty(s)) {
										composicao.setCustoMaoObra(StrToBig(s, 2)); 
									}	
									break;	
									
								case 20: // Percentual da Mão de Obra 
									if (!Lib.Empty(s)) {
										composicao.setPercMaoObra(StrToBig(s, 7)); 
									}	
									break;	
									
								case 21:
									if (!Lib.Empty(s)) {
										composicao.setCustoMaterial(StrToBig(s, 2)); 
									}	
									break;	
									
								case 22:
									if (!Lib.Empty(s)) {
										composicao.setPercMaterial(StrToBig(s, 7)); 
									}	
									break;	
									
								case 23:
									if (!Lib.Empty(s)) {
										composicao.setCustoEquipamento(StrToBig(s, 2));
									}	
									break;	
									
								case 24:
									
									if (!Lib.Empty(s)) {
										composicao.setPercEquipamento(StrToBig(s, 7));
									}	
																	
									
									
									break;
									
								case 29:
									if (!Lib.Empty(campo18Atual)) {
										
										itemComposicao.setBasePreco(basePreco);
										itens.add(itemComposicao); 
										 
										itemComposicao = new ItemComposicao(); 
									}
									
									campo0Atual = "";
									campo1Atual = ""; 
									campo2Atual = ""; 
									campo3Atual = 0D;
									campo6Atual = "";
									campo18Atual = "";
									
									break; 
							
							}
						}
					}
				} // for row
			} // for sheet 
		} catch (Exception erro) {
				erro.printStackTrace();
		}
		
		return "Composicoes Sinapi importadas com sucesso!";
	}
	 
	private static HSSFWorkbook readFile(String filename) throws IOException {
		try (FileInputStream fis = new FileInputStream(filename)) {
			return new HSSFWorkbook(fis);      
		}
	}
	
	public static String strRemove(String str){     
        StringBuilder sb=new StringBuilder();
        
        for(String st: str.split(" ")){
 
            if (!st.equals(""))         
               sb.append(st+" ");        
        } 
        return new String(sb.toString());
    } 
	
    public static BigDecimal StrToBig (String numero, Integer decimais) {
        String casasDecimais = "";
        String num = numero;
        DecimalFormat df = null;
        try {
            if (decimais > 0) {
                for (int i = 0; i < decimais; i++) {
                    casasDecimais = casasDecimais.concat("0");
                }
                if (num.equals("")) {
                    num = "0.".concat(casasDecimais);
                }
                df = new DecimalFormat("#,##0.".concat(casasDecimais), new DecimalFormatSymbols(new Locale("pt", "BR")));
                df.setParseBigDecimal(true); // aqui esta o pulo do gato
                df.setRoundingMode(RoundingMode.DOWN);
                return (BigDecimal) df.parse(num); // deve voltar o BigDecimal "1234.56"
            } else {
                if (num.equals("")) {
                    num = "0";
                }
                df = new DecimalFormat("###########");
                df.setParseBigDecimal(true);
                df.setRoundingMode(RoundingMode.DOWN);
                return new BigDecimal(((BigDecimal) df.parse(num)).intValue());
            }
        } catch (ParseException ex) {
            // Logger.getLogger(Utilitarios.class.getName()).log(Level.SEVERE, null, ex);
            return new BigDecimal("0");
        }
    }
	
}

/*  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_01a062018.zip
 * 
 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_072018_NaoDesonerado.zip
 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_082018_NaoDesonerado.zip
 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_072018_Desonerado.zip
 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_082018_Desonerado.zip
 *  
 */


