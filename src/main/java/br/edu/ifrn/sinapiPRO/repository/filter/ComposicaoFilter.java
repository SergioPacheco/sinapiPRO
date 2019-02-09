package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.model.ComposicaoGrupo;

public class ComposicaoFilter {

	private Long codigo;
	private BasePreco basePreco;
	private ComposicaoClasse composicaoClasse;
	private ComposicaoGrupo composicaoGrupo;
	private String descricao;
	
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public BasePreco getBasePreco() {
		return basePreco;
	}
	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}
	public ComposicaoClasse getClasseComposicao() {
		return composicaoClasse;
	}
	public void setClasseComposicao(ComposicaoClasse composicaoClasse) {
		this.composicaoClasse = composicaoClasse;
	}
	public ComposicaoGrupo getGrupoComposicao() {
		return composicaoGrupo;
	}
	public void setGrupoComposicao(ComposicaoGrupo composicaoGrupo) {
		this.composicaoGrupo = composicaoGrupo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
