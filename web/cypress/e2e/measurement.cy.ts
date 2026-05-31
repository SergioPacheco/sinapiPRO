describe('Measurement Workflow', () => {
  beforeEach(() => {
    cy.login();
  });

  it('should display measurement list with status badges', () => {
    cy.visit('/projects');
    cy.get('[data-cy="project-row"]').first().click();
    cy.get('[data-cy="tab-measurements"]').click();
    cy.get('[data-cy="measurement-row"]').should('have.length.at.least', 1);
    cy.get('[data-cy="status-badge"]').should('be.visible');
  });

  it('should show measurement detail with items', () => {
    cy.visit('/projects');
    cy.get('[data-cy="project-row"]').first().click();
    cy.get('[data-cy="tab-measurements"]').click();
    cy.get('[data-cy="measurement-row"]').first().click();
    cy.get('[data-cy="measurement-items"]').should('be.visible');
  });
});
