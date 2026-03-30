package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;

public class ReajustePreviewDTO {

	private Long codigoItem;
	private String descricao;
	private BigDecimal valorAtual;
	private BigDecimal valorNovo;
	private BigDecimal diferenca;
	private BigDecimal percentualVariacao;

	public Long getCodigoItem() { return codigoItem; }
	public void setCodigoItem(Long codigoItem) { this.codigoItem = codigoItem; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public BigDecimal getValorAtual() { return valorAtual; }
	public void setValorAtual(BigDecimal valorAtual) { this.valorAtual = valorAtual; }
	public BigDecimal getValorNovo() { return valorNovo; }
	public void setValorNovo(BigDecimal valorNovo) { this.valorNovo = valorNovo; }
	public BigDecimal getDiferenca() { return diferenca; }
	public void setDiferenca(BigDecimal diferenca) { this.diferenca = diferenca; }
	public BigDecimal getPercentualVariacao() { return percentualVariacao; }
	public void setPercentualVariacao(BigDecimal percentualVariacao) { this.percentualVariacao = percentualVariacao; }
}
