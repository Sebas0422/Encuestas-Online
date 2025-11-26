import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  PaginatedMembers,
  MemberResponse,
  AddMemberRequest,
  UpdateRoleRequest
} from '../Models/member.model';

@Injectable({
  providedIn: 'root'
})
export class MemberRepository {
  private apiUrl = `${environment.apiUrl}/campaigns`;

  constructor(private http: HttpClient) { }

  // ============================================
  // GET - Obtener miembros de una campaña
  // ============================================

  getMembers(
    campaignId: number,
    page: number = 0,
    size: number = 20
  ): Observable<PaginatedMembers> {
    const url = `${this.apiUrl}/${campaignId}/members`;
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedMembers>(url, { params });
  }

  // ============================================
  // POST - Agregar miembro a campaña
  // ============================================

  addMember(
    campaignId: number,
    request: AddMemberRequest
  ): Observable<MemberResponse> {
    const url = `${this.apiUrl}/${campaignId}/members`;
    return this.http.post<MemberResponse>(url, request);
  }

  // ============================================
  // PATCH - Actualizar rol de miembro
  // ============================================

  updateRole(
    campaignId: number,
    userId: number,
    request: UpdateRoleRequest
  ): Observable<MemberResponse> {
    const url = `${this.apiUrl}/${campaignId}/members/${userId}/role`;
    return this.http.patch<MemberResponse>(url, request);
  }

  // ============================================
  // DELETE - Remover miembro de campaña
  // ============================================

  removeMember(campaignId: number, userId: number): Observable<void> {
    const url = `${this.apiUrl}/${campaignId}/members/${userId}`;
    return this.http.delete<void>(url);
  }
}
