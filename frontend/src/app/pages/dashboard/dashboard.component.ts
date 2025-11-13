import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../shared/header/header.component';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, HeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  navLinks = [
    { label: 'Home', route: '/' }
  ];
  // Sample data - in a real app, this would come from a service
  capacityProjected = signal(120);
  capacityPerformed = signal(105);
  
  storyPointsProjected = signal(85);
  storyPointsDelivered = signal(78);

  get capacityPercentage(): number {
    return this.capacityPerformed() / this.capacityProjected() * 100;
  }

  get storyPointsPercentage(): number {
    return this.storyPointsDelivered() / this.storyPointsProjected() * 100;
  }

  get capacityDifference(): number {
    return this.capacityPerformed() - this.capacityProjected();
  }

  get storyPointsDifference(): number {
    return this.storyPointsDelivered() - this.storyPointsProjected();
  }
}

