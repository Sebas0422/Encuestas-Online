import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  QuestionResponse,
  PaginatedQuestions,
  QuestionType,
  SelectionMode,
  TextMode,
  CreateChoiceQuestionRequest,
  CreateTrueFalseQuestionRequest,
  CreateTextQuestionRequest,
  CreateMatchingQuestionRequest,
  QuestionFormData
} from '../Models/question.model';
import { QuestionRepository } from '../Repositories/question.repository';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  constructor(private repository: QuestionRepository) { }

  // ============================================
  // CREATE - Métodos simplificados
  // ============================================

  createQuestion(
    formId: number,
    sectionId: number | null,
    formData: QuestionFormData
  ): Observable<QuestionResponse> {
    switch (formData.type) {
      case QuestionType.CHOICE:
        return this.createChoiceQuestion(formId, sectionId, formData);
      case QuestionType.TRUE_FALSE:
        return this.createTrueFalseQuestion(formId, sectionId, formData);
      case QuestionType.TEXT:
        return this.createTextQuestion(formId, sectionId, formData);
      case QuestionType.MATCHING:
        return this.createMatchingQuestion(formId, sectionId, formData);
      default:
        throw new Error(`Tipo de pregunta no soportado: ${formData.type}`);
    }
  }

  private createChoiceQuestion(
    formId: number,
    sectionId: number | null,
    data: QuestionFormData
  ): Observable<QuestionResponse> {
    const request: CreateChoiceQuestionRequest = {
      prompt: data.prompt,
      helpText: data.helpText,
      required: data.required,
      shuffleOptions: data.shuffleOptions,
      selectionMode: data.selectionMode || SelectionMode.SINGLE,
      minSelections: data.minSelections,
      maxSelections: data.maxSelections,
      options: data.options || []
    };
    return this.repository.createChoiceQuestion(formId, sectionId, request);
  }

  private createTrueFalseQuestion(
    formId: number,
    sectionId: number | null,
    data: QuestionFormData
  ): Observable<QuestionResponse> {
    const request: CreateTrueFalseQuestionRequest = {
      prompt: data.prompt,
      helpText: data.helpText,
      required: data.required,
      shuffleOptions: data.shuffleOptions,
      trueIsCorrect: data.trueIsCorrect || false,
      trueLabel: data.trueLabel || 'Verdadero',
      falseLabel: data.falseLabel || 'Falso'
    };
    return this.repository.createTrueFalseQuestion(formId, sectionId, request);
  }

  private createTextQuestion(
    formId: number,
    sectionId: number | null,
    data: QuestionFormData
  ): Observable<QuestionResponse> {
    const request: CreateTextQuestionRequest = {
      prompt: data.prompt,
      helpText: data.helpText,
      required: data.required,
      textMode: data.textMode || TextMode.SHORT,
      placeholder: data.placeholder,
      minLength: data.minLength,
      maxLength: data.maxLength
    };
    return this.repository.createTextQuestion(formId, sectionId, request);
  }

  private createMatchingQuestion(
    formId: number,
    sectionId: number | null,
    data: QuestionFormData
  ): Observable<QuestionResponse> {
    const request: CreateMatchingQuestionRequest = {
      prompt: data.prompt,
      helpText: data.helpText,
      required: data.required,
      shuffleRightColumn: data.shuffleOptions,
      leftTexts: data.leftTexts || [],
      rightTexts: data.rightTexts || [],
      keyPairs: data.keyPairs || []
    };
    return this.repository.createMatchingQuestion(formId, sectionId, request);
  }

  // ============================================
  // READ
  // ============================================

  getQuestionById(id: number): Observable<QuestionResponse> {
    return this.repository.getQuestionById(id);
  }

  getQuestionsByForm(
    formId: number,
    sectionId?: number,
    type?: QuestionType,
    search?: string,
    page?: number,
    size?: number
  ): Observable<QuestionResponse[]> {
    return this.repository
      .getQuestionsByForm(formId, sectionId, type, search, page, size)
      .pipe(map((response: PaginatedQuestions) => response.items));
  }

  getQuestionsByFormPaginated(
    formId: number,
    sectionId?: number,
    type?: QuestionType,
    search?: string,
    page?: number,
    size?: number
  ): Observable<PaginatedQuestions> {
    return this.repository.getQuestionsByForm(formId, sectionId, type, search, page, size);
  }

  // ============================================
  // UPDATE
  // ============================================

  updatePrompt(id: number, prompt: string): Observable<QuestionResponse> {
    return this.repository.updatePrompt(id, prompt);
  }

  updateHelp(id: number, helpText?: string): Observable<QuestionResponse> {
    return this.repository.updateHelp(id, helpText);
  }

  setRequired(id: number, required: boolean): Observable<QuestionResponse> {
    return this.repository.setRequired(id, required);
  }

  setShuffle(id: number, shuffle: boolean): Observable<QuestionResponse> {
    return this.repository.setShuffle(id, shuffle);
  }

  updateOptions(
    id: number,
    options: Array<{ label: string; correct: boolean }>
  ): Observable<QuestionResponse> {
    return this.repository.replaceOptions(id, options);
  }

  setBounds(id: number, min?: number, max?: number): Observable<QuestionResponse> {
    return this.repository.setBounds(id, min, max);
  }

  updateTextSettings(
    id: number,
    textMode: TextMode,
    placeholder?: string,
    minLength?: number,
    maxLength?: number
  ): Observable<QuestionResponse> {
    return this.repository.setTextSettings(id, textMode, placeholder, minLength, maxLength);
  }

  updateMatchingKey(id: number, key: { [key: number]: number }): Observable<QuestionResponse> {
    return this.repository.setMatchingKey(id, key);
  }

  moveQuestion(
    id: number,
    targetSectionId?: number,
    newPosition?: number
  ): Observable<QuestionResponse> {
    return this.repository.moveQuestion(id, targetSectionId, newPosition);
  }

  // ============================================
  // DELETE
  // ============================================

  deleteQuestion(id: number): Observable<void> {
    return this.repository.deleteQuestion(id);
  }

  // ============================================
  // HELPERS
  // ============================================

  getQuestionTypeLabel(type: QuestionType): string {
    const labels: Record<QuestionType, string> = {
      [QuestionType.CHOICE]: 'Opción Múltiple',
      [QuestionType.TRUE_FALSE]: 'Verdadero/Falso',
      [QuestionType.TEXT]: 'Texto',
      [QuestionType.MATCHING]: 'Relacionar'
    };
    return labels[type] || type;
  }

  getQuestionTypeIcon(type: QuestionType): string {
    const icons: Record<QuestionType, string> = {
      [QuestionType.CHOICE]: 'bi-ui-radios',
      [QuestionType.TRUE_FALSE]: 'bi-toggle-on',
      [QuestionType.TEXT]: 'bi-text-paragraph',
      [QuestionType.MATCHING]: 'bi-arrow-left-right'
    };
    return icons[type] || 'bi-question-circle';
  }
}
