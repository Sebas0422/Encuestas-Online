import { Component, OnInit } from '@angular/core';
import { Form } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormService } from '../../../Services/form.service';
import { PermissionService } from '../../../Services/permission.service';
import { Forms } from '../../../Models/form.model';
import { MemberRole } from '../../../Models/member.model';


@Component({
  selector: 'app-forms-list',
  imports: [CommonModule, RouterModule],
  templateUrl: './forms-list.component.html',
  styleUrl: './forms-list.component.css'
})
export class FormsListComponent implements OnInit {
  forms: Forms[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  currentCampaignId: number | null = null;
  userRole: MemberRole | null = null;

  constructor(
    private formService: FormService,
    private permissionService: PermissionService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.currentCampaignId = +idParam;
      this.loadPermissions();
      this.loadForms(this.currentCampaignId);
    } else {
      this.errorMessage = 'No se ha especificado una campaña válida.';
    }
  }

  loadPermissions(): void {
    if (!this.currentCampaignId) return;

    this.permissionService.getUserRoleInCampaign(this.currentCampaignId).subscribe({
      next: (role) => {
        this.userRole = role;
      },
      error: () => {
        this.userRole = null;
      }
    });
  }


  loadForms(campaignId: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.formService.getAllForms(campaignId).subscribe({
      next: (data) => {
        this.forms = data;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar las campañas';
        this.loading = false;
        console.error('Error:', error);
      }
    });
  }


  navigateToCreate(): void {
    if (this.currentCampaignId) {
      this.router.navigate(['/forms/create'], {
        queryParams: { campaignId: this.currentCampaignId }
      });
    } else {
      console.error('No tengo un campaignId para enviar');
    }
  }

  navigateToEdit(id: number): void {
    this.router.navigate(['/forms/edit', id]);
  }

  navigateToForms(campaignId: number): void {
    console.log('Navegando a formularios de la campaña:', campaignId);
    this.router.navigate(['/campaigns', campaignId, 'forms']);
  }

  deleteForm(id: number, name: string): void {
    if (!this.canDeleteForms()) {
      this.errorMessage = 'No tienes permisos para eliminar formularios';
      return;
    }

    if (confirm(`¿Está seguro de eliminar el Formulario "${name}"?`)) {
      this.formService.deleteForm(id).subscribe({
        next: () => {
          this.loadForms(this.currentCampaignId!);
        },
        error: (error) => {
          this.errorMessage = 'Error al eliminar el formulario';
          console.error('Error:', error);
        }
      });
    }
  }

  navigateToQuestionBuilder(formId: number): void {
    this.router.navigate(['/forms', formId, 'questions']);
  }

  navigateToPreview(formId: number): void {
    this.router.navigate(['/forms', formId, 'preview']);
  }

  navigateToResponses(formId: number): void {
    this.router.navigate(['/forms', formId, 'responses']);
  }

  canManageForms(): boolean {
    return this.permissionService.canManageForms(this.userRole);
  }

  canDeleteForms(): boolean {
    return this.permissionService.canDeleteForms(this.userRole);
  }
}