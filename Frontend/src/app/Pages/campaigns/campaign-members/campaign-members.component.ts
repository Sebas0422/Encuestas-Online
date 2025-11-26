import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MemberService } from '../../../Services/member.service';
import { CampaignService } from '../../../Services/campaign.service';
import { UserService } from '../../../Services/user.service';
import { AuthService } from '../../../Services/auth.service';
import { PermissionService } from '../../../Services/permission.service';
import { MemberResponse, MemberRole, MemberWithUserInfo } from '../../../Models/member.model';
import { Campaign } from '../../../Models/campaign.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-campaign-members',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './campaign-members.component.html',
  styleUrl: './campaign-members.component.css'
})
export class CampaignMembersComponent implements OnInit {
  campaignId!: number;
  campaign: Campaign | null = null;
  members: MemberWithUserInfo[] = [];
  currentUserRole: MemberRole | null = null;
  currentUserId: number | null = null;

  // Estados de carga y mensajes
  loading = false;
  errorMessage = '';
  successMessage = '';

  // Modal para agregar miembro
  showAddModal = false;
  newMemberUserId: number | null = null;
  newMemberRole: MemberRole = MemberRole.READER;
  searchEmail: string = '';
  searchResults: any[] = [];
  searchLoading = false;
  selectedUsers: any[] = [];

  // Modal para editar rol
  showEditModal = false;
  editingMember: MemberWithUserInfo | null = null;
  editRole: MemberRole = MemberRole.READER;

  // Enums para el template
  MemberRole = MemberRole;
  availableRoles = Object.values(MemberRole);

  constructor(
    private memberService: MemberService,
    private campaignService: CampaignService,
    private userService: UserService,
    private authService: AuthService,
    private permissionService: PermissionService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    const currentUser = this.authService.getCurrentUser();

    if (id && currentUser) {
      this.campaignId = +id;
      this.currentUserId = currentUser.userId;
      this.loadCampaignData();
      this.loadMembers();
    }
  }

  loadCampaignData(): void {
    this.campaignService.getCampaignById(this.campaignId).subscribe({
      next: (campaign) => {
        this.campaign = campaign;
      },
      error: (err) => {
        console.error('Error al cargar campaña:', err);
        this.errorMessage = 'Error al cargar información de la campaña';
      }
    });
  }

  loadMembers(): void {
    this.loading = true;
    this.errorMessage = '';

    this.memberService.getMembers(this.campaignId).subscribe({
      next: (members) => {
        // Cargar información de usuarios para cada miembro
        this.loadMembersWithUserInfo(members);

        // Determinar el rol del usuario actual
        if (this.currentUserId) {
          const currentMember = members.find(m => m.userId === this.currentUserId);
          this.currentUserRole = currentMember?.role || null;
        }
      },
      error: (err) => {
        console.error('Error al cargar miembros:', err);
        this.errorMessage = 'Error al cargar la lista de miembros';
        this.loading = false;
      }
    });
  }

