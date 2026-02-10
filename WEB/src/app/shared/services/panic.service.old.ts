// import { effect, inject, Injectable, signal } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import Swal from 'sweetalert2';
// import { environment } from '@environments/environment';
// import { Observable } from 'rxjs';
// import { WebSocketService } from '@core/services/web-socket.service';
// import { AuthService } from '@core/services/auth.service';
// import { LoggedInUserRole } from '@core/models/loggedInUser.model';
//
// export interface PanicAlert {
// 	rideId: number;
// 	driverName: string;
// 	driverLocation: string;
// 	passengerName?: string;
// 	timestamp?: string;
// }
//
// @Injectable({
// 	providedIn: 'root',
// })
// export class PanicService {
// 	private http = inject(HttpClient);
// 	private webSocketService = inject(WebSocketService);
// 	private authService = inject(AuthService);
//
// 	private unsubscribeFn: (() => void) | null = null;
//
// 	// Signal to track latest panic alert
// 	private _latestPanicAlert = signal<PanicAlert | null>(null);
// 	readonly latestPanicAlert = this._latestPanicAlert.asReadonly();
//
// 	// Signal to track all panic alerts
// 	private _panicAlerts = signal<PanicAlert[]>([]);
// 	readonly panicAlerts = this._panicAlerts.asReadonly();
//
// 	constructor() {
// 		this.setupPanicSubscription();
// 	}
//
// 	/**
// 	 * Setup effect to subscribe/unsubscribe to panic alerts based on user role
// 	 */
// 	private setupPanicSubscription(): void {
// 		effect(() => {
// 			const user = this.authService.user();
//
// 			// Only admins should receive panic alerts
// 			if (user?.role === LoggedInUserRole.ADMIN) {
// 				this.subscribeToPanic().then();
// 			} else {
// 				this.unsubscribeFromPanic();
// 			}
// 		});
// 	}
//
// 	/**
// 	 * Subscribe to panic alerts
// 	 */
// 	private async subscribeToPanic(): Promise<void> {
// 		// If already subscribed, don't subscribe again
// 		if (this.unsubscribeFn) {
// 			return;
// 		}
//
// 		console.log('[PanicService] Subscribing to panic alerts');
//
// 		try {
// 			this.unsubscribeFn = await this.webSocketService.subscribe<PanicAlert>(
// 				'/topic/panic',
// 				(data) => this.handlePanic(data),
// 			);
// 		} catch (error) {
// 			console.error('[PanicService] Error subscribing to panic alerts:', error);
// 		}
// 	}
//
// 	/**
// 	 * Unsubscribe from panic alerts
// 	 */
// 	private unsubscribeFromPanic(): void {
// 		if (this.unsubscribeFn) {
// 			console.log('[PanicService] Unsubscribing from panic alerts');
// 			this.unsubscribeFn();
// 			this.unsubscribeFn = null;
// 		}
// 	}
//
// 	/**
// 	 * Handle incoming panic alert
// 	 */
// 	public handlePanic(data: PanicAlert | null): void {
// 		console.log('[PanicService] Panic alert received:', data);
//
// 		// Update signals
// 		this._latestPanicAlert.set(data);
//
// 		if (data) {
// 			this._panicAlerts.update((alerts) => [data, ...alerts]);
// 		}
//
// 		// Show alert to admin
// 		this.showPanicAlert(data);
// 	}
//
// 	/**
// 	 * Show panic alert UI
// 	 */
// 	private showPanicAlert(data: PanicAlert | null): void {
// 		let text = '';
//
// 		if (!data) {
// 			text = 'Panic alert received with no additional data.';
// 		} else if (data.passengerName) {
// 			text = `Panic alert received from ride ID: ${data.rideId}
// Driver: ${data.driverName}
// Triggered by: ${data.passengerName}
// Location: ${data.driverLocation}`;
// 		} else {
// 			text = `Panic alert received from ride ID: ${data.rideId}
// Driver: ${data.driverName}
// Location: ${data.driverLocation}`;
// 		}
//
// 		Swal.fire({
// 			title: '🚨 Panic Alert',
// 			text: text,
// 			icon: 'warning',
// 			confirmButtonText: 'Acknowledge',
// 			showCancelButton: true,
// 			cancelButtonText: 'View Details',
// 			customClass: {
// 				popup: 'panic-alert-popup',
// 			},
// 		}).then((result) => {
// 			if (result.isDismissed && result.dismiss === Swal.DismissReason.cancel) {
// 				// Navigate to panic details or ride details
// 				console.log('[PanicService] View panic details for ride:', data?.rideId);
// 			}
// 		});
// 	}
//
// 	/**
// 	 * Trigger panic for current ride (HTTP request)
// 	 */
// 	triggerPanic(token: string | null): Observable<never> {
// 		const endpoint = token
// 			? `/api/${environment.apiVersion}/rides/panic?accessToken=${token}`
// 			: `/api/${environment.apiVersion}/rides/panic`;
//
// 		return this.http.post<never>(endpoint, {});
// 	}
//
// 	/**
// 	 * Clear panic alerts history
// 	 */
// 	clearPanicAlerts(): void {
// 		this._panicAlerts.set([]);
// 		this._latestPanicAlert.set(null);
// 	}
//
// 	/**
// 	 * Get panic alert by ride ID
// 	 */
// 	getPanicAlertByRideId(rideId: number): PanicAlert | undefined {
// 		return this._panicAlerts().find((alert) => alert.rideId === rideId);
// 	}
//
// 	/**
// 	 * Manual cleanup (called on logout)
// 	 */
// 	cleanup(): void {
// 		this.unsubscribeFromPanic();
// 		this.clearPanicAlerts();
// 	}
// }
