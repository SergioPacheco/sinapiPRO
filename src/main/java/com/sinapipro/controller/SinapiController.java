package com.sinapipro.controller;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinapipro.model.BaseInsumo;
import com.sinapipro.model.BasePreco;
import com.sinapipro.model.BasePrecoItem;
import com.sinapipro.model.Composicao;
import com.sinapipro.model.ComposicaoClasse;
import com.sinapipro.model.ComposicaoGrupo;
import com.sinapipro.model.ComposicaoItem;
import com.sinapipro.model.ComposicaoSituacao;
import com.sinapipro.model.Especie;
import com.sinapipro.model.Insumo;
import com.sinapipro.model.Tipo;
import com.sinapipro.repository.BaseInsumosRepository;
import com.sinapipro.repository.BasePrecoItemRepository;
import com.sinapipro.repository.BasePrecosRepository;
import com.sinapipro.repository.ComposicaoClassesRepository;
import com.sinapipro.repository.ComposicaoGruposRepository;
import com.sinapipro.repository.ComposicaoRepository;
import com.sinapipro.repository.InsumosRepository;
import com.sinapipro.security.UsuarioSistema;
import com.sinapipro.service.ComposicaoService;
import com.sinapipro.service.InsumoService;
import com.sinapipro.service.exception.ResourceNotFoundException;
import com.sinapipro.utils.Lib;

@Controller     
@RequestMapping(path="/sinapi")  
public class SinapiController {
	
	private final InsumosRepository insumoRepository;
	private final ComposicaoRepository composicaoRepository;
	private final BasePrecosRepository basePrecoRepository;
	private final BaseInsumosRepository baseInsumoRepository;
	private final BasePrecoItemRepository basePrecoItemRepository;
	private final ComposicaoGruposRepository composicaoGruposRepository;
	private final ComposicaoClassesRepository composicaoClassesRepository;
	private final ComposicaoService composicaoService;
	private final InsumoService insumoService;
	
	public SinapiController(
			InsumosRepository insumoRepository,
			ComposicaoRepository composicaoRepository,
			BasePrecosRepository basePrecoRepository,
			BaseInsumosRepository baseInsumoRepository,
			BasePrecoItemRepository basePrecoItemRepository,
			ComposicaoGruposRepository composicaoGruposRepository,
			ComposicaoClassesRepository composicaoClassesRepository,
			ComposicaoService composicaoService,
			InsumoService insumoService) {
		this.insumoRepository = insumoRepository;
		this.composicaoRepository = composicaoRepository;
		this.basePrecoRepository = basePrecoRepository;
		this.baseInsumoRepository = baseInsumoRepository;
		this.basePrecoItemRepository = basePrecoItemRepository;
		this.composicaoGruposRepository = composicaoGruposRepository;
		this.composicaoClassesRepository = composicaoClassesRepository;
		this.composicaoService = composicaoService;
		this.insumoService = insumoService;
	}
	
	
	private static Logger logger = LoggerFactory.getLogger(SinapiController.class);
	private Double valueNumeric; 
	private String valueString;
	private List<ComposicaoItem> itens = new ArrayList<>();

	/**
	 * 
	 * @param codigo   - codigo da base de Preço 
	 * @param onerado  - D-Desonerado O-Onerado(Não Desonerado) 
	 * @return
	 */
	@GetMapping(path="/insumo/{codigo}/{onerado}")  
	public @ResponseBody String importaInsumos (@PathVariable Long codigo, @PathVariable String onerado, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		//TODO: Fazer aqui o download do arquivo e unzip
		//TODO: Checar se o arquivo existe
	
		Long codigoBasePreco = codigo;
		
		if(!basePrecoRepository.existsById(codigoBasePreco)) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Precos");
        }
		
		BasePreco basePreco = basePrecoRepository.findById(codigoBasePreco).get();

