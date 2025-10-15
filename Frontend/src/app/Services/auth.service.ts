import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

interface LoginResponse {
  token: string;
  expiresIn: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly EXPIRES_KEY = 'token_expires';

  constructor(private http: HttpClient) { }

  /**
   * Envía las credenciales al backend para iniciar sesión.
   * @param credentials Objeto con {username, password}.
   * @returns Observable con la respuesta (token JWT y expiresIn).
   */
  login(credentials: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.saveToken(response.token, response.expiresIn);
      })
    );
  }

  /**
   * Guarda el token JWT en cookies con fecha de expiración.
   * @param token El token JWT.
   * @param expiresIn Tiempo de expiración en milisegundos.
   */
  saveToken(token: string, expiresIn: number): void {
    const expirationDate = new Date(Date.now() + expiresIn);

    document.cookie = `${this.TOKEN_KEY}=${token}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`;
    document.cookie = `${this.EXPIRES_KEY}=${expirationDate.getTime()}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`;

    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.EXPIRES_KEY, expirationDate.getTime().toString());
  }

  /**
   * Obtiene el token desde cookies o localStorage.
   * @returns El token JWT o null si no existe.
   */
  getToken(): string | null {
    const cookieToken = this.getCookie(this.TOKEN_KEY);
    if (cookieToken) {
      return cookieToken;
    }

    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Obtiene una cookie por nombre.
   * @param name Nombre de la cookie.
   * @returns Valor de la cookie o null.
   */
  private getCookie(name: string): string | null {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
      let c = ca[i];
      while (c.charAt(0) === ' ') c = c.substring(1, c.length);
      if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
  }

  /**
   * Verifica si el usuario está autenticado y el token no ha expirado.
   * @returns true si está autenticado, false en caso contrario.
   */
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    const expiresStr = this.getCookie(this.EXPIRES_KEY) || localStorage.getItem(this.EXPIRES_KEY);
    if (expiresStr) {
      const expires = parseInt(expiresStr, 10);
      if (Date.now() > expires) {
        this.logout();
        return false;
      }
    }

    return true;
  }

  /**
   * Cierra sesión eliminando el token de cookies y localStorage.
   */
  logout(): void {
    document.cookie = `${this.TOKEN_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
    document.cookie = `${this.EXPIRES_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;

    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.EXPIRES_KEY);
  }

  /**
   * Registra un nuevo usuario.
   * @param userData Datos del usuario a registrar.
   * @returns Observable con la respuesta del backend.
   */
  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, userData);
  }
}
