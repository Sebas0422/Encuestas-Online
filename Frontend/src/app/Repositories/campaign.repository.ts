import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Campaign } from '../Models/campaign.model';

@Injectable({
  providedIn: 'root'
})
export class CampaignRepository {
  private apiUrl = 'http://localhost:8080/api/campaigns';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(this.apiUrl);
  }

  getById(id: number): Observable<Campaign> {
    return this.http.get<Campaign>(`${this.apiUrl}/${id}`);
  }

  create(campaign: Campaign): Observable<Campaign> {
    return this.http.post<Campaign>(`${this.apiUrl}/create`, JSON.stringify(campaign), {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  update(id: number, campaign: Campaign): Observable<Campaign> {
    return this.http.put<Campaign>(`${this.apiUrl}/update/${id}`, JSON.stringify(campaign), {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}