		if(!baseInsumoRepository.existsById(basePreco.getBaseInsumo().getCodigo())) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Insumos");
        }
		
		Long codigoBaseInsumo = basePreco.getBaseInsumo().getCodigo(); 
		BaseInsumo baseInsumo = baseInsumoRepository.findById(codigoBaseInsumo).get();
		
		String uf = basePreco.getEstado().getSigla();
	
		Date df = Lib.asDate(basePreco.getDataReferencia());
		String ano = Integer.toString(Lib.Year(df)); 
		String mes = Lib.StrZero(Lib.Month(df), 2);
		
		String oneracao = "Desonerado";
		
		if ("O".equals(onerado)) { 
			oneracao = "NaoDesonerado";
		}
		String fileName = "";
		
		// TODO: renomear a extensão (.xls .XLS) dos arquivos para caixa baixa
		
		// /home/sergio/sinapi-download/RN/2018/01/Desonerado/SINAPI_Preco_Ref_Insumos_RN_201801_Desonerado.xls
		// /home/sergio/sinapi-download/RN/2018/01/Desonerado/SINAPI_Preco_Ref_Insumos_RN_012018_Desonerado.XLS
		fileName = "src/main/resources/sinapi-download/"+uf+"/"+oneracao+"/SINAPI_Preco_Ref_Insumos_"+uf+"_"+ano+mes+"_"+oneracao+".xls";
		Double n = null; 
		Long   l = 0L;
		String s = null;
		
		System.out.println(fileName);
		   
		try (HSSFWorkbook wb = SinapiController.readFile(fileName)) {
			
			logger.info("Data dump:\n");
			
			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				
				HSSFSheet sheet = wb.getSheetAt(k);
				int rows = sheet.getPhysicalNumberOfRows();
				
				for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					
					if (row == null || row.getPhysicalNumberOfCells() < 5 || row.getRowNum() ==6 )  {
						continue;
					}
					
					Insumo insumo = new Insumo(); 
					
					for (int c = 0; c < row.getLastCellNum(); c++) {
						HSSFCell cell = row.getCell(c);
						String value = null; 
						s=null;
						n=null; 
				 
						if (cell != null) {
							switch (cell.getCellType()) {
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
									value = "UNKNOWN value of type " + cell.getCellType();
							}
							
							switch (cell.getColumnIndex()) {
								case 0:
									insumo.setCodigoInsumo( Lib.Round(valueNumeric,0).toString() );
									break;
								case 1:
									insumo.setDescricao(valueString.trim()); 
									break;
								case 2:
									insumo.setUnidade(valueString.trim());
									break;
								case 3:
									// Origem
									break;
								case 4:
									 
									insumo.setPrecoPadrao(strToBig(valueString, 2)); 
									break;
								default:
									insumo = null; 
							}
						}
					} // end for
					
					if (insumo==null) {
						continue;
					}
					
					// ok ... então vamos persistir 
					
					Optional<Insumo> insumoExistente = insumoRepository
							.findByBaseInsumoAndCodigoInsumo(baseInsumo, insumo.getCodigoInsumo()); 
					
					if(!insumoExistente.isPresent()) {
						
						// Novo Insumo
						 
						if (insumo.getCodigoInsumo() !=null &&
							insumo.getDescricao()    !=null &&
							insumo.getUnidade()      !=null &&
							insumo.getPrecoPadrao()  !=null) {
							insumo.setEspecie(defineEspecie(insumo.getUnidade(), insumo.getDescricao()) );
							insumo.setBaseInsumo(baseInsumo);
							insumo.setBasePreco(basePreco);
							insumo.setUsuario(usuarioSistema.getUsuario());
							
							insumoService.salvar(insumo);
							
						} else {
							
							insumo=null;
							
							continue;
						}
					} else { 
						
						// Atualiza Insumo
						
						if (insumo.getCodigoInsumo() !=null &&
							insumo.getDescricao()    !=null &&
							insumo.getUnidade()      !=null &&
							insumo.getPrecoPadrao()  !=null) {
							
							Insumo insumoEdita = insumoExistente.get(); 
							
							insumoEdita.setUnidade(insumo.getUnidade());
							insumoEdita.setDescricao(insumo.getDescricao());
							insumoEdita.setPrecoPadrao(insumo.getPrecoPadrao());
							insumoEdita.setEspecie(insumo.getEspecie());
							insumoEdita.setEspecie(defineEspecie(insumo.getUnidade(), insumo.getDescricao()) );
							
							insumoService.salvar(insumoEdita);
							
							System.out.println("Atualiza Insumo "+insumoEdita.getCodigoInsumo());
							 
							
						} else {

							insumo=null;
							
							continue;
						}
							
					}
					
					// Salva o Item da Base de Preço  
					 
					Optional<BasePrecoItem> item = basePrecoItemRepository
							.findByBasePrecoAndCodigoInsumo(basePreco, insumo.getCodigoInsumo());
					
					if(!item.isPresent()){
						
						BasePrecoItem novoItem = new BasePrecoItem();
						
						novoItem.setBasePreco(basePreco);
						novoItem.setCodigoInsumo(insumo.getCodigoInsumo());
						
						if ("D".equals(onerado)) {
							novoItem.setPreco(insumo.getPrecoPadrao());	
						} else {
							novoItem.setPrecoOnerado(insumo.getPrecoPadrao());	
						}
						
						novoItem.setAnoMes(ano+"/"+mes);

						basePrecoItemRepository.saveAndFlush(novoItem);
												
					} else { 
						
						BasePrecoItem editaItem = item.get();
						if ("D".equals(onerado)) {
							editaItem.setPreco(insumo.getPrecoPadrao());
						} else {
							editaItem.setPrecoOnerado(insumo.getPrecoPadrao());
						}
					 
						editaItem.setAnoMes(ano+"/"+mes);
						basePrecoItemRepository.saveAndFlush(editaItem);
					
					}
					
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
	 * @param codigo   - codigo da base de Preço 
	 * @param onerado  - D-Desonerado O-Onerado(Não Desonerado) 
	 * @return
	 */
	@GetMapping(path="/composicao/{codigo}/{onerado}")  
	public @ResponseBody String importaComposicoes(@PathVariable Long codigo, @PathVariable String onerado, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		
		// TODO: Importar o Arquivo zip e descompactar no diretorio  da aplicação 
		// TODO: Tratar melhor exceptions resources not found

		Long codigoBasePreco = codigo;
		if(!basePrecoRepository.existsById(codigoBasePreco)) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Precos");
        }
		
		BasePreco basePreco = basePrecoRepository.findById(codigoBasePreco).get();
		if(!baseInsumoRepository.existsById(basePreco.getBaseInsumo().getCodigo())) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Insumos");
        }
		
		Long codigoBaseInsumo = basePreco.getBaseInsumo().getCodigo(); 
		BaseInsumo baseInsumo = baseInsumoRepository.findById(codigoBaseInsumo).get();
		
				
		String uf = basePreco.getEstado().getSigla();
		Date df = Lib.asDate(basePreco.getDataReferencia());
		String ano = Integer.toString(Lib.Year(df)); 
		String mes = Lib.StrZero(Lib.Month(df), 2);
		String oneracao = "Desonerado";
		
		if ("O".equals(onerado)) { 
			oneracao = "NaoDesonerado";
		}

		String fileName = "src/main/resources/sinapi-download/"+uf+"/"+oneracao+"/SINAPI_Custo_Ref_Composicoes_Analitico_"+uf+"_"+ano+mes+"_"+oneracao+".xls";
		
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
		ComposicaoItem composicaoItem = new ComposicaoItem(); 
		List<ComposicaoItem> itens = new ArrayList<>();
		 
		
		try (HSSFWorkbook wb = SinapiController.readFile(fileName)) {
			
			logger.info("Data dump:\\n");
			
			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				
				HSSFSheet sheet = wb.getSheetAt(k);
				int rows = sheet.getPhysicalNumberOfRows();
				
				logger.info("Planilha " + k + " \"" + wb.getSheetName(k) + "\" tem " + rows + " linha(s).");
				
				for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					if (row == null || row.getRowNum() < 8)  {
						continue;
					}
					
					logger.info("\nROW " + row.getRowNum() + " has " + row.getPhysicalNumberOfCells() + " cell(s).");
					
					for (int c = 0; c < row.getLastCellNum(); c++) {
						HSSFCell cell = row.getCell(c);
						String value;
						if (cell != null) {
							s=null;
							n=null; 
							switch (cell.getCellType()) {
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
									value = "UNKNOWN value of type " + cell.getCellType();
									continue;
							}
							
							s=Lib.RTrim(Lib.LTrim(s));
							 
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
									
								case 3: // codigo do Grupo
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
										composicao.setCodigoComposicao(campo6Atual);
										composicao = pesquisaClasseGrupo(composicao, campo1Atual, campo0Atual, campo3Atual.longValue(), campo2Atual);
									}
																	
									if (composicaoAnterior.compareTo(campo6Atual) != 0) {    // 0 são idênticas   diferentes -1  +1
										
										composicaoAnterior = campo6Atual;
										
										System.out.println("Quebra ="+campo6Atual+" Anterior="+composicaoAnterior+" DENTRO="+composicaoAnterior.compareTo(campo6Atual));
										
										composicao.setBasePreco(basePreco);
										composicao.setBaseInsumo(baseInsumo);
										composicao.setDataCriacao(Lib.asLocalDateTime(Lib.Today()));
										composicao.setStatus(ComposicaoSituacao.ATIVA);
										composicao.setUsuario(usuarioSistema.getUsuario());
										 
										/**
										 *  Salva Composição 
										 */
										
										Optional<Composicao> composicaoExistente = composicaoRepository
												.findByBaseInsumoAndCodigoComposicao(baseInsumo, composicao.getCodigoComposicao()); 
										
										if(!composicaoExistente.isPresent()) {
											composicaoService.salvar(composicao);
										} else { 
											Composicao editaComposicao = composicaoExistente.get() ; 
											editaComposicao.setCodigoComposicao(composicao.getCodigoComposicao());
											editaComposicao.setBaseInsumo(composicao.getBaseInsumo());
											editaComposicao.setBasePreco(composicao.getBasePreco()); 
											editaComposicao.setComposicaoGrupo(composicao.getComposicaoGrupo()); 
											editaComposicao.setStatus(ComposicaoSituacao.ATIVA);
											editaComposicao.setDescricao(composicao.getDescricao());
											editaComposicao.setUnidade(composicao.getUnidade()); 
											editaComposicao.setCustoTotal(composicao.getCustoTotal());
											editaComposicao.setCustoMaoObra(composicao.getCustoMaoObra());
											editaComposicao.setPercMaoObra(composicao.getPercMaoObra()); 
											editaComposicao.setCustoMaterial(composicao.getCustoMaterial()); 
											editaComposicao.setPercMaterial(composicao.getPercMaterial());
											editaComposicao.setCustoEquipamento(composicao.getCustoEquipamento());
											editaComposicao.setPercEquipamento(composicao.getPercEquipamento());
											composicaoService.salvar(editaComposicao);
											
										}
										
										itens.clear();
										composicao = new Composicao(); 
									    composicaoItem =  new ComposicaoItem();
									    
									    // Nova composicao
										
										composicaoAnterior=campo6Atual;
										composicao.setCodigoComposicao(campo6Atual);
										composicao = pesquisaClasseGrupo(composicao, campo1Atual, campo0Atual, campo3Atual.longValue(), campo2Atual);
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
										composicao.setCustoTotal(strToBig(s, 2)); 
									}	
									break;	
									
								case 11: // tipo do item - {COMPOSICAO INSUMO}
									 
									if (!Lib.Empty(s)) {
										composicaoItem = new ComposicaoItem(); 
										if (s.equals("COMPOSICAO")) {
											composicaoItem.setTipo(Tipo.COMPOSICAO);
										} else {
											if (s.equals("INSUMO")) {
												composicaoItem.setTipo(Tipo.INSUMO);
											}
										}
									}
									break;	
									
								case 12: // codigo do item
									
									if (!Lib.Empty(s)) {
										composicaoItem.setCodigoItem(s);
									}
									break;	
									
								case 13: // Descricao do Item 
									if (!Lib.Empty(s)) {
										composicaoItem.setDescricao(s); 
									}
									break;	
									
								case 14: // Unidade do Item
									if (!Lib.Empty(s)) {
										composicaoItem.setUnidade(s);
									}
									break;	
									
								case 15: // pula origem do preco 
									break;	
									
								case 16: // coeficiente do item
									if (!Lib.Empty(s)) {
										composicaoItem.setCoeficiente(strToBig(s, 7)); 
									}	
									break;	
									
								case 17: // preco unitario do item 
									if (!Lib.Empty(s)) {
										composicaoItem.setPrecoUnitario(strToBig(s, 2)); 
									}
									break;	
									
								case 18: // Custo Total do Item 
									campo18Atual = s;
									if (!Lib.Empty(s)) {
										composicaoItem.setCustoTotal(strToBig(s, 2)); 
									
									}
									break;	
									
								case 19: // Custo Mão de Obra 
									if (!Lib.Empty(s)) {
										composicao.setCustoMaoObra(strToBig(s, 2)); 
									}	
									break;	
									
								case 20: // Percentual da Mão de Obra 
									if (!Lib.Empty(s)) {
										composicao.setPercMaoObra(strToBig(s, 7)); 
									}	
									break;	
									
								case 21:
									if (!Lib.Empty(s)) {
										composicao.setCustoMaterial(strToBig(s, 2)); 
									}	
									break;	
									
								case 22:
									if (!Lib.Empty(s)) {
										composicao.setPercMaterial(strToBig(s, 7)); 
									}	
									break;	
									
								case 23:
									if (!Lib.Empty(s)) {
										composicao.setCustoEquipamento(strToBig(s, 2));
									}	
									break;	
									
								case 24:
									
									if (!Lib.Empty(s)) {
										composicao.setPercEquipamento(strToBig(s, 7));
									}	
									
									break;
									
								case 29:
									if (!Lib.Empty(campo18Atual)) {
										
										if(composicaoItem.getTipo() == Tipo.INSUMO ) {
											Optional<Insumo> optional = insumoRepository
													.findByBaseInsumoAndCodigoInsumo(baseInsumo,composicaoItem.getCodigoItem());
											if (optional.isPresent()) {
												composicaoItem.setInsumo(optional.get());
											} else {

												Insumo novoInsumo = new Insumo(); 
												novoInsumo.setCodigoInsumo(composicaoItem.getCodigoItem());
												novoInsumo.setUnidade(composicaoItem.getUnidade());
												novoInsumo.setEspecie(defineEspecie(composicaoItem.getUnidade(), composicaoItem.getDescricao()) );
												novoInsumo.setBaseInsumo(baseInsumo);
												novoInsumo.setBasePreco(basePreco);
												novoInsumo.setPrecoPadrao(composicaoItem.getCustoTotal() );
												novoInsumo.setUsuario(usuarioSistema.getUsuario());
												composicaoItem.setInsumo(insumoService.salvar(novoInsumo));
												//TODO: Criar item da base de preco
											}
												
										}
										if(composicaoItem.getTipo() == Tipo.COMPOSICAO) {
											Optional<Composicao> optional = composicaoRepository
													.findByBaseInsumoAndCodigoComposicao(baseInsumo, composicaoItem.getCodigoItem());
											if (optional.isPresent()) {
												composicaoItem.setComposicao(optional.get());
											} else {
												// Cria nova composicao 
												Composicao novaComposicao = new Composicao(); 
												novaComposicao.setCodigoComposicao(composicaoItem.getCodigoItem());
												novaComposicao.setBaseInsumo(baseInsumo);
												novaComposicao.setBasePreco(basePreco);
												novaComposicao.setUsuario(usuarioSistema.getUsuario()); 
												novaComposicao.setDescricao(composicaoItem.getDescricao());
												novaComposicao.setUnidade(composicaoItem.getUnidade());
												novaComposicao.setCustoTotal(composicaoItem.getCustoTotal());
												
												composicaoItem.setComposicao(composicaoService.salvar(novaComposicao) );
											}
										}	
										composicao.addItem(composicaoItem);
										composicaoItem = new ComposicaoItem(); 
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
	 
	/**
	 * Procura por novos insumos na base de composições
	 * 
	 * @param codigo   - codigo da base de Preço 
	 * @return
	 */
	@GetMapping(path="/composicao/novosInsumos/{codigo}")  
	public @ResponseBody String novosInsumos(@PathVariable Long codigo, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
	
		
		if(!basePrecoRepository.existsById(codigo)) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Precos");
        }
		
		BasePreco basePreco = basePrecoRepository.findById(codigo).get();
		if(!baseInsumoRepository.existsById(basePreco.getBaseInsumo().getCodigo())) {
            throw new ResourceNotFoundException("Erro ao pesquisar Base Insumos");
        }
		
		Long codigoBaseInsumo = basePreco.getBaseInsumo().getCodigo(); 
		BaseInsumo baseInsumo = baseInsumoRepository.findById(codigoBaseInsumo).get();
		List<Composicao> composicoes = composicaoRepository.findAll(); 
		
		for (int c = 0; c < composicoes.size(); c++) {
			
			for (int ci = 0; ci <  composicoes.get(c).getItens().size(); ci++) {
				
				if (composicoes.get(c).getItens().get(ci).getTipo() == Tipo.INSUMO) {
					
					Optional<Insumo> insumoExistente = insumoRepository
							.findByBaseInsumoAndCodigoInsumo(baseInsumo, 
									composicoes.get(c).getItens().get(ci).getCodigoItem());
					
					if (!insumoExistente.isPresent()) {
						Insumo novoInsumo = new Insumo();
						novoInsumo.setCodigoInsumo(composicoes.get(c).getItens().get(ci).getCodigoItem());
						novoInsumo.setBaseInsumo(baseInsumo);
						novoInsumo.setBasePreco(basePreco);
						novoInsumo.setUsuario(composicoes.get(c).getUsuario());  
						novoInsumo.setDescricao(composicoes.get(c).getItens().get(ci).getDescricao());
						novoInsumo.setUnidade(composicoes.get(c).getItens().get(ci).getUnidade());
						novoInsumo.setPrecoPadrao(composicoes.get(c).getItens().get(ci).getPrecoUnitario());
						novoInsumo.setEspecie(defineEspecie(composicoes.get(c).getItens().get(ci).getUnidade(), 
											  composicoes.get(c).getItens().get(ci).getDescricao()) );
						composicoes.get(c).getItens().get(ci).setInsumo(insumoRepository.save(novoInsumo));
						
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
						System.out.println("     NOVO INSUMO "+composicoes.get(c).getItens().get(ci).getCodigoItem());
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					} else {
						composicoes.get(c).getItens().get(ci).setInsumo(insumoExistente.get() );
					}
					
				} else {
					
					Optional<Composicao> composicaoExistente = composicaoRepository
							.findByBaseInsumoAndCodigoComposicao(baseInsumo, 
									composicoes.get(c).getItens().get(ci).getCodigoItem());
					
					if (!composicaoExistente.isPresent()) { 
						Composicao novaComposicao = new Composicao();
						novaComposicao.setCodigoComposicao(composicoes.get(c).getItens().get(ci).getCodigoItem());
						novaComposicao.setBaseInsumo(baseInsumo);
						novaComposicao.setBasePreco(basePreco);
						novaComposicao.setUnidade(composicoes.get(c).getItens().get(ci).getUnidade());
						novaComposicao.setDescricao(composicoes.get(c).getItens().get(ci).getDescricao());
						novaComposicao.setCustoTotal(composicoes.get(c).getItens().get(ci).getCustoTotal());
						composicoes.get(c).getItens().get(ci).setComposicao(composicaoRepository.save(novaComposicao));
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
						System.out.println("     NOVO COMPOSIÇÃO "+composicoes.get(c).getItens().get(ci).getCodigoItem());
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					} else {
						composicoes.get(c).getItens().get(ci).setComposicao(composicaoExistente.get());
					}
				}	
				
			} // itens
			
			composicaoService.salvar(composicoes.get(c));
			
		} // composicao
		return "ok";
	}	
	
	private static HSSFWorkbook readFile(String filename) throws IOException {
		try (FileInputStream fis = new FileInputStream(filename)) {
			return new HSSFWorkbook(fis);      
		}
	}
	
	private Composicao pesquisaClasseGrupo (Composicao composicao, String siglaClasse, 
								String nomeClasse, Long codigoGrupo, String nomeGrupo) {
		
		ComposicaoClasse classe =new ComposicaoClasse();
		ComposicaoGrupo grupo = new ComposicaoGrupo();
		
		Optional<ComposicaoGrupo> grupoExistente = composicaoGruposRepository
				.findById( codigoGrupo );
		
		if (grupoExistente.isPresent()) {
			composicao.setComposicaoGrupo(grupoExistente.get()); 
		} else {
			
			Optional<ComposicaoClasse> classeExistente = composicaoClassesRepository
					.findBySiglaIgnoreCase(siglaClasse);
			
			if (classeExistente.isPresent()) {
				composicao.setComposicaoClasse(classeExistente.get()); 
			} else { 
				classe.setNome(nomeClasse);
				classe.setSigla(siglaClasse);
				composicaoClassesRepository.save(classe);
				composicao.setComposicaoClasse(composicaoClassesRepository.saveAndFlush(classe));
			}
			grupo.setCodigo(codigoGrupo); 
			grupo.setNome(nomeGrupo);
			grupo.setComposicaoClasse(composicao.getComposicaoClasse());
			composicao.setComposicaoGrupo(composicaoGruposRepository.saveAndFlush(grupo));
		}
		
		return composicao;
	}
	
	public static String strRemove(String str){     
        StringBuilder sb=new StringBuilder();
        
        for(String st: str.split(" ")){
 
            if (!st.equals(""))         
               sb.append(st+" ");        
        } 
        return new String(sb.toString());
    } 
	
	public static Especie defineEspecie(String unidade, String descricao){     
        
		if ("H".equals(unidade) || "MES".equals(unidade)) {
			if (descricao.length() > 7) {
			    if ( "LOCACAO".equals( descricao.substring(0, 7) ) ) { 
			       return Especie.EQUIPAMENTO;	
			    }
	 	    }
			return Especie.MAO_DE_OBRA;
		}
		return Especie.MATERIAL;
    } 
	
    public static BigDecimal strToBig (String numero, Integer decimais) {
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
