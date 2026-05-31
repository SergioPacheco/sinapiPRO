describe('Budget Workflow', () => {
  beforeEach(() => {
    cy.login();
  });

  it('should list budgets', () => {
    cy.visit('/budgets');
    cy.get('[data-cy="budget-list"]').should('be.visible');
    cy.get('[data-cy="budget-row"]').should('have.length.at.least', 1);
  });

  it('should open budget worksheet', () => {
    cy.visit('/budgets');
    cy.get('[data-cy="budget-row"]').first().click();
    cy.url().should('include', '/worksheet');
    cy.get('[data-cy="budget-tree"]').should('be.visible');
  });

  it('should search SINAPI compositions', () => {
    cy.visit('/sinapi');
    cy.get('[data-cy="search-input"]').type('CONCRETO FCK 25');
    cy.get('[data-cy="composition-row"]').should('have.length.at.least', 1);
  });
});
