import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

interface AuthResponse {
  tokenType: string;
  accessToken: string;
  expiresIn: number;
  userId: number;
  email: string;
  fullName: string;
  systemAdmin: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly EXPIRES_KEY = 'token_expires';
  private readonly USER_ID_KEY = 'user_id';
  private readonly USER_EMAIL_KEY = 'user_email';
  private readonly USER_NAME_KEY = 'user_name';
  private readonly IS_ADMIN_KEY = 'is_admin';

  constructor(private http: HttpClient) { }

  /**
   * Envía las credenciales al backend para iniciar sesión.
   * @param credentials Objeto con {email, password}.
   * @returns Observable con la respuesta (token JWT, userData y expiresIn).
   */
  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.saveToken(response.accessToken, response.expiresIn);
        this.saveUserData(response);
      })
    );
  }

  /**
   * Guarda el token JWT en cookies con fecha de expiración.
   * @param token El token JWT.
   * @param expiresIn Tiempo de expiración en segundos.
   */
  saveToken(token: string, expiresIn: number): void {
    const expirationDate = new Date(Date.now() + expiresIn * 1000);

    document.cookie = `${this.TOKEN_KEY}=${token}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`;
    document.cookie = `${this.EXPIRES_KEY}=${expirationDate.getTime()}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`;

    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.EXPIRES_KEY, expirationDate.getTime().toString());
  }

  /**
   * Guarda los datos del usuario en localStorage.
   * @param response Respuesta de autenticación con datos del usuario.
   */
  saveUserData(response: AuthResponse): void {
    localStorage.setItem(this.USER_ID_KEY, response.userId.toString());
    localStorage.setItem(this.USER_EMAIL_KEY, response.email);
    localStorage.setItem(this.USER_NAME_KEY, response.fullName);
    localStorage.setItem(this.IS_ADMIN_KEY, response.systemAdmin.toString());
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
    localStorage.removeItem(this.USER_ID_KEY);
    localStorage.removeItem(this.USER_EMAIL_KEY);
    localStorage.removeItem(this.USER_NAME_KEY);
    localStorage.removeItem(this.IS_ADMIN_KEY);
  }

  /**
   * Obtiene los datos del usuario autenticado.
   * @returns Objeto con los datos del usuario o null.
   */
  getCurrentUser(): { userId: number; email: string; fullName: string; systemAdmin: boolean } | null {
    const userId = localStorage.getItem(this.USER_ID_KEY);
    const email = localStorage.getItem(this.USER_EMAIL_KEY);
    const fullName = localStorage.getItem(this.USER_NAME_KEY);
    const systemAdmin = localStorage.getItem(this.IS_ADMIN_KEY);

    if (userId && email && fullName) {
      return {
        userId: +userId,
        email,
        fullName,
        systemAdmin: systemAdmin === 'true'
      };
    }
    return null;
  }

  /**
   * Registra un nuevo usuario.
   * @param userData Datos del usuario a registrar.
   * @returns Observable con la respuesta del backend.
   */
  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }
}
