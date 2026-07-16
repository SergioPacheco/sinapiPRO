/// <reference types="cypress" />

describe('Accessibility (WCAG 2.1 AA)', () => {
  beforeEach(() => {
    cy.login();
  });

  it('Dashboard has no critical a11y violations', () => {
    cy.visit('/dashboard');
    cy.injectAxe();
    cy.checkA11y(null, {
      includedImpacts: ['critical', 'serious'],
      rules: {
        // PrimeNG components may have known issues — track separately
        'color-contrast': { enabled: true },
        'button-name': { enabled: true },
        'link-name': { enabled: true },
        'image-alt': { enabled: true },
      },
    });
  });

  it('Budget worksheet has no critical a11y violations', () => {
    cy.visit('/budgets');
    cy.injectAxe();
    cy.checkA11y(null, {
      includedImpacts: ['critical', 'serious'],
    });
  });

  it('Skip navigation link is accessible via keyboard', () => {
    cy.visit('/dashboard');
    // Tab into the skip link (first focusable element)
    cy.get('body').tab();
    cy.focused().should('have.class', 'skip-link');
    cy.focused().should('have.attr', 'href', '#main-content');
  });

  it('Sidebar collapse button has aria-label', () => {
    cy.visit('/dashboard');
    cy.get('.collapse-btn').should('have.attr', 'aria-label');
  });

  it('Notification bell is a button with aria-label', () => {
    cy.visit('/dashboard');
    cy.get('.btn-icon-topbar[aria-label="Notificações"]').should('exist');
  });
});
