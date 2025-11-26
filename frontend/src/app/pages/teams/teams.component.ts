import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HeaderComponent} from '../../shared/header/header.component';
import {TeamService, TeamsService, ProjectsService, TeamDto, ProjectDto} from '../../../api';

@Component({
  selector: 'app-teams',
  imports: [CommonModule, ReactiveFormsModule, HeaderComponent],
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.scss'
})
export class TeamsComponent {
  private teamService = inject(TeamService);
  private teamsService = inject(TeamsService);
  private projectsService = inject(ProjectsService);
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
  deletingTeamUuid = signal<string | null>(null);
  deleteError = signal<string | null>(null);
  selectionMode = signal<boolean>(false);
  selectedTeamUuids = signal<Set<string>>(new Set());
  bulkDeleting = signal<boolean>(false);
  bulkDeleteError = signal<string | null>(null);
  
  // Projects management
  expandedTeamUuid = signal<string | null>(null);
  teamProjects = signal<Map<string, ProjectDto[]>>(new Map());
  loadingProjects = signal<Set<string>>(new Set());
  projectsError = signal<Map<string, string>>(new Map());
  editingProjectUuid = signal<string | null>(null);
  updatingProject = signal<boolean>(false);
  updateProjectError = signal<string | null>(null);
  editProjectForm: FormGroup;

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
    this.editProjectForm = this.fb.group({
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

  deleteTeam(team: TeamDto): void {
    if (!team.uuid) {
      console.error('Cannot delete team: UUID is missing');
      return;
    }

    // Clear any previous errors
    this.deleteError.set(null);

    const teamName = team.name || 'this team';
    const confirmed = confirm(`Are you sure you want to delete "${teamName}"? This action cannot be undone.`);

    if (!confirmed) {
      return;
    }

    this.deletingTeamUuid.set(team.uuid);

    this.teamsService.deleteTeam(team.uuid).subscribe({
      next: () => {
        // Reload teams list
        this.loadTeams();
        this.deletingTeamUuid.set(null);
        this.deleteError.set(null);
      },
      error: (err) => {
        console.error('Error deleting team:', err);
        this.deleteError.set(
          err?.error?.detail ||
          err?.error?.message ||
          'Failed to delete team. Please try again.'
        );
        this.deletingTeamUuid.set(null);
      }
    });
  }

  toggleSelectionMode(): void {
    const newMode = !this.selectionMode();
    this.selectionMode.set(newMode);
    if (!newMode) {
      // Clear selection when exiting selection mode
      this.selectedTeamUuids.set(new Set());
      this.bulkDeleteError.set(null);
    } else {
      // Hide create form when entering selection mode
      if (this.showCreateForm()) {
        this.toggleCreateForm();
      }
      // Cancel any active edit when entering selection mode
      if (this.editingTeamUuid()) {
        this.cancelEdit();
      }
    }
  }

  toggleTeamSelection(teamUuid: string | undefined): void {
    if (!teamUuid) return;

    const selected = new Set(this.selectedTeamUuids());
    if (selected.has(teamUuid)) {
      selected.delete(teamUuid);
    } else {
      selected.add(teamUuid);
    }
    this.selectedTeamUuids.set(selected);
  }

  isTeamSelected(teamUuid: string | undefined): boolean {
    if (!teamUuid) return false;
    return this.selectedTeamUuids().has(teamUuid);
  }

  toggleSelectAll(): void {
    const currentSelected = this.selectedTeamUuids();
    const allUuids = this.teams().map(t => t.uuid).filter((uuid): uuid is string => !!uuid);

    if (currentSelected.size === allUuids.length) {
      // Deselect all
      this.selectedTeamUuids.set(new Set());
    } else {
      // Select all
      this.selectedTeamUuids.set(new Set(allUuids));
    }
  }

  isAllSelected(): boolean {
    const allUuids = this.teams().map(t => t.uuid).filter((uuid): uuid is string => !!uuid);
    return allUuids.length > 0 && this.selectedTeamUuids().size === allUuids.length;
  }

  isSomeSelected(): boolean {
    return this.selectedTeamUuids().size > 0;
  }

  getSelectedCount(): number {
    return this.selectedTeamUuids().size;
  }

  deleteSelectedTeams(): void {
    const selectedUuids = Array.from(this.selectedTeamUuids());

    if (selectedUuids.length === 0) {
      return;
    }

    const count = selectedUuids.length;
    const confirmed = confirm(`Are you sure you want to delete ${count} team${count > 1 ? 's' : ''}? This action cannot be undone.`);

    if (!confirmed) {
      return;
    }

    this.bulkDeleting.set(true);
    this.bulkDeleteError.set(null);

    this.teamsService.deleteSelectedTeams(selectedUuids).subscribe({
      next: () => {
        // Reload teams list
        this.loadTeams();
        // Clear selection and exit selection mode
        this.selectedTeamUuids.set(new Set());
        this.selectionMode.set(false);
        this.bulkDeleting.set(false);
        this.bulkDeleteError.set(null);
      },
      error: (err) => {
        console.error('Error deleting teams:', err);
        this.bulkDeleteError.set(
          err?.error?.detail ||
          err?.error?.message ||
          'Failed to delete teams. Please try again.'
        );
        this.bulkDeleting.set(false);
      }
    });
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  toggleProjects(team: TeamDto): void {
    if (!team.uuid) return;

    const currentExpanded = this.expandedTeamUuid();
    
    // If clicking the same team, collapse it
    if (currentExpanded === team.uuid) {
      this.expandedTeamUuid.set(null);
      return;
    }

    // Expand the clicked team
    this.expandedTeamUuid.set(team.uuid);

    // Load projects if not already loaded
    const projects = this.teamProjects().get(team.uuid);
    if (!projects && !this.loadingProjects().has(team.uuid || '')) {
      this.loadProjects(team.uuid);
    }
  }

  isProjectsExpanded(teamUuid: string | undefined): boolean {
    if (!teamUuid) return false;
    return this.expandedTeamUuid() === teamUuid;
  }

  loadProjects(teamUuid: string): void {
    if (this.loadingProjects().has(teamUuid)) return;

    this.loadingProjects.update(loading => {
      const newSet = new Set(loading);
      newSet.add(teamUuid);
      return newSet;
    });

    // Clear any previous error for this team
    this.projectsError.update(errors => {
      const newMap = new Map(errors);
      newMap.delete(teamUuid);
      return newMap;
    });

    this.teamsService.getTeamProjects(teamUuid).subscribe({
      next: (projects: ProjectDto[]) => {
        this.teamProjects.update(projectsMap => {
          const newMap = new Map(projectsMap);
          newMap.set(teamUuid, projects);
          return newMap;
        });
        this.loadingProjects.update(loading => {
          const newSet = new Set(loading);
          newSet.delete(teamUuid);
          return newSet;
        });
      },
      error: (err) => {
        console.error('Error loading projects:', err);
        this.projectsError.update(errors => {
          const newMap = new Map(errors);
          newMap.set(teamUuid, err?.error?.detail || err?.error?.message || 'Failed to load projects. Please try again.');
          return newMap;
        });
        this.loadingProjects.update(loading => {
          const newSet = new Set(loading);
          newSet.delete(teamUuid);
          return newSet;
        });
      }
    });
  }

  getTeamProjects(teamUuid: string | undefined): ProjectDto[] {
    if (!teamUuid) return [];
    return this.teamProjects().get(teamUuid) || [];
  }

  isLoadingProjects(teamUuid: string | undefined): boolean {
    if (!teamUuid) return false;
    return this.loadingProjects().has(teamUuid);
  }

  getProjectsError(teamUuid: string | undefined): string | null {
    if (!teamUuid) return null;
    return this.projectsError().get(teamUuid) || null;
  }

  startEditProject(project: ProjectDto, teamUuid: string): void {
    if (!project.uuid) return;
    
    this.editingProjectUuid.set(project.uuid);
    this.updateProjectError.set(null);

    // Format dates for input fields (YYYY-MM-DD)
    const formatDateForInput = (dateString: string | undefined): string => {
      if (!dateString) return '';
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    this.editProjectForm.patchValue({
      name: project.name || '',
      description: project.description || '',
      startDate: formatDateForInput(project.startDate),
      endDate: formatDateForInput(project.endDate)
    });
  }

  cancelEditProject(): void {
    this.editingProjectUuid.set(null);
    this.editProjectForm.reset();
    this.updateProjectError.set(null);
  }

  onUpdateProject(teamUuid: string): void {
    if (this.editProjectForm.valid && this.editingProjectUuid()) {
      this.updatingProject.set(true);
      this.updateProjectError.set(null);

      const projectUuid = this.editingProjectUuid()!;
      
      // Get the current project to preserve team reference
      const currentProjects = this.teamProjects().get(teamUuid) || [];
      const currentProject = currentProjects.find(p => p.uuid === projectUuid);
      
      if (!currentProject) {
        this.updateProjectError.set('Project not found');
        this.updatingProject.set(false);
        return;
      }

      const projectData: ProjectDto = {
        uuid: projectUuid,
        name: this.editProjectForm.value.name,
        description: this.editProjectForm.value.description || undefined,
        startDate: this.editProjectForm.value.startDate,
        endDate: this.editProjectForm.value.endDate || undefined,
        team: currentProject.team // Preserve team reference
      };

      this.projectsService.updateProject(projectUuid, projectData).subscribe({
        next: (updatedProject: ProjectDto) => {
          // Update the project in the cached projects list
          this.teamProjects.update(projectsMap => {
            const newMap = new Map(projectsMap);
            const projects = newMap.get(teamUuid) || [];
            const updatedProjects = projects.map(p => 
              p.uuid === projectUuid ? updatedProject : p
            );
            newMap.set(teamUuid, updatedProjects);
            return newMap;
          });
          
          // Reset form and hide it
          this.editProjectForm.reset();
          this.editingProjectUuid.set(null);
          this.updatingProject.set(false);
        },
        error: (err) => {
          console.error('Error updating project:', err);
          this.updateProjectError.set(
            err?.error?.detail ||
            err?.error?.message ||
            'Failed to update project. Please try again.'
          );
          this.updatingProject.set(false);
        }
      });
    } else {
      this.editProjectForm.markAllAsTouched();
    }
  }

  isEditingProject(projectUuid: string | undefined): boolean {
    if (!projectUuid) return false;
    return this.editingProjectUuid() === projectUuid;
  }
}

