import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { TeamService } from '../../../api/api/team.service';
import { TeamsService } from '../../../api/api/teams.service';
import { TeamDto } from '../../../api/model/teamDto';

@Component({
  selector: 'app-teams',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, HeaderComponent],
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.scss'
})
export class TeamsComponent {
  private teamService = inject(TeamService);
  private teamsService = inject(TeamsService);
  private fb = inject(FormBuilder);

  navLinks = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Home', route: '/' }
  ];

  teams = signal<TeamDto[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);
  showCreateForm = signal<boolean>(false);
  creating = signal<boolean>(false);
  createError = signal<string | null>(null);

  createTeamForm: FormGroup;

  constructor() {
    this.createTeamForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(200)]],
      description: ['', [Validators.maxLength(1000)]],
      startDate: ['', [Validators.required]],
      endDate: ['']
    });
    this.loadTeams();
  }

  loadTeams(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.teamService.myTeams().subscribe({
      next: (teamDtos: TeamDto[]) => {
        this.teams.set(teamDtos);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading teams:', err);
        this.error.set('Failed to load teams. Please try again later.');
        this.loading.set(false);
      }
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm.set(!this.showCreateForm());
    if (!this.showCreateForm()) {
      this.createTeamForm.reset();
      this.createError.set(null);
    }
  }

  onSubmit(): void {
    if (this.createTeamForm.valid) {
      this.creating.set(true);
      this.createError.set(null);

      const teamData: TeamDto = {
        name: this.createTeamForm.value.name,
        description: this.createTeamForm.value.description || undefined,
        startDate: this.createTeamForm.value.startDate,
        endDate: this.createTeamForm.value.endDate || undefined
      };

      this.teamsService.createTeam(teamData).subscribe({
        next: (createdTeam: TeamDto) => {
          // Reload teams list
          this.loadTeams();
          // Reset form and hide it
          this.createTeamForm.reset();
          this.showCreateForm.set(false);
          this.creating.set(false);
        },
        error: (err) => {
          console.error('Error creating team:', err);
          this.createError.set(
            err?.error?.detail || 
            err?.error?.message || 
            'Failed to create team. Please try again.'
          );
          this.creating.set(false);
        }
      });
    } else {
      this.createTeamForm.markAllAsTouched();
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}

