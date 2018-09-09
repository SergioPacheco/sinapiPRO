package br.edu.ifrn.sinapiPRO.session;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.ItemComposicao;

@SessionScope
@Component
public class TabelaItensComposicaoSession {

	private Set<TabelaItensComposicao> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Insumo insumo, BigDecimal coeficiente) {
		TabelaItensComposicao tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(insumo, coeficiente);
		tabelas.add(tabela);
	}


	public void alterarQuantidadeItens(String uuid, Insumo insumo, BigDecimal coeficiente) {
		TabelaItensComposicao tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(insumo, coeficiente);
	}

	public void excluirItem(String uuid, Insumo insumo) {
		TabelaItensComposicao tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(insumo);
	}

	public List<ItemComposicao> getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}
	
	public Object getValorTotal(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorTotal();
	}

	private TabelaItensComposicao buscarTabelaPorUuid(String uuid) {
		TabelaItensComposicao tabela = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new TabelaItensComposicao(uuid));
		return tabela;
	}


}