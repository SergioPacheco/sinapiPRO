/// <reference types="cypress" />
import 'cypress-axe';

Cypress.Commands.add('login', (username = 'admin@sinapipro.dev', password = 'SinapiPro#2026') => {
  cy.request('POST', 'http://localhost:8080/api/auth/token', {
    grantType: 'password', username, password,
  }).then((resp) => {
    window.localStorage.setItem('access_token', resp.body.accessToken);
  });
});

declare global {
  namespace Cypress {
    interface Chainable {
      login(username?: string, password?: string): Chainable<void>;
    }
  }
}

export {};
