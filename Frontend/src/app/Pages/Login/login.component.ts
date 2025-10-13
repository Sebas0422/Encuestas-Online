import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../Services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';



@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports:[FormsModule,CommonModule],
})
export class LoginComponent {
  errorMessage: string = '';
  username: string = '';
  password: string = '';

  // Inyecta el AuthService y el Router en el constructor
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
    this.errorMessage = ''; // Limpia errores anteriores

    this.authService.login(formData).subscribe({
      next: (response) => {
        // 1. La llamada al backend fue exitosa
        console.log('Login exitoso:', response);

        this.username = response.username;
        this.password = response.password;

        this.username = '';
        this.password = '';

        // 2. Guarda el token de la respuesta
        this.authService.saveToken(response.token);

        // 3. Redirige al usuario a la página de inicio
        this.router.navigate(['/home']);
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
