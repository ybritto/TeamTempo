import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

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
  title = input<string>();
  logoWithTagline = input<boolean>(false);
  navLinks = input<NavLink[]>([]);
  showLogoLink = input<boolean>(true);
}

