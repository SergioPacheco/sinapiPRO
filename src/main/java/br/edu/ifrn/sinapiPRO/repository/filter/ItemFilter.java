package br.edu.ifrn.sinapiPRO.repository.filter;

public class ItemFilter {
	
	private String tipo;

	public ItemFilter(String tipo) {
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
