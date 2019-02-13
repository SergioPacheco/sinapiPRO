package br.edu.ifrn.sinapiPRO.repository.filter;

public class OrcamentoItemFilter {
	
	private String tipo;

	public OrcamentoItemFilter(String tipo) {
		super();
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
	
}
