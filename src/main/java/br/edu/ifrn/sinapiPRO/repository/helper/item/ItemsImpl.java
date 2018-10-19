package br.edu.ifrn.sinapiPRO.repository.helper.item;

public class ItemsImpl implements ItemsQueries {
/*
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Orcamento> filtrar(OrcamentoFilter filtro, Pageable pageable) {
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Orcamento.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	@Transactional(readOnly = true)
	@Override
	public Orcamento buscarComItens(Long codigo) {
		@SuppressWarnings("deprecation")
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Orcamento.class);
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Orcamento) criteria.uniqueResult();
		
	}
	
	@Override
	public BigDecimal valorTotalNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Orcamento where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", SituacaoOrcamento.CONCLUIDO)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal) from Orcamento where month(dataCriacao) = :mes and status = :status", BigDecimal.class)
					.setParameter("mes", MonthDay.now().getMonthValue())
					.setParameter("status", SituacaoOrcamento.CONCLUIDO)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTicketMedioNoAno() {

		Optional<BigDecimal> optional = Optional.ofNullable(
				manager.createQuery("select sum(valorTotal)/count(*) from Orcamento where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", SituacaoOrcamento.CONCLUIDO)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
 
	private Long total(OrcamentoFilter filtro) {
		@SuppressWarnings("deprecation")
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Orcamento.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(OrcamentoFilter filtro, Criteria criteria) {
		
		if (filtro != null) {
				
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			
		}
	}
*/
}
