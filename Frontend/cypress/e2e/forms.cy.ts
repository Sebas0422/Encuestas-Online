describe('Gestión de Formularios', () => {
  const mockForms = [
    {
      id: 1,
      campaignId: 1,
      title: 'Formulario de Satisfacción',
      description: 'Mide la satisfacción del cliente',
      accessMode: 'PUBLIC',
      anonymousMode: true,
      status: 'DRAFT',
      themePrimary: '#0066cc',
      openAt: '2025-01-01T08:00:00',
      closeAt: '2025-12-31T18:00:00',
      allowEditBeforeSubmit: false,
      autoSave: false,
      progressBar: false,
      paginated: false,
      shuffleQuestions: false,
      createdAt: '2025-01-01T00:00:00',
      updatedAt: '2025-01-01T00:00:00'
    },
    {
      id: 2,
      campaignId: 1,
      title: 'Feedback de Producto',
      description: 'Retroalimentación sobre productos',
      accessMode: 'PRIVATE',
      anonymousMode: false,
      status: 'PUBLISHED',
      themePrimary: '#ff6600',
      openAt: '2025-02-01T08:00:00',
      closeAt: '2025-11-30T18:00:00',
      allowEditBeforeSubmit: false,
      autoSave: false,
      progressBar: false,
      paginated: false,
      shuffleQuestions: false,
      createdAt: '2025-02-01T00:00:00',
      updatedAt: '2025-02-01T00:00:00'
    }
  ];

  beforeEach(() => {
    // Mock login
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: {
        tokenType: 'Bearer',
        accessToken: 'mock-jwt-token',
        expiresIn: 3600,
        userId: 1,
        email: 'user0@test.com',
        fullName: 'Usuario Test',
        systemAdmin: false
      }
    }).as('login');

    // Mock campaigns
    cy.intercept('GET', '**/api/campaigns*', {
      statusCode: 200,
      body: {
        items: [{
          id: 1,
          name: 'Campaña Test',
          description: 'Descripción',
          startDate: '2025-01-01T00:00:00',
          endDate: '2025-12-31T23:59:59'
        }],
        total: 1,
        page: 0,
        size: 10
      }
    }).as('getCampaigns');

    // Mock permissions
    cy.intercept('GET', '**/api/campaigns/1/members*', {
      statusCode: 200,
      body: []
    });

    // Mock forms for campaign (match query params exactly)
    cy.intercept('GET', /\/api\/campaigns\/1\/forms(\?.*)?$/, {
      statusCode: 200,
      body: {
        items: mockForms,
        total: 2,
        page: 0,
        size: 20
      }
    }).as('getForms');

    // Login y navegar
    cy.visit('http://localhost:4200/login');
    cy.get('[data-cy="login-email"]').type('user0@test.com');
    cy.get('[data-cy="login-password"]').type('Test123!');
    cy.get('[data-cy="login-submit"]').click();
    cy.wait('@login');

    // Navegar a formularios de la campaña
    cy.get('[data-cy="campaign-card"]').first().click();
    cy.wait('@getForms');
    cy.url().should('include', '/campaigns/1/forms');
  });

  it('Debe mostrar la lista de formularios', () => {
    cy.contains('Gestión de Formularios').should('be.visible');
    cy.get('.form-card').should('have.length', 2);
    cy.contains('Formulario de Satisfacción').should('be.visible');
    cy.contains('Feedback de Producto').should('be.visible');
  });

  it('Debe crear un formulario con secciones y preguntas', () => {
    const newForm = {
      id: 3,
      campaignId: 1,
      title: 'Nuevo Formulario Test',
      description: 'Descripción E2E',
      accessMode: 'PUBLIC',
      anonymousMode: true,
      status: 'DRAFT',
      themePrimary: '#0066cc',
      openAt: '2025-12-10T08:00:00',
      closeAt: '2025-12-31T18:00:00',
      allowEditBeforeSubmit: false,
      autoSave: false,
      progressBar: false,
      paginated: false,
      shuffleQuestions: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    // Mock crear formulario
    cy.intercept('POST', '**/api/campaigns/1/forms', {
      statusCode: 201,
      body: newForm
    }).as('createForm');

    // Mock GET forms actualizado
    cy.intercept('GET', '**/api/campaigns/1/forms*', {
      statusCode: 200,
      body: {
        items: [...mockForms, newForm],
        total: 3,
        page: 0,
        size: 20
      }
    });

    cy.get('[data-cy="create-form-btn"]').click();
    cy.url().should('include', '/forms/create');

    cy.get('[data-cy="form-title"]').type(newForm.title);
    cy.get('[data-cy="form-description"]').type(newForm.description);
    cy.get('[data-cy="form-accessMode"]').select('PUBLIC');
    cy.get('[data-cy="form-anonymousMode"]').check();

    // Llenar fechas
    cy.get('input[name="openAt"]').type('2025-12-10T08:00');
    cy.get('input[name="closeAt"]').type('2025-12-31T18:00');

    cy.get('[data-cy="form-submit"]').click();
    cy.wait('@createForm');

    cy.url().should('include', '/campaigns/1/forms');
    cy.contains(newForm.title).should('be.visible');
  });

  it('Debe cambiar el modo de acceso del formulario', () => {
    // Mock GET form
    cy.intercept('GET', '**/api/forms/1', {
      statusCode: 200,
      body: mockForms[0]
    }).as('getForm');

    // Mock PATCH access mode (el componente usa endpoints específicos)
    cy.intercept('PATCH', '**/api/forms/1/access-mode', {
      statusCode: 200,
      body: { ...mockForms[0], accessMode: 'PRIVATE' }
    }).as('updateAccessMode');

    // Mock PATCH anonymous (cambia automáticamente cuando es PRIVATE)
    cy.intercept('PATCH', '**/api/forms/1/anonymous', {
      statusCode: 200,
      body: { ...mockForms[0], anonymousMode: false }
    }).as('updateAnonymous');

    cy.get('.form-card').first().within(() => {
      cy.get('[data-cy="form-edit-btn"]').click();
    });

    cy.wait('@getForm');
    cy.url().should('include', '/forms/edit/1');

    // Esperar que el formulario esté completamente cargado
    cy.get('[data-cy="form-title"]').should('have.value', 'Formulario de Satisfacción');
    cy.get('[data-cy="form-submit"]').should('not.be.disabled');

    // Cambiar a privado
    cy.get('[data-cy="form-accessMode"]').select('PRIVATE');

    // El modo anónimo debe deshabilitarse automáticamente
    cy.get('[data-cy="form-anonymousMode"]').should('be.disabled');
    cy.get('[data-cy="form-anonymousMode"]').should('not.be.checked');

    cy.get('[data-cy="form-submit"]').click();
    cy.wait('@updateAccessMode');
    cy.wait('@updateAnonymous');
  });

  it('Debe previsualizar un formulario', () => {
    cy.get('.form-card').first().within(() => {
      cy.get('[data-cy="form-preview-btn"]').click();
    });

    cy.url().should('include', '/forms/1/preview');
  });

  it('Debe publicar un formulario desde preguntas', () => {
    // Mock GET form
    cy.intercept('GET', '**/api/forms/1', {
      statusCode: 200,
      body: mockForms[0]
    });

    // Mock GET permissions
    cy.intercept('GET', '**/api/campaigns/1/members*', {
      statusCode: 200,
      body: []
    });

    // Mock GET sections
    cy.intercept('GET', '**/api/forms/1/sections*', {
      statusCode: 200,
      body: { items: [], total: 0, page: 0, size: 50 }
    });

    // Mock GET questions
    cy.intercept('GET', '**/api/forms/1/questions*', {
      statusCode: 200,
      body: { items: [], total: 0, page: 0, size: 50 }
    });

    // Mock POST public-link (endpoint real)
    cy.intercept('POST', '**/api/forms/1/public-link', {
      statusCode: 200,
      body: {
        code: 'ABC123',
        formId: 1,
        publishedAt: new Date().toISOString()
      }
    }).as('publishForm');

    // Navegar a preguntas
    cy.get('.form-card').first().within(() => {
      cy.get('[data-cy="form-questions-btn"]').click();
    });

    cy.url().should('include', '/forms/1/questions');

    // Click en publicar
    cy.get('[data-cy="publish-form-btn"]').click();

    // Confirmar en el modal
    cy.get('[data-cy="confirm-publish-btn"]').click();
    cy.wait('@publishForm');

    // Verificar que el input del enlace contiene el código
    cy.get('input[readonly]').should('have.value').and('contain', '/public/forms/ABC123');
  });

  it('Debe eliminar un formulario', () => {
    // Mock DELETE
    cy.intercept('DELETE', '**/api/forms/2', {
      statusCode: 204
    }).as('deleteForm');

    // Mock GET forms actualizado (sin el form 2)
    cy.intercept('GET', '**/api/campaigns/1/forms*', {
      statusCode: 200,
      body: {
        items: [mockForms[0]],
        total: 1,
        page: 0,
        size: 20
      }
    }).as('getFormsAfterDelete');

    cy.get('.form-card').eq(1).within(() => {
      cy.get('[data-cy="form-delete-btn"]').click();
    });

    cy.on('window:confirm', () => true);
    cy.wait('@deleteForm');
    cy.wait('@getFormsAfterDelete');

    cy.get('.form-card').should('have.length', 1);
    cy.contains('Feedback de Producto').should('not.exist');
  });
});
