package br.edu.ifrn.sinapiPRO.session.composicao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import br.edu.ifrn.sinapiPRO.model.ComposicaoKey;
import br.edu.ifrn.sinapiPRO.model.ItemComposicao;

class TabelaItensComposicao {

	private String uuid;
	private List<ItemComposicao> itens = new ArrayList<>();
	
	public TabelaItensComposicao(String uuid) {
		this.uuid = uuid;
	}

	public BigDecimal getValorTotal() {
		return itens.stream()
				.map(ItemComposicao::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(String tipo, Long codigoItem, BigDecimal coeficiente) {
		
		Optional<ItemComposicao> itemComposicaoOptional = buscarItem(codigoItem);
		
		ItemComposicao itemComposicao = null;
		
		if (itemComposicaoOptional.isPresent()) {
			itemComposicao = itemComposicaoOptional.get();
		} else {
			
			itemComposicao = new ItemComposicao();
			ComposicaoKey compoKey = new ComposicaoKey();
			compoKey.setItemID(codigoItem);
			itemComposicao.setComposicaoKey(compoKey);
			itemComposicao.setCoeficiente(coeficiente);
			
			// Buscar preco {basePreco -> codigoIusmo -> itemBasePreco }
			if (tipo=="INSUMO") {
				
			}
			
			// itemComposicao.setPrecoUnitario(insumo.getPrecoGenerico());
			itens.add(0, itemComposicao);
		}
	}
	
	public void alterarQuantidadeItens(Long codigoItem, BigDecimal coeficiente) {
		
		ItemComposicao itemComposicao = buscarItem(codigoItem).get();
		itemComposicao.setCoeficiente(coeficiente);
	}
	
	public void excluirItem(Long codigoItem) {
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getComposicaoKey().getItemID().equals(codigoItem))
				.findAny().getAsInt();
		itens.remove(indice);
	}
	
	public int total() {
		return itens.size();
	}

	public List<ItemComposicao> getItens() {
		return itens;
	}
	
	private Optional<ItemComposicao> buscarItem(Long codigoItem) {
		return itens.stream()
				.filter(i -> i.getComposicaoKey().getItemID().equals(codigoItem))
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
		TabelaItensComposicao other = (TabelaItensComposicao) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
}
