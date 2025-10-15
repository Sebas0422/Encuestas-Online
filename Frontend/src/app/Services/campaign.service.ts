import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Campaign } from '../Models/campaign.model';
import { CampaignRepository } from '../Repositories/campaign.repository';

@Injectable({
  providedIn: 'root'
})
export class CampaignService {
  constructor(private repository: CampaignRepository) { }

  getAllCampaigns(): Observable<Campaign[]> {
    return this.repository.getAll();
  }

  getCampaignById(id: number): Observable<Campaign> {
    return this.repository.getById(id);
  }

  createCampaign(campaign: Campaign): Observable<Campaign> {
    return this.repository.create(campaign);
  }

  updateCampaign(id: number, campaign: Campaign): Observable<Campaign> {
    return this.repository.update(id, campaign);
  }

  deleteCampaign(id: number): Observable<void> {
    return this.repository.delete(id);
  }
}
