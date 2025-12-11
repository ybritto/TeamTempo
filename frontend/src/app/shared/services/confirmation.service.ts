import { Injectable, inject } from '@angular/core';
import { ConfirmationService, Confirmation } from 'primeng/api';
import { Observable } from 'rxjs';

export interface ConfirmationOptions {
  message?: string;
  header?: string;
  icon?: string;
  acceptLabel?: string;
  rejectLabel?: string;
  acceptButtonStyleClass?: string;
  rejectButtonStyleClass?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmationDialogService {
  private confirmationService = inject(ConfirmationService);

  /**
   * Show a confirmation dialog and return an Observable that emits true if accepted, false if rejected
   */
  confirm(options: ConfirmationOptions): Observable<boolean> {
    return new Observable<boolean>(observer => {
      const confirmation: Confirmation = {
        message: options.message || 'Are you sure?',
        header: options.header || 'Confirmation',
        icon: options.icon || 'pi pi-exclamation-triangle',
        acceptLabel: options.acceptLabel || 'Yes',
        rejectLabel: options.rejectLabel || 'No',
        acceptButtonStyleClass: options.acceptButtonStyleClass || 'p-button-danger',
        rejectButtonStyleClass: options.rejectButtonStyleClass || 'p-button-secondary',
        accept: () => {
          observer.next(true);
          observer.complete();
        },
        reject: () => {
          observer.next(false);
          observer.complete();
        }
      };

      this.confirmationService.confirm(confirmation);
    });
  }

  /**
   * Show a delete confirmation dialog
   */
  confirmDelete(itemName: string, itemType: string = 'item', suggestInactivation: boolean = false): Observable<boolean> {
    let message: string;
    
    if (suggestInactivation) {
      // Compact HTML message with styled suggestion
      message = `Delete "<strong>${itemName}</strong>"?<br/><span style="font-size: 0.875rem; color: #6c757d; display: block; margin-top: 0.5rem;">💡 Consider inactivation instead</span>`;
    } else {
      message = `Delete "<strong>${itemName}</strong>"?<br/><span style="font-size: 0.875rem; color: #6c757d; display: block; margin-top: 0.5rem;">This action cannot be undone.</span>`;
    }

    return this.confirm({
      message: message,
      header: 'Delete Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary'
    });
  }

  /**
   * Show a bulk delete confirmation dialog
   */
  confirmBulkDelete(count: number, itemType: string = 'items'): Observable<boolean> {
    return this.confirm({
      message: `Are you sure you want to delete ${count} ${itemType}${count > 1 ? '' : ''}? This action cannot be undone.`,
      header: 'Delete Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary'
    });
  }
}
