package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.BasePreco;

public class InsumoDTO {

	private Long codigo;
	private Long codigoInsumo;
	private String descricao;
	private String unidade; 
	private BigDecimal precoPadrao;
	
	private List<Lista> HistoricoPrecos = new ArrayList<>();
	
	public InsumoDTO(Long codigo, Long codigoInsumo, String descricao,  String unidade,
			BasePreco basePreco, BaseInsumo baseInsumo, BigDecimal precoPadrao, List<Lista> historicoPrecos) {
		super();
		this.codigo = codigo;
		this.codigoInsumo = codigoInsumo;
		this.descricao = descricao;
		this.unidade = unidade;
		this.precoPadrao = precoPadrao;
		HistoricoPrecos = historicoPrecos;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getCodigoInsumo() {
		return codigoInsumo;
	}
	
	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}


	public BigDecimal getPrecoPadrao() {
		return precoPadrao;
	}

	public void setPrecoPadrao(BigDecimal precoPadrao) {
		this.precoPadrao = precoPadrao;
	}

	public List<Lista> getHistoricoPrecos() {
		return HistoricoPrecos;
	}

	public void setHistoricoPrecos(List<Lista> historicoPrecos) {
		HistoricoPrecos = historicoPrecos;
	}

	public class Lista {
		public Long codigoInsumo;
		public BigDecimal preco; 
	}
	
}
