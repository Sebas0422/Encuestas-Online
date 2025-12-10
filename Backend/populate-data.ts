/**
 * Script para poblar la base de datos con datos de prueba
 * Ejecutar con: npx tsx populate-data.ts
 * O compilar: tsc populate-data.ts && node populate-data.js
 */

const API_BASE_URL = 'http://localhost:8080/api';

interface LoginResponse {
  tokenType: string;
  accessToken: string;
  expiresIn: number;
  userId: number;
  email: string;
  fullName: string;
  systemAdmin: boolean;
}

let adminToken = '';
let currentUserId = 0;

// Utilidad para hacer requests
async function apiRequest(
  endpoint: string,
  method: string = 'GET',
  body?: any,
  useAuth: boolean = true
) {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  if (useAuth && adminToken) {
    headers['Authorization'] = `Bearer ${adminToken}`;
  }

  const options: RequestInit = {
    method,
    headers,
  };

  if (body && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
    options.body = JSON.stringify(body);
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Error ${response.status}: ${errorText}`);
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return await response.json();
  }

  return await response.text();
}

// 1. Registrar usuarios
async function registerUsers() {
  console.log('📝 Registrando usuarios...');

  const users = [
    { email: 'admin@nur.edu', password: 'Test123!', fullName: 'Administrador Sistema' },
    { email: 'maria.gonzalez@nur.edu', password: 'Test123!', fullName: 'María González López' },
    { email: 'juan.perez@nur.edu', password: 'Test123!', fullName: 'Juan Pérez Sánchez' },
    { email: 'ana.rodriguez@nur.edu', password: 'Test123!', fullName: 'Ana Rodríguez Morales' },
    { email: 'carlos.martinez@nur.edu', password: 'Test123!', fullName: 'Carlos Martínez Ruiz' },
    { email: 'sofia.lopez@nur.edu', password: 'Test123!', fullName: 'Sofía López Torres' },
    { email: 'pedro.sanchez@nur.edu', password: 'Test123!', fullName: 'Pedro Sánchez Vargas' },
    { email: 'lucia.fernandez@nur.edu', password: 'Test123!', fullName: 'Lucía Fernández Castro' },
    { email: 'diego.ramirez@nur.edu', password: 'Test123!', fullName: 'Diego Ramírez Mendoza' },
    { email: 'elena.torres@nur.edu', password: 'Test123!', fullName: 'Elena Torres Jiménez' },
  ];

  for (const user of users) {
    try {
      await apiRequest('/auth/register', 'POST', user, false);
      console.log(`  ✅ Registrado: ${user.email}`);
    } catch (error: any) {
      console.log(`  ⚠️  ${user.email} - ${error.message}`);
    }
  }
}

// 2. Login como admin
async function loginAsAdmin() {
  console.log('\n🔐 Iniciando sesión como admin...');

  try {
    const response = await apiRequest('/auth/login', 'POST', {
      email: 'admin@nur.edu',
      password: 'Test123!'
    }, false);

    // Verificar estructura de la respuesta
    if (!response) {
      throw new Error('Respuesta vacía del servidor');
    }

    // Tu API retorna: accessToken, userId
    adminToken = response.accessToken;
    currentUserId = response.userId;

    console.log(`  ✅ Login exitoso - Token obtenido`);
    console.log(`  👤 User ID: ${currentUserId} (${response.fullName})`);
  } catch (error) {
    console.error('  ❌ Error en login:', error);
    throw error;
  }
}

// 3. Crear campañas
async function createCampaigns() {
  console.log('\n📋 Creando campañas...');

  const campaigns = [
    {
      name: 'Evaluación Docente 2025-1',
      description: 'Evaluación de desempeño docente primer semestre 2025',
      startDate: '2025-01-15',
      endDate: '2025-06-30'
    },
    {
      name: 'Satisfacción Servicios Académicos',
      description: 'Encuesta de satisfacción sobre servicios académicos y administrativos',
      startDate: '2025-02-01',
      endDate: '2025-05-31'
    },
    {
      name: 'Evaluación Infraestructura',
      description: 'Evaluación de instalaciones y recursos de la universidad',
      startDate: '2025-03-01',
      endDate: '2025-07-31'
    },
    {
      name: 'Clima Organizacional',
      description: 'Evaluación del clima laboral y organizacional',
      startDate: '2025-01-01',
      endDate: '2025-12-31'
    },
    {
      name: 'Egresados 2024',
      description: 'Seguimiento a egresados de la gestión 2024',
      startDate: '2024-12-01',
      endDate: '2025-12-31'
    }
  ];

  const createdCampaigns = [];
  for (const campaign of campaigns) {
    try {
      const created = await apiRequest('/campaigns', 'POST', campaign);
      createdCampaigns.push(created);
      console.log(`  ✅ Creada: ${campaign.name}`);
    } catch (error: any) {
      // Si ya existe, intentar buscarla
      if (error.message.includes('ya existe')) {
        console.log(`  ℹ️  Ya existe: ${campaign.name} - Buscando...`);
      } else {
        console.log(`  ❌ Error: ${campaign.name} - ${error.message}`);
      }
    }
  }

  // Si no se creó ninguna, obtener las existentes
  if (createdCampaigns.length === 0) {
    try {
      const response = await apiRequest('/campaigns?page=0&size=50');
      const existingCampaigns = response.items || response;
      console.log(`  📦 Obtenidas ${existingCampaigns.length} campañas existentes`);
      return existingCampaigns;
    } catch (error: any) {
      console.log(`  ❌ Error obteniendo campañas: ${error.message}`);
    }
  }

  return createdCampaigns;
}

// 4. Crear formularios
async function createForms(campaigns: any[]) {
  console.log('\n📝 Creando formularios...');

  if (campaigns.length === 0) {
    console.log('  ⚠️  No hay campañas disponibles');
    return [];
  }

  const forms = [
    {
      campaignId: campaigns[0]?.id,
      title: 'Evaluación Docente - Estudiantes',
      description: 'Evalúa el desempeño de tus docentes en este semestre',
      coverUrl: 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=600',
      themeMode: 'light',
      themePrimary: '#1e40af',
      accessMode: 'PUBLIC',
      anonymousMode: true,
      allowEditBeforeSubmit: true,
      autoSave: true,
      progressBar: true,
      paginated: true
    },
    {
      campaignId: campaigns[1]?.id,
      title: 'Satisfacción con Servicios de Biblioteca',
      description: 'Ayúdanos a mejorar los servicios de la biblioteca universitaria',
      coverUrl: 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?w=600',
      themeMode: 'light',
      themePrimary: '#059669',
      accessMode: 'PUBLIC',
      anonymousMode: false,
      allowEditBeforeSubmit: true,
      autoSave: true,
      shuffleOptions: true,
      progressBar: true,
      paginated: true
    },
    {
      campaignId: campaigns[2]?.id,
      title: 'Evaluación de Aulas y Laboratorios',
      description: 'Evalúa las condiciones de las aulas y laboratorios',
      coverUrl: 'https://images.unsplash.com/photo-1562774053-701939374585?w=600',
      themeMode: 'dark',
      themePrimary: '#dc2626',
      accessMode: 'PUBLIC',
      anonymousMode: true,
      autoSave: true,
      progressBar: true,
      paginated: false
    },
    {
      campaignId: campaigns[1]?.id,
      title: 'Satisfacción con Servicio de Cafetería',
      description: 'Evalúa la calidad y variedad del servicio de cafetería',
      coverUrl: 'https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=600',
      themeMode: 'light',
      themePrimary: '#f59e0b',
      accessMode: 'PUBLIC',
      anonymousMode: true,
      allowEditBeforeSubmit: true,
      autoSave: true,
      shuffleOptions: true,
      progressBar: true,
      paginated: false
    }
  ];

  const createdForms = [];
  for (const form of forms) {
    try {
      // El campaignId va tanto en la URL como en el body
      const created = await apiRequest(`/campaigns/${form.campaignId}/forms`, 'POST', form);
      createdForms.push(created);
      console.log(`  ✅ Creado: ${form.title}`);
    } catch (error: any) {
      if (error.message.includes('Ya existe')) {
        console.log(`  ℹ️  Ya existe: ${form.title}`);
      } else {
        console.log(`  ❌ Error: ${form.title} - ${error.message}`);
      }
    }
  }

  // Si no se creó ninguno, obtener los existentes
  if (createdForms.length === 0) {
    try {
      // Obtener formularios de todas las campañas
      for (const campaign of campaigns) {
        const response = await apiRequest(`/campaigns/${campaign.id}/forms?page=0&size=50`);
        const campaignForms = response.items || response;
        if (Array.isArray(campaignForms)) {
          createdForms.push(...campaignForms);
        }
      }
      console.log(`  📦 Obtenidos ${createdForms.length} formularios existentes`);
    } catch (error: any) {
      console.log(`  ❌ Error obteniendo formularios: ${error.message}`);
    }
  }

  return createdForms;
}

// 5. Crear secciones
async function createSections(forms: any[]) {
  console.log('\n📑 Creando secciones...');

  const sectionsData = [
    // Formulario 1: Evaluación Docente
    [
      { title: 'Información General', position: 0 },
      { title: 'Metodología de Enseñanza', position: 1 },
      { title: 'Materiales y Recursos', position: 2 },
      { title: 'Evaluación y Retroalimentación', position: 3 }
    ],
    // Formulario 2: Biblioteca
    [
      { title: 'Infraestructura', position: 0 },
      { title: 'Colección Bibliográfica', position: 1 },
      { title: 'Atención al Usuario', position: 2 }
    ],
    // Formulario 3: Infraestructura
    [
      { title: 'Aulas', position: 0 },
      { title: 'Laboratorios', position: 1 },
      { title: 'Áreas Comunes', position: 2 }
    ],
    // Formulario 4: Cafetería
    [
      { title: 'Calidad de Alimentos', position: 0 },
      { title: 'Servicio', position: 1 }
    ]
  ];

  const createdSections: any[][] = [];

  for (let i = 0; i < forms.length && i < sectionsData.length; i++) {
    const form = forms[i];
    const sections = sectionsData[i];
    const formSections = [];

    for (const section of sections) {
      try {
        const created = await apiRequest(`/forms/${form.id}/sections`, 'POST', section);
        formSections.push(created);
        console.log(`  ✅ Sección "${section.title}" en "${form.title}"`);
      } catch (error: any) {
        console.log(`  ❌ Error: ${section.title} - ${error.message}`);
      }
    }

    createdSections.push(formSections);
  }

  return createdSections;
}

// 6. Crear preguntas con opciones
async function createQuestions(forms: any[], sections: any[][]) {
  console.log('\n❓ Creando preguntas...');

  // Formulario 1: Evaluación Docente
  if (forms[0] && sections[0]) {
    const form = forms[0];

    // Sección 0: Información General
    await createQuestion(form.id, sections[0][0]?.id, {
      type: 'CHOICE',
      prompt: '¿En qué carrera estás inscrito?',
      helpText: 'Selecciona tu carrera actual',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: [
        { label: 'Ingeniería de Sistemas', position: 0 },
        { label: 'Ingeniería Comercial', position: 1 },
        { label: 'Derecho', position: 2 },
        { label: 'Psicología', position: 3 },
        { label: 'Administración de Empresas', position: 4 },
        { label: 'Medicina', position: 5 }
      ]
    });

    await createQuestion(form.id, sections[0][0]?.id, {
      type: 'CHOICE',
      prompt: 'Semestre que cursas',
      position: 1,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: [
        { label: 'Primer Semestre', position: 0 },
        { label: 'Segundo Semestre', position: 1 },
        { label: 'Tercer Semestre', position: 2 },
        { label: 'Cuarto Semestre', position: 3 },
        { label: 'Quinto Semestre', position: 4 },
        { label: 'Sexto Semestre o más', position: 5 }
      ]
    });

    // Sección 1: Metodología de Enseñanza
    const likertOptions = [
      { label: 'Totalmente en desacuerdo', position: 0 },
      { label: 'En desacuerdo', position: 1 },
      { label: 'Neutral', position: 2 },
      { label: 'De acuerdo', position: 3 },
      { label: 'Totalmente de acuerdo', position: 4 }
    ];

    await createQuestion(form.id, sections[0][1]?.id, {
      type: 'CHOICE',
      prompt: 'El docente explica los contenidos de forma clara y comprensible',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[0][1]?.id, {
      type: 'CHOICE',
      prompt: 'El docente fomenta la participación activa en clase',
      position: 1,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[0][1]?.id, {
      type: 'TRUE_FALSE',
      prompt: 'El docente domina los temas que enseña',
      position: 2,
      required: true
    });

    // Sección 2: Materiales y Recursos
    await createQuestion(form.id, sections[0][2]?.id, {
      type: 'CHOICE',
      prompt: '¿Qué herramientas tecnológicas utiliza el docente?',
      helpText: 'Puedes seleccionar varias opciones',
      position: 0,
      required: false,
      selectionMode: 'MULTI',
      shuffleOptions: true,
      options: [
        { label: 'Pizarra digital/Smart TV', position: 0 },
        { label: 'Proyector multimedia', position: 1 },
        { label: 'Plataforma Moodle', position: 2 },
        { label: 'Google Classroom', position: 3 },
        { label: 'Videoconferencias (Zoom/Teams)', position: 4 },
        { label: 'Simuladores', position: 5 },
        { label: 'Laboratorios virtuales', position: 6 },
        { label: 'Ninguna', position: 7 }
      ]
    });

    await createQuestion(form.id, sections[0][2]?.id, {
      type: 'TEXT',
      prompt: '¿Qué recursos adicionales te gustaría que el docente utilice?',
      helpText: 'Describe libremente',
      position: 1,
      required: false
    });

    // Sección 3: Evaluación y Retroalimentación
    await createQuestion(form.id, sections[0][3]?.id, {
      type: 'CHOICE',
      prompt: 'El docente devuelve las evaluaciones en tiempo oportuno',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[0][3]?.id, {
      type: 'TEXT',
      prompt: 'Comentarios adicionales sobre el desempeño del docente',
      helpText: 'Opcional',
      position: 1,
      required: false
    });
  }

  // Formulario 2: Biblioteca
  if (forms[1] && sections[1]) {
    const form = forms[1];
    const likertOptions = [
      { label: 'Totalmente en desacuerdo', position: 0 },
      { label: 'En desacuerdo', position: 1 },
      { label: 'Neutral', position: 2 },
      { label: 'De acuerdo', position: 3 },
      { label: 'Totalmente de acuerdo', position: 4 }
    ];

    await createQuestion(form.id, sections[1][0]?.id, {
      type: 'CHOICE',
      prompt: 'Las instalaciones de la biblioteca son cómodas',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[1][1]?.id, {
      type: 'CHOICE',
      prompt: 'La biblioteca cuenta con bibliografía actualizada',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[1][1]?.id, {
      type: 'TRUE_FALSE',
      prompt: 'He encontrado los libros que necesito para mis materias',
      position: 1,
      required: true
    });

    await createQuestion(form.id, sections[1][2]?.id, {
      type: 'CHOICE',
      prompt: 'El personal de biblioteca es amable y servicial',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });
  }

  // Formulario 3: Infraestructura
  if (forms[2] && sections[2]) {
    const form = forms[2];
    const likertOptions = [
      { label: 'Totalmente en desacuerdo', position: 0 },
      { label: 'En desacuerdo', position: 1 },
      { label: 'Neutral', position: 2 },
      { label: 'De acuerdo', position: 3 },
      { label: 'Totalmente de acuerdo', position: 4 }
    ];

    await createQuestion(form.id, sections[2][0]?.id, {
      type: 'CHOICE',
      prompt: 'Las aulas tienen buena iluminación',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[2][0]?.id, {
      type: 'CHOICE',
      prompt: 'El mobiliario de las aulas es cómodo',
      position: 1,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[2][1]?.id, {
      type: 'TRUE_FALSE',
      prompt: 'Hay suficientes equipos para todos los estudiantes',
      position: 0,
      required: true
    });
  }

  // Formulario 4: Cafetería
  if (forms[3] && sections[3]) {
    const form = forms[3];
    const likertOptions = [
      { label: 'Totalmente en desacuerdo', position: 0 },
      { label: 'En desacuerdo', position: 1 },
      { label: 'Neutral', position: 2 },
      { label: 'De acuerdo', position: 3 },
      { label: 'Totalmente de acuerdo', position: 4 }
    ];

    await createQuestion(form.id, sections[3][0]?.id, {
      type: 'CHOICE',
      prompt: 'La calidad de los alimentos es buena',
      position: 0,
      required: true,
      selectionMode: 'SINGLE',
      minSelections: 1,
      maxSelections: 1,
      options: likertOptions
    });

    await createQuestion(form.id, sections[3][0]?.id, {
      type: 'TRUE_FALSE',
      prompt: 'Los precios son accesibles',
      position: 1,
      required: true
    });

    await createQuestion(form.id, sections[3][1]?.id, {
      type: 'TEXT',
      prompt: '¿Qué platos te gustaría que se agreguen al menú?',
      position: 0,
      required: false
    });
  }
}

async function createQuestion(formId: number, sectionId: number, questionData: any) {
  try {
    const { options, type, ...questionBody } = questionData;
    questionBody.sectionId = sectionId;

    // Mapear tipo a endpoint específico y preparar body según el tipo
    let endpoint = '';
    let requestBody: any = { ...questionBody };

    switch (type) {
      case 'CHOICE':
        endpoint = `/forms/${formId}/questions/choice`;
        // Para CHOICE, las opciones van en el body con formato {label, correct}
        if (options && options.length > 0) {
          requestBody.options = options.map((opt: any) => ({
            label: opt.label,
            correct: opt.correct || false
          }));
        } else {
          requestBody.options = [];
        }
        break;
      case 'TRUE_FALSE':
        endpoint = `/forms/${formId}/questions/true-false`;
        break;
      case 'TEXT':
        endpoint = `/forms/${formId}/questions/text`;
        // TEXT requiere textMode
        if (!requestBody.textMode) {
          requestBody.textMode = 'LONG'; // Por defecto LONG
        }
        break;
      case 'MATCHING':
        endpoint = `/forms/${formId}/questions/matching`;
        break;
      default:
        throw new Error(`Tipo de pregunta desconocido: ${type}`);
    }

    const question = await apiRequest(endpoint, 'POST', requestBody);
    console.log(`  ✅ Pregunta: "${questionData.prompt.substring(0, 50)}..."`);
  } catch (error: any) {
    console.log(`  ❌ Error: ${error.message}`);
  }
}

// 7. Publicar formularios
async function publishForms(forms: any[]) {
  console.log('\n📤 Publicando formularios...');

  const publicCodes = ['eval2025abc', 'biblio2025x', 'infra2025y', 'cafet2025v'];

  for (let i = 0; i < forms.length; i++) {
    const form = forms[i];
    const publicCode = publicCodes[i] || `form${Date.now()}${i}`;

    try {
      // 1. Cambiar estado a published
      await apiRequest(`/forms/${form.id}/status`, 'PATCH', {
        status: 'published'
      });

      // 2. Generar enlace público (esto puede crear o forzar un código)
      try {
        await apiRequest(`/forms/${form.id}/public-link?force=true`, 'POST');
      } catch (linkError: any) {
        console.log(`    ⚠️  No se pudo generar enlace público: ${linkError.message}`);
      }

      console.log(`  ✅ Publicado: ${form.title}`);
    } catch (error: any) {
      console.log(`  ❌ Error publicando ${form.title}: ${error.message}`);
    }
  }
}

// Función principal
async function main() {
  console.log('🚀 Iniciando población de datos...\n');
  console.log('⚠️  ADVERTENCIA: Este script creará datos de prueba en tu base de datos\n');

  try {
    // 1. Registrar usuarios
    await registerUsers();

    // 2. Login como admin
    await loginAsAdmin();

    // 3. Crear campañas
    const campaigns = await createCampaigns();

    // 4. Crear formularios
    const forms = await createForms(campaigns);

    // 5. Crear secciones
    const sections = await createSections(forms);

    // 6. Crear preguntas
    await createQuestions(forms, sections);

    // 7. Publicar formularios
    await publishForms(forms);

    console.log('\n✅ ¡Población de datos completada exitosamente!');
    console.log('\n📊 Resumen:');
    console.log(`  👥 Usuarios: 10 (password: Test123!)`);
    console.log(`  📋 Campañas: ${campaigns.length}`);
    console.log(`  📝 Formularios: ${forms.length}`);
    console.log(`  📑 Secciones: ${sections.flat().length}`);
    console.log('\n🔗 Códigos públicos de formularios:');
    console.log('  • eval2025abc - Evaluación Docente');
    console.log('  • biblio2025x - Biblioteca');
    console.log('  • infra2025y - Infraestructura');
    console.log('  • cafet2025v - Cafetería');
    console.log('\n💡 Puedes acceder con: admin@nur.edu / Test123!');

  } catch (error) {
    console.error('\n❌ Error fatal:', error);
    process.exit(1);
  }
}

// Ejecutar
main();
