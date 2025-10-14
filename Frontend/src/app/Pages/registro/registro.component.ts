import { Component } from '@angular/core';
import { Router } from '@angular/router';
 import { AuthService } from '../../Services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.css'
})

export class RegisterComponent {
  errorMessage: string = '';

  fullName: string = '';
  email: string = '';
  password: string = '';

  constructor(
     private authService: AuthService,
    private router: Router) {
  }


  onRegister(formData: any) {
    this.errorMessage = '';
    console.log('Datos de registro:', formData);

    /* Aquí llamarías a tu servicio de registro*/
    this.authService.register(formData).subscribe({
      next: (response) => {
        console.log('Registro exitoso:', response);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Error en el registro:', error);
        this.errorMessage = 'No se pudo completar el registro. Inténtalo de nuevo.';
      }
    });

  }
}
