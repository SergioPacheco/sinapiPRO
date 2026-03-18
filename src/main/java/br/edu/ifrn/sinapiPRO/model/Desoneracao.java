package br.edu.ifrn.sinapiPRO.model;

public enum Desoneracao {

	DESONERADO("Desonerado"),
	NAODESONERADO("NaoDesonerado");
	
	private String descricao;
	
	Desoneracao(String descricao) {
		this.descricao = descricao;
	}
	 
	public String getDescricao() {
		return descricao;
	}
	
}
