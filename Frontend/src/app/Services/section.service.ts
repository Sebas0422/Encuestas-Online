import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  SectionResponse,
  PaginatedSections
} from '../Models/section.model';
import { SectionRepository } from '../Repositories/section.repository';

@Injectable({
  providedIn: 'root'
})
export class SectionService {
  constructor(private repository: SectionRepository) { }

  // ============================================
  // CREATE
  // ============================================

  addSection(formId: number, title: string): Observable<SectionResponse> {
    return this.repository.addSection(formId, title);
  }

  // ============================================
  // READ
  // ============================================

  getSectionsByForm(
    formId: number,
    page?: number,
    size?: number
  ): Observable<SectionResponse[]> {
    console.log('🔧 SectionService.getSectionsByForm - formId:', formId, 'page:', page, 'size:', size);
    return this.repository
      .getSectionsByForm(formId, page, size)
      .pipe(map((response: PaginatedSections) => {
        console.log('📦 Respuesta paginada completa:', response);
        console.log('📋 Items extraídos:', response.items);
        return response.items;
      }));
  }

  getSectionsByFormPaginated(
    formId: number,
    page?: number,
    size?: number
  ): Observable<PaginatedSections> {
    return this.repository.getSectionsByForm(formId, page, size);
  }

  // ============================================
  // UPDATE
  // ============================================

  renameSection(
    formId: number,
    sectionId: number,
    title: string
  ): Observable<SectionResponse> {
    return this.repository.renameSection(formId, sectionId, title);
  }

  moveSection(
    formId: number,
    sectionId: number,
    newPosition: number
  ): Observable<SectionResponse> {
    return this.repository.moveSection(formId, sectionId, newPosition);
  }

  // ============================================
  // DELETE
  // ============================================

  deleteSection(formId: number, sectionId: number): Observable<void> {
    return this.repository.deleteSection(formId, sectionId);
  }
}
