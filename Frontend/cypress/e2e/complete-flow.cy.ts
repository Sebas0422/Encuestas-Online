import { mockFullSetup, setupAuthMocks, setupCampaignsMocks, setupFormsMocks, setupQuestionsMocks, setupPublicFormsMocks, setupResponsesMocks } from '../support/mock-helpers';

describe('Flujo Completo E2E con Mocks', () => {
  const uniqueId = Date.now();
  const campaignName = `Campaña E2E ${uniqueId}`;
  const formTitle = `Formulario E2E ${uniqueId}`;

  it('Debe completar el flujo completo con mocks: Login -> Crear campaña -> Formulario -> Responder -> Ver estadísticas', () => {
    // 1. SETUP MOCKS INICIALES
    setupAuthMocks();
    setupCampaignsMocks();

    // 2. LOGIN
    cy.visit('http://localhost:4200/login');
    cy.get('[data-cy="login-email"]').type('user0@test.com');
    cy.get('[data-cy="login-password"]').type('Test123!');
    cy.get('[data-cy="login-submit"]').click();
    cy.wait('@login');
    cy.url().should('include', '/campaigns');

    // 3. CREAR CAMPAÑA
    const newCampaign = {
      id: 3,
      name: campaignName,
      description: 'Campaña para prueba E2E completa',
      startDate: '2025-12-01',
      endDate: '2025-12-31',
      createdAt: new Date().toISOString(),
      forms: []
    };

    cy.intercept('POST', '**/api/campaigns', {
      statusCode: 201,
      body: newCampaign
    }).as('createCampaign');

    cy.fixture('campaigns').then((campaigns) => {
      cy.intercept('GET', '**/api/campaigns', {
        statusCode: 200,
        body: [...campaigns, newCampaign]
      });
    });

    cy.contains('Nueva Campaña').click();
    cy.get('input[formControlName="name"]').type(campaignName);
    cy.get('textarea[formControlName="description"]').type('Campaña para prueba E2E completa');
    cy.get('input[formControlName="startDate"]').type('2025-12-01');
    cy.get('input[formControlName="endDate"]').type('2025-12-31');
    cy.get('button[type="submit"]').click();
    cy.wait('@createCampaign');

    // 4. ENTRAR A LA CAMPAÑA
    cy.intercept('GET', `**/api/campaigns/${newCampaign.id}`, {
      statusCode: 200,
      body: newCampaign
    });

    cy.intercept('GET', `**/api/campaigns/${newCampaign.id}/forms`, {
      statusCode: 200,
      body: []
    });

    cy.contains(campaignName).click();

    // 5. CREAR FORMULARIO
    const newForm = {
      id: 10,
      campaignId: newCampaign.id,
      title: formTitle,
      description: 'Formulario de prueba E2E',
      accessMode: 'PUBLIC',
      anonymousMode: true,
      status: 'DRAFT',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    cy.intercept('POST', `**/api/campaigns/${newCampaign.id}/forms`, {
      statusCode: 201,
      body: newForm
    }).as('createForm');

    cy.contains('Nuevo Formulario').click();
    cy.get('input[formControlName="title"]').type(formTitle);
    cy.get('textarea[formControlName="description"]').type('Formulario de prueba E2E');
    cy.get('select[formControlName="accessMode"]').select('PUBLIC');
    cy.get('input[formControlName="anonymousMode"]').check();

    // 6. GUARDAR FORMULARIO (simplificado para el test)
    cy.get('button[type="submit"]').contains('Guardar').click();
    cy.wait('@createForm');

    // 7. PUBLICAR FORMULARIO
    cy.intercept('PATCH', `**/api/forms/${newForm.id}/status`, {
      statusCode: 200,
      body: { ...newForm, status: 'PUBLISHED' }
    }).as('publishForm');

    cy.contains(formTitle).parents('.card').within(() => {
      cy.contains('Publicar').click();
    });
    cy.on('window:confirm', () => true);
    cy.wait('@publishForm');

    // 8. SETUP MOCKS PARA FORMULARIO PÚBLICO
    const mockQuestions = [
      {
        id: 1,
        formId: newForm.id,
        type: 'CHOICE',
        prompt: '¿Cómo calificarías este servicio?',
        required: true,
        options: [
          { id: 1, label: 'Excelente' },
          { id: 2, label: 'Bueno' }
        ]
      },
      {
        id: 2,
        formId: newForm.id,
        type: 'TRUE_FALSE',
        prompt: '¿Recomendarías este servicio?',
        required: true,
        options: [
          { id: 3, label: 'Sí' },
          { id: 4, label: 'No' }
        ]
      },
      {
        id: 3,
        formId: newForm.id,
        type: 'TEXT',
        prompt: 'Comentarios adicionales:',
        required: false,
        textMode: 'LONG'
      }
    ];

    cy.intercept('GET', '**/api/public-forms', {
      statusCode: 200,
      body: [{ ...newForm, status: 'PUBLISHED' }]
    });

    cy.intercept('GET', `**/api/forms/${newForm.id}`, {
      statusCode: 200,
      body: newForm
    });

    cy.intercept('GET', `**/api/forms/${newForm.id}/sections`, {
      statusCode: 200,
      body: []
    });

    cy.intercept('GET', `**/api/forms/${newForm.id}/questions**`, {
      statusCode: 200,
      body: mockQuestions
    });

    cy.intercept('POST', `**/api/forms/${newForm.id}/submissions`, {
      statusCode: 201,
      body: {
        id: 1,
        formId: newForm.id,
        status: 'SUBMITTED',
        submittedAt: new Date().toISOString()
      }
    }).as('submitResponse');

    // 9. RESPONDER FORMULARIO (sin logout, directamente a public-forms)
    cy.visit('http://localhost:4200/public-forms');
    cy.contains(formTitle).should('be.visible');

    cy.contains(formTitle).parents('.card').within(() => {
      cy.contains('Responder').click();
    });

    // 10. COMPLETAR RESPUESTAS
    cy.get('input[type="radio"]').first().check();
    cy.get('input[type="radio"][value="true"]').check();
    cy.get('textarea').type('Este es un comentario de prueba E2E muy completo.');

    // 11. ENVIAR FORMULARIO
    cy.contains('Enviar Respuesta').click();
    cy.wait('@submitResponse');

    // 12. SETUP MOCKS PARA VER ESTADÍSTICAS
    const mockReport = {
      formId: newForm.id,
      totalSubmissions: 1,
      completedCount: 1,
      draftCount: 0,
      questions: [
        {
          questionId: 1,
          questionText: '¿Cómo calificarías este servicio?',
          questionType: 'CHOICE',
          answeredCount: 1,
          omittedCount: 0,
          options: [
            { optionId: 1, label: 'Excelente', count: 1 },
            { optionId: 2, label: 'Bueno', count: 0 }
          ]
        },
        {
          questionId: 2,
          questionText: '¿Recomendarías este servicio?',
          questionType: 'TRUE_FALSE',
          answeredCount: 1,
          omittedCount: 0,
          trueCount: 1,
          falseCount: 0
        },
        {
          questionId: 3,
          questionText: 'Comentarios adicionales:',
          questionType: 'TEXT',
          answeredCount: 1,
          omittedCount: 0,
          responses: ['Este es un comentario de prueba E2E muy completo.']
        }
      ]
    };

    cy.intercept('GET', `**/api/forms/${newForm.id}/report**`, {
      statusCode: 200,
      body: mockReport
    }).as('getReport');

    cy.intercept('GET', `**/api/campaigns/${newCampaign.id}/forms`, {
      statusCode: 200,
      body: [{ ...newForm, status: 'PUBLISHED' }]
    });

    // 13. VOLVER A LOGIN PARA VER ESTADÍSTICAS
    cy.visit('http://localhost:4200/login');
    cy.get('input[type="email"]').type('user0@test.com');
    cy.get('input[type="password"]').type('Test123!');
    cy.get('button[type="submit"]').click();

    // 14. NAVEGAR A RESPUESTAS
    cy.contains(campaignName).click();
    cy.contains(formTitle).parents('.card').within(() => {
      cy.contains('Ver Respuestas').click();
    });

    cy.wait('@getReport');

    // 15. VERIFICAR ESTADÍSTICAS
    cy.contains('Respuestas Totales').should('be.visible');
    cy.contains('1').should('be.visible');

    // 16. VERIFICAR GRÁFICOS
    cy.get('canvas').should('have.length.at.least', 1);

    // 17. CAMBIAR TIPO DE GRÁFICO
    cy.contains('Rosca').click();
    cy.get('canvas').should('be.visible');

    // 18. VER TABLA
    cy.contains('Tabla').click();
    cy.get('table').should('be.visible');
    cy.contains('Excelente').should('be.visible');

    // 19. VERIFICAR TASA DE COMPLETACIÓN
    cy.contains('100').should('be.visible');
  });
});

it('Debe completar el flujo completo: Crear campaña -> Formulario -> Responder -> Ver estadísticas', () => {
  // 1. REGISTRO/LOGIN
  cy.visit('http://localhost:4200/login');
  cy.get('input[type="email"]').type('user0@test.com');
  cy.get('input[type="password"]').type('Test123!');
  cy.get('button[type="submit"]').click();
  cy.url().should('include', '/campaigns');

  // 2. CREAR CAMPAÑA
  cy.contains('Nueva Campaña').click();
  cy.get('input[formControlName="name"]').type(campaignName);
  cy.get('textarea[formControlName="description"]').type('Campaña para prueba E2E completa');
  cy.get('input[formControlName="startDate"]').type('2025-12-01');
  cy.get('input[formControlName="endDate"]').type('2025-12-31');
  cy.get('button[type="submit"]').click();
  cy.contains(campaignName).should('be.visible');

  // 3. ENTRAR A LA CAMPAÑA
  cy.contains(campaignName).click();

  // 4. CREAR FORMULARIO
  cy.contains('Nuevo Formulario').click();
  cy.get('input[formControlName="title"]').type(formTitle);
  cy.get('textarea[formControlName="description"]').type('Formulario de prueba E2E');
  cy.get('select[formControlName="accessMode"]').select('PUBLIC');
  cy.get('input[formControlName="anonymousMode"]').check();

  // 5. AGREGAR SECCIÓN
  cy.contains('Agregar Sección').click();
  cy.get('input[placeholder*="Título de la sección"]').type('Información General');

  // 6. AGREGAR PREGUNTA CHOICE
  cy.contains('Agregar Pregunta').click();
  cy.get('input[placeholder*="Escribe tu pregunta"]').type('¿Cómo calificarías este servicio?');
  cy.get('select').contains('Selección').parent().select('CHOICE');
  cy.get('input[placeholder*="Opción"]').eq(0).type('Excelente');
  cy.contains('Agregar opción').click();
  cy.get('input[placeholder*="Opción"]').eq(1).type('Bueno');
  cy.contains('Agregar opción').click();
  cy.get('input[placeholder*="Opción"]').eq(2).type('Regular');
  cy.contains('Agregar opción').click();
  cy.get('input[placeholder*="Opción"]').eq(3).type('Malo');

  // 7. AGREGAR PREGUNTA TRUE_FALSE
  cy.contains('Agregar Pregunta').click();
  cy.get('input[placeholder*="Escribe tu pregunta"]').last().type('¿Recomendarías este servicio?');
  cy.get('select').last().select('TRUE_FALSE');

  // 8. AGREGAR PREGUNTA TEXT
  cy.contains('Agregar Pregunta').click();
  cy.get('input[placeholder*="Escribe tu pregunta"]').last().type('Comentarios adicionales:');
  cy.get('select').last().select('TEXT');

  // 9. GUARDAR FORMULARIO
  cy.get('button[type="submit"]').contains('Guardar').click();
  cy.contains(formTitle).should('be.visible');

  // 10. PUBLICAR FORMULARIO
  cy.contains(formTitle).parents('.card').within(() => {
    cy.contains('Publicar').click();
  });
  cy.on('window:confirm', () => true);
  cy.wait(1000);

  // 11. CERRAR SESIÓN
  cy.get('[data-cy="logout-button"]').click();

  // 12. RESPONDER FORMULARIO ANÓNIMAMENTE
  cy.visit('http://localhost:4200/public-forms');
  cy.contains(formTitle).should('be.visible');
  cy.contains(formTitle).parents('.card').within(() => {
    cy.contains('Responder').click();
  });

  // 13. COMPLETAR RESPUESTAS
  // Responder pregunta CHOICE
  cy.contains('Excelente').click();

  // Responder pregunta TRUE_FALSE
  cy.get('input[type="radio"][value="true"]').check();

  // Responder pregunta TEXT
  cy.get('textarea').type('Este es un comentario de prueba E2E muy completo.');

  // 14. ENVIAR FORMULARIO
  cy.contains('Enviar Respuesta').click();
  cy.contains('enviada', { matchCase: false }).should('be.visible');

  // 15. LOGIN DE NUEVO PARA VER ESTADÍSTICAS
  cy.visit('http://localhost:4200/login');
  cy.get('input[type="email"]').type('user0@test.com');
  cy.get('input[type="password"]').type('Test123!');
  cy.get('button[type="submit"]').click();

  // 16. NAVEGAR A RESPUESTAS
  cy.contains(campaignName).click();
  cy.contains(formTitle).parents('.card').within(() => {
    cy.contains('Ver Respuestas').click();
  });

  // 17. VERIFICAR ESTADÍSTICAS
  cy.contains('Respuestas Totales').should('be.visible');
  cy.contains('1').should('be.visible'); // Al menos 1 respuesta

  // 18. VERIFICAR GRÁFICOS
  cy.get('canvas').should('have.length.at.least', 1);

  // 19. CAMBIAR TIPO DE GRÁFICO
  cy.contains('Rosca').click();
  cy.get('canvas').should('be.visible');

  // 20. VER TABLA
  cy.contains('Tabla').click();
  cy.get('table').should('be.visible');
  cy.contains('Excelente').should('be.visible');

  // 21. VERIFICAR TASA DE COMPLETACIÓN
  cy.contains('100%').should('be.visible');
});
});
