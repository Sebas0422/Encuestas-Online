import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { MemberRepository } from '../Repositories/member.repository';
import {
  MemberResponse,
  PaginatedMembers,
  AddMemberRequest,
  UpdateRoleRequest,
  MemberRole
} from '../Models/member.model';

@Injectable({
  providedIn: 'root'
})
export class MemberService {
  constructor(private repository: MemberRepository) { }

  /**
   * Obtiene la lista de miembros de una campaña
   */
  getMembers(
    campaignId: number,
    page: number = 0,
    size: number = 20
  ): Observable<MemberResponse[]> {
    return this.repository.getMembers(campaignId, page, size).pipe(
      map((response: PaginatedMembers) => response.items)
    );
  }

  /**
   * Obtiene la respuesta paginada completa de miembros
   */
  getMembersPaginated(
    campaignId: number,
    page: number = 0,
    size: number = 20
  ): Observable<PaginatedMembers> {
    return this.repository.getMembers(campaignId, page, size);
  }

  /**
   * Agrega un miembro a la campaña por userId
   */
  addMember(
    campaignId: number,
    userId: number,
    role: MemberRole
  ): Observable<MemberResponse> {
    const request: AddMemberRequest = { userId, role };
    return this.repository.addMember(campaignId, request);
  }

  /**
   * Actualiza el rol de un miembro
   */
  updateMemberRole(
    campaignId: number,
    userId: number,
    role: MemberRole
  ): Observable<MemberResponse> {
    const request: UpdateRoleRequest = { role };
    return this.repository.updateRole(campaignId, userId, request);
  }

  /**
   * Remueve un miembro de la campaña
   */
  removeMember(campaignId: number, userId: number): Observable<void> {
    return this.repository.removeMember(campaignId, userId);
  }

  /**
   * Obtiene la etiqueta en español para un rol
   */
  getRoleLabel(role: MemberRole): string {
    const labels: Record<MemberRole, string> = {
      [MemberRole.ADMIN]: 'Administrador',
      [MemberRole.CREATOR]: 'Creador',
      [MemberRole.READER]: 'Lector'
    };
    return labels[role] || role;
  }

  /**
   * Obtiene el color del badge para un rol
   */
  getRoleBadgeClass(role: MemberRole): string {
    const classes: Record<MemberRole, string> = {
      [MemberRole.ADMIN]: 'badge-admin',
      [MemberRole.CREATOR]: 'badge-creator',
      [MemberRole.READER]: 'badge-reader'
    };
    return classes[role] || 'badge-secondary';
  }

  /**
   * Obtiene el ícono para un rol
   */
  getRoleIcon(role: MemberRole): string {
    const icons: Record<MemberRole, string> = {
      [MemberRole.ADMIN]: 'bi-star-fill',
      [MemberRole.CREATOR]: 'bi-pencil-fill',
      [MemberRole.READER]: 'bi-eye-fill'
    };
    return icons[role] || 'bi-person';
  }
}
