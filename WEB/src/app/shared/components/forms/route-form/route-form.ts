import { Component, effect, inject, input, OnDestroy, signal } from '@angular/core';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { NominatimService } from '@shared/services/locations/nominatim-service';

@Component({
	selector: 'app-route-form',
	imports: [ReactiveFormsModule, CommonModule, RouterModule],
	templateUrl: './route-form.html',
	styleUrl: './route-form.css',
})
export class RouteForm implements OnDestroy {
	// Input properties
	maxPoints = input<number | undefined>(undefined);
	allowReordering = input<boolean>(true);

	// Injectables
	private sharedLocationsService = inject(SharedLocationsService);
	private nominatimService = inject(NominatimService);

	routeForm = new FormGroup({
		points: new FormArray([]),
	});

	points = signal<RoutePoint[]>([]);

	// Drag and drop state
	draggedIndex = signal<number | null>(null);

	private querySubjects: Map<number, Subject<string>> = new Map();
	private isUpdatingFromService = false;

	constructor() {
		// Initialize with 2 empty points first
		this.addEmptyPoint(); // Start point
		this.addEmptyPoint(); // Destination point

		// Sync with shared locations service
		effect(() => {
			console.log("SHARED SERVICES UPDATED")
			const locations = this.sharedLocationsService.locations();
			if (!this.isUpdatingFromService && locations.length > 0) {
				this.syncFromSharedService(locations);
			}
		});
	}

	get pointsFormArray(): FormArray {
		return this.routeForm.get('points') as FormArray;
	}

	private syncFromSharedService(locations: NominatimResult[]) {
		this.isUpdatingFromService = true;

		const maxPointsValue = this.maxPoints();
		const limitedLocations = maxPointsValue
			? locations.slice(Math.max(0, maxPointsValue - locations.length))
			: locations;

		const currentLength = this.pointsFormArray.length;
		// Add points from service
		limitedLocations.forEach((location, index) => {
			if (index >= currentLength) {
				this.addPointFromLocation(location, index);
			}
			else {
				this.updatePointFromLocation(location, index);
			}
		});

		if (this.pointsFormArray.length < 2) {
			const toInsert = 2 - this.pointsFormArray.length;
			for (let i = 0; i < toInsert; i++) {
				this.addEmptyPoint()
			}
		}

		this.isUpdatingFromService = false;
	}

	private addEmptyPoint() {
		const index = this.points().length;
		const pointGroup = new FormGroup({
			query: new FormControl(['', Validators.required]),
		});

		const point: RoutePoint = {
			id: Date.now() + Math.random(), // Ensure unique ID
			query: signal(''),
			suggestions: signal<NominatimResult[]>([]),
			showSuggestions: signal(false),
			selectedLocation: undefined,
		};

		this.pointsFormArray.push(pointGroup);
		this.points.update((p) => [...p, point]);
		this.setupQuerySubject(index);
	}

	private updatePointFromLocation(location: NominatimResult, index: number) {
		this.pointsFormArray.at(index)?.get('query')?.setValue('new value');
		const point = this.points()[index];
		point.query.set(location.display_name);
		point.showSuggestions.set(false);
		point.suggestions.set([]);
		point.selectedLocation = location;
	}

	private addPointFromLocation(location: NominatimResult, index: number) {
		const pointGroup = new FormGroup({
			query: new FormControl([location.display_name, Validators.required]),
		});

		const point: RoutePoint = {
			id: Date.now() + index,
			query: signal(location.display_name),
			suggestions: signal<NominatimResult[]>([]),
			showSuggestions: signal(false),
			selectedLocation: location,
		};

		this.pointsFormArray.push(pointGroup);
		this.points.update((p) => [...p, point]);
		this.setupQuerySubject(index);
	}

	addPoint() {
		const maxPointsValue = this.maxPoints();
		if (maxPointsValue !== undefined && this.points().length >= maxPointsValue) {
			return;
		}

		this.addEmptyPoint();
	}

	removePoint(index: number) {
		// Don't allow removing if we'd have less than 2 points (start and destination)
		// or if trying to remove start (0) or destination (last) point
		if (this.points().length <= 2 || index === 0 || index === this.points().length - 1) {
			return;
		}

		this.pointsFormArray.removeAt(index);
		this.points.update((p) => p.filter((_, i) => i !== index));

		// Clean up the query subject
		const subject = this.querySubjects.get(index);
		if (subject) {
			subject.complete();
			this.querySubjects.delete(index);
		}

		// Update shared service
		this.updateSharedService();
	}

