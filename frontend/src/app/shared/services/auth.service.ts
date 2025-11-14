import { Injectable, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../../api';
import { LoginUserDto, RegisterUserDto, LoginResponseDto } from '../../../api';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenKey = 'auth_token';
  private readonly userKey = 'auth_user';
  private readonly authService = inject(AuthenticationService);
  private readonly router = inject(Router);

  isAuthenticated = signal<boolean>(this.hasToken());
  currentUser = signal<LoginResponseDto | null>(this.getStoredUser());

  login(credentials: LoginUserDto) {
    return this.authService.login(credentials).pipe(
      tap((response) => {
        this.setAuthData(response);
      })
    );
  }

  signup(userData: RegisterUserDto) {
    return this.authService.signup(userData);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.clearAuthData();
        this.router.navigate(['/']);
      },
      error: () => {
        // Even if logout fails, clear local data
        this.clearAuthData();
        this.router.navigate(['/']);
      }
    });
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  private setAuthData(response: LoginResponseDto): void {
    if (response.token) {
      localStorage.setItem(this.tokenKey, response.token);
      localStorage.setItem(this.userKey, JSON.stringify(response));
      this.isAuthenticated.set(true);
      this.currentUser.set(response);
    }
  }

  private clearAuthData(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.isAuthenticated.set(false);
    this.currentUser.set(null);
  }

  private getStoredUser(): LoginResponseDto | null {
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }
}

