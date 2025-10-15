import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { CampaignService } from '../../../Services/campaign.service';
import { Campaign } from '../../../Models/campaign.model';

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
        this.campaign = {
          ...data,
          startDate: this.formatDateForInput(data.startDate),
          endDate: this.formatDateForInput(data.endDate)
        };
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

    const campaignData: Campaign = {
      ...this.campaign,
      startDate: this.formatDateForAPI(this.campaign.startDate),
      endDate: this.formatDateForAPI(this.campaign.endDate)
    };

    console.log('📤 Enviando campaña:', campaignData);
    console.log('🔑 Token en localStorage:', localStorage.getItem('auth_token') ? 'Existe ✅' : 'NO EXISTE ❌');

    if (this.isEditMode && this.campaignId) {
      this.campaignService.updateCampaign(this.campaignId, campaignData).subscribe({
        next: () => {
          this.successMessage = 'Campaña actualizada exitosamente';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/campaigns']), 1500);
        },
        error: (error: any) => {
          console.error('❌ Error completo:', error);
          console.error('Status:', error.status);
          console.error('Message:', error.message);
          console.error('Error body:', error.error);
          this.errorMessage = `Error al actualizar: ${error.status} - ${error.error?.message || error.message}`;
          this.loading = false;
        }
      });
    } else {
      this.campaignService.createCampaign(campaignData).subscribe({
        next: () => {
          this.successMessage = 'Campaña creada exitosamente';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/campaigns']), 1500);
        },
        error: (error: any) => {
          console.error('❌ Error completo:', error);
          console.error('Status:', error.status);
          console.error('Message:', error.message);
          console.error('Error body:', error.error);
          this.errorMessage = `Error al crear: ${error.status} - ${error.error?.message || error.message}`;
          this.loading = false;
        }
      });
    }
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