  loadMembersWithUserInfo(members: MemberResponse[]): void {
    if (members.length === 0) {
      this.members = [];
      this.loading = false;
      return;
    }

    // Crear observables para obtener info de cada usuario
    const userRequests = members.map(member =>
      this.userService.getUserById(member.userId)
    );

    forkJoin(userRequests).subscribe({
      next: (users) => {
        this.members = members.map((member, index) => ({
          ...member,
          userEmail: users[index].email,
          userFullName: users[index].fullName
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar información de usuarios:', err);
        // Mostrar miembros sin información de usuario
        this.members = members as MemberWithUserInfo[];
        this.loading = false;
      }
    });
  }

  // ============================================
  // CONTROL DE PERMISOS
  // ============================================

  canManageMembers(): boolean {
    // Usar PermissionService que maneja null como creador
    return this.permissionService.canManageMembers(this.currentUserRole);
  }

  canEditMember(member: MemberWithUserInfo): boolean {
    // No puedes editarte a ti mismo
    if (member.userId === this.currentUserId) {
      return false;
    }

    // Si no puedes gestionar miembros, no puedes editar
    if (!this.permissionService.canManageMembers(this.currentUserRole)) {
      return false;
    }

    // null (creador) o ADMIN pueden editar a cualquiera
    if (this.currentUserRole === null || this.currentUserRole === MemberRole.ADMIN) {
      return true;
    }

    // CREATOR puede editar solo a READER
    if (this.currentUserRole === MemberRole.CREATOR && member.role === MemberRole.READER) {
      return true;
    }

    return false;
  }

  canRemoveMember(member: MemberWithUserInfo): boolean {
    // Misma lógica que editar
    return this.canEditMember(member);
  }

  // ============================================
  // AGREGAR MIEMBRO
  // ============================================

  openAddModal(): void {
    if (!this.canManageMembers()) {
      this.errorMessage = 'No tienes permisos para agregar miembros';
      return;
    }

    this.showAddModal = true;
    this.newMemberUserId = null;
    this.newMemberRole = MemberRole.READER;
    this.searchEmail = '';
    this.searchResults = [];
    this.selectedUsers = [];
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeAddModal(): void {
    this.showAddModal = false;
    this.newMemberUserId = null;
    this.newMemberRole = MemberRole.READER;
    this.searchEmail = '';
    this.searchResults = [];
    this.selectedUsers = [];
    this.errorMessage = '';
  }

  searchUsersByEmail(): void {
    if (!this.searchEmail || this.searchEmail.trim().length < 3) {
      this.errorMessage = 'Ingresa al menos 3 caracteres para buscar';
      return;
    }

    this.searchLoading = true;
    this.errorMessage = '';

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        const searchLower = this.searchEmail.toLowerCase();
        this.searchResults = users.filter(user =>
          user.email.toLowerCase().includes(searchLower) ||
          user.fullName.toLowerCase().includes(searchLower)
        );

        // Filtrar usuarios que ya son miembros
        const memberUserIds = this.members.map(m => m.userId);
        this.searchResults = this.searchResults.filter(u => !memberUserIds.includes(u.id));

        if (this.searchResults.length === 0) {
          this.errorMessage = 'No se encontraron usuarios con ese email';
        }
        this.searchLoading = false;
      },
      error: (err) => {
        console.error('Error al buscar usuarios:', err);
        this.errorMessage = 'Error al buscar usuarios';
        this.searchLoading = false;
      }
    });
  }

  selectUser(user: any): void {
    const index = this.selectedUsers.findIndex(u => u.id === user.id);
    if (index > -1) {
      // Si ya está seleccionado, lo quitamos
      this.selectedUsers.splice(index, 1);
    } else {
      // Si no está seleccionado, lo agregamos
      this.selectedUsers.push(user);
    }
    this.errorMessage = '';
  }

  isUserSelected(user: any): boolean {
    return this.selectedUsers.some(u => u.id === user.id);
  }

  removeSelectedUser(user: any): void {
    const index = this.selectedUsers.findIndex(u => u.id === user.id);
    if (index > -1) {
      this.selectedUsers.splice(index, 1);
    }
  }

  addMember(): void {
    if (!this.selectedUsers || this.selectedUsers.length === 0) {
      this.errorMessage = 'Debes seleccionar al menos un usuario de la lista';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Crear un array de observables para agregar cada miembro
    const addRequests = this.selectedUsers.map(user =>
      this.memberService.addMember(this.campaignId, user.id, this.newMemberRole)
    );

    // Ejecutar todas las peticiones en paralelo
    forkJoin(addRequests).subscribe({
      next: () => {
        this.successMessage = 'Miembro agregado exitosamente';
        this.closeAddModal();
        this.loadMembers();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('Error al agregar miembro:', err);
        this.errorMessage = err.error?.message || 'Error al agregar miembro. Verifica que el usuario exista.';
        this.loading = false;
      }
    });
  }

  // ============================================
  // EDITAR ROL
  // ============================================

  openEditModal(member: MemberWithUserInfo): void {
    if (!this.canEditMember(member)) {
      this.errorMessage = 'No tienes permisos para editar este miembro';
      return;
    }

    this.editingMember = member;
    this.editRole = member.role;
    this.showEditModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingMember = null;
  }

  updateRole(): void {
    if (!this.editingMember) return;

    this.loading = true;
    this.errorMessage = '';

    this.memberService.updateMemberRole(
      this.campaignId,
      this.editingMember.userId,
      this.editRole
    ).subscribe({
      next: (member) => {
        this.successMessage = `Rol actualizado a ${this.getRoleLabel(member.role)}`;
        this.closeEditModal();
        this.loadMembers();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('Error al actualizar rol:', err);
        this.errorMessage = 'Error al actualizar el rol del miembro';
        this.loading = false;
      }
    });
  }

  // ============================================
  // ELIMINAR MIEMBRO
  // ============================================

  removeMember(member: MemberWithUserInfo): void {
    if (!this.canRemoveMember(member)) {
      this.errorMessage = 'No tienes permisos para eliminar este miembro';
      return;
    }

    const memberName = member.userFullName || member.userEmail || `Usuario ${member.userId}`;
    const confirmed = confirm(
      `¿Estás seguro de eliminar a ${memberName} de esta campaña?`
    );

    if (!confirmed) return;

    this.loading = true;
    this.errorMessage = '';

    this.memberService.removeMember(this.campaignId, member.userId).subscribe({
      next: () => {
        this.successMessage = `${memberName} eliminado de la campaña`;
        this.loadMembers();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('Error al eliminar miembro:', err);
        this.errorMessage = 'Error al eliminar el miembro';
        this.loading = false;
      }
    });
  }

  // ============================================
  // HELPERS
  // ============================================

  getRoleLabel(role: MemberRole): string {
    return this.memberService.getRoleLabel(role);
  }

  getRoleBadgeClass(role: MemberRole): string {
    return this.memberService.getRoleBadgeClass(role);
  }

  getRoleIcon(role: MemberRole): string {
    return this.memberService.getRoleIcon(role);
  }

  getMemberDisplayName(member: MemberWithUserInfo): string {
    return member.userFullName || member.userEmail || `Usuario ${member.userId}`;
  }

  goBack(): void {
    this.router.navigate(['/campaigns']);
  }
}
