package br.edu.ifrn.sinapiPRO.session.orcamento;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.OrcamentoItem;

@SessionScope
@Component
public class TabelasItensOrcamentoSession {
/*
	private Set<TabelaItensOrcamento> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Etapa etapa) {
		
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(etapa);
		tabela.Itemizar();
		tabelas.add(tabela);
	 
	}
	public void adicionarItem(String uuid, Etapa etapa, Insumo insumo, BigDecimal quantidade) {
		
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(etapa, insumo, quantidade);
		tabela.Itemizar();
		tabelas.add(tabela);
	
	}
	public void adicionarItem(String uuid, Etapa etapa, Composicao composicao, BigDecimal quantidade) {
		
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(etapa, composicao, quantidade);
		tabela.Itemizar();
		tabelas.add(tabela);
	
	}

	public void alterarQuantidade(String uuid, Etapa etapa, Composicao composicao, BigDecimal quantidade) {
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(etapa, composicao, quantidade);
		 
	}
	
	public void alterarQuantidade(String uuid, Etapa etapa, Insumo insumo, BigDecimal quantidade) {
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(etapa, insumo, quantidade);
		 
	}
	
	public void excluirItem(String uuid, Etapa etapa) {
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(etapa);
	}
	
	public void excluirItem(String uuid, Etapa etapa, Composicao composicao) {
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(etapa, composicao);
	}
	
	public void excluirItem(String uuid, Etapa etapa, Insumo insumo) {
		TabelaItensOrcamento tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(etapa, insumo);
	}
	
	public List<OrcamentoItem> getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}
	
	public Object getValorTotal(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorTotal();
	}
	
	public Object getValorMaoObra(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorMaoObra();
	}
	
	public Object getValorMaterial(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorMaterial();
	}
	
	public Object getValorEquipamento(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorEquipamento();
	}
	
	private TabelaItensOrcamento buscarTabelaPorUuid(String uuid) {
		TabelaItensOrcamento tabela = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new TabelaItensOrcamento(uuid));
		return tabela;
	}

	*/
}
