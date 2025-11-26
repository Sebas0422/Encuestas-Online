import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Section,
  SectionResponse,
  PaginatedSections,
  AddSectionRequest
} from '../Models/section.model';

@Injectable({
  providedIn: 'root'
})
export class SectionRepository {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  // ============================================
  // CREATE
  // ============================================

  addSection(formId: number, title: string): Observable<SectionResponse> {
    const url = `${this.apiUrl}/forms/${formId}/sections`;
    const body: AddSectionRequest = { title };
    return this.http.post<SectionResponse>(url, body);
  }

  // ============================================
  // READ
  // ============================================

  getSectionsByForm(
    formId: number,
    page: number = 0,
    size: number = 50
  ): Observable<PaginatedSections> {
    const url = `${this.apiUrl}/forms/${formId}/sections`;
    const params = new HttpParams()
      .append('page', page.toString())
      .append('size', size.toString());

    return this.http.get<PaginatedSections>(url, { params });
  }

  // ============================================
  // UPDATE
  // ============================================

  renameSection(
    formId: number,
    sectionId: number,
    title: string
  ): Observable<SectionResponse> {
    const url = `${this.apiUrl}/forms/${formId}/sections/${sectionId}/title`;
    const body: AddSectionRequest = { title };
    return this.http.patch<SectionResponse>(url, body);
  }

  moveSection(
    formId: number,
    sectionId: number,
    newPosition: number
  ): Observable<SectionResponse> {
    const url = `${this.apiUrl}/forms/${formId}/sections/${sectionId}/move/${newPosition}`;
    return this.http.patch<SectionResponse>(url, {});
  }

  // ============================================
  // DELETE
  // ============================================

  deleteSection(formId: number, sectionId: number): Observable<void> {
    const url = `${this.apiUrl}/forms/${formId}/sections/${sectionId}`;
    return this.http.delete<void>(url);
  }
}
