import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserResponse, PaginatedUsers } from '../Models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserRepository {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  /**
   * Obtiene un usuario por ID
   */
  getById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtiene un usuario por email
   */
  getByEmail(email: string): Observable<UserResponse> {
    const params = new HttpParams().set('email', email);
    return this.http.get<UserResponse>(`${this.apiUrl}/by-email`, { params });
  }

  /**
   * Lista todos los usuarios con paginación
   */
  list(
    search?: string,
    page: number = 0,
    size: number = 20
  ): Observable<PaginatedUsers> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<PaginatedUsers>(this.apiUrl, { params });
  }
}
