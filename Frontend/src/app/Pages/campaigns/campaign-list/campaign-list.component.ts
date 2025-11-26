import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { CampaignService } from '../../../Services/campaign.service';
import { PermissionService } from '../../../Services/permission.service';
import { Campaign } from '../../../Models/campaign.model';
import { MemberRole } from '../../../Models/member.model';
import { forkJoin, of } from 'rxjs';

@Component({
  selector: 'app-campaign-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './campaign-list.component.html',
  styleUrls: ['./campaign-list.component.css']
})
export class CampaignListComponent implements OnInit {
  campaigns: Campaign[] = [];
  campaignRoles: Map<number, MemberRole | null> = new Map();
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private campaignService: CampaignService,
    private permissionService: PermissionService,
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
        this.loadPermissions();
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar las campañas';
        this.loading = false;
        console.error('Error:', error);
      }
    });
  }

  loadPermissions(): void {
    if (this.campaigns.length === 0) {
      this.loading = false;
      return;
    }

    const roleRequests = this.campaigns.map(campaign =>
      campaign.id ? this.permissionService.getUserRoleInCampaign(campaign.id) : of(null)
    );

    forkJoin(roleRequests).subscribe({
      next: (roles) => {
        this.campaigns.forEach((campaign, index) => {
          if (campaign.id) {
            this.campaignRoles.set(campaign.id, roles[index]);
          }
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  canEdit(campaignId: number | undefined): boolean {
    if (!campaignId) return false;
    const role = this.campaignRoles.get(campaignId) || null;
    return this.permissionService.canManageCampaigns(role);
  }

  canDelete(campaignId: number | undefined): boolean {
    if (!campaignId) return false;
    const role = this.campaignRoles.get(campaignId) || null;
    return this.permissionService.canDeleteCampaigns(role);
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

  navigateToMembers(campaignId: number): void {
    this.router.navigate(['/campaigns', campaignId, 'members']);
  }

  deleteCampaign(id: number, name: string): void {
    if (!this.canDelete(id)) {
      this.errorMessage = 'No tienes permisos para eliminar esta campaña';
      return;
    }

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
