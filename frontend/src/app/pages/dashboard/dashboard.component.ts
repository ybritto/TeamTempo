import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { TeamService } from '../../../api/api/team.service';
import { TeamDto } from '../../../api/model/teamDto';
import { ProjectDto } from '../../../api/model/projectDto';

interface Team {
  id: string;
  name: string;
  capacityProjected: number;
  capacityPerformed: number;
  storyPointsProjected: number;
  storyPointsDelivered: number;
  projects: ProjectDto[];
}

interface Project {
  id: string;
  name: string;
  capacityProjected: number;
  capacityPerformed: number;
  storyPointsProjected: number;
  storyPointsDelivered: number;
}

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink, HeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  private teamService = inject(TeamService);

  navLinks = [
    { label: 'Teams', route: '/teams' },
    { label: 'Home', route: '/' }
  ];

  teams = signal<Team[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  selectedTeamId = signal<string>('');
  selectedProjectId = signal<string>('');

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
          const firstTeam = teams[0];
          this.selectedTeamId.set(firstTeam.id);
          
          // Set the first project as selected if available
          if (firstTeam.projects.length > 0) {
            this.selectedProjectId.set(firstTeam.projects[0].uuid || '');
          }
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
      storyPointsDelivered: 0,
      projects: dto.projects || []
    };
  }

  selectedTeam = computed(() => {
    const teams = this.teams();
    if (teams.length === 0) {
      return null;
    }
    return teams.find(team => team.id === this.selectedTeamId()) || teams[0];
  });

  availableProjects = computed(() => {
    const team = this.selectedTeam();
    return team?.projects || [];
  });

  selectedProject = computed(() => {
    const projects = this.availableProjects();
    const selectedId = this.selectedProjectId();
    if (!selectedId || projects.length === 0) {
      return null;
    }
    const projectDto = projects.find(p => p.uuid === selectedId);
    if (!projectDto) return null;
    
    // Map ProjectDto to Project interface
    // Note: Project metrics are not available in the API yet, using defaults
    return {
      id: projectDto.uuid || '',
      name: projectDto.name || 'Unnamed Project',
      capacityProjected: 0,
      capacityPerformed: 0,
      storyPointsProjected: 0,
      storyPointsDelivered: 0
    };
  });

  capacityProjected = computed(() => this.selectedProject()?.capacityProjected ?? 0);
  capacityPerformed = computed(() => this.selectedProject()?.capacityPerformed ?? 0);
  storyPointsProjected = computed(() => this.selectedProject()?.storyPointsProjected ?? 0);
  storyPointsDelivered = computed(() => this.selectedProject()?.storyPointsDelivered ?? 0);

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
    const teamId = selectElement.value;
    this.selectedTeamId.set(teamId);
    
    // Reset project selection when team changes
    // Access projects directly from teams array to avoid timing issues with computed
    const team = this.teams().find(t => t.id === teamId);
    if (team && team.projects.length > 0) {
      this.selectedProjectId.set(team.projects[0].uuid || '');
    } else {
      this.selectedProjectId.set('');
    }
  }

  onProjectChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedProjectId.set(selectElement.value);
  }
}

