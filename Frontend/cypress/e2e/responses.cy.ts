describe('Respuestas de Formularios', () => {
  beforeEach(() => {
    // Mock formularios públicos
    cy.fixture('forms').then((forms) => {
      const publicForms = forms.filter((f: any) => f.status === 'PUBLISHED' && f.accessMode === 'PUBLIC');
      cy.intercept('GET', '**/api/public-forms', {
        statusCode: 200,
        body: publicForms
      }).as('getPublicForms');
    });

    cy.visit('http://localhost:4200/public-forms');
    cy.wait('@getPublicForms');
  });

  it('Debe mostrar formularios públicos disponibles', () => {
    cy.contains('Formularios Públicos').should('be.visible');
    cy.get('.card').should('have.length.at.least', 1);
  });

  it('Debe responder un formulario anónimamente', () => {
    // Mock GET form con preguntas
    cy.fixture('forms').then((forms) => {
      cy.fixture('questions').then((questions) => {
        const publicForm = forms.find((f: any) => f.status === 'PUBLISHED');

        cy.intercept('GET', `**/api/forms/${publicForm.id}`, {
          statusCode: 200,
          body: publicForm
        });

        cy.intercept('GET', `**/api/forms/${publicForm.id}/sections`, {
          statusCode: 200,
          body: []
        });

        cy.intercept('GET', `**/api/forms/${publicForm.id}/questions**`, {
          statusCode: 200,
          body: questions
        });

        // Mock POST submit response
        cy.intercept('POST', `**/api/forms/${publicForm.id}/submissions`, {
          statusCode: 201,
          body: {
            id: 1,
            formId: publicForm.id,
            status: 'SUBMITTED',
            submittedAt: new Date().toISOString()
          }
        }).as('submitResponse');
      });
    });

    cy.get('.card').first().within(() => {
      cy.contains('Responder').click();
    });

    cy.url().should('include', '/submit');

    // Responder preguntas
    cy.get('input[type="radio"]').first().check();

    cy.contains('Enviar Respuesta').click();
    cy.wait('@submitResponse');
    cy.contains('enviada', { matchCase: false }).should('be.visible');
  });

  it('Debe validar campos requeridos', () => {
    cy.fixture('forms').then((forms) => {
      cy.fixture('questions').then((questions) => {
        const publicForm = forms.find((f: any) => f.status === 'PUBLISHED');

        cy.intercept('GET', `**/api/forms/${publicForm.id}`, {
          statusCode: 200,
          body: publicForm
        });

        cy.intercept('GET', `**/api/forms/${publicForm.id}/sections`, {
          statusCode: 200,
          body: []
        });

        cy.intercept('GET', `**/api/forms/${publicForm.id}/questions**`, {
          statusCode: 200,
          body: questions
        });
      });
    });

    cy.get('.card').first().within(() => {
      cy.contains('Responder').click();
    });

    // Intentar enviar sin responder campos requeridos
    cy.contains('Enviar Respuesta').click();
    cy.contains('requerido', { matchCase: false }).should('be.visible');
  });

  it('Debe responder pregunta tipo TEXT', () => {
    cy.fixture('forms').then((forms) => {
      cy.fixture('questions').then((questions) => {
        const publicForm = forms.find((f: any) => f.status === 'PUBLISHED');

        cy.intercept('GET', `**/api/forms/${publicForm.id}`, {
          statusCode: 200,
          body: publicForm
        });

        cy.intercept('GET', `**/api/forms/${publicForm.id}/sections`, {
          statusCode: 200,
          body: []
        });

        cy.intercept('GET', `**/api/forms/${publicForm.id}/questions**`, {
          statusCode: 200,
          body: questions
        });

        cy.intercept('POST', `**/api/forms/${publicForm.id}/submissions`, {
          statusCode: 201,
          body: { id: 1, formId: publicForm.id, status: 'SUBMITTED' }
        });
      });
    });

    cy.get('.card').first().within(() => {
      cy.contains('Responder').click();
    });

    // Responder campos requeridos primero
    cy.get('input[type="radio"]').first().check();

    // Responder pregunta de texto
    cy.get('textarea').type('Esta es mi respuesta de texto para la prueba E2E');

    cy.contains('Enviar Respuesta').click();
  });
});

describe('Visualización de Respuestas (Admin)', () => {
  beforeEach(() => {
    // Mock login
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: {
        accessToken: 'mock-jwt-token-12345',
        user: { id: 1, email: 'user0@test.com', name: 'Usuario Test' }
      }
    });

    // Mock campaigns
    cy.fixture('campaigns').then((campaigns) => {
      cy.intercept('GET', '**/api/campaigns', {
        statusCode: 200,
        body: campaigns
      });

      cy.intercept('GET', '**/api/campaigns/1', {
        statusCode: 200,
        body: campaigns[0]
      });
    });

    // Mock forms
    cy.fixture('forms').then((forms) => {
      cy.intercept('GET', '**/api/campaigns/1/forms', {
        statusCode: 200,
        body: forms
      });

      cy.intercept('GET', `**/api/forms/${forms[0].id}`, {
        statusCode: 200,
        body: forms[0]
      });
    });

    // Mock questions
    cy.fixture('questions').then((questions) => {
      cy.intercept('GET', '**/api/forms/1/sections', {
        statusCode: 200,
        body: []
      });

      cy.intercept('GET', '**/api/forms/1/questions**', {
        statusCode: 200,
        body: questions
      });
    });

    // Mock responses report
    cy.fixture('responses-report').then((report) => {
      cy.intercept('GET', '**/api/forms/1/report**', {
        statusCode: 200,
        body: report
      }).as('getReport');
    });

    // Login como admin
    cy.visit('http://localhost:4200/login');
    cy.get('input[type="email"]').type('user0@test.com');
    cy.get('input[type="password"]').type('Test123!');
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/campaigns');

    // Navegar a un formulario
    cy.get('.card').first().click();
    cy.get('.card').first().within(() => {
      cy.contains('Ver Respuestas').click();
    });

    cy.wait('@getReport');
  });

  it('Debe mostrar estadísticas de respuestas', () => {
    cy.contains('Respuestas Totales').should('be.visible');
    cy.contains('15').should('be.visible');
    cy.contains('Completadas').should('be.visible');
    cy.contains('12').should('be.visible');
    cy.contains('Tasa de Completación').should('be.visible');
  });

  it('Debe mostrar gráficos de respuestas', () => {
    cy.get('canvas').should('have.length.at.least', 1);
    cy.get('.chart-container').should('be.visible');
  });

  it('Debe cambiar tipo de gráfico (Pastel/Rosca/Tabla)', () => {
    // Cambiar a rosca
    cy.contains('Rosca').click();
    cy.get('canvas').should('be.visible');

    // Cambiar a tabla
    cy.contains('Tabla').click();
    cy.get('table').should('be.visible');

    // Volver a pastel
    cy.contains('Pastel').click();
    cy.get('canvas').should('be.visible');
  });

  it('Debe mostrar datos correctos en las tablas', () => {
    // Cambiar a vista tabla
    cy.contains('Tabla').first().click();
    cy.get('table').within(() => {
      cy.contains('Excelente').should('be.visible');
      cy.contains('Bueno').should('be.visible');
    });
  });
});
