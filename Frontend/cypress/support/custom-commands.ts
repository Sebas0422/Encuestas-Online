// Comandos personalizados para Cypress

Cypress.Commands.add('login', (email: string, password: string) => {
  cy.visit('http://localhost:4200/login');
  cy.get('input[type="email"]').type(email);
  cy.get('input[type="password"]').type(password);
  cy.get('button[type="submit"]').click();
  cy.url().should('include', '/campaigns');
});

Cypress.Commands.add('logout', () => {
  cy.get('[data-cy="logout-button"]').click();
  cy.url().should('include', '/login');
});

Cypress.Commands.add('createCampaign', (name: string, description: string, startDate: string, endDate: string) => {
  cy.contains('Nueva Campaña').click();
  cy.get('input[formControlName="name"]').type(name);
  cy.get('textarea[formControlName="description"]').type(description);
  cy.get('input[formControlName="startDate"]').type(startDate);
  cy.get('input[formControlName="endDate"]').type(endDate);
  cy.get('button[type="submit"]').click();
  cy.contains(name).should('be.visible');
});

Cypress.Commands.add('createForm', (title: string, description: string, isPublic: boolean = true, isAnonymous: boolean = true) => {
  cy.contains('Nuevo Formulario').click();
  cy.get('input[formControlName="title"]').type(title);
  cy.get('textarea[formControlName="description"]').type(description);

  if (isPublic) {
    cy.get('select[formControlName="accessMode"]').select('PUBLIC');
  } else {
    cy.get('select[formControlName="accessMode"]').select('PRIVATE');
  }

  if (isAnonymous && isPublic) {
    cy.get('input[formControlName="anonymousMode"]').check();
  }
});

Cypress.Commands.add('addQuestion', (questionText: string, type: 'CHOICE' | 'TRUE_FALSE' | 'TEXT', options?: string[]) => {
  cy.contains('Agregar Pregunta').click();
  cy.get('input[placeholder*="Escribe tu pregunta"]').last().type(questionText);
  cy.get('select').last().select(type);

  if (type === 'CHOICE' && options) {
    options.forEach((option, index) => {
      if (index > 0) {
        cy.contains('Agregar opción').click();
      }
      cy.get('input[placeholder*="Opción"]').eq(index).type(option);
    });
  }
});

Cypress.Commands.add('publishForm', (formTitle: string) => {
  cy.contains(formTitle).parents('.card').within(() => {
    cy.contains('Publicar').click();
  });
  cy.on('window:confirm', () => true);
});

Cypress.Commands.add('answerChoiceQuestion', (optionText: string) => {
  cy.contains(optionText).click();
});

Cypress.Commands.add('answerTrueFalseQuestion', (value: boolean) => {
  cy.get(`input[type="radio"][value="${value}"]`).check();
});

Cypress.Commands.add('answerTextQuestion', (text: string) => {
  cy.get('textarea').last().type(text);
});

Cypress.Commands.add('submitForm', () => {
  cy.contains('Enviar Respuesta').click();
});

Cypress.Commands.add('viewResponses', (formTitle: string) => {
  cy.contains(formTitle).parents('.card').within(() => {
    cy.contains('Ver Respuestas').click();
  });
});

// Declaración de tipos para TypeScript
declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>;
      logout(): Chainable<void>;
      createCampaign(name: string, description: string, startDate: string, endDate: string): Chainable<void>;
      createForm(title: string, description: string, isPublic?: boolean, isAnonymous?: boolean): Chainable<void>;
      addQuestion(questionText: string, type: 'CHOICE' | 'TRUE_FALSE' | 'TEXT', options?: string[]): Chainable<void>;
      publishForm(formTitle: string): Chainable<void>;
      answerChoiceQuestion(optionText: string): Chainable<void>;
      answerTrueFalseQuestion(value: boolean): Chainable<void>;
      answerTextQuestion(text: string): Chainable<void>;
      submitForm(): Chainable<void>;
      viewResponses(formTitle: string): Chainable<void>;
    }
  }
}

export { };
