import { effect, inject, Injectable, signal, computed } from '@angular/core';
import { WebSocketService } from '@core/services/web-socket.service';
import { AuthService } from '@core/services/auth.service';
import { DriverLocation } from '@shared/models/driver-location';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

@Injectable({
	providedIn: 'root',
})
export class DriverLocationWebsocketService {
	private webSocketService = inject(WebSocketService);
	private authService = inject(AuthService);

	// Map of driver ID to their location
	private _driverLocations = signal<Map<number, DriverLocation>>(new Map());
	readonly driverLocations = this._driverLocations.asReadonly();

	// Computed values
	readonly driversArray = computed(() => Array.from(this._driverLocations().values()));

	readonly activeDriversCount = computed(() => this._driverLocations().size);

	// Track subscriptions
	private allDriversUnsubscribe: (() => void) | null = null;
	private specificDriverUnsubscribes = new Map<number, () => void>();

	constructor() {
		this.setupDriverLocationSubscription();
	}

	/**
	 * Setup effect to subscribe/unsubscribe based on user login
	 */
	private setupDriverLocationSubscription(): void {
		effect(() => {
			const user = this.authService.user();

			if (user) {
				// Subscribe to all driver locations for admins and customers
				if (
					user.role === LoggedInUserRole.ADMIN ||
					user.role === LoggedInUserRole.CUSTOMER
				) {
					this.subscribeToAllDrivers();
				}
			} else {
				// User logged out - unsubscribe from everything
				this.unsubscribeFromAllDrivers();
				this.clearAll();
			}
		});
	}

	/**
	 * Subscribe to all driver locations
	 * Typically for admins or customers tracking available drivers
	 */
	async subscribeToAllDrivers(): Promise<void> {
		if (this.allDriversUnsubscribe) {
			return; // Already subscribed
		}

		console.log('[DriverLocationService] Subscribing to all driver locations');

		try {
			this.allDriversUnsubscribe = await this.webSocketService.subscribe<DriverLocation>(
				'/topic/driver-locations',
				(location) => this.updateDriverLocation(location),
			);
		} catch (error) {
			console.error('[DriverLocationService] Error subscribing to all drivers:', error);
		}
	}

	/**
	 * Unsubscribe from all driver locations
	 */
	unsubscribeFromAllDrivers(): void {
		if (this.allDriversUnsubscribe) {
			console.log('[DriverLocationService] Unsubscribing from all driver locations');
			this.allDriversUnsubscribe();
			this.allDriversUnsubscribe = null;
		}
	}

	/**
	 * Subscribe to a specific driver's location
	 * Useful for tracking a driver during an active ride
	 *
	 * @param driverId - The driver ID to track
	 * @returns Unsubscribe function
	 */
	async subscribeToSpecificDriver(driverId: number): Promise<() => void> {
		// If already subscribed, return existing unsubscribe function
		const existing = this.specificDriverUnsubscribes.get(driverId);
		if (existing) {
			return existing;
		}

		console.log(`[DriverLocationService] Subscribing to driver ${driverId} location`);

		try {
			const unsubscribe = await this.webSocketService.subscribe<DriverLocation>(
				`/topic/driver-locations/${driverId}`,
				(location) => this.updateDriverLocation(location),
			);

			// Wrap unsubscribe to also remove from our map
			const wrappedUnsubscribe = () => {
				unsubscribe();
				this.specificDriverUnsubscribes.delete(driverId);
				console.log(`[DriverLocationService] Unsubscribed from driver ${driverId}`);
			};

			this.specificDriverUnsubscribes.set(driverId, wrappedUnsubscribe);
			return wrappedUnsubscribe;
		} catch (error) {
			console.error(
				`[DriverLocationService] Error subscribing to driver ${driverId}:`,
				error,
			);
			throw error;
		}
	}

	/**
	 * Unsubscribe from a specific driver's location
	 */
	unsubscribeFromSpecificDriver(driverId: number): void {
		const unsubscribe = this.specificDriverUnsubscribes.get(driverId);
		if (unsubscribe) {
			unsubscribe();
		}
	}

	/**
	 * Send driver's current location to backend
	 * Should be called by drivers to update their position
	 *
	 * @param location - Driver's current location
	 */
	async sendDriverLocation(location: { latitude: number; longitude: number }): Promise<void> {
		try {
			await this.webSocketService.send('/app/driver/location', location);
			console.log('[DriverLocationService] Location sent:', location);
		} catch (error) {
			console.error('[DriverLocationService] Error sending location:', error);
			throw error;
		}
	}

	/**
	 * Update driver location in the map
	 * If latitude/longitude is null, remove the driver (offline)
	 */
	updateDriverLocation(location: DriverLocation): void {
		this._driverLocations.update((map) => {
			const newMap = new Map(map);

			// Remove driver if they're offline (null coordinates)
			if (location.latitude == null || location.longitude == null) {
				newMap.delete(location.driverId);
				console.log(`[DriverLocationService] Driver ${location.driverId} went offline`);
			} else {
				newMap.set(location.driverId, location);
				console.log(
					`[DriverLocationService] Driver ${location.driverId} location updated:`,
					location,
				);
			}

			return newMap;
		});
	}

	/**
	 * Get location for a specific driver
	 */
	getDriverLocation(driverId: number): DriverLocation | undefined {
		return this._driverLocations().get(driverId);
	}

	/**
	 * Check if a driver is currently online
	 */
	isDriverOnline(driverId: number): boolean {
		return this._driverLocations().has(driverId);
	}

	/**
	 * Get all online driver IDs
	 */
	getOnlineDriverIds(): number[] {
		return Array.from(this._driverLocations().keys());
	}

	/**
	 * Clear all driver locations
	 */
	clearAll(): void {
		this._driverLocations.set(new Map());
		console.log('[DriverLocationService] All driver locations cleared');
	}

	/**
	 * Manual cleanup (called on logout)
	 */
	cleanup(): void {
		this.unsubscribeFromAllDrivers();

		// Unsubscribe from all specific drivers
		this.specificDriverUnsubscribes.forEach((unsubscribe) => unsubscribe());
		this.specificDriverUnsubscribes.clear();

		this.clearAll();
	}
}
