package br.edu.ifrn.sinapiPRO.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import br.edu.ifrn.sinapiPRO.model.Orcamento;

public class OrcamentoExportDTO {

	private Long codigo;
	private String nome;
	private String dataCriacao;
	private String situacao;
	private String desoneracao;
	private String estadoNome;
	private String clienteNome;
	private String obraNome;
	private String observacao;
	private BigDecimal valorTotal;
	private BigDecimal subTotal;
	private BigDecimal totalBDI;
	private BigDecimal totalLeisSociais;
	private BigDecimal totalTaxaAdm;
	private BigDecimal totalTaxas;
	private BigDecimal percentualBdi;
	private BigDecimal percentualLeisSociais;
	private BigDecimal percentualTaxaAdm;
	private List<ItemExportDTO> itens;

	public OrcamentoExportDTO(Orcamento o) {
		this.codigo = o.getCodigo();
		this.nome = o.getNome();
		this.dataCriacao = o.getDataCriacao() != null ? o.getDataCriacao().toString() : null;
		this.situacao = o.getSituacao() != null ? o.getSituacao().name() : null;
		this.desoneracao = o.getDesoneracao() != null ? o.getDesoneracao().getDescricao() : null;
		this.estadoNome = o.getEstado() != null ? o.getEstado().getNome() : null;
		this.clienteNome = o.getCliente() != null ? o.getCliente().getNome() : null;
		this.obraNome = o.getObra() != null ? o.getObra().getNome() : null;
		this.observacao = o.getObservacao();
		this.valorTotal = o.getValorTotal();
		this.subTotal = o.getSubTotal();
		this.totalBDI = o.getTotalBDI();
		this.totalLeisSociais = o.getTotaLeisSociais();
		this.totalTaxaAdm = o.getTotalTaxaAdm();
		this.totalTaxas = o.getTotalTaxas();
		this.percentualBdi = o.getPercentualBdi();
		this.percentualLeisSociais = o.getPercentualLeisSociais();
		this.percentualTaxaAdm = o.getPercentualTaxaAdm();
		this.itens = o.getItens().stream()
				.map(ItemExportDTO::new)
				.collect(Collectors.toList());
	}

	public Long getCodigo() {
		return codigo;
	}

	public String getNome() {
		return nome;
	}

	public String getDataCriacao() {
		return dataCriacao;
	}

	public String getSituacao() {
		return situacao;
	}

	public String getDesoneracao() {
		return desoneracao;
	}

	public String getEstadoNome() {
		return estadoNome;
	}

	public String getClienteNome() {
		return clienteNome;
	}

	public String getObraNome() {
		return obraNome;
	}

	public String getObservacao() {
		return observacao;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public BigDecimal getTotalBDI() {
		return totalBDI;
	}

	public BigDecimal getTotalLeisSociais() {
		return totalLeisSociais;
	}

	public BigDecimal getTotalTaxaAdm() {
		return totalTaxaAdm;
	}

	public BigDecimal getTotalTaxas() {
		return totalTaxas;
	}

	public BigDecimal getPercentualBdi() {
		return percentualBdi;
	}

	public BigDecimal getPercentualLeisSociais() {
		return percentualLeisSociais;
	}

	public BigDecimal getPercentualTaxaAdm() {
		return percentualTaxaAdm;
	}

	public List<ItemExportDTO> getItens() {
		return itens;
	}
}
