import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../Services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  styleUrl: './login.component.css',
  imports: [FormsModule, CommonModule],
})

export class LoginComponent {
  errorMessage: string = '';
  email: string = '';
  password: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.errorMessage = '';
  }
  /**
   * Se ejecuta cuando el formulario HTML es enviado.
   * @param formData Los datos del formulario ({username: '...', password: '...'}).
   */

  onLogin(formData: any) {
    this.errorMessage = '';

    this.authService.login(formData).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);

        this.email = '';
        this.password = '';

        this.router.navigate(['/campaigns']);
      },
      error: (error) => {
        console.error('Error al iniciar sesión:', error);
        this.errorMessage = 'Credenciales inválidas. Por favor, inténtalo de nuevo.';
        this.password = '';
      },
      complete: () => {
      }
    });
  }
}
