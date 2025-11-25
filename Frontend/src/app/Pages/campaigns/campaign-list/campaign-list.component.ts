import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { CampaignService } from '../../../Services/campaign.service';
import { Campaign } from '../../../Models/campaign.model';

@Component({
  selector: 'app-campaign-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './campaign-list.component.html',
  styleUrls: ['./campaign-list.component.css']
})
export class CampaignListComponent implements OnInit {
  campaigns: Campaign[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private campaignService: CampaignService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCampaigns();
  }

  loadCampaigns(): void {
    this.loading = true;
    this.errorMessage = '';
    this.campaignService.getAllCampaigns().subscribe({
      next: (data) => {
        this.campaigns = data;
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
    this.router.navigate(['/campaigns/create']);
  }

  navigateToEdit(id: number): void {
    this.router.navigate(['/campaigns/edit', id]);
  }

  navigateToForms(campaignId: number): void {
    console.log('Navegando a formularios de la campaña:', campaignId);
    this.router.navigate(['/campaigns', campaignId, 'forms']);
  }

  deleteCampaign(id: number, name: string): void {
    if (confirm(`¿Está seguro de eliminar la campaña "${name}"?`)) {
      this.campaignService.deleteCampaign(id).subscribe({
        next: () => {
          this.loadCampaigns();
        },
        error: (error) => {
          this.errorMessage = 'Error al eliminar la campaña';
          console.error('Error:', error);
        }
      });
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
