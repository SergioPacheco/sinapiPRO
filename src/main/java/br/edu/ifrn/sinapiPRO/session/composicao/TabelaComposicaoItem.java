package br.edu.ifrn.sinapiPRO.session.composicao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import br.edu.ifrn.sinapiPRO.model.ComposicaoItem;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.Tipo;

public class TabelaComposicaoItem {

	private String uuid;
	private List<ComposicaoItem> itens = new ArrayList<>();
	
	public TabelaComposicaoItem(String uuid) {
		this.uuid = uuid;
	}

	public BigDecimal getValorTotal() {
		
		return itens
					.stream()
					.map(ComposicaoItem::getValorTotal)
					.reduce(BigDecimal::add)
					.orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(Insumo insumo, BigDecimal coeficiente) {
		
		Optional<ComposicaoItem> itemExistente = buscarItemPorInsumo(insumo);
		
		ComposicaoItem composicaoItem = null;
		
		if (itemExistente.isPresent()) {
			composicaoItem = itemExistente.get();
			composicaoItem.setCoeficiente(composicaoItem.getCoeficiente().add(coeficiente));
		} else {
		    composicaoItem = new ComposicaoItem();
			composicaoItem.setInsumo(insumo);
			composicaoItem.setCodigoItem(insumo.getCodigoInsumo());
			composicaoItem.setUnidade(insumo.getUnidade());
			composicaoItem.setDescricao(insumo.getDescricao());
			composicaoItem.setTipo(Tipo.INSUMO);
			composicaoItem.setPrecoUnitario(insumo.getPrecoPadrao());
			composicaoItem.setCoeficiente(coeficiente);
			itens.add(0, composicaoItem);
		}
	}
	
	public void alterarCoeficiente(Insumo insumo, BigDecimal coeficiente) {
		
		ComposicaoItem composicaoItem = buscarItemPorInsumo(insumo).get();
		composicaoItem.setCoeficiente(coeficiente);
	}
	
	public void excluirItem(Insumo insumo) {
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getInsumo().equals(insumo))
				.findAny().getAsInt();
		itens.remove(indice);
	}
	
	
	public int total() {
		return itens.size();
	}

	public List<ComposicaoItem> getItens() {
		return itens;
	}
	
	public Optional<ComposicaoItem> buscarItemPorInsumo(Insumo insumo) {
		
		return itens.stream()
				.filter(i -> i.getInsumo().equals(insumo))
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
		TabelaComposicaoItem other = (TabelaComposicaoItem) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
}
