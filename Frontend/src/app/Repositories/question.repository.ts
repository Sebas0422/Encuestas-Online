import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  QuestionResponse,
  PaginatedQuestions,
  CreateChoiceQuestionRequest,
  CreateTrueFalseQuestionRequest,
  CreateTextQuestionRequest,
  CreateMatchingQuestionRequest,
  UpdatePromptRequest,
  UpdateHelpRequest,
  ToggleFlagRequest,
  ReplaceOptionsRequest,
  SetBoundsRequest,
  SetTextSettingsRequest,
  SetMatchingKeyRequest,
  MoveQuestionRequest,
  QuestionType
} from '../Models/question.model';

@Injectable({
  providedIn: 'root'
})
export class QuestionRepository {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  // ============================================
  // CREATE - Crear preguntas por tipo
  // ============================================

  createChoiceQuestion(
    formId: number,
    sectionId: number | null,
    request: CreateChoiceQuestionRequest
  ): Observable<QuestionResponse> {
    const url = `${this.apiUrl}/forms/${formId}/questions/choice`;
    let params = new HttpParams();
    if (sectionId) {
      params = params.append('sectionId', sectionId.toString());
    }
    return this.http.post<QuestionResponse>(url, request, { params });
  }

  createTrueFalseQuestion(
    formId: number,
    sectionId: number | null,
    request: CreateTrueFalseQuestionRequest
  ): Observable<QuestionResponse> {
    const url = `${this.apiUrl}/forms/${formId}/questions/true-false`;
    let params = new HttpParams();
    if (sectionId) {
      params = params.append('sectionId', sectionId.toString());
    }
    return this.http.post<QuestionResponse>(url, request, { params });
  }

  createTextQuestion(
    formId: number,
    sectionId: number | null,
    request: CreateTextQuestionRequest
  ): Observable<QuestionResponse> {
    const url = `${this.apiUrl}/forms/${formId}/questions/text`;
    let params = new HttpParams();
    if (sectionId) {
      params = params.append('sectionId', sectionId.toString());
    }
    return this.http.post<QuestionResponse>(url, request, { params });
  }

  createMatchingQuestion(
    formId: number,
    sectionId: number | null,
    request: CreateMatchingQuestionRequest
  ): Observable<QuestionResponse> {
    const url = `${this.apiUrl}/forms/${formId}/questions/matching`;
    let params = new HttpParams();
    if (sectionId) {
      params = params.append('sectionId', sectionId.toString());
    }
    return this.http.post<QuestionResponse>(url, request, { params });
  }

  // ============================================
  // READ - Obtener preguntas
  // ============================================

  getQuestionById(id: number): Observable<QuestionResponse> {
    return this.http.get<QuestionResponse>(`${this.apiUrl}/questions/${id}`);
  }

  getQuestionsByForm(
    formId: number,
    sectionId?: number,
    type?: QuestionType,
    search?: string,
    page: number = 0,
    size: number = 50
  ): Observable<PaginatedQuestions> {
    const url = `${this.apiUrl}/forms/${formId}/questions`;
    let params = new HttpParams()
      .append('page', page.toString())
      .append('size', size.toString());

    if (sectionId !== undefined) {
      params = params.append('sectionId', sectionId.toString());
    }
    if (type) {
      params = params.append('type', type);
    }
    if (search) {
      params = params.append('search', search);
    }

    return this.http.get<PaginatedQuestions>(url, { params });
  }

  

  // ============================================
  // UPDATE - Actualizar preguntas
  // ============================================

  updatePrompt(id: number, prompt: string): Observable<QuestionResponse> {
    const body: UpdatePromptRequest = { prompt };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/prompt`, body);
  }

  updateHelp(id: number, helpText?: string): Observable<QuestionResponse> {
    const body: UpdateHelpRequest = { helpText };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/help`, body);
  }

  setRequired(id: number, enabled: boolean): Observable<QuestionResponse> {
    const body: ToggleFlagRequest = { enabled };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/required`, body);
  }

  setShuffle(id: number, enabled: boolean): Observable<QuestionResponse> {
    const body: ToggleFlagRequest = { enabled };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/shuffle`, body);
  }

  replaceOptions(
    id: number,
    options: Array<{ label: string; correct: boolean }>
  ): Observable<QuestionResponse> {
    const body: ReplaceOptionsRequest = { options };
    return this.http.put<QuestionResponse>(`${this.apiUrl}/questions/${id}/options`, body);
  }

  setBounds(id: number, min?: number, max?: number): Observable<QuestionResponse> {
    const body: SetBoundsRequest = { min, max };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/bounds`, body);
  }

  setTextSettings(
    id: number,
    textMode: string,
    placeholder?: string,
    minLength?: number,
    maxLength?: number
  ): Observable<QuestionResponse> {
    const body: SetTextSettingsRequest = {
      textMode: textMode as any,
      placeholder,
      minLength,
      maxLength
    };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/text-settings`, body);
  }

  setMatchingKey(id: number, key: { [key: number]: number }): Observable<QuestionResponse> {
    const body: SetMatchingKeyRequest = { key };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/matching-key`, body);
  }

  moveQuestion(
    id: number,
    targetSectionId?: number,
    newPosition?: number
  ): Observable<QuestionResponse> {
    const body: MoveQuestionRequest = { targetSectionId, newPosition };
    return this.http.patch<QuestionResponse>(`${this.apiUrl}/questions/${id}/move`, body);
  }

  // ============================================
  // DELETE - Eliminar pregunta
  // ============================================

  deleteQuestion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/questions/${id}`);
  }
}
