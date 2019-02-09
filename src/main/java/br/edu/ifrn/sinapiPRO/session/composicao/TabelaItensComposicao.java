package br.edu.ifrn.sinapiPRO.session.composicao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import br.edu.ifrn.sinapiPRO.model.ComposicaoItem;

class TabelaItensComposicao {

	private String uuid;
	private List<ComposicaoItem> itens = new ArrayList<>();
	
	public TabelaItensComposicao(String uuid) {
		this.uuid = uuid;
	}

	public BigDecimal getValorTotal() {
		
		return itens
					.stream()
					.map(ComposicaoItem::getValorTotal)
					.reduce(BigDecimal::add)
					.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(String tipo, Long codigoItem, BigDecimal precoUnitario, BigDecimal coeficiente) {
		
		Optional<ComposicaoItem> itemExistente = buscarItem(codigoItem);
		
		ComposicaoItem composicaoItem = null;
		
		if (itemExistente.isPresent()) {
			composicaoItem = itemExistente.get();
		} else {
		    composicaoItem = new ComposicaoItem();
			composicaoItem.setTipo(tipo);
			composicaoItem.setCodigoItem(codigoItem);
			composicaoItem.setCoeficiente(coeficiente);
			composicaoItem.setPrecoUnitario(precoUnitario);
			
			// Buscar preco {basePreco -> codigoIusmo -> itemBasePreco }
			
			// itemComposicao.setPrecoUnitario(insumo.getPrecoPadrao());
			itens.add(0, composicaoItem);
		}
	}
	
	public void alterarCoeficiente(Long codigoItem, BigDecimal coeficiente) {
		
		ComposicaoItem composicaoItem = buscarItem(codigoItem).get();
		composicaoItem.setCoeficiente(coeficiente);
	}
	
	public void excluirItem(Long codigoItem) {
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getCodigoItem().equals(codigoItem))
				.findAny().getAsInt();
		itens.remove(indice);
	}
	
	public int quantidadeItens() {
		return itens.size();
	}

	public List<ComposicaoItem> getItens() {
		return itens;
	}
	
	private Optional<ComposicaoItem> buscarItem(Long codigoItem) {
		
		return itens.stream()
				.filter(i -> i.getCodigoItem().equals(codigoItem))
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
