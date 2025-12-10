// Utilidades para mocks de Cypress

export const setupAuthMocks = () => {
  cy.intercept('POST', '**/api/auth/login', {
    statusCode: 200,
    body: {
      tokenType: 'Bearer',
      accessToken: 'mock-jwt-token-12345',
      expiresIn: 3600,
      userId: 1,
      email: 'user0@test.com',
      fullName: 'Usuario Test',
      systemAdmin: false
    }
  }).as('login');

  cy.intercept('POST', '**/api/auth/register', {
    statusCode: 201,
    body: {
      tokenType: 'Bearer',
      accessToken: 'mock-jwt-token-67890',
      expiresIn: 3600,
      userId: 2,
      email: 'newuser@test.com',
      fullName: 'Nuevo Usuario',
      systemAdmin: false
    }
  }).as('register');
};

export const setupCampaignsMocks = () => {
  cy.fixture('campaigns').then((campaigns) => {
    cy.intercept('GET', '**/api/campaigns', {
      statusCode: 200,
      body: campaigns
    }).as('getCampaigns');

    cy.intercept('GET', '**/api/campaigns/1', {
      statusCode: 200,
      body: campaigns[0]
    }).as('getCampaign');
  });
};

export const setupFormsMocks = (campaignId: number = 1) => {
  cy.fixture('forms').then((forms) => {
    cy.intercept('GET', `**/api/campaigns/${campaignId}/forms`, {
      statusCode: 200,
      body: forms
    }).as('getForms');

    forms.forEach((form: any) => {
      cy.intercept('GET', `**/api/forms/${form.id}`, {
        statusCode: 200,
        body: form
      });
    });
  });
};

export const setupQuestionsMocks = (formId: number = 1) => {
  cy.fixture('questions').then((questions) => {
    cy.intercept('GET', `**/api/forms/${formId}/sections`, {
      statusCode: 200,
      body: []
    });

    cy.intercept('GET', `**/api/forms/${formId}/questions**`, {
      statusCode: 200,
      body: questions
    }).as('getQuestions');
  });
};

export const setupResponsesMocks = (formId: number = 1) => {
  cy.fixture('responses-report').then((report) => {
    cy.intercept('GET', `**/api/forms/${formId}/report**`, {
      statusCode: 200,
      body: report
    }).as('getReport');
  });
};

export const setupPublicFormsMocks = () => {
  cy.fixture('forms').then((forms) => {
    const publicForms = forms.filter((f: any) => f.status === 'PUBLISHED' && f.accessMode === 'PUBLIC');
    cy.intercept('GET', '**/api/public-forms', {
      statusCode: 200,
      body: publicForms
    }).as('getPublicForms');
  });
};

export const mockLogin = (email: string = 'user0@test.com', password: string = 'Test123!') => {
  setupAuthMocks();
  cy.visit('http://localhost:4200/login');
  cy.get('[data-cy="login-email"]').type(email);
  cy.get('[data-cy="login-password"]').type(password);
  cy.get('[data-cy="login-submit"]').click();
  cy.wait('@login');
};

export const mockLoginAndNavigateToCampaigns = () => {
  setupAuthMocks();
  setupCampaignsMocks();

  cy.visit('http://localhost:4200/login');
  cy.get('[data-cy="login-email"]').type('user0@test.com');
  cy.get('[data-cy="login-password"]').type('Test123!');
  cy.get('[data-cy="login-submit"]').click();
  cy.wait('@login');
  cy.url().should('include', '/campaigns');
};

export const mockFullSetup = () => {
  setupAuthMocks();
  setupCampaignsMocks();
  setupFormsMocks();
  setupQuestionsMocks();
  setupResponsesMocks();
};
