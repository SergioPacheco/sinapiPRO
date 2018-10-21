package br.edu.ifrn.sinapiPRO.session.orcamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Item;

class TabelaItensOrcamento {

	private String uuid;
	private List<Item> itens = new ArrayList<>();
	
	public TabelaItensOrcamento(String uuid) {
		this.uuid = uuid;
	}

	 
	public BigDecimal getValorTotal() {
		return itens.stream()
				.map(Item::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	 
	
	public void adicionarItem(Etapa etapa) {
		
		Optional<Item> itemEtapaOptional = itens.stream()
											 .filter(i -> i.getEtapa().equals(etapa) && i.getTipo().equals("E") )
											 .findAny();
		
		Item etapaOrcamento = null;
		if (itemEtapaOptional.isPresent()) {
			etapaOrcamento = itemEtapaOptional.get();
			
			// TODO: Calcular subtotais  [mao de obra] [materiais ] [equipamentos]
		} else {
			etapaOrcamento = new Item();
			// mudou para Item
			etapaOrcamento.setTipo("E");
			etapaOrcamento.setEtapa(etapa);
			
			//TODO: Calcular subtotais
			//itemOrcamento.setQuantidade(quantidade);
			// itemOrcamento.setValorUnitario(composicao.getValor());
			itens.add(0, etapaOrcamento);
		}
	}
	public void adicionarItem(Etapa etapa, Composicao composicao, BigDecimal quantidade) {
		
		Optional<Item> itemComposicaoOptional = itens.stream()
											 .filter(i -> i.getComposicao().equals(composicao) && i.getEtapa().equals(etapa))
											 .findAny();
		
		Item composicaoOrcamento = null;
		if (itemComposicaoOptional.isPresent()) {
			composicaoOrcamento = itemComposicaoOptional.get();
			
			// TODO: Calcular subtotais  [mao de obra] [materiais ] [equipamentos]
		} else {
			composicaoOrcamento = new Item();
			composicaoOrcamento.setTipo("C");
			composicaoOrcamento.setEtapa(etapa);
			composicaoOrcamento.setComposicao(composicao);
			composicaoOrcamento.setQuantidade(quantidade);
			
			//TODO: Calcular subtotais
			//itemOrcamento.setQuantidade(quantidade);
			// itemOrcamento.setValorUnitario(composicao.getValor());
			itens.add(0, composicaoOrcamento);
		}
	}
	public void adicionarItem(Etapa etapa, Insumo insumo, BigDecimal quantidade) {
		
		Optional<Item> itemInsumoOptional = itens.stream()
											 .filter(i -> i.getInsumo().equals(insumo) && i.getEtapa().equals(etapa))
											 .findAny();
		
		Item insumoOrcamento = null;
		if (itemInsumoOptional.isPresent()) {
			insumoOrcamento = itemInsumoOptional.get();
			
			// TODO: Calcular subtotais  [mao de obra] [materiais ] [equipamentos]
		} else {
			insumoOrcamento = new Item();
			insumoOrcamento.setTipo("I");
			insumoOrcamento.setEtapa(etapa);
			insumoOrcamento.setInsumo(insumo);
			insumoOrcamento.setQuantidade(quantidade);
			
			//TODO: Calcular subtotais
			//itemOrcamento.setQuantidade(quantidade);
			// itemOrcamento.setValorUnitario(composicao.getValor());
			itens.add(0, insumoOrcamento);
		}
	}
	
	public void alterarQuantidadeItens(Etapa etapa, Composicao composicao, BigDecimal quantidade) {
		Item itemOrcamento = buscarItemPorComposicao(etapa, composicao).get();
		itemOrcamento.setQuantidade(quantidade); 
	}
	
	public void alterarQuantidadeItens(Etapa etapa, Insumo insumo, BigDecimal quantidade) {
		Item itemOrcamento = buscarItemPorInsumo(etapa, insumo).get();
		itemOrcamento.setQuantidade(quantidade); 
	}
	
	public void excluirItem(Etapa etapa, Composicao composicao) {
		int indice = IntStream.range(0, itens.size())
							  .filter(i -> itens.get(i).getComposicao().equals(composicao) && 
									       itens.get(i).getEtapa().equals(etapa))
							  .findAny().getAsInt();
		itens.remove(indice);
	}
	
	public void excluirItem(Etapa etapa, Insumo insumo) {
		int indice = IntStream.range(0, itens.size())
							  .filter(i -> itens.get(i).getInsumo().equals(insumo) && 
									       itens.get(i).getEtapa().equals(etapa))
							  .findAny().getAsInt();
		itens.remove(indice);
	}
	
	public void excluirItem(Etapa etapa) {
		for (int j = 0; j < itens.size(); j++) {
			if (itens.get(j).getEtapa().equals(etapa)) {
				itens.remove(j);
			}
		}
	}
	
	public int total() {
		return itens.size();
	}

	public List<Item> getItens() {
		return itens;
	}
	private Optional<Item> buscarItemPorComposicao(Etapa etapa, Composicao composicao) {
		return itens.stream()
				.filter(i -> i.getComposicao().equals(composicao) && i.getEtapa().equals(etapa) )
				.findAny();
	}
	private Optional<Item> buscarItemPorInsumo(Etapa etapa, Insumo insumo) {
		return itens.stream()
				.filter(i -> i.getInsumo().equals(insumo) && i.getEtapa().equals(etapa) )
				.findAny();
	}

	public String getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabelaItensOrcamento other = (TabelaItensOrcamento) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
}
