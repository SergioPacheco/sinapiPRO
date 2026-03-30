package br.edu.ifrn.sinapiPRO.session.composicao;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import br.edu.ifrn.sinapiPRO.model.ComposicaoItem;
import br.edu.ifrn.sinapiPRO.model.Insumo;

@SessionScope
@Component
public class TabelaComposicaoItemSession {

	private Set<TabelaComposicaoItem> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Insumo insumo, BigDecimal coeficiente) {
		
		TabelaComposicaoItem tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(insumo, coeficiente);
		tabelas.add(tabela);
	}

	public void alterarCoeficiente(String uuid, Insumo insumo, BigDecimal coeficiente) {
		
		TabelaComposicaoItem tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarCoeficiente(insumo, coeficiente);
	}
 
	public void excluirItem(String uuid, Insumo insumo) {
		TabelaComposicaoItem tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(insumo);
	}

	public List<ComposicaoItem> getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}
	
	public Object getValorTotal(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorTotal();
	}

	public TabelaComposicaoItem buscarTabelaPorUuid(String uuid) {
		TabelaComposicaoItem tabela = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new TabelaComposicaoItem(uuid));
		return tabela;
	}
}