package br.edu.ifrn.sinapiPRO.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
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

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Classe;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.ItemComposicao;
import br.edu.ifrn.sinapiPRO.repository.Classes;
import br.edu.ifrn.sinapiPRO.repository.Composicoes;
import br.edu.ifrn.sinapiPRO.repository.Estados;
import br.edu.ifrn.sinapiPRO.repository.Insumos;

@Controller     
@RequestMapping(path="/sinapi")  
public class SinapiController {
	
	@Autowired  
	private Insumos insumoRepository;
	
	@Autowired  
	private Composicoes composicaoRepository;
	
	@Autowired  
	private Estados estadoRepository;
	
	@Autowired  
	private Classes classeRepository;
	
	private Double valueNumeric; 
	private String valueString;
	
	private List<ItemComposicao> itens = new ArrayList<>();
	
	
	@GetMapping(path="/insumo/{estado}/{ano}/{mes}/{oneracao}")  
	public @ResponseBody String importaInsumos (@PathVariable String estado, @PathVariable String ano, @PathVariable String mes, @PathVariable String oneracao) {
		
		//TODO: Fazer aqui o download do arquivo 
		//TODO: Checar se o arquivo existe
		 
		String fileName = "/home/sergio/sinapi-download/"+estado+"/"+ano+"/"+mes+"/"+oneracao+"/SINAPI_Preco_Ref_Insumos_"+estado.trim()+"_"+ano+mes.trim()+"_Desonerado.xls";
		Double n = null; 
		String s = null;
		
		System.out.println(fileName);
		System.out.println("<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		 
		   
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
					if ( row.getPhysicalNumberOfCells() < 5 ) {
						continue;
					}
					
					if (row.getRowNum() ==6) { 
						continue; 
					}
					
					System.out.println("\nROW " + row.getRowNum() + " has " + row.getPhysicalNumberOfCells() + " cell(s).");
					
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
									// insumo.setCodigo((int) Math.floor(valueNumeric));  s.replace("/", "")
									//s = String.valueOf(Math.floor(valueNumeric)); 
									//s = s.replace(".0","").trim(); 
									s = String.format ("%.0f", valueNumeric);
									insumo.setSku(s); 
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
									insumo.setPrecoGenerico(StrToBig(valueString, 2)); 
									break;
								default:
									insumo = null; 
							
							}
						}
					}
					System.out.println(insumo.toString());	
					
					
					Optional<Estado> e = estadoRepository.findBySiglaIgnoreCase(estado);
					insumo.setEstado(e.get());
					
					insumo.setAnoMes(ano+mes);
					insumo.setBase(BaseInsumo.SINAPI);
					System.out.println(insumo.toString());
					insumoRepository.save(insumo);
				}
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
	 				 
		return "Insumos Importados com Sucesso!";
	}
	
	
	/*  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_01a062018.zip (6 arquivos zip)
	 * 
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_072018_NaoDesonerado.zip
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_082018_NaoDesonerado.zip
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_072018_Desonerado.zip
	 *  http://www.caixa.gov.br/Downloads/sinapi-a-partir-jul-2009-rn/SINAPI_ref_Insumos_Composicoes_RN_082018_Desonerado.zip
	 *  
	 */
	
	@GetMapping(path="/composicao/{estado}/{ano}/{mes}")
	public @ResponseBody String importaComposicoes(@PathVariable String estado, @PathVariable String ano, @PathVariable String mes) {
		
		// TODO: Importar o Arquivo zip e descompactar no diretorio  da aplicação 
	
		String fileName = "/home/sergio/sinapi-download/"+estado+"/"+ano+"/"+mes+"/desonerado/SINAPI_Custo_Ref_Composicoes_Analitico_"+estado+"_"+ano+mes+"_Desonerado.xls";
		Double n = null; 
		String s = null;
		String aux = null;
		String siglaAnterior      = "ASTU";
		String siglaAtual         = "ASTU"; 
		String composicaoAnterior = "97141";
		int i = 0; 
		Composicao composicao = new Composicao(); 
		ItemComposicao itemComposicao = new ItemComposicao(); 
		Classe classe = new Classe(); 

		Optional<Estado> e = estadoRepository.findBySiglaIgnoreCase(estado);
		if (!e.isPresent()) {
			e.get().setSigla(estado);
		}
		composicao.setEstado(e.get());
		
		
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
									break;
									
								case NUMERIC:
									value = "NUMERIC value=" + cell.getNumericCellValue();
									n = cell.getNumericCellValue();
									break;

								case STRING:
									value = "STRING value=" + cell.getStringCellValue();
									s = cell.getStringCellValue(); 
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
							
							System.out.println("CELL col=" + cell.getColumnIndex() + " VALUE="+ value);
							
							if (s != null) {
								s = deleteSpace(s);
							}
							
							 
							switch (cell.getColumnIndex()) {
								case 0:
									// composicao.setDescricaoClasse(s); 
									aux = s; 
									i=0; 
									
									break;
								case 1:
									// composicao.setSiglaClasse(s);
									siglaAtual=s; 
									
									Optional<Classe> classeOptional = classeRepository.findBySiglaIgnoreCase(s);
									
									if (classeOptional.isPresent()) {
										composicao.setClasse(classeOptional.get()); 
									}  

									break;
								case 2:
									// composicao.setDescricaoTipo(s);
									break;
								case 3:
									// composicao.setSiglaTipo(n.longValue());
									break;
								case 4:
									//composicao.setCodigoAgrupador(n.longValue());
									break;
								case 5:
									// composicao.setDescricaoAgrupador(s); 
									break;	
								case 6:
									// Quebra composicao 
									System.out.println("Quebra composicao="+s+" anterior="
											+composicaoAnterior+" FORA=" 
											+composicaoAnterior.compareTo(s));
									
									if (composicaoAnterior.compareTo(s) != 0) {
										System.out.println("Quebra composicao= "+s+" anterior="+composicaoAnterior);
										composicao.setEstado(e.get());
										composicao.setAnoMes(ano+mes);
										composicao.setBase(BaseInsumo.SINAPI);
										composicao.adicionarItens(itens);
										composicaoRepository.save(composicao);
										// 
										composicao = new Composicao(); 
										
										
										
										classeOptional = classeRepository.findBySiglaIgnoreCase(s);
										
										if (classeOptional.isPresent()) {
											composicao.setClasse(classeOptional.get()); 
										} else {   
											classe =new Classe();
											classe.setNome(aux);
											classe.setSigla(siglaAtual);
											composicao.setClasse(classe);
										}	
									   
									    composicaoAnterior=s;
									    itens = new ArrayList<>(); 
									    i=0;
									}
									
									composicao.setSku(s);
									break;	
								case 7:
									composicao.setDescricao(s); 
									break;	
								case 8:
									composicao.setUnidade(s); 
									break;	
								case 9:
									// composicao.setOrigem(s); 
									break;	
								case 10:
									composicao.setCustoTotal(StrToBig(s, 2)); 
									break;	
								case 11:
									itemComposicao = new ItemComposicao(); 
									itemComposicao.setTipo(s); 
									break;	
								case 12:
									itemComposicao.setSku(s);
									break;	
								case 13:
									itemComposicao.setDescricao(s); 
									break;	
								case 14:
									itemComposicao.setUnidade(s); 
									break;	
								case 15:
									// itemComposicao.setOrigem(s); 
									break;	
								case 16:
									itemComposicao.setCoeficiente(StrToBig(s, 7)); 
									break;	
								case 17:
									itemComposicao.setPrecoUnitario(StrToBig(s, 2)); 
									break;	
								case 18:
									itemComposicao.setCustoTotal(StrToBig(s, 2)); 
									itens.add(i, itemComposicao); 
									i++;
									break;	
								case 19:
									composicao.setCustoMaoObra(StrToBig(s, 2)); 
									break;	
								case 20:
									composicao.setPercentualMaoObra(StrToBig(s, 7)); 
									break;	
								case 21:
									composicao.setCustoMaterial(StrToBig(s, 2)); 
									break;	
								case 22:
									composicao.setPercentualMaterial(StrToBig(s, 7)); 
									break;	
								case 23:
									composicao.setCustoEquipamento(StrToBig(s, 2));
									break;	
								case 24:
									composicao.setPercentualEquipamento(StrToBig(s, 7)); 
									break;	
								case 25:
									composicao.setCustoServicosTerceiros(StrToBig(s, 2)); 
									break;	
								case 26:
									composicao.setPercentualServicosTerceiros(StrToBig(s, 7)); 
									break;	
								case 27:
									composicao.setCustoOutros(StrToBig(s, 2)); 
									break;	
								case 28:
									composicao.setPercentualOutros(StrToBig(s, 7)); 
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
	
	public static String deleteSpace(String str){     
        StringBuilder sb=new StringBuilder();
        
        for(String st: str.split(" ")){
 
            if(!st.equals(""))         
             sb.append(st+" ");        
        } 
        return new String(sb.toString());
    } 
	
    public static BigDecimal StrToBig (String numero, Integer qtdeCasasDecimais) {
        String casasDecimais = "";
        String num = numero;
        DecimalFormat df = null;
        try {
            if (qtdeCasasDecimais > 0) {
                for (int i = 0; i < qtdeCasasDecimais; i++) {
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


