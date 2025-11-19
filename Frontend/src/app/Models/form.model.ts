export interface Forms {
    id?: number;
    campaignId: number;
    title: string;
    description: string;
    coverUrl: string;
    themeMode: string;
    themePrimary: string;
    accessMode: string;
    openAt: string;
    closeAt: string;
    responseLimitMode: string;
    limitedN: null;
    anonymousMode: boolean;
    allowEditBeforeSubmit: boolean;
    autoSave: boolean;
    shuffleQuestions: boolean;
    shuffleOptions: boolean;
    progressBar: boolean;
    paginated: boolean;
}


export interface PaginatedForms {
  items: Forms[];
  total: number;
  page: number;
  size: number;
}

// Payload para /title
export interface UpdateTitleRequest { title: string; }

// Payload para /description
export interface UpdateDescriptionRequest { description: string; }

// Payload para /theme
export interface UpdateThemeRequest { mode: string; primaryColor: string; }

// Payload para /schedule
export interface RescheduleRequest { openAt: string; closeAt: string; }

// Payload para /access-mode
export interface SetAccessModeRequest { mode: string; }

// Payload para flags simples (/anonymous, /autosave, /allow-edit)
export interface ToggleFlagRequest { enabled: boolean; }

// Payload para /presentation (Agrupa 4 booleans)
export interface SetPresentationRequest {
  shuffleQuestions: boolean;
  shuffleOptions: boolean;
  progressBar: boolean;
  paginated: boolean;
}