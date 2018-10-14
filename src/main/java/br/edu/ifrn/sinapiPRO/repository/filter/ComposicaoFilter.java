package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.Classe;
import br.edu.ifrn.sinapiPRO.model.TipoComposicao;

public class ComposicaoFilter {

	private Long codigo;
	private BasePreco basePreco;
	private Classe classe;
	private TipoComposicao tipoComposicao;
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
	public Classe getClasse() {
		return classe;
	}
	public void setClasse(Classe classe) {
		this.classe = classe;
	}
	public TipoComposicao getTipoComposicao() {
		return tipoComposicao;
	}
	public void setTipoComposicao(TipoComposicao tipoComposicao) {
		this.tipoComposicao = tipoComposicao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
