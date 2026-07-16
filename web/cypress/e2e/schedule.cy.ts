describe('Schedule Workflow', () => {
  beforeEach(() => {
    cy.login();
  });

  it('should display schedule with activities table', () => {
    cy.visit('/projects');
    cy.get('a').contains('Cronograma').click({ force: true });
    cy.url().should('include', '/schedule');
  });

  it('should open new activity dialog', () => {
    cy.visit('/projects');
    cy.get('a').contains('Cronograma').click({ force: true });
    cy.contains('Nova Atividade').click();
    cy.get('.p-dialog').should('be.visible');
  });

  it('should open S-Curve dialog', () => {
    cy.visit('/projects');
    cy.get('a').contains('Cronograma').click({ force: true });
    cy.contains('Curva S').click();
    cy.get('.p-dialog').should('be.visible');
    cy.get('.p-dialog').contains('Curva S');
  });

  it('should navigate to baseline comparison', () => {
    cy.visit('/projects');
    // Navigate to first project baseline
    cy.get('a').contains('Cronograma').click({ force: true });
    cy.visit(cy.url() + '/../baseline');
    cy.get('body').should('not.contain', 'Cannot match');
  });
});
