package br.edu.ifrn.sinapiPRO.session.orcamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.OrcamentoItem;

class TabelaItensOrcamento {

	/*
	private String uuid;
	private List<OrcamentoItem> itens = new ArrayList<>();
	
	public void Itemizar() { 
		
	 
		 Collections.sort(itens, new Comparator<OrcamentoItem>() {
		        @Override public int compare(OrcamentoItem p1, OrcamentoItem p2) {
		            return p1.getEtapa().getCodigo().intValue() - p2.getEtapa().getCodigo().intValue(); // Ascending
		        }

		 });
		
		Long aux =  0L;
		Long sub = 1L; 
		for (OrcamentoItem orcamentoItem : itens) {
			if (orcamentoItem.getEtapa().getCodigo() == aux) { 
			    if (orcamentoItem.getTipo().equals("E")) { 
			    	orcamentoItem.setItemizacao(orcamentoItem.getEtapa().getCodigo()+".");
			    } else { 
			    	orcamentoItem.setItemizacao(orcamentoItem.getEtapa().getCodigo()+"."+sub+".");
			    	 sub++;
			    }
				continue; 
			}
		    aux = orcamentoItem.getEtapa().getCodigo();
		    sub = 1L;
		    if (orcamentoItem.getTipo().equals("E")) { 
		    	orcamentoItem.setItemizacao(orcamentoItem.getEtapa().getCodigo()+".");
		    } else { 
		    	orcamentoItem.setItemizacao(orcamentoItem.getEtapa().getCodigo()+"."+sub+".");
		    	sub++;
		    }
		} 
		 
		Collections.sort(itens, (o1, o2) -> (o1.getItemizacao().compareTo(o2.getItemizacao())));
		
	}
	
	public TabelaItensOrcamento(String uuid) {
		this.uuid = uuid;
	}
 
	 
	public BigDecimal getValorTotal() {
		return itens.stream()
				.map(OrcamentoItem::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal getValorMaoObra() {
		return itens.stream()
				.map(OrcamentoItem::getValorMaoObra)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal getValorMaterial() {
		return itens.stream()
				.map(OrcamentoItem::getValorMaterial)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public BigDecimal getValorEquipamento() {
		return itens.stream()
				.map(OrcamentoItem::getValorEquipamento)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	 
	
	public void adicionarItem(Etapa etapa) {
		
		Optional<OrcamentoItem> itemEtapaOptional = itens.stream()
				 								.filter(i -> i.getEtapa()  != null )
											    .filter(i -> i.getEtapa().equals(etapa) )
											    .findAny();
		
		OrcamentoItem itemEtapaOrcamento = null;
		if (itemEtapaOptional.isPresent()) {
			
			System.out.println();
			itemEtapaOrcamento = itemEtapaOptional.get();
			itemEtapaOrcamento.setItemizacao(etapa.getCodigo().toString()+".");
			
			// TODO: Calcular subtotais {servico, material, equipamento}
			
		} else {
			itemEtapaOrcamento = new OrcamentoItem();
			itemEtapaOrcamento.setTipo("E");
			itemEtapaOrcamento.setEtapa(etapa);
			itemEtapaOrcamento.setItemizacao(etapa.getCodigo().toString()+".");
						
			//TODO: Calcular subtotais {servico, material, equipamento}
			// itemOrcamento.setQuantidade(quantidade);
			// itemOrcamento.setValorUnitario(composicao.getValor());
			itens.add(0, itemEtapaOrcamento);
		}
	}
	public void adicionarItem(Etapa etapa, Composicao composicao, BigDecimal quantidade) {
		
		Optional<OrcamentoItem> itemComposicaoOptional = itens.stream()
											 .filter(i -> i.getEtapa()      != null )
											 .filter(i -> i.getComposicao() != null )
											 .filter(i -> i.getComposicao().equals(composicao) && i.getEtapa().equals(etapa))
											 .findAny();
		
		OrcamentoItem composicaoOrcamento = null;
		if (itemComposicaoOptional.isPresent()) {
			composicaoOrcamento = itemComposicaoOptional.get();
			
			// TODO: Calcular subtotais  [mao de obra] [materiais ] [equipamentos]
		} else {
			
			composicaoOrcamento = new OrcamentoItem();
			composicaoOrcamento.setTipo("C");
			composicaoOrcamento.setEtapa(etapa);
			composicaoOrcamento.setComposicao(composicao);
			composicaoOrcamento.setQuantidade(quantidade);
			
			// TODO: Pesquisar base de preco selecionada no orcamento
			composicaoOrcamento.setValorUnitario(composicao.getCustoTotal());
			
			
			//TODO: Calcular subtotais
			//itemOrcamento.setQuantidade(quantidade);
			// itemOrcamento.setValorUnitario(composicao.getValor());
			itens.add(0, composicaoOrcamento);
		}
	}
	
	public void adicionarItem(Etapa etapa, Insumo insumo, BigDecimal quantidade) {
		
		Optional<OrcamentoItem> itemInsumoOptional = itens.stream() 
									   .filter(i -> i.getEtapa()  != null )
									   .filter(i -> i.getInsumo() != null )
									   .filter(i -> i.getInsumo().equals(insumo) && i.getEtapa().equals(etapa)  )
									   .findAny();
		OrcamentoItem insumoOrcamento = null;
		if (itemInsumoOptional.isPresent()) {
			insumoOrcamento = itemInsumoOptional.get();
			
			// TODO: Calcular subtotais  [mao de obra] [materiais ] [equipamentos]
		} else {
			insumoOrcamento = new OrcamentoItem();
			insumoOrcamento.setTipo("I");
			insumoOrcamento.setEtapa(etapa);
			insumoOrcamento.setInsumo(insumo);
			insumoOrcamento.setQuantidade(quantidade);
			insumoOrcamento.setValorUnitario(insumo.getPrecoPadrao());
			
			//TODO: Calcular subtotais
			//itemOrcamento.setQuantidade(quantidade);
			// itemOrcamento.setValorUnitario(composicao.getValor());
			itens.add(0, insumoOrcamento);
		}
	}
	
	public void alterarQuantidadeItens(Etapa etapa, Composicao composicao, BigDecimal quantidade) {
		Optional<OrcamentoItem> itemOrcamento = buscarItemPorComposicao(etapa, composicao);
		
		if (!itemOrcamento.isPresent()) {
			System.out.println("Item não encontrado: etapa="+etapa.getCodigo() + " composicao=" + composicao.getCodigo() );
		} else {
			itemOrcamento.get().setQuantidade(quantidade);
		}
		
		 
	}
	
	public void alterarQuantidadeItens(Etapa etapa, Insumo insumo, BigDecimal quantidade) {
		Optional<OrcamentoItem> itemOrcamento = buscarItemPorInsumo(etapa, insumo);
		
		if (!itemOrcamento.isPresent()) {
			System.out.println("Item não encontrado: etapa="+etapa.getCodigo() + " insumo=" + insumo.getCodigoInsumo() );
		} else {
			itemOrcamento.get().setQuantidade(quantidade);
		}
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

	public List<OrcamentoItem> getItens() {
		return itens;
	}
	private Optional<OrcamentoItem> buscarItemPorComposicao(Etapa etapa, Composicao composicao) {
		return itens.stream()
				.filter(i -> i.getComposicao() != null )
				.filter(i -> i.getEtapa()      != null )
				.filter(i -> i.getComposicao().equals(composicao) && i.getEtapa().equals(etapa) )
				.findAny();
	}
	private Optional<OrcamentoItem> buscarItemPorInsumo(Etapa etapa, Insumo insumo) {
		return itens.stream()
				.filter(i -> i.getInsumo() != null )
				.filter(i -> i.getEtapa()  != null )
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
	*/ 
}
