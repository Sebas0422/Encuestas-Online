describe('Gestión de Campañas', () => {
  const mockCampaigns = [
    {
      id: 1,
      name: 'Campaña de Satisfacción 2025',
      description: 'Encuesta de satisfacción del cliente',
      startDate: '2025-01-01T00:00:00',
      endDate: '2025-12-31T23:59:59',
      createdAt: '2025-01-01T00:00:00'
    },
    {
      id: 2,
      name: 'Feedback Producto',
      description: 'Retroalimentación sobre nuevos productos',
      startDate: '2025-02-01T00:00:00',
      endDate: '2025-11-30T23:59:59',
      createdAt: '2025-02-01T00:00:00'
    }
  ];

  const mockPaginatedResponse = {
    items: mockCampaigns,
    total: 2,
    page: 0,
    size: 10
  };

  beforeEach(() => {
    // Mock login con estructura correcta
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

    // Mock GET campaigns - debe devolver estructura paginada
    cy.intercept('GET', '**/api/campaigns*', {
      statusCode: 200,
      body: mockPaginatedResponse
    }).as('getCampaigns');

    // Mock permissions for each campaign - devuelve vacío para que sea creador
    mockCampaigns.forEach(campaign => {
      cy.intercept('GET', `**/api/campaigns/${campaign.id}/members`, {
        statusCode: 200,
        body: []
      }).as(`getMembers${campaign.id}`);
    });

    cy.visit('http://localhost:4200/login');
    cy.get('[data-cy="login-email"]').type('user0@test.com');
    cy.get('[data-cy="login-password"]').type('Test123!');
    cy.get('[data-cy="login-submit"]').click();
    cy.wait('@login');
    cy.url().should('include', '/campaigns');
    cy.wait('@getCampaigns');
  });

  it('Debe mostrar la lista de campañas', () => {
    cy.contains('Campañas').should('be.visible');
    cy.get('[data-cy="campaign-card"]').should('have.length', 2);
    cy.contains('Campaña de Satisfacción 2025').should('be.visible');
    cy.contains('Feedback Producto').should('be.visible');
  });

  it('Debe crear una nueva campaña', () => {
    const newCampaign = {
      id: 3,
      name: 'Nueva Campaña Test',
      description: 'Descripción de prueba E2E',
      startDate: '2025-12-10T08:00:00',
      endDate: '2025-12-31T18:00:00',
      createdAt: new Date().toISOString()
    };

    // Mock POST create campaign
    cy.intercept('POST', '**/api/campaigns', {
      statusCode: 201,
      body: newCampaign
    }).as('createCampaign');

    // Mock GET campaigns actualizado después de crear
    const updatedPaginatedResponse = {
      items: [...mockCampaigns, newCampaign],
      total: 3,
      page: 0,
      size: 10
    };

    cy.intercept('GET', '**/api/campaigns*', {
      statusCode: 200,
      body: updatedPaginatedResponse
    }).as('getCampaignsUpdated');

    // Mock members para la nueva campaña
    cy.intercept('GET', `**/api/campaigns/${newCampaign.id}/members`, {
      statusCode: 200,
      body: []
    });

    cy.get('[data-cy="create-campaign-btn"]').click();
    cy.url().should('include', '/campaigns/create');

    cy.get('[data-cy="campaign-name"]').type(newCampaign.name);
    cy.get('[data-cy="campaign-description"]').type(newCampaign.description);
    cy.get('[data-cy="campaign-startDate"]').type('2025-12-10T08:00');
    cy.get('[data-cy="campaign-endDate"]').type('2025-12-31T18:00');

    cy.get('[data-cy="campaign-submit"]').click();
    cy.wait('@createCampaign');

    cy.url().should('include', '/campaigns');
    cy.wait('@getCampaignsUpdated');
    cy.contains(newCampaign.name).should('be.visible');
  });

  it('Debe validar fechas de campaña', () => {
    cy.get('[data-cy="create-campaign-btn"]').click();
    cy.url().should('include', '/campaigns/create');

    cy.get('[data-cy="campaign-name"]').type('Campaña con fechas inválidas');
    cy.get('[data-cy="campaign-description"]').type('Descripción');
    cy.get('[data-cy="campaign-startDate"]').type('2025-12-31T23:00');
    cy.get('[data-cy="campaign-endDate"]').type('2025-12-01T08:00');

    // El formulario NO debe permitir enviar porque tiene validación client-side
    cy.get('[data-cy="campaign-submit"]').should('not.be.disabled');
    cy.get('[data-cy="campaign-submit"]').click();

    // Verificar que se muestra mensaje de error de validación client-side
    cy.get('.alert-danger')
      .should('be.visible')
      .and('contain', 'La fecha de fin debe ser posterior a la fecha de inicio');
  });

  it('Debe editar una campaña existente', () => {
    const updatedCampaign = {
      ...mockCampaigns[0],
      name: 'Campaña Editada E2E'
    };

    // Mock GET single campaign
    cy.intercept('GET', '**/api/campaigns/1', {
      statusCode: 200,
      body: mockCampaigns[0]
    }).as('getCampaign');

    // Mock GET members
    cy.intercept('GET', '**/api/campaigns/1/members', {
      statusCode: 200,
      body: []
    });

    // Mock PATCH rename (el componente usa patch específicos)
    cy.intercept('PATCH', '**/api/campaigns/1/name', {
      statusCode: 200,
      body: updatedCampaign
    }).as('renameCampaign');

    // Mock GET campaigns actualizado
    const updatedList = {
      items: [updatedCampaign, mockCampaigns[1]],
      total: 2,
      page: 0,
      size: 10
    };
    cy.intercept('GET', '**/api/campaigns*', {
      statusCode: 200,
      body: updatedList
    });

    cy.get('[data-cy="campaign-card"]').first().within(() => {
      cy.get('[data-cy="edit-campaign-btn"]').click({ force: true });
    });

    cy.wait('@getCampaign');
    cy.url().should('include', '/campaigns/edit/1');
    cy.get('[data-cy="campaign-name"]').clear().type('Campaña Editada E2E');
    cy.get('[data-cy="campaign-submit"]').click();

    cy.wait('@renameCampaign');
    cy.url().should('include', '/campaigns');
  });

  it('Debe ver detalles de una campaña', () => {
    // Mock GET forms for campaign (clicking card navigates to forms)
    cy.intercept('GET', '**/api/campaigns/1/forms*', {
      statusCode: 200,
      body: {
        items: [],
        total: 0,
        page: 0,
        size: 20
      }
    }).as('getForms');

    cy.get('[data-cy="campaign-card"]').first().click();
    cy.wait('@getForms');
    cy.url().should('include', '/campaigns/1/forms');
  });

  it('Debe eliminar una campaña', () => {
    const campaignToDelete = {
      id: 3,
      name: 'Campaña a Eliminar',
      description: 'Para eliminar',
      startDate: '2025-12-10T08:00:00',
      endDate: '2025-12-31T18:00:00',
      createdAt: new Date().toISOString()
    };

    // Mock POST create
    cy.intercept('POST', '**/api/campaigns', {
      statusCode: 201,
      body: campaignToDelete
    }).as('createCampaign');

    const updatedCampaigns = [...mockCampaigns, campaignToDelete];
    const updatedPaginatedResponse = {
      items: updatedCampaigns,
      total: 3,
      page: 0,
      size: 10
    };

    cy.intercept('GET', '**/api/campaigns*', {
      statusCode: 200,
      body: updatedPaginatedResponse
    }).as('getCampaignsWithNew');

    // Mock members para nueva campaña
    cy.intercept('GET', `**/api/campaigns/${campaignToDelete.id}/members`, {
      statusCode: 200,
      body: []
    });

    // Crear campaña
    cy.get('[data-cy="create-campaign-btn"]').click();
    cy.url().should('include', '/campaigns/create');

    // Esperar que el formulario esté listo
    cy.get('[data-cy="campaign-name"]').should('be.visible').and('not.be.disabled');
    cy.get('[data-cy="campaign-name"]').type(campaignToDelete.name);
    cy.get('[data-cy="campaign-description"]').type(campaignToDelete.description);
    cy.get('[data-cy="campaign-startDate"]').should('not.be.disabled').type('2025-12-10T08:00');
    cy.get('[data-cy="campaign-endDate"]').should('not.be.disabled').type('2025-12-31T18:00');
    cy.get('[data-cy="campaign-submit"]').click();
    cy.wait('@createCampaign');
    cy.url().should('include', '/campaigns');
    cy.wait('@getCampaignsWithNew');

    // Ahora mockear DELETE
    cy.intercept('DELETE', `**/api/campaigns/${campaignToDelete.id}`, {
      statusCode: 204
    }).as('deleteCampaign');

    // Mock GET campaigns sin la campaña eliminada
    cy.intercept('GET', '**/api/campaigns*', {
      statusCode: 200,
      body: mockPaginatedResponse
    }).as('getCampaignsAfterDelete');

    // Eliminar
    cy.contains(campaignToDelete.name).parents('[data-cy="campaign-card"]').within(() => {
      cy.get('[data-cy="delete-campaign-btn"]').click({ force: true });
    });

    cy.on('window:confirm', () => true);
    cy.wait('@deleteCampaign');
    cy.wait('@getCampaignsAfterDelete');
    cy.contains(campaignToDelete.name).should('not.exist');
  });
});