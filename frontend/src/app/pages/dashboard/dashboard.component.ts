import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../shared/header/header.component';
import { TeamService } from '../../../api/api/team.service';
import { TeamDto } from '../../../api/model/teamDto';

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
  private teamService = inject(TeamService);

  navLinks = [
    { label: 'Home', route: '/' }
  ];

  teams = signal<Team[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  selectedTeamId = signal<string>('');

  constructor() {
    this.loadTeams();
  }

  loadTeams(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.teamService.myTeams().subscribe({
      next: (teamDtos: TeamDto[]) => {
        const teams = teamDtos.map(dto => this.mapTeamDtoToTeam(dto));
        this.teams.set(teams);
        
        // Set the first team as selected if available
        if (teams.length > 0 && !this.selectedTeamId()) {
          this.selectedTeamId.set(teams[0].id);
        }
        
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading teams:', err);
        this.error.set('Failed to load teams. Please try again later.');
        this.loading.set(false);
      }
    });
  }

  private mapTeamDtoToTeam(dto: TeamDto): Team {
    return {
      id: dto.uuid || '',
      name: dto.name || 'Unnamed Team',
      // These metrics are not available in the API yet, setting defaults
      capacityProjected: 0,
      capacityPerformed: 0,
      storyPointsProjected: 0,
      storyPointsDelivered: 0
    };
  }

  selectedTeam = computed(() => {
    const teams = this.teams();
    if (teams.length === 0) {
      return null;
    }
    return teams.find(team => team.id === this.selectedTeamId()) || teams[0];
  });

  capacityProjected = computed(() => this.selectedTeam()?.capacityProjected ?? 0);
  capacityPerformed = computed(() => this.selectedTeam()?.capacityPerformed ?? 0);
  storyPointsProjected = computed(() => this.selectedTeam()?.storyPointsProjected ?? 0);
  storyPointsDelivered = computed(() => this.selectedTeam()?.storyPointsDelivered ?? 0);

  capacityPercentage = computed(() => {
    const projected = this.capacityProjected();
    if (projected === 0) return 0;
    return this.capacityPerformed() / projected * 100;
  });

  storyPointsPercentage = computed(() => {
    const projected = this.storyPointsProjected();
    if (projected === 0) return 0;
    return this.storyPointsDelivered() / projected * 100;
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

