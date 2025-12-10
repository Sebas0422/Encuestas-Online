describe('Autenticación', () => {
  beforeEach(() => {
    cy.visit('http://localhost:4200/login');
  });

  it('Debe mostrar la página de login', () => {
    cy.url().should('include', '/login');
    cy.contains('Encuestas NUR').should('be.visible');
    cy.get('[data-cy="login-email"]').should('be.visible');
    cy.get('[data-cy="login-password"]').should('be.visible');
  });

  it('Debe validar campos vacíos', () => {
    // El botón debe estar deshabilitado cuando los campos están vacíos
    cy.get('[data-cy="login-submit"]').should('be.disabled');
  });

  it('Debe habilitar botón cuando se llenan los campos', () => {
    cy.get('[data-cy="login-email"]').type('test@test.com');
    cy.get('[data-cy="login-password"]').type('Password123');
    cy.get('[data-cy="login-submit"]').should('not.be.disabled');
  });

  it('Debe iniciar sesión correctamente', () => {
    // Mock campaigns para cuando el componente cargue
    cy.fixture('campaigns').then((campaigns) => {
      cy.intercept('GET', '**/api/campaigns**', {
        statusCode: 200,
        body: campaigns
      }).as('getCampaigns');
    });

    // Mock de login exitoso con estructura correcta
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

    cy.get('[data-cy="login-email"]').type('user0@test.com');
    cy.get('[data-cy="login-password"]').type('Test123!');
    cy.get('[data-cy="login-submit"]').click();

    cy.wait('@login');
    cy.url().should('include', '/campaigns');
  });

  it('Debe mostrar error con credenciales incorrectas', () => {
    // Mock de login fallido
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Credenciales inválidas'
      }
    }).as('loginFailed');

    cy.get('[data-cy="login-email"]').type('wrong@test.com');
    cy.get('[data-cy="login-password"]').type('WrongPassword');
    cy.get('[data-cy="login-submit"]').click();

    cy.wait('@loginFailed');
    cy.get('[data-cy="login-error"]').should('be.visible');
  });

  it('Debe navegar a registro', () => {
    cy.get('[data-cy="register-link"]').click();
    cy.url().should('include', '/register');
  });

  it('Debe registrarse correctamente', () => {
    // Mock de registro exitoso
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

    cy.get('[data-cy="register-link"]').click();
    cy.get('[data-cy="register-fullName"]').type('Nuevo Usuario');
    cy.get('[data-cy="register-email"]').type('newuser@test.com');
    cy.get('[data-cy="register-password"]').type('Test123!');
    cy.get('[data-cy="register-submit"]').click();

    cy.wait('@register');
    // El registro redirige a /login, no a /campaigns
    cy.url().should('include', '/login');
    cy.contains('Encuestas NUR').should('be.visible');
  });

  it('Debe cerrar sesión correctamente', () => {
    // Mock campaigns para cuando el componente cargue
    cy.fixture('campaigns').then((campaigns) => {
      cy.intercept('GET', '**/api/campaigns**', {
        statusCode: 200,
        body: campaigns
      }).as('getCampaigns');
    });

    // Mock de login con estructura correcta
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

    cy.get('[data-cy="login-email"]').type('user0@test.com');
    cy.get('[data-cy="login-password"]').type('Test123!');
    cy.get('[data-cy="login-submit"]').click();

    cy.wait('@login');
    cy.url().should('include', '/campaigns');

    // Logout - buscar el botón de logout en el header
    cy.get('[data-cy="logout-button"]').click();
    cy.url().should('include', '/login');

    // Verificar que el token fue removido
    cy.window().then((win) => {
      expect(win.localStorage.getItem('auth_token')).to.be.null;
    });
  });
});