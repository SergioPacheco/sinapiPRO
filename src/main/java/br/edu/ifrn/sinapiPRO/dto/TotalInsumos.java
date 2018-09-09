package br.edu.ifrn.sinapiPRO.dto;

public class TotalInsumos {

	private Long quantidade;
	
	public TotalInsumos() {
	}

	public TotalInsumos(Long quantidade) {
		this.quantidade = quantidade;
	}

	public Long getQuantidades() {
		return quantidade != null ? quantidade : 0L;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	
}
