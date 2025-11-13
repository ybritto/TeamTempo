import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../shared/header/header.component';

interface Team {
  id: string;
  name: string;
  capacityProjected: number;
  capacityPerformed: number;
  storyPointsProjected: number;
  storyPointsDelivered: number;
}

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

  // Sample teams data - in a real app, this would come from a service
  teams = signal<Team[]>([
    {
      id: '1',
      name: 'Frontend Team',
      capacityProjected: 120,
      capacityPerformed: 105,
      storyPointsProjected: 85,
      storyPointsDelivered: 78
    },
    {
      id: '2',
      name: 'Backend Team',
      capacityProjected: 150,
      capacityPerformed: 145,
      storyPointsProjected: 110,
      storyPointsDelivered: 108
    },
    {
      id: '3',
      name: 'DevOps Team',
      capacityProjected: 80,
      capacityPerformed: 85,
      storyPointsProjected: 60,
      storyPointsDelivered: 65
    },
    {
      id: '4',
      name: 'QA Team',
      capacityProjected: 100,
      capacityPerformed: 95,
      storyPointsProjected: 70,
      storyPointsDelivered: 68
    }
  ]);

  selectedTeamId = signal<string>(this.teams()[0].id);

  selectedTeam = computed(() => {
    return this.teams().find(team => team.id === this.selectedTeamId()) || this.teams()[0];
  });

  capacityProjected = computed(() => this.selectedTeam().capacityProjected);
  capacityPerformed = computed(() => this.selectedTeam().capacityPerformed);
  storyPointsProjected = computed(() => this.selectedTeam().storyPointsProjected);
  storyPointsDelivered = computed(() => this.selectedTeam().storyPointsDelivered);

  capacityPercentage = computed(() => {
    return this.capacityPerformed() / this.capacityProjected() * 100;
  });

  storyPointsPercentage = computed(() => {
    return this.storyPointsDelivered() / this.storyPointsProjected() * 100;
  });

  capacityDifference = computed(() => {
    return this.capacityPerformed() - this.capacityProjected();
  });

  storyPointsDifference = computed(() => {
    return this.storyPointsDelivered() - this.storyPointsProjected();
  });

  onTeamChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedTeamId.set(selectElement.value);
  }
}

