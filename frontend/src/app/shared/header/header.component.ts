import { Component, input, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

export interface NavLink {
  label: string;
  route: string;
}

@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);

  title = input<string>();
  logoWithTagline = input<boolean>(false);
  navLinks = input<NavLink[]>([]);
  showLogoLink = input<boolean>(true);

  isAuthenticated = this.authService.isAuthenticated;

  onLogout(): void {
    this.authService.logout();
  }
}

