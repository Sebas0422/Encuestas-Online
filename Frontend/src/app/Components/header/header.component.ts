import { Component, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../Services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterModule, CommonModule],
  standalone: true,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  userName: string = '';
  userEmail: string = '';
  userInitials: string = '';
  showUserMenu: boolean = false;
  showNotifications: boolean = false;
  notificationCount: number = 0;

  constructor(
    public authService: AuthService,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    // Obtener información del usuario desde localStorage
    this.userName = localStorage.getItem('user_name') || 'Usuario';
    this.userEmail = localStorage.getItem('user_email') || 'usuario@example.com';

    // Generar iniciales del nombre
    const names = this.userName.split(' ');
    this.userInitials = names.length >= 2
      ? names[0][0] + names[1][0]
      : names[0][0] + (names[0][1] || '');
    this.userInitials = this.userInitials.toUpperCase();
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
    if (this.showUserMenu) {
      this.showNotifications = false;
    }
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.showUserMenu = false;
    }
  }

  closeMenus(): void {
    this.showUserMenu = false;
    this.showNotifications = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navigateToProfile(): void {
    this.closeMenus();
    // Aquí puedes navegar al perfil cuando lo implementes
    console.log('Navegar a perfil');
  }

  navigateToSettings(): void {
    this.closeMenus();
    // Aquí puedes navegar a configuración cuando lo implementes
    console.log('Navegar a configuración');
  }
}
