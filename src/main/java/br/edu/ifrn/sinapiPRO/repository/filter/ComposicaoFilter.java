package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.ClasseComposicao;
import br.edu.ifrn.sinapiPRO.model.GrupoComposicao;

public class ComposicaoFilter {

	private Long codigo;
	private BasePreco basePreco;
	private ClasseComposicao classeComposicao;
	private GrupoComposicao grupoComposicao;
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
	public ClasseComposicao getClasseComposicao() {
		return classeComposicao;
	}
	public void setClasseComposicao(ClasseComposicao classeComposicao) {
		this.classeComposicao = classeComposicao;
	}
	public GrupoComposicao getGrupoComposicao() {
		return grupoComposicao;
	}
	public void setGrupoComposicao(GrupoComposicao grupoComposicao) {
		this.grupoComposicao = grupoComposicao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