	private setupQuerySubject(index: number) {
		const subject = new Subject<string>();
		this.querySubjects.set(index, subject);

		subject
			.pipe(
				debounceTime(500),
				distinctUntilChanged(),
				switchMap((query) => {
					if (query.length > 3) {
						return this.searchLocation(query);
					}
					return [];
				}),
			)
			.subscribe((results) => {
				const currentPoints = this.points();
				if (index < currentPoints.length) {
					const point = currentPoints[index];

					// Don't show suggestions if there's exactly one result that matches current query
					if (results.length === 1 && point.query() === results[0].display_name) {
						point.showSuggestions.set(false);
						return;
					}

					point.suggestions.set(results);
					point.showSuggestions.set(results.length > 0);
				}
			});
	}

	onQueryChange(index: number, query: string) {
		const currentPoints = this.points();
		if (index >= currentPoints.length) return;

		const point = currentPoints[index];
		point.query.set(query);

		if (query.length > 3) {
			const subject = this.querySubjects.get(index);
			if (subject) {
				subject.next(query);
			}
		} else {
			point.suggestions.set([]);
			point.showSuggestions.set(false);
		}
	}

	searchLocation(query: string) {
		return this.nominatimService.search(query, 5);
	}

	selectSuggestion(index: number, suggestion: NominatimResult) {
		const currentPoints = this.points();
		if (index >= currentPoints.length) return;

		const point = currentPoints[index];
		point.query.set(suggestion.display_name);
		point.showSuggestions.set(false);
		point.suggestions.set([]);
		point.selectedLocation = suggestion;

		// Update form control
		this.pointsFormArray.at(index).patchValue({
			query: suggestion.display_name,
		});

		// Update shared service
		this.updateSharedService();
	}

	onBlur(index: number) {
		setTimeout(() => {
			const currentPoints = this.points();
			if (index < currentPoints.length) {
				currentPoints[index].showSuggestions.set(false);
			}
		}, 200);
	}

	// Drag and drop methods
	onDragStart(event: DragEvent, index: number) {
		this.draggedIndex.set(index);
		if (event.dataTransfer) {
			event.dataTransfer.effectAllowed = 'move';
			event.dataTransfer.setData('text/html', (event.target as HTMLElement).innerHTML);
		}
	}

	onDragOver(event: DragEvent) {
		event.preventDefault();
		if (event.dataTransfer) {
			event.dataTransfer.dropEffect = 'move';
		}
	}

	onDrop(event: DragEvent, dropIndex: number) {
		event.preventDefault();
		const dragIndex = this.draggedIndex();

		if (dragIndex !== null && dragIndex !== dropIndex) {
			this.reorderPoints(dragIndex, dropIndex);
		}

		this.draggedIndex.set(null);
	}

	onDragEnd() {
		this.draggedIndex.set(null);
	}

	private reorderPoints(fromIndex: number, toIndex: number) {
		const currentPoints = [...this.points()];
		const [movedPoint] = currentPoints.splice(fromIndex, 1);
		currentPoints.splice(toIndex, 0, movedPoint);

		this.points.set(currentPoints);

		// Rebuild form array
		const currentValues = currentPoints.map((p) => ({ query: p.query() }));
		this.pointsFormArray.clear();
		currentValues.forEach((value) => {
			this.pointsFormArray.push(
				new FormGroup({
					query: new FormControl([value.query]),
				}),
			);
		});

		// Update shared service
		this.updateSharedService();
	}

	private updateSharedService() {
		if (this.isUpdatingFromService) {
			return;
		}

		const locations = this.points()
			.map((p) => p.selectedLocation)
			.filter(Boolean) as NominatimResult[];

		this.sharedLocationsService.locations.set(locations);
	}

	isStartPoint(index: number): boolean {
		return index === 0 && this.points().length > 0;
	}

	isEndPoint(index: number): boolean {
		return index === this.points().length - 1 && this.points().length > 1;
	}

	getPointLabel(index: number): string {
		const pointsLength = this.points().length;

		// If only one point, it's the start point
		if (pointsLength === 1 && index === 0) {
			return 'Start Point';
		}

		// First point is always "Start Point"
		if (index === 0) {
			return 'Start Point';
		}

		// Last point is always "Destination"
		if (index === pointsLength - 1) {
			return 'Destination';
		}

		// Middle points are numbered
		return `Point ${index + 1}`;
	}

	ngOnDestroy() {
		this.querySubjects.forEach((subject) => subject.complete());
		this.querySubjects.clear();
	}
}

interface RoutePoint {
	id: number;
	query: ReturnType<typeof signal<string>>;
	suggestions: ReturnType<typeof signal<NominatimResult[]>>;
	showSuggestions: ReturnType<typeof signal<boolean>>;
	selectedLocation?: NominatimResult;
}
