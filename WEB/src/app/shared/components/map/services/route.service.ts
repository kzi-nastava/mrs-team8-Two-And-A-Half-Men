import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { MapService } from './map.service';

export interface RouteOptions {
	color?: string;
	weight?: number;
	opacity?: number;
	showAlternatives?: boolean;
	addWaypoints?: boolean;
}

export interface RouteInfo {
	id: string;
	waypoints: L.LatLng[];
	distance: number; // in meters
	duration: number; // in seconds
	control?: L.Routing.Control;
}

@Injectable({
	providedIn: 'root',
})
export class RouteService {
	private routes = new Map<string, RouteInfo>();

	constructor(private mapService: MapService) {}

	initialize(): void {
		if (!this.mapService.isInitialized()) {
			console.warn('Map not initialized. Cannot initialize RouteService.');
			return;
		}
		console.log('RouteService initialized');
	}

	/**
	 * Creates a route between waypoints
	 * @param waypoints Array of coordinates [lat, lng]
	 * @param options Route styling and behavior options
	 * @returns Route ID for future reference
	 */
	createRoute(waypoints: [number, number][], options?: RouteOptions): string {
		const map = this.mapService.getMap();
		const routeId = this.generateRouteId();

		const latLngWaypoints = waypoints.map((wp) => L.latLng(wp[0], wp[1]));

		const routingControl = L.Routing.control({
			waypoints: latLngWaypoints,
			routeWhileDragging: false,
			showAlternatives: options?.showAlternatives ?? false,
			addWaypoints: options?.addWaypoints ?? false,

			// KLJUČNO: Ukloni panel sa instrukcijama
			show: false,

			// Custom plan bez draggable waypoints
			plan: L.Routing.plan(latLngWaypoints, {
				createMarker: function () {
					return false;
				}, // Ne prikazuj waypoint markere
				draggableWaypoints: false,
				addWaypoints: false,
			}),

			lineOptions: {
				styles: [
					{
						color: options?.color || '#3388ff',
						weight: options?.weight || 6,
						opacity: options?.opacity || 0.7,
					},
				],
				extendToWaypoints: true,
				missingRouteTolerance: 0,
			},

			router: L.Routing.osrmv1({
				serviceUrl: 'https://routing.openstreetmap.de/routed-car/route/v1',
				profile: 'driving',
			}),

			// Dodatno: ukloni container potpuno
			fitSelectedRoutes: false,
		}).addTo(map);

		// Ukloni routing container iz DOM-a (ako se prikaže)
		setTimeout(() => {
			const container = document.querySelector('.leaflet-routing-container');
			if (container) {
				container.remove();
			}
		}, 100);

		// Store route info when calculated
		routingControl.on('routesfound', (e: any) => {
			const routes = e.routes;
			const route = routes[0];

			const routeInfo: RouteInfo = {
				id: routeId,
				waypoints: latLngWaypoints,
				distance: route.summary.totalDistance,
				duration: route.summary.totalTime,
				control: routingControl,
			};

			this.routes.set(routeId, routeInfo);

			console.log(
				`Route created: ${routeId}, Distance: ${(route.summary.totalDistance / 1000).toFixed(2)} km, ` +
					`Duration: ${Math.round(route.summary.totalTime / 60)} min`,
			);
		});

		return routeId;
	}

	/**
	 * Creates a simple route between two points
	 */
	createSimpleRoute(
		start: [number, number],
		end: [number, number],
		options?: RouteOptions,
	): string {
		return this.createRoute([start, end], options);
	}

	/**
	 * Updates waypoints of an existing route
	 */
	updateRoute(routeId: string, waypoints: [number, number][]): void {
		const routeInfo = this.routes.get(routeId);
		if (!routeInfo || !routeInfo.control) {
			console.warn(`Route ${routeId} not found`);
			return;
		}

		const latLngWaypoints = waypoints.map((wp) => L.latLng(wp[0], wp[1]));
		routeInfo.control.setWaypoints(latLngWaypoints);
		routeInfo.waypoints = latLngWaypoints;
	}

	/**
	 * Removes a route from the map
	 */
	removeRoute(routeId: string): void {
		const routeInfo = this.routes.get(routeId);
		if (!routeInfo) {
			console.warn(`Route ${routeId} not found`);
			return;
		}

		const map = this.mapService.getMap();
		if (routeInfo.control) {
			map.removeControl(routeInfo.control);
		}

		this.routes.delete(routeId);
		console.log(`Route removed: ${routeId}`);
	}

	/**
	 * Gets route information
	 */
	getRoute(routeId: string): RouteInfo | undefined {
		return this.routes.get(routeId);
	}

	/**
	 * Gets all active routes
	 */
	getAllRoutes(): RouteInfo[] {
		return Array.from(this.routes.values());
	}

	/**
	 * Clears all routes from the map
	 */
	clearAllRoutes(): void {
		const map = this.mapService.getMap();

		this.routes.forEach((routeInfo) => {
			if (routeInfo.control) {
				map.removeControl(routeInfo.control);
			}
		});

		this.routes.clear();
		console.log('All routes cleared');
	}

	/**
	 * Fits map view to show the entire route
	 */
	fitRouteInView(routeId: string): void {
		const routeInfo = this.routes.get(routeId);
		if (!routeInfo) {
			console.warn(`Route ${routeId} not found`);
			return;
		}

		const map = this.mapService.getMap();
		const bounds = L.latLngBounds(routeInfo.waypoints);
		map.fitBounds(bounds, { padding: [50, 50] });
	}

	private generateRouteId(): string {
		return `route_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
	}

	cleanup(): void {
		this.clearAllRoutes();
	}
}
