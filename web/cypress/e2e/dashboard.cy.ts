describe('Dashboard', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/');
  });

  it('should display the dashboard with KPI cards', () => {
    cy.get('[data-cy="dashboard"]').should('be.visible');
    cy.get('[data-cy="kpi-card"]').should('have.length.at.least', 3);
  });

  it('should navigate to projects list', () => {
    cy.get('[data-cy="nav-projects"]').click();
    cy.url().should('include', '/projects');
    cy.get('[data-cy="project-list"]').should('be.visible');
  });
});
