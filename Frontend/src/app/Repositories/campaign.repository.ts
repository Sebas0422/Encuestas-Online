import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Campaign, PaginatedCampaigns,
  RenameCampaignRequest,
  ChangeDescriptionRequest,
  RescheduleCampaignRequest,
  ChangeStatusRequest } from '../Models/campaign.model';

@Injectable({
  providedIn: 'root'
})
export class CampaignRepository {
  private apiUrl = 'http://localhost:8080/api/campaigns';

  constructor(private http: HttpClient) { }

  getAll(): Observable<PaginatedCampaigns> {
    return this.http.get<PaginatedCampaigns>(this.apiUrl);
  }

  getById(id: number): Observable<Campaign> {
    return this.http.get<Campaign>(`${this.apiUrl}/${id}`);
  }

  create(campaign: Campaign): Observable<Campaign> {
    return this.http.post<Campaign>(`${this.apiUrl}`, JSON.stringify(campaign), {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  // --- MÉTODOS DE ACTUALIZACIÓN ESPECÍFICOS (PATCH) ---
  rename(id: number, name: string): Observable<Campaign> {
    const payload: RenameCampaignRequest = { name };
    return this.http.patch<Campaign>(`${this.apiUrl}/${id}/name`, payload);
  }

  changeDescription(id: number, description: string): Observable<Campaign> {
    const payload: ChangeDescriptionRequest = { description };
    return this.http.patch<Campaign>(`${this.apiUrl}/${id}/description`, payload);
  }

  reschedule(id: number, startDate: string, endDate: string): Observable<Campaign> {
    const payload: RescheduleCampaignRequest = { startDate, endDate };
    return this.http.patch<Campaign>(`${this.apiUrl}/${id}/schedule`, payload);
  }

  changeStatus(id: number, status: string): Observable<Campaign> {
    const payload: ChangeStatusRequest = { status };
    return this.http.patch<Campaign>(`${this.apiUrl}/${id}/status`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
