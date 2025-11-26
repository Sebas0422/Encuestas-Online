// ============================================
// ENUMS Y TIPOS
// ============================================

export enum QuestionType {
  CHOICE = 'CHOICE',
  TRUE_FALSE = 'TRUE_FALSE',
  TEXT = 'TEXT',
  MATCHING = 'MATCHING'
}

export enum SelectionMode {
  SINGLE = 'SINGLE',
  MULTI = 'MULTI'
}

export enum TextMode {
  SHORT = 'SHORT',
  LONG = 'LONG'
}

// ============================================
// INTERFACES BASE
// ============================================

export interface QuestionBase {
  id?: number;
  formId: number;
  sectionId?: number;
  position: number;
  type: QuestionType;
  prompt: string;
  helpText?: string;
  required: boolean;
  shuffleOptions: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// ============================================
// OPCIONES Y ELEMENTOS
// ============================================

export interface QuestionOption {
  id?: number;
  label: string;
  correct: boolean;
}

export interface MatchingItem {
  id?: number;
  text: string;
}

// ============================================
// PREGUNTAS POR TIPO
// ============================================

export interface ChoiceQuestion extends QuestionBase {
  type: QuestionType.CHOICE;
  selectionMode: SelectionMode;
  minSelections?: number;
  maxSelections?: number;
  options: QuestionOption[];
}

export interface TrueFalseQuestion extends QuestionBase {
  type: QuestionType.TRUE_FALSE;
  selectionMode: SelectionMode.SINGLE;
  options: [QuestionOption, QuestionOption]; // Siempre 2 opciones
}

export interface TextQuestion extends QuestionBase {
  type: QuestionType.TEXT;
  textMode: TextMode;
  placeholder?: string;
  minLength?: number;
  maxLength?: number;
}

export interface MatchingQuestion extends QuestionBase {
  type: QuestionType.MATCHING;
  left: MatchingItem[];
  right: MatchingItem[];
  answerKey: { [leftId: number]: number }; // leftId -> rightId
}

// Union type para cualquier tipo de pregunta
export type Question = ChoiceQuestion | TrueFalseQuestion | TextQuestion | MatchingQuestion;

// ============================================
// RESPONSE DEL BACKEND
// ============================================

export interface QuestionResponse {
  id: number;
  formId: number;
  sectionId?: number;
  position: number;
  type: QuestionType;
  prompt: string;
  helpText?: string;
  required: boolean;
  shuffleOptions: boolean;
  // Para CHOICE/TRUE_FALSE:
  selectionMode?: string;
  minSelections?: number;
  maxSelections?: number;
  options?: QuestionOption[];
  // Para TEXT:
  textMode?: string;
  placeholder?: string;
  minLength?: number;
  maxLength?: number;
  // Para MATCHING:
  left?: MatchingItem[];
  right?: MatchingItem[];
  answerKey?: { [key: number]: number };
  createdAt: string;
  updatedAt: string;
}

// ============================================
// DTOs DE REQUEST
// ============================================

// Crear pregunta CHOICE
export interface CreateChoiceQuestionRequest {
  prompt: string;
  helpText?: string;
  required: boolean;
  shuffleOptions: boolean;
  selectionMode: SelectionMode;
  minSelections?: number;
  maxSelections?: number;
  options: Array<{ label: string; correct: boolean }>;
}

// Crear pregunta TRUE/FALSE
export interface CreateTrueFalseQuestionRequest {
  prompt: string;
  helpText?: string;
  required: boolean;
  shuffleOptions: boolean;
  trueIsCorrect: boolean;
  trueLabel?: string;
  falseLabel?: string;
}

// Crear pregunta TEXT
export interface CreateTextQuestionRequest {
  prompt: string;
  helpText?: string;
  required: boolean;
  textMode: TextMode;
  placeholder?: string;
  minLength?: number;
  maxLength?: number;
}

// Crear pregunta MATCHING
export interface CreateMatchingQuestionRequest {
  prompt: string;
  helpText?: string;
  required: boolean;
  shuffleRightColumn: boolean;
  leftTexts: string[];
  rightTexts: string[];
  keyPairs: Array<{ leftIndex: number; rightIndex: number }>;
}

// ============================================
// DTOs DE UPDATE
// ============================================

export interface UpdatePromptRequest {
  prompt: string;
}

export interface UpdateHelpRequest {
  helpText?: string;
}

export interface ToggleFlagRequest {
  enabled: boolean;
}

export interface ReplaceOptionsRequest {
  options: Array<{ label: string; correct: boolean }>;
}

export interface SetBoundsRequest {
  min?: number;
  max?: number;
}

export interface SetTextSettingsRequest {
  textMode: TextMode;
  placeholder?: string;
  minLength?: number;
  maxLength?: number;
}

export interface SetMatchingKeyRequest {
  key: { [key: number]: number };
}

export interface MoveQuestionRequest {
  targetSectionId?: number;
  newPosition?: number;
}

// ============================================
// PAGINACIÓN
// ============================================

export interface PaginatedQuestions {
  items: QuestionResponse[];
  total: number;
  page: number;
  size: number;
}

// ============================================
// HELPER TYPES
// ============================================

export interface QuestionFormData {
  type: QuestionType;
  prompt: string;
  helpText?: string;
  required: boolean;
  shuffleOptions: boolean;
  // CHOICE específico
  selectionMode?: SelectionMode;
  minSelections?: number;
  maxSelections?: number;
  options?: QuestionOption[];
  // TEXT específico
  textMode?: TextMode;
  placeholder?: string;
  minLength?: number;
  maxLength?: number;
  // TRUE_FALSE específico
  trueIsCorrect?: boolean;
  trueLabel?: string;
  falseLabel?: string;
  // MATCHING específico
  leftTexts?: string[];
  rightTexts?: string[];
  keyPairs?: Array<{ leftIndex: number; rightIndex: number }>;
}
