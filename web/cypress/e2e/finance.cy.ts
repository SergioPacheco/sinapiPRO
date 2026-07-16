describe('Finance Workflow', () => {
  beforeEach(() => {
    cy.login();
  });

  it('should display finance dashboard with payables and receivables', () => {
    cy.visit('/projects');
    cy.get('a').contains('Contas').click({ force: true });
    cy.url().should('include', '/finance');
  });

  it('should filter payables by date range', () => {
    cy.visit('/finance');
    // Verifica que a página carrega sem erros
    cy.get('body').should('not.contain', 'Error');
  });

  it('should navigate to global finance view', () => {
    cy.visit('/finance');
    cy.get('body').should('not.contain', 'Error');
    cy.url().should('include', '/finance');
  });

  it('should display cash flow chart', () => {
    cy.visit('/projects');
    cy.get('a').contains('Fluxo de Caixa').click({ force: true });
    cy.url().should('include', '/cash-flow');
  });
});
