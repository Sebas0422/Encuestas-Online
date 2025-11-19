import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { CampaignService } from '../../../Services/campaign.service';
import { Campaign } from '../../../Models/campaign.model';
import { forkJoin, Observable } from 'rxjs'; 

@Component({
  selector: 'app-campaign-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './campaign-form.component.html',
  styleUrls: ['./campaign-form.component.css']
})
export class CampaignFormComponent implements OnInit {

  campaign: Campaign = {
    name: '',
    description: '',
    startDate: '',
    endDate: ''
  };

  // Nuevo: Para comparar qué cambió realmente
  originalCampaign: Campaign | null = null;

  isEditMode: boolean = false;
  campaignId: number | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private campaignService: CampaignService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.campaignId = +id;
      this.loadCampaign(this.campaignId);
    }
  }

  loadCampaign(id: number): void {
    this.loading = true;
    this.campaignService.getCampaignById(id).subscribe({
      next: (data: Campaign) => {
        // Formateamos fechas para el input (datetime-local)
        this.campaign = {
          ...data,
          startDate: this.formatDateForInput(data.startDate),
          endDate: this.formatDateForInput(data.endDate)
        };

        // GUARDA UNA COPIA: Clonamos el objeto para tener la referencia original
        // Esto es crucial para saber qué campos editó el usuario
        this.originalCampaign = JSON.parse(JSON.stringify(this.campaign));

        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al cargar la campaña';
        this.loading = false;
        console.error('Error:', error);
      }
    });
  }

  formatDateForInput(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
  }

  formatDateForAPI(dateString: string): string {
    return new Date(dateString).toISOString();
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.isEditMode) {
      const campaignData: Campaign = {
        ...this.campaign,
        startDate: this.formatDateForAPI(this.campaign.startDate),
        endDate: this.formatDateForAPI(this.campaign.endDate)
      };

      this.campaignService.createCampaign(campaignData).subscribe({
        next: () => this.handleSuccess('Campaña creada exitosamente'),
        error: (err) => this.handleError(err)
      });

    } else {
      this.handleUpdate();
    }
  }

  handleUpdate(): void {
    if (!this.campaignId || !this.originalCampaign) return;

    const requests: Observable<any>[] = [];

    if (this.campaign.name !== this.originalCampaign.name) {
      requests.push(
        this.campaignService.renameCampaign(this.campaignId, this.campaign.name)
      );
    }

    if (this.campaign.description !== this.originalCampaign.description) {
      requests.push(
        this.campaignService.changeCampaignDescription(this.campaignId, this.campaign.description)
      );
    }
    if (this.campaign.startDate !== this.originalCampaign.startDate ||
      this.campaign.endDate !== this.originalCampaign.endDate) {

      const startISO = this.formatDateForAPI(this.campaign.startDate);
      const endISO = this.formatDateForAPI(this.campaign.endDate);

      requests.push(
        this.campaignService.rescheduleCampaign(this.campaignId, startISO, endISO)
      );
    }
    if (requests.length === 0) {
      this.successMessage = 'No se detectaron cambios';
      this.loading = false;
      setTimeout(() => this.router.navigate(['/campaigns']), 1000);
      return;
    }

    forkJoin(requests).subscribe({
      next: () => this.handleSuccess('Campaña actualizada exitosamente'),
      error: (err) => this.handleError(err)
    });
  }

  handleSuccess(msg: string): void {
    this.successMessage = msg;
    this.loading = false;
    setTimeout(() => this.router.navigate(['/campaigns']), 1500);
  }

  handleError(error: any): void {
    console.error('❌ Error:', error);
    this.errorMessage = `Error: ${error.status} - ${error.error?.message || error.message}`;
    this.loading = false;
  }

  validateForm(): boolean {
    if (!this.campaign.name || !this.campaign.description ||
      !this.campaign.startDate || !this.campaign.endDate) {
      this.errorMessage = 'Todos los campos son obligatorios';
      return false;
    }

    const startDate = new Date(this.campaign.startDate);
    const endDate = new Date(this.campaign.endDate);

    if (endDate <= startDate) {
      this.errorMessage = 'La fecha de fin debe ser posterior a la fecha de inicio';
      return false;
    }

    return true;
  }

  cancel(): void {
    this.router.navigate(['/campaigns']);
  }
}