package br.edu.ifrn.sinapiPRO.dto;


public class ItemBasePrecoDTO {
	
	private String mes; 
	private String preco; 
	
	public ItemBasePrecoDTO() {
	}
	

	public ItemBasePrecoDTO(String mes, String preco) {
		super();
		this.mes = mes;
		this.preco = preco;
	}


	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getPreco() {
		return preco;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}

}

 