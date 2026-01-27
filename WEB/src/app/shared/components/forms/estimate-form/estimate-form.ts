import { Component, effect, OnInit, signal } from '@angular/core';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
import { EstimateService } from './service/estimate-service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { NominatimService } from '@shared/services/locations/nominatim-service';

@Component({
	selector: 'app-estimate-form',
	imports: [ReactiveFormsModule, CommonModule, RouterModule, HttpClientModule],
	templateUrl: './estimate-form.html',
	styleUrl: './estimate-form.css',
})
export class EstimateForm {
	estimateForm: FormGroup;
	startQuery = signal('');
	endQuery = signal('');

	startSuggestions = signal<NominatimResult[]>([]);
	endSuggestions = signal<NominatimResult[]>([]);

	showStartSuggestions = signal(false);
	showEndSuggestions = signal(false);
	estimatedTime = signal<number | null>(null);

	private startQuerySubject = new Subject<string>();
	private endQuerySubject = new Subject<string>();

	constructor(
		private fb: FormBuilder,
		private http: HttpClient,
		private sharedLocationsService: SharedLocationsService,
		private estimateService: EstimateService,
		private nominatimService: NominatimService,
	) {
		this.estimateForm = this.fb.group({
			startpoint: ['', Validators.required],
			endpoint: ['', Validators.required],
		});
		this.startQuerySubject
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
				if (results.length === 1 && this.startQuery() === results[0].display_name) {
					this.showStartSuggestions.set(false);
					return;
				}
				this.startSuggestions.set(results);
				this.showStartSuggestions.set(results.length > 0);
			});
		this.endQuerySubject
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
				if (results.length === 1 && this.endQuery() === results[0].display_name) {
					this.showEndSuggestions.set(false);
					return;
				}
				this.endSuggestions.set(results);
				this.showEndSuggestions.set(results.length > 0);
			});

		// Watch for start query changes
		effect(() => {
			const query = this.startQuery();
			if (query.length > 3) {
				this.startQuerySubject.next(query);
			} else {
				this.startSuggestions.set([]);
				this.showStartSuggestions.set(false);
			}
		});

		// Watch for end query changes
		effect(() => {
			const query = this.endQuery();
			if (query.length > 3) {
				this.endQuerySubject.next(query);
			} else {
				this.endSuggestions.set([]);
				this.showEndSuggestions.set(false);
			}
		});

		// Sync with shared locations
		effect(() => {
			const locations = this.sharedLocationsService.locations();
			if (locations.length > 2) {
				this.sharedLocationsService.locations.set([
					locations[0],
					locations[locations.length - 1],
				]);
			} else if (locations.length === 2) {
				this.startQuery.set(locations[0].display_name);
				this.endQuery.set(locations[1].display_name);
			} else if (locations.length === 1) {
				this.startQuery.set(locations[0].display_name);
				this.endQuery.set('');
			} else if (locations.length === 0) {
				this.startQuery.set('');
				this.endQuery.set('');
			}
		});
	}

	searchLocation(query: string) {
		return this.nominatimService.search(query, 5);
	}

	selectStartSuggestion(suggestion: NominatimResult) {
		this.startQuery.set(suggestion.display_name);
		this.showStartSuggestions.set(false);
		this.startSuggestions.set([]);
		this.sharedLocationsService.locations.update(
			(list) => [suggestion, list[list.length - 1]].filter(Boolean) as NominatimResult[],
		);
	}

	selectEndSuggestion(suggestion: NominatimResult) {
		this.endQuery.set(suggestion.display_name);
		this.showEndSuggestions.set(false);
		this.endSuggestions.set([]);
		this.sharedLocationsService.locations.update((list) => {
			if (list.length === 0) {
				return [suggestion];
			}
			return [list[0], suggestion].filter(Boolean) as NominatimResult[];
		});
	}

	onSubmit() {
		if (this.estimateForm.valid) {
			const startpoint = this.estimateForm.get('startpoint')?.value ?? '';
			const endpoint = this.estimateForm.get('endpoint')?.value ?? '';
			if (!startpoint || !endpoint) {
				console.log('Startpoint or endpoint is missing');
				return;
			}
			const startEnd = this.sharedLocationsService.locations();
			const startpointLocation = startEnd[0];
			const endpointLocation = startEnd[1];
			console.log('Estimate Form Submitted', { startpoint, endpoint });

			this.estimateService
				.estimateTime(startpointLocation, endpointLocation)
				.subscribe((estimatedTime) => {
					let timeInMinutes = Math.round(estimatedTime * 100) / 100;
					this.estimatedTime.set(timeInMinutes);
				});
		} else {
			console.log('Form is invalid');
		}
	}

	onBlur(field: 'start' | 'end') {
		setTimeout(() => {
			if (field === 'start') {
				this.showStartSuggestions.set(false);
			} else {
				this.showEndSuggestions.set(false);
			}
		}, 200);
	}

	onReset() {
		this.estimateForm.reset();
		this.startQuery.set('');
		this.endQuery.set('');
		this.startSuggestions.set([]);
		this.endSuggestions.set([]);
		this.showStartSuggestions.set(false);
		this.showEndSuggestions.set(false);
		this.sharedLocationsService.locations.set([]);
	}

	ngOnDestroy() {
		this.startQuerySubject.complete();
		this.endQuerySubject.complete();
	}
}
