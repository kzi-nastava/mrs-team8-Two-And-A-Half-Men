import { Component, input, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FavouriteRoute } from '@shared/models/favourite-route.model';

@Component({
	selector: 'app-favourite-routes',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './favourite-routes.component.html',
	styleUrl: './favourite-routes.component.css',
})
export class FavouriteRoutesComponent {
	onSelect = input.required<(routeId: number) => void>();
	routes = input.required<FavouriteRoute[]>();
	onRemove = input.required<(routeId: number) => void>();

	handleSelectRoute(routeId: number): void {
		this.onSelect()(routeId);
	}

	handleRemoveRoute(event: Event, routeId: number): void {
		event.stopPropagation(); // Prevent triggering select when removing
		this.onRemove()(routeId);
	}

	getRouteDisplay(route: FavouriteRoute): { start: string; end: string; stops: number } {
		const points = route.points;
		return {
			start: points[0]?.address || 'Unknown',
			end: points[points.length - 1]?.address || 'Unknown',
			stops: points.length - 2, // Exclude start and end
		};
	}

	getStopPoints(route: FavouriteRoute): string[] {
		// Return intermediate stops (exclude first and last)
		return route.points.slice(1, -1).map((point) => point.address);
	}
}
