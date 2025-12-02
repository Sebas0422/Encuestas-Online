import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';





@Injectable({
    providedIn: 'root'
})
export class ResponsesRepository {
    private apiUrl = 'http://localhost:8080/api';

    constructor(private http: HttpClient) { }

   
    saveResponseTrueFalseAnswer(submission_id: number, questionId: number, answer: boolean): Observable<void> {
        const url = `${this.apiUrl}/submissions/${submission_id}/answers/true-false`;
        const body = { questionId: questionId, value: answer };
        return this.http.post<void>(url, body);
    }

    saveResponseChoiceAnswer(submission_id: number, questionId: number, optionIds: number[]): Observable<void> {
        const url = `${this.apiUrl}/submissions/${submission_id}/answers/choice`;
        const body = { questionId: questionId, optionIds: optionIds };
        return this.http.post<void>(url, body);
    }

    saveResponseTextAnswer(submission_id: number, questionId: number, text: string): Observable<void> {
        const url = `${this.apiUrl}/submissions/${submission_id}/answers/text`;
        const body = { questionId: questionId, value: text };
        return this.http.post<void>(url, body);
    }

    // Obtener todas las respuestas de un formulario
    getSubmissionsByForm(formId: number): Observable<any> {
        const url = `${this.apiUrl}/forms/${formId}/submissions`;
        return this.http.get<any>(url);
    }

    // Obtener respuestas detalladas de una pregunta específica
    getResponsesByQuestion(formId: number, questionId: number): Observable<any> {
        const url = `${this.apiUrl}/forms/${formId}/questions/${questionId}/responses`;
        return this.http.get<any>(url);
    }

    // Obtener estadísticas de un formulario
    getFormStatistics(formId: number): Observable<any> {
        const url = `${this.apiUrl}/forms/${formId}/statistics`;
        return this.http.get<any>(url);
    }

    // Obtener reporte agregado de un formulario (con opción de incluir DRAFT)
    getFormReport(formId: number, includeDrafts: boolean = true): Observable<any> {
        const url = `${this.apiUrl}/reports/forms/${formId}?includeDrafts=${includeDrafts}`;
        return this.http.get<any>(url);
    }


}