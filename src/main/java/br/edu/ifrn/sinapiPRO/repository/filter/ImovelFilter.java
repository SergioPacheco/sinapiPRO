package br.edu.ifrn.sinapiPRO.repository.filter;

public class ImovelFilter {
	
	private String estado; 
	private String cidade; 
	private String precoMin;
	private String precoMax;
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getPrecoMin() {
		return precoMin;
	}
	public void setPrecoMin(String precoMin) {
		this.precoMin = precoMin;
	}
	public String getPrecoMax() {
		return precoMax;
	}
	public void setPrecoMax(String precoMax) {
		this.precoMax = precoMax;
	} 
	
}
