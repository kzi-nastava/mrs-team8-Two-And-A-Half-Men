export interface MapConfig {
	enableLocationPins?: boolean;
	enableDriverMarkers?: boolean;
	enableRouting?: boolean;
	enableClickToAddLocation?: boolean;
	enableRightClickToRemove?: boolean;
	enableDriverTracking?: boolean;

	center?: [number, number];
	zoom?: number;

	path?: string;
}

export const DEFAULT_MAP_CONFIG: MapConfig = {
	enableLocationPins: false,
	enableDriverMarkers: false,
	enableRouting: false,
	enableClickToAddLocation: false,
	enableRightClickToRemove: false,
	enableDriverTracking: false,
	center: [45.2396, 19.8227],
	zoom: 13,
};

export const MAP_CONFIGS = {
	HISTORY_VIEW: {
		enableLocationPins: false,
		enableDriverMarkers: false,
		enableRouting: true,
		enableClickToAddLocation: false,
		enableRightClickToRemove: false,
		enableDriverTracking: false,
	} as MapConfig,

	BOOKING: {
		enableLocationPins: true,
		enableDriverMarkers: true,
		enableRouting: true,
		enableClickToAddLocation: true,
		enableRightClickToRemove: true,
		enableDriverTracking: false,
	} as MapConfig,

	ACTIVE_RIDE: {
		enableLocationPins: false,
		enableDriverMarkers: true,
		enableRouting: true,
		enableClickToAddLocation: false,
		enableRightClickToRemove: false,
		enableDriverTracking: true,
	} as MapConfig,

	ADMIN_OVERVIEW: {
		enableLocationPins: false,
		enableDriverMarkers: true,
		enableRouting: false,
		enableClickToAddLocation: false,
		enableRightClickToRemove: false,
		enableDriverTracking: true,
	} as MapConfig,

	ROUTE_DISPLAY_ONLY: {
		enableLocationPins: false,
		enableDriverMarkers: false,
		enableRouting: true,
		enableClickToAddLocation: false,
		enableRightClickToRemove: false,
		enableDriverTracking: false,
	} as MapConfig,
};
