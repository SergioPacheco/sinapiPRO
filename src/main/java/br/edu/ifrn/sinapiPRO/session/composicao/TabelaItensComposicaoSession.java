package br.edu.ifrn.sinapiPRO.session.composicao;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import br.edu.ifrn.sinapiPRO.model.ComposicaoItem;

@SessionScope
@Component
public class TabelaItensComposicaoSession {

	private Set<TabelaItensComposicao> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, String tipo, Long codigoItem, BigDecimal precoUnitario, BigDecimal coeficiente) {
		
		TabelaItensComposicao tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(tipo, codigoItem, precoUnitario, coeficiente);
		tabelas.add(tabela);
	}

	public void alterarCoeficiente(String uuid, String tipo, Long codigoItem, BigDecimal coeficiente) {
		
		TabelaItensComposicao tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarCoeficiente(codigoItem, coeficiente);
	}

	public void excluirItem(String uuid, Long codigoItem) {
		TabelaItensComposicao tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(codigoItem);
	}

	public List<ComposicaoItem> getItens(String uuid) {
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