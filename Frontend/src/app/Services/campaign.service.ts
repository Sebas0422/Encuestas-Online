import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Campaign, PaginatedCampaigns } from '../Models/campaign.model';
import { CampaignRepository } from '../Repositories/campaign.repository';

@Injectable({
  providedIn: 'root'
})
export class CampaignService {
  constructor(private repository: CampaignRepository) { }

  getAllCampaigns(): Observable<Campaign[]> {
    return this.repository.getAll().pipe(
      map((response: PaginatedCampaigns) => response.items)
    );
  }

  getCampaignById(id: number): Observable<Campaign> {
    return this.repository.getById(id);
  }

  createCampaign(campaign: Campaign): Observable<Campaign> {
    return this.repository.create(campaign);
  }
  renameCampaign(id: number, name: string): Observable<Campaign> {
    return this.repository.rename(id, name);
  }

  changeCampaignDescription(id: number, description: string): Observable<Campaign> {
    return this.repository.changeDescription(id, description);
  }

  rescheduleCampaign(id: number, startDate: string, endDate: string): Observable<Campaign> {
    return this.repository.reschedule(id, startDate, endDate);
  }

  changeCampaignStatus(id: number, status: string): Observable<Campaign> {
    return this.repository.changeStatus(id, status);
  }

  deleteCampaign(id: number): Observable<void> {
    return this.repository.delete(id);
  }
}
