import { RideStatus } from '@shared/models/ride.model';

export interface MapConfig {
	enableLocationPins?: boolean;
	enableClickToAddLocation?: boolean;
	enableRightClickToRemove?: boolean;
	enableActiveDriverMarkers?: boolean;
	enableDriverTracking?: boolean;
	enableRouting?: boolean;
	enablePath?: boolean;
}

export const DEFAULT_MAP_CONFIG: MapConfig = {
	enableLocationPins: false,
	enableActiveDriverMarkers: false,
	enableRouting: false,
	enableClickToAddLocation: false,
	enableRightClickToRemove: false,
	enableDriverTracking: false,
	enablePath: false
};

export const BOOKING_MAP_CONFIG: MapConfig = {
	enableLocationPins: true,
	enableClickToAddLocation: true,
	enableRightClickToRemove: true,
	enableActiveDriverMarkers: true,
	enableDriverTracking: false,
	enableRouting: true,
	enablePath: false,
};

/**
 * Generates map configuration based on ride status
 * Used in ride-details component
 */
export function getMapConfigForRideStatus(status: RideStatus): MapConfig {
	const baseConfig: MapConfig = {
		enableLocationPins: true,
		enableClickToAddLocation: false,
		enableRightClickToRemove: false,
		enableActiveDriverMarkers: false,
		enableDriverTracking: false,
		enableRouting: true,
		enablePath: false,
	};

	switch (status) {
		case RideStatus.PENDING:
		case RideStatus.ACCEPTED:
		case RideStatus.CANCELLED:
			return {
				...baseConfig,
			};

		case RideStatus.ACTIVE:
			return {
				...baseConfig,
				enableDriverTracking: true,
			};

		case RideStatus.PANICKED:
		case RideStatus.INTERRUPTED:
		case RideStatus.FINISHED:
			return {
				...baseConfig,
				enablePath: true,
			};

		default:
			return baseConfig;
	}
}
