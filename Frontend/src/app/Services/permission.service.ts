import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { MemberService } from './member.service';
import { AuthService } from './auth.service';
import { MemberRole } from '../Models/member.model';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private userRolesCache: Map<number, MemberRole> = new Map();

  constructor(
    private memberService: MemberService,
    private authService: AuthService
  ) { }

  /**
   * Obtiene el rol del usuario actual en una campaña
   */
  getUserRoleInCampaign(campaignId: number): Observable<MemberRole | null> {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      return of(null);
    }

    // Verificar cache
    const cacheKey = campaignId;
    if (this.userRolesCache.has(cacheKey)) {
      return of(this.userRolesCache.get(cacheKey) || null);
    }

    // Obtener de la API
    return this.memberService.getMembers(campaignId).pipe(
      map(members => {
        const member = members.find(m => m.userId === currentUser.userId);
        const role = member?.role || null;

        // Guardar en cache
        if (role) {
          this.userRolesCache.set(cacheKey, role);
        }

        return role;
      }),
      catchError(() => of(null))
    );
  }

  /**
   * Verifica si el usuario puede crear/editar campañas
   */
  canManageCampaigns(role: MemberRole | null): boolean {
    // Si no tiene rol explícito (null), es el creador -> acceso completo
    // READER no puede gestionar
    return role !== MemberRole.READER;
  }

  /**
   * Verifica si el usuario puede eliminar campañas
   */
  canDeleteCampaigns(role: MemberRole | null): boolean {
    // Si no tiene rol explícito (null), es el creador -> puede eliminar
    // ADMIN puede eliminar, CREATOR y READER no
    return role !== MemberRole.READER && role !== MemberRole.CREATOR;
  }

  /**
   * Verifica si el usuario puede crear/editar formularios
   */
  canManageForms(role: MemberRole | null): boolean {
    // Si no tiene rol explícito (null), es el creador -> acceso completo
    // READER no puede gestionar
    return role !== MemberRole.READER;
  }

  /**
   * Verifica si el usuario puede eliminar formularios
   */
  canDeleteForms(role: MemberRole | null): boolean {
    // Si no tiene rol explícito (null), es el creador -> puede eliminar
    // ADMIN puede eliminar, CREATOR y READER no
    return role !== MemberRole.READER && role !== MemberRole.CREATOR;
  }

  /**
   * Verifica si el usuario puede crear/editar preguntas
   */
  canManageQuestions(role: MemberRole | null): boolean {
    // Si no tiene rol explícito (null), es el creador -> acceso completo
    // READER no puede gestionar
    return role !== MemberRole.READER;
  }

  /**
   * Verifica si el usuario puede eliminar preguntas
   */
  canDeleteQuestions(role: MemberRole | null): boolean {
    // Si no tiene rol explícito (null), es el creador -> puede eliminar
    // ADMIN puede eliminar, CREATOR y READER no
    return role !== MemberRole.READER && role !== MemberRole.CREATOR;
  }

  /**
   * Verifica si el usuario puede gestionar miembros
   */
  canManageMembers(role: MemberRole | null): boolean {
    // Si no tiene rol explícito (null), es el creador -> puede gestionar miembros
    // READER no puede gestionar
    return role !== MemberRole.READER;
  }

  /**
   * Verifica si el usuario es READER (solo lectura)
   */
  isReadOnly(role: MemberRole | null): boolean {
    // Solo READER es de solo lectura
    // null = creador con acceso completo
    return role === MemberRole.READER;
  }

  /**
   * Limpia el cache de roles
   */
  clearCache(): void {
    this.userRolesCache.clear();
  }

  /**
   * Limpia el cache de una campaña específica
   */
  clearCampaignCache(campaignId: number): void {
    this.userRolesCache.delete(campaignId);
  }
}
