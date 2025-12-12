import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { TeamService } from '../../../api/api/team.service';
import { TeamDto } from '../../../api/model/teamDto';
import { ProjectDto } from '../../../api/model/projectDto';
import { IterationDto } from '../../../api/model/iterationDto';

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
  selectedIterationId = signal<string>('');
  
  projectDetails = signal<ProjectDto | null>(null);
  loadingProjectDetails = signal<boolean>(false);

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
            const firstProjectUuid = firstTeam.projects[0].uuid || '';
            this.selectedProjectId.set(firstProjectUuid);
            this.loadProjectDetails(firstProjectUuid);
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
    return projects.find(p => p.uuid === selectedId) || null;
  });

  availableIterations = computed(() => {
    const projectDetails = this.projectDetails();
    return projectDetails?.iterations || [];
  });

  selectedIteration = computed(() => {
    const iterations = this.availableIterations();
    const selectedId = this.selectedIterationId();
    if (!selectedId || iterations.length === 0) {
      return iterations.length > 0 ? iterations[0] : null;
    }
    return iterations.find(i => i.uuid === selectedId) || iterations[0] || null;
  });

  capacityProjected = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.plannedCapacity ?? 0;
  });

  capacityPerformed = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.actualCapacity ?? 0;
  });

  storyPointsProjected = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.plannedForecast ?? 0;
  });

  storyPointsDelivered = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.actualForecast ?? 0;
  });

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
      const firstProjectUuid = team.projects[0].uuid || '';
      this.selectedProjectId.set(firstProjectUuid);
      this.loadProjectDetails(firstProjectUuid);
    } else {
      this.selectedProjectId.set('');
      this.projectDetails.set(null);
      this.selectedIterationId.set('');
    }
  }

  onProjectChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const projectId = selectElement.value;
    this.selectedProjectId.set(projectId);
    this.selectedIterationId.set('');
    this.loadProjectDetails(projectId);
  }

  onIterationChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedIterationId.set(selectElement.value);
  }

  loadProjectDetails(projectUuid: string): void {
    if (!projectUuid) {
      this.projectDetails.set(null);
      this.selectedIterationId.set('');
      return;
    }

    this.loadingProjectDetails.set(true);
    
    // Get iterations from the project in the teams list
    const project = this.selectedProject();
    if (project) {
      this.projectDetails.set(project);
      this.loadingProjectDetails.set(false);
      
      // Set first iteration as selected if available and none is selected
      if (project.iterations && project.iterations.length > 0) {
        if (!this.selectedIterationId()) {
          this.selectedIterationId.set(project.iterations[0].uuid || '');
        }
      } else {
        this.selectedIterationId.set('');
      }
    } else {
      this.projectDetails.set(null);
      this.selectedIterationId.set('');
      this.loadingProjectDetails.set(false);
    }
  }

  formatIterationName(iteration: IterationDto): string {
    if (iteration.name) {
      return iteration.name;
    }
    if (iteration.plannedStartDate) {
      const startDate = new Date(iteration.plannedStartDate);
      return `Sprint ${startDate.toLocaleDateString()}`;
    }
    return 'Unnamed Sprint';
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }

  plannedStartDate = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.plannedStartDate;
  });

  plannedEndDate = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.plannedEndDate;
  });

  actualStartDate = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.actualStartDate;
  });

  actualEndDate = computed(() => {
    const iteration = this.selectedIteration();
    return iteration?.actualEndDate;
  });
}

