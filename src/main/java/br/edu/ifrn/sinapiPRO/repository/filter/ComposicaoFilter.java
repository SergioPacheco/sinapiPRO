package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.model.ComposicaoGrupo;

public class ComposicaoFilter {

	private BaseInsumo baseInsumo; 
	private Long codigoComposicao;
	private ComposicaoClasse composicaoClasse;
	private ComposicaoGrupo composicaoGrupo;
	private String descricao;
	
	public BaseInsumo getBaseInsumo() {
		return baseInsumo;
	}
	public void setBaseInsumo(BaseInsumo baseInsumo) {
		this.baseInsumo = baseInsumo;
	}
	public Long getCodigoComposicao() {
		return codigoComposicao;
	}
	public void setCodigoComposicao(Long codigoComposicao) {
		this.codigoComposicao = codigoComposicao;
	}
	public ComposicaoClasse getComposicaoClasse() {
		return composicaoClasse;
	}
	public void setComposicaoClasse(ComposicaoClasse composicaoClasse) {
		this.composicaoClasse = composicaoClasse;
	}
	public ComposicaoGrupo getComposicaoGrupo() {
		return composicaoGrupo;
	}
	public void setComposicaoGrupo(ComposicaoGrupo composicaoGrupo) {
		this.composicaoGrupo = composicaoGrupo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	 
}
