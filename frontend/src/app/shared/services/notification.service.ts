import { Injectable, signal } from '@angular/core';

export type NotificationType = 'success' | 'error' | 'info' | 'warning';

export interface Notification {
  id: string;
  message: string;
  type: NotificationType;
  duration?: number; // Duration in milliseconds, undefined means it won't auto-dismiss
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = signal<Notification[]>([]);
  private defaultDuration = 5000; // 5 seconds

  getNotifications() {
    return this.notifications.asReadonly();
  }

  show(message: string, type: NotificationType = 'info', duration?: number): void {
    const notification: Notification = {
      id: this.generateId(),
      message,
      type,
      duration: duration !== undefined ? duration : this.defaultDuration
    };

    this.notifications.update(notifications => [...notifications, notification]);

    // Auto-dismiss if duration is set
    if (notification.duration && notification.duration > 0) {
      setTimeout(() => {
        this.dismiss(notification.id);
      }, notification.duration);
    }
  }

  success(message: string, duration?: number): void {
    this.show(message, 'success', duration);
  }

  error(message: string, duration?: number): void {
    this.show(message, 'error', duration);
  }

  info(message: string, duration?: number): void {
    this.show(message, 'info', duration);
  }

  warning(message: string, duration?: number): void {
    this.show(message, 'warning', duration);
  }

  dismiss(id: string): void {
    this.notifications.update(notifications => 
      notifications.filter(n => n.id !== id)
    );
  }

  clear(): void {
    this.notifications.set([]);
  }

  private generateId(): string {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }
}

