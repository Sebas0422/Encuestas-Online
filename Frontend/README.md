# Frontend - Encuestas Online Universidad NUR

Sistema de encuestas en línea para la Universidad NUR, desarrollado con Angular 19.2.15.

## 🚀 Inicio Rápido

### Requisitos Previos
- Node.js (v18 o superior)
- npm o yarn
- Angular CLI (`npm install -g @angular/cli`)

### Instalación

1. Clonar el repositorio
2. Instalar dependencias:
```bash
npm install
```

3. Configurar variables de entorno:
   - Revisa `src/environments/environment.development.ts` para desarrollo
   - Revisa `src/environments/environment.ts` para producción
   - Ver `src/environments/README.md` para más detalles

## 🛠️ Desarrollo

### Servidor de Desarrollo

Para iniciar un servidor de desarrollo local, ejecuta:

```bash
npm start
# o
ng serve
```

Navega a `http://localhost:4200/`. La aplicación se recargará automáticamente cuando modifiques archivos fuente.

**Nota**: En desarrollo, la API apunta a `http://localhost:8080` por defecto.

## 🏗️ Construcción

Para construir el proyecto ejecuta:

```bash
npm run build
# o
ng build
```

Esto compilará tu proyecto y guardará los artefactos en el directorio `dist/`. Por defecto, la construcción de producción optimiza tu aplicación para rendimiento y velocidad.

### Build de Producción
```bash
ng build --configuration production
```

**⚠️ Importante**: Antes de desplegar a producción, actualiza la `apiUrl` en `src/environments/environment.ts` con tu URL real de backend.

## 🎨 Paleta de Colores

El proyecto usa los colores corporativos de la Universidad NUR:

- **Azul Principal**: `#004a99`
- **Azul Hover**: `#003a7a`
- **Fondo**: `#f0f2f5`
- **Texto**: `#333`

## 📁 Estructura del Proyecto

```
src/
├── app/
│   ├── Components/       # Componentes reutilizables (Header, Footer)
│   ├── Pages/           # Páginas (Login, Registro)
│   ├── Services/        # Servicios (AuthService)
│   ├── Guards/          # Guards de autenticación
│   ├── Interceptors/    # HTTP Interceptors
│   ├── Models/          # Modelos de datos
│   └── Repositories/    # Repositorios
├── environments/        # Configuración por ambiente
└── styles.css          # Estilos globales
```

## 🔧 Scaffolding de Código

Angular CLI incluye herramientas de scaffolding. Para generar un nuevo componente:

```bash
ng generate component component-name
```

Para ver la lista completa de esquemas disponibles (`components`, `directives`, `pipes`):

```bash
ng generate --help
```

## 🧪 Tests

Para ejecutar tests unitarios con [Karma](https://karma-runner.github.io):

```bash
npm test
# o
ng test
```

## 📚 Variables de Entorno

Este proyecto usa archivos de environment de Angular en lugar de `.env` tradicional:

- **Desarrollo**: `src/environments/environment.development.ts`
- **Producción**: `src/environments/environment.ts`

Ver `src/environments/README.md` para más información.

## 🔐 Autenticación

El proyecto incluye un sistema completo de autenticación con:
- Login con email y contraseña
- Registro de usuarios
- Gestión de tokens JWT
- Guards de autenticación
- Interceptores HTTP

## 📦 Dependencias Principales

- Angular 19.2.0
- Bootstrap 5.3.8
- Bootstrap Icons 1.13.1
- RxJS 7.8.0

## 📝 Recursos Adicionales

Para más información sobre Angular CLI, visita la [Documentación Oficial de Angular CLI](https://angular.dev/tools/cli).

