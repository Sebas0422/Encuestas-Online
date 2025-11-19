import { Component, OnInit } from '@angular/core';
import { Form } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormService } from '../../../Services/form.service';
import { Forms } from '../../../Models/form.model';


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

  constructor(
    private formService: FormService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.currentCampaignId = +idParam; 
      this.loadForms(this.currentCampaignId);
    } else {
      this.errorMessage = 'No se ha especificado una campaña válida.';
    }
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
}