import { Component, computed, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../shared/header/header.component';
import { AuthService } from '../../shared/services/auth.service';

@Component({
  selector: 'app-landing',
  imports: [RouterLink, CommonModule, HeaderComponent],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  navLinks = computed(() => {
    if (this.authService.isAuthenticated()) {
      return [
        { label: 'Dashboard', route: '/dashboard' }
      ];
    }
    return [];
  });

  ngOnInit(): void {
    // Redirect authenticated users to dashboard
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }
}

