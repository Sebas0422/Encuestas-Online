import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {

  }

  /**
   * Envía las credenciales al backend para iniciar sesión.
   * @param credentials Objeto con {username, password}.
   * @returns Observable con la respuesta (debería incluir el token JWT).
   */

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  // Aquí agregarías la lógica para guardar/obtener el token JWT en localStorage
  // y otras funciones como logout() y isAuthenticated().

  // Ejemplo básico de cómo guardar el token

  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }
}
