import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { forkJoin, Observable } from 'rxjs';

import { FormService } from '../../../Services/form.service';
import { PermissionService } from '../../../Services/permission.service';
import { Forms } from '../../../Models/form.model';
import { MemberRole } from '../../../Models/member.model';

@Component({
  selector: 'app-form-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './form-form.component.html',
  styleUrl: './form-form.component.css'
})
export class FormFormComponent implements OnInit {

  // Inicializamos con valores por defecto según tu JSON
  formData: Forms = {
    campaignId: 0,
    title: '',
    description: '',
    coverUrl: 'https://placehold.co/600x200', // Placeholder por defecto
    themeMode: 'light',
    themePrimary: '#3b82f6',
    accessMode: 'PUBLIC',
    openAt: '',
    closeAt: '',
    responseLimitMode: 'UNLIMITED',
    limitedN: null,
    anonymousMode: true,
    allowEditBeforeSubmit: true,
    autoSave: true,
    shuffleQuestions: false,
    shuffleOptions: false,
    progressBar: true,
    paginated: true,
  };

  originalData: Forms | null = null;

  isEditMode: boolean = false;
  formId: number | null = null;
  userRole: MemberRole | null = null;

  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';


  constructor(
    private formService: FormService,
    private permissionService: PermissionService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {

      this.isEditMode = true;
      this.formId = +idParam;
      this.loadFormData(this.formId);
    } else {

      this.isEditMode = false;

      this.route.queryParams.subscribe(params => {
        if (params['campaignId']) {
          this.formData.campaignId = +params['campaignId'];
          this.loadPermissions(this.formData.campaignId);
        }
      });
    }
  }

  loadPermissions(campaignId: number): void {
    this.permissionService.getUserRoleInCampaign(campaignId).subscribe({
      next: (role) => {
        this.userRole = role;
        if (!this.permissionService.canManageForms(role)) {
          this.errorMessage = 'No tienes permisos para gestionar formularios';
          setTimeout(() => this.goBack(), 2000);
        }
      },
      error: () => {
        this.userRole = null;
      }
    });
  }

  loadFormData(id: number): void {
    this.loading = true;
    this.formService.getFormById(id).subscribe({
      next: (data) => {
        this.formData = data;

        if (this.formData.openAt) this.formData.openAt = String(this.formData.openAt).slice(0, 16);
        if (this.formData.closeAt) this.formData.closeAt = String(this.formData.closeAt).slice(0, 16);


        this.originalData = JSON.parse(JSON.stringify(this.formData));
        this.loadPermissions(this.formData.campaignId);
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar el formulario';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.isValid()) return;

    if (!this.permissionService.canManageForms(this.userRole)) {
      this.errorMessage = 'No tienes permisos para gestionar formularios';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.isEditMode && this.formId) {
      this.handleUpdate(this.formId);
    } else {

      this.handleCreate();
    }
  }

  handleCreate(): void {
    const payload = {
      ...this.formData,
      openAt: new Date(this.formData.openAt).toISOString(),
      closeAt: new Date(this.formData.closeAt).toISOString()
    };

    this.formService.createForm(this.formData.campaignId, payload).subscribe({
      next: (response) => {
        this.successMessage = 'Formulario creado con éxito';
        this.goBack();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Error al crear el formulario.';
        this.loading = false;
      }
    });
  }


  handleUpdate(id: number): void {
    if (!this.originalData) return;

    const requests: Observable<any>[] = [];
    const current = this.formData;
    const original = this.originalData;

    // --- COMPARACIONES ---

    // A) Título
    if (current.title !== original.title) {
      requests.push(this.formService.updateTitle(id, current.title));
    }

    // B) Descripción
    if (current.description !== original.description) {
      requests.push(this.formService.updateDescription(id, current.description));
    }

    // C) Fechas (Schedule)
    if (current.openAt !== original.openAt || current.closeAt !== original.closeAt) {
      const startISO = new Date(current.openAt).toISOString();
      const endISO = new Date(current.closeAt).toISOString();
      requests.push(this.formService.updateSchedule(id, startISO, endISO));
    }

    // D) Tema / Color
    if (current.themePrimary !== original.themePrimary || current.themeMode !== original.themeMode) {
      requests.push(this.formService.updateTheme(id, current.themeMode, current.themePrimary));
    }

    // D2) Modo de acceso (PUBLIC, PRIVATE, RESTRICTED)
    if (current.accessMode !== original.accessMode) {
      requests.push(this.formService.updateAccessMode(id, current.accessMode));
    }

    // E) Flags sueltos (Anónimo, AutoGuardado, Edición)
    if (current.anonymousMode !== original.anonymousMode) {
      requests.push(this.formService.toggleAnonymous(id, current.anonymousMode));
    }
    if (current.autoSave !== original.autoSave) {
      requests.push(this.formService.toggleAutoSave(id, current.autoSave));
    }
    if (current.allowEditBeforeSubmit !== original.allowEditBeforeSubmit) {
      requests.push(this.formService.toggleAllowEdit(id, current.allowEditBeforeSubmit));
    }

    // F) Presentación (4 campos juntos)
    if (current.progressBar !== original.progressBar ||
      current.paginated !== original.paginated ||
      current.shuffleQuestions !== original.shuffleQuestions ||
      current.shuffleOptions !== original.shuffleOptions) {

      requests.push(this.formService.updatePresentation(id, current));
    }

    // --- EJECUCIÓN ---

    if (requests.length === 0) {
      this.successMessage = 'No detecté cambios para guardar.';
      this.loading = false;
      setTimeout(() => this.goBack(), 1000);
      return;
    }

    // Ejecutar todas las peticiones en paralelo
    forkJoin(requests).subscribe({
      next: () => {
        this.successMessage = 'Formulario actualizado correctamente';
        this.goBack();
      },
      error: (err) => {
        this.errorMessage = 'Hubo un error al actualizar algunos campos.';
        console.error(err);
        this.loading = false;
      }
    });
  }

  isValid(): boolean {
    if (!this.formData.title) {
      this.errorMessage = 'El título es obligatorio';
      return false;
    }
    // Verificamos campaignId, pero aceptamos que sea 0 si estamos en modo edición (ya que el backend sabe el ID)
    // Ojo: En tu modelo de datos Forms, campaignId es number.
    if ((!this.formData.campaignId || this.formData.campaignId === 0) && !this.isEditMode) {
      this.errorMessage = 'Error interno: No se ha recibido el ID de la campaña.';
      return false;
    }
    return true;
  }

  goBack(): void {
    this.loading = false;
    setTimeout(() => {
      if (this.formData.campaignId) {
        this.router.navigate(['/campaigns', this.formData.campaignId, 'forms']);
      } else {
        this.router.navigate(['/campaigns']);
      }
    }, 1000);
  }

  cancel(): void {
    this.goBack();
  }

  /**
   * Detecta si el modo anónimo debe estar deshabilitado
   * Solo está disponible para formularios PUBLIC
   */
  isAnonymousModeDisabled(): boolean {
    return this.formData.accessMode !== 'PUBLIC';
  }

  /**
   * Se ejecuta cuando cambia el modo de acceso
   * Si el formulario es PRIVATE o RESTRICTED, fuerza anonymousMode a false
   */
  onAccessModeChange(): void {
    if (this.formData.accessMode !== 'PUBLIC') {
      this.formData.anonymousMode = false;
    }
  }
}