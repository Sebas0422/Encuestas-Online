# Pruebas E2E con Cypress

## 📋 Descripción

Este proyecto incluye pruebas end-to-end completas usando Cypress para validar todos los flujos principales de la aplicación de encuestas online.

## 🧪 Suites de Pruebas

### 1. **auth.cy.ts** - Autenticación
- ✅ Mostrar página de login
- ✅ Validar campos vacíos
- ✅ Validar email inválido
- ✅ Login exitoso
- ✅ Error con credenciales incorrectas
- ✅ Navegación a registro
- ✅ Cerrar sesión

### 2. **campaigns.cy.ts** - Gestión de Campañas
- ✅ Listar campañas
- ✅ Crear nueva campaña
- ✅ Validar fechas de campaña
- ✅ Editar campaña existente
- ✅ Ver detalles de campaña
- ✅ Eliminar campaña

### 3. **forms.cy.ts** - Gestión de Formularios
- ✅ Listar formularios
- ✅ Crear formulario con secciones y preguntas
- ✅ Cambiar modo de acceso
- ✅ Publicar formulario
- ✅ Despublicar formulario
- ✅ Previsualizar formulario
- ✅ Agregar diferentes tipos de preguntas (CHOICE, TRUE_FALSE, TEXT)
- ✅ Eliminar formulario

### 4. **responses.cy.ts** - Respuestas
- ✅ Ver formularios públicos
- ✅ Responder formulario anónimamente
- ✅ Validar campos requeridos
- ✅ Responder diferentes tipos de preguntas
- ✅ Ver estadísticas (admin)
- ✅ Visualizar gráficos
- ✅ Cambiar tipo de gráfico (Pastel/Rosca/Tabla)
- ✅ Filtrar respuestas

### 5. **complete-flow.cy.ts** - Flujo Completo
- ✅ Flujo end-to-end completo desde login hasta visualización de estadísticas

## 🚀 Comandos Personalizados

Se han creado comandos personalizados de Cypress para facilitar las pruebas:

```typescript
cy.login(email, password)
cy.logout()
cy.createCampaign(name, description, startDate, endDate)
cy.createForm(title, description, isPublic, isAnonymous)
cy.addQuestion(questionText, type, options)
cy.publishForm(formTitle)
cy.answerChoiceQuestion(optionText)
cy.answerTrueFalseQuestion(value)
cy.answerTextQuestion(text)
cy.submitForm()
cy.viewResponses(formTitle)
```

## 📦 Instalación

Las dependencias de Cypress ya están instaladas. Si necesitas reinstalar:

```bash
cd Frontend
npm install --save-dev cypress @cypress/schematic
```

## ▶️ Ejecutar Pruebas

### Modo Interactivo (Cypress UI)
```bash
cd Frontend
npx cypress open
```

Esto abrirá la interfaz de Cypress donde podrás:
- Seleccionar E2E Testing
- Elegir un navegador (Chrome, Firefox, Edge, etc.)
- Ejecutar pruebas individuales o todas a la vez

### Modo Headless (CI/CD)
```bash
cd Frontend
npx cypress run
```

### Ejecutar una suite específica
```bash
npx cypress run --spec "cypress/e2e/auth.cy.ts"
npx cypress run --spec "cypress/e2e/campaigns.cy.ts"
npx cypress run --spec "cypress/e2e/forms.cy.ts"
npx cypress run --spec "cypress/e2e/responses.cy.ts"
npx cypress run --spec "cypress/e2e/complete-flow.cy.ts"
```

### Ejecutar en un navegador específico
```bash
npx cypress run --browser chrome
npx cypress run --browser firefox
npx cypress run --browser edge
```

## 🔧 Configuración

El archivo `cypress.config.ts` contiene:
- **baseUrl**: http://localhost:4200
- **viewportWidth**: 1280px
- **viewportHeight**: 720px
- **videos**: Habilitados en `cypress/videos`
- **screenshots**: Habilitados en `cypress/screenshots`
- **retries**: 2 intentos en modo CI

## 📝 Requisitos Previos

Antes de ejecutar las pruebas E2E:

1. **Backend debe estar corriendo**:
   ```bash
   cd Backend
   ./mvnw spring-boot:run
   ```
   El backend debe estar disponible en `http://localhost:8080`

2. **Frontend debe estar corriendo**:
   ```bash
   cd Frontend
   ng serve
   ```
   El frontend debe estar disponible en `http://localhost:4200`

3. **Base de datos debe tener datos de prueba**:
   - Usuario de prueba: `user0@test.com` / `Test123!`
   - Idealmente ejecutar el script `populate-data.ts` para tener datos de prueba:
     ```bash
     cd Frontend
     npx tsx populate-data.ts
     ```

## 📊 Reportes

Después de ejecutar las pruebas en modo headless:
- **Videos**: Se guardan en `cypress/videos/`
- **Screenshots** (de fallos): Se guardan en `cypress/screenshots/`

## 🎯 Mejores Prácticas

1. **Usar comandos personalizados** para operaciones comunes
2. **beforeEach** para setup común (login, navegación)
3. **Selectores robustos**: Preferir `data-cy` attributes cuando sea posible
4. **Assertions específicas**: Usar `.should()` con mensajes claros
5. **Timeouts apropiados**: Configurados en `cypress.config.ts`

## 🐛 Debugging

Si una prueba falla:

1. **Modo interactivo**: `npx cypress open` y ejecutar la prueba paso a paso
2. **Screenshots**: Revisar `cypress/screenshots/` para ver el estado cuando falló
3. **Videos**: Revisar `cypress/videos/` para ver toda la ejecución
4. **Console logs**: Los logs de la aplicación aparecen en la consola de Cypress

## 🔄 Integración Continua

Para GitHub Actions o similar:

```yaml
- name: Run Cypress tests
  run: |
    npm run start & # Iniciar frontend
    npx wait-on http://localhost:4200
    npx cypress run
```

## 📚 Documentación Adicional

- [Cypress Documentation](https://docs.cypress.io)
- [Best Practices](https://docs.cypress.io/guides/references/best-practices)
- [Cypress with Angular](https://docs.cypress.io/guides/component-testing/angular/overview)

## ✅ Checklist de Pruebas

- [x] Autenticación completa
- [x] CRUD de Campañas
- [x] CRUD de Formularios
- [x] Creación de preguntas (CHOICE, TRUE_FALSE, TEXT)
- [x] Respuestas anónimas
- [x] Visualización de estadísticas
- [x] Gráficos interactivos
- [x] Flujo end-to-end completo

## 🎓 Ejemplos de Uso

### Ejemplo 1: Usar comandos personalizados
```typescript
it('Debe crear y publicar formulario', () => {
  cy.login('user0@test.com', 'Test123!');
  cy.createCampaign('Mi Campaña', 'Descripción', '2025-12-01', '2025-12-31');
  cy.createForm('Mi Formulario', 'Descripción', true, true);
  cy.addQuestion('¿Pregunta?', 'CHOICE', ['Opción 1', 'Opción 2']);
  cy.publishForm('Mi Formulario');
});
```

### Ejemplo 2: Flujo de respuesta
```typescript
it('Debe responder formulario', () => {
  cy.visit('/public-forms');
  cy.contains('Mi Formulario').click();
  cy.answerChoiceQuestion('Opción 1');
  cy.answerTrueFalseQuestion(true);
  cy.answerTextQuestion('Mi respuesta');
  cy.submitForm();
});
```

---

**¡Las pruebas E2E están listas para ejecutar! 🎉**
