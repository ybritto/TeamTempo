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
  editingTeamUuid = signal<string | null>(null);
  updating = signal<boolean>(false);
  updateError = signal<string | null>(null);

  createTeamForm: FormGroup;
  editTeamForm: FormGroup;

  constructor() {
    this.createTeamForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(200)]],
      description: ['', [Validators.maxLength(1000)]],
      startDate: ['', [Validators.required]],
      endDate: ['']
    });
    this.editTeamForm = this.fb.group({
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

  startEdit(team: TeamDto): void {
    this.editingTeamUuid.set(team.uuid || null);
    this.updateError.set(null);
    
    // Format dates for input fields (YYYY-MM-DD)
    const formatDateForInput = (dateString: string | undefined): string => {
      if (!dateString) return '';
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    this.editTeamForm.patchValue({
      name: team.name || '',
      description: team.description || '',
      startDate: formatDateForInput(team.startDate),
      endDate: formatDateForInput(team.endDate)
    });
  }

  cancelEdit(): void {
    this.editingTeamUuid.set(null);
    this.editTeamForm.reset();
    this.updateError.set(null);
  }

  onUpdate(): void {
    if (this.editTeamForm.valid && this.editingTeamUuid()) {
      this.updating.set(true);
      this.updateError.set(null);

      const teamUuid = this.editingTeamUuid()!;
      const teamData: TeamDto = {
        uuid: teamUuid,
        name: this.editTeamForm.value.name,
        description: this.editTeamForm.value.description || undefined,
        startDate: this.editTeamForm.value.startDate,
        endDate: this.editTeamForm.value.endDate || undefined
      };

      this.teamsService.updateTeam(teamUuid, teamData).subscribe({
        next: (updatedTeam: TeamDto) => {
          // Reload teams list
          this.loadTeams();
          // Reset form and hide it
          this.editTeamForm.reset();
          this.editingTeamUuid.set(null);
          this.updating.set(false);
        },
        error: (err) => {
          console.error('Error updating team:', err);
          this.updateError.set(
            err?.error?.detail || 
            err?.error?.message || 
            'Failed to update team. Please try again.'
          );
          this.updating.set(false);
        }
      });
    } else {
      this.editTeamForm.markAllAsTouched();
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}

