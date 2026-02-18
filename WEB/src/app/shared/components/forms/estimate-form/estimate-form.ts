import { Component, effect, inject, OnDestroy, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { LocationsService } from '@shared/services/locations/locations.service';
import { EstimateService } from './service/estimate-service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { NominatimService } from '@shared/services/locations/nominatim-service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { NominatimResult } from '@shared/models/nominatim-results.model';

@Component({
	selector: 'app-estimate-form',
	imports: [ReactiveFormsModule, CommonModule, RouterModule, HttpClientModule],
	templateUrl: './estimate-form.html',
	styleUrl: './estimate-form.css',
})
export class EstimateForm implements OnDestroy {
	estimateForm: FormGroup;
	startQuery = signal('');
	endQuery = signal('');

	startSuggestions = signal<NominatimResult[]>([]);
	endSuggestions = signal<NominatimResult[]>([]);

	showStartSuggestions = signal(false);
	showEndSuggestions = signal(false);

	private startQuerySubject = new Subject<string>();
	private endQuerySubject = new Subject<string>();
	private popupsService = inject(PopupsService);

	private suppressLocationSync = false;

	constructor(
		private fb: FormBuilder,
		private http: HttpClient,
		private sharedLocationsService: LocationsService,
		private estimateService: EstimateService,
		private nominatimService: NominatimService,
	) {
		this.estimateForm = this.fb.group({
			startpoint: ['', Validators.required],
			endpoint: ['', Validators.required],
		});

		// ── Autocomplete pipelines ──────────────────────────────────────────

		this.startQuerySubject
			.pipe(
				debounceTime(500),
				distinctUntilChanged(),
				switchMap((query) => (query.length > 3 ? this.searchLocation(query) : [])),
			)
			.subscribe((results) => {
				// Don't show dropdown if the current text already matches the selected result
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
				switchMap((query) => (query.length > 3 ? this.searchLocation(query) : [])),
			)
			.subscribe((results) => {
				if (results.length === 1 && this.endQuery() === results[0].display_name) {
					this.showEndSuggestions.set(false);
					return;
				}
				this.endSuggestions.set(results);
				this.showEndSuggestions.set(results.length > 0);
			});

		// ── Trigger search on query change ──────────────────────────────────

		effect(() => {
			const query = this.startQuery();
			if (query.length > 3) {
				this.startQuerySubject.next(query);
			} else {
				this.startSuggestions.set([]);
				this.showStartSuggestions.set(false);
			}
		});

		effect(() => {
			const query = this.endQuery();
			if (query.length > 3) {
				this.endQuerySubject.next(query);
			} else {
				this.endSuggestions.set([]);
				this.showEndSuggestions.set(false);
			}
		});

		// ── Sync form display when shared locations change (e.g. map click) ─
		// The map can only add/remove pins for start (index 0) and end (index 1).
		// If somehow more than 2 locations slip in, we trim to first + last here.

		effect(() => {
			const locations = this.sharedLocationsService.locations();

			if (this.suppressLocationSync) return;

			if (locations.length > 2) {
				// Defensive: keep only start + end, let service + map stay in sync
				this.sharedLocationsService.locations.set([
					locations[0],
					locations[locations.length - 1],
				]);
				return; // effect will re-run after the set()
			}

			if (locations.length === 2) {
				this.startQuery.set(locations[0].display_name);
				this.endQuery.set(locations[1].display_name);
				this.estimateForm.patchValue({
					startpoint: locations[0].display_name,
					endpoint: locations[1].display_name,
				});
			} else if (locations.length === 1) {
				this.startQuery.set(locations[0].display_name);
				this.endQuery.set('');
				this.estimateForm.patchValue({
					startpoint: locations[0].display_name,
					endpoint: '',
				});
			} else {
				this.startQuery.set('');
				this.endQuery.set('');
				this.estimateForm.patchValue({ startpoint: '', endpoint: '' });
			}
		});
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	searchLocation(query: string) {
		return this.nominatimService.search(query, 5);
	}

	// ── Suggestion selection ─────────────────────────────────────────────────

	selectStartSuggestion(suggestion: NominatimResult): void {
		this.startQuery.set(suggestion.display_name);
		this.estimateForm.patchValue({ startpoint: suggestion.display_name });
		this.showStartSuggestions.set(false);
		this.startSuggestions.set([]);

		this.suppressLocationSync = true;
		this.sharedLocationsService.locations.update((list) => {
			// Replace or set start (index 0); keep end (index 1) if present
			const end = list[1] ?? list[list.length - 1];
			return end ? [suggestion, end] : [suggestion];
		});
		this.suppressLocationSync = false;
	}

	selectEndSuggestion(suggestion: NominatimResult): void {
		this.endQuery.set(suggestion.display_name);
		this.estimateForm.patchValue({ endpoint: suggestion.display_name });
		this.showEndSuggestions.set(false);
		this.endSuggestions.set([]);

		this.suppressLocationSync = true;
		this.sharedLocationsService.locations.update((list) => {
			// Replace or set end (index 1); keep start (index 0) if present
			const start = list[0];
			return start ? [start, suggestion] : [suggestion];
		});
		this.suppressLocationSync = false;
	}

	// ── Form actions ─────────────────────────────────────────────────────────

	onSubmit(): void {
		if (!this.estimateForm.valid) {
			console.log('Form is invalid');
			return;
		}

		const locations = this.sharedLocationsService.locations();
		if (locations.length < 2) {
			this.popupsService.error(
				'Missing Locations',
				'Please select both a start and end location.',
			);
			return;
		}

		const [startpointLocation, endpointLocation] = locations;

		this.estimateService.estimateTime(startpointLocation, endpointLocation).subscribe({
			next: (response) => {
				this.popupsService.success(
					'Estimated Time',
					`The estimated time for your ride is ${Math.ceil(response.time)} minutes.`,
				);
			},
			error: (err) => {
				this.popupsService.error(
					'Estimation Failed',
					err.error?.error ||
						'An error occurred while estimating your ride time. Please try again.',
				);
			},
		});
	}

	onBlur(field: 'start' | 'end'): void {
		setTimeout(() => {
			if (field === 'start') {
				this.showStartSuggestions.set(false);
			} else {
				this.showEndSuggestions.set(false);
			}
		}, 200);
	}

	onReset(): void {
		this.estimateForm.reset();
		this.startQuery.set('');
		this.endQuery.set('');
		this.startSuggestions.set([]);
		this.endSuggestions.set([]);
		this.showStartSuggestions.set(false);
		this.showEndSuggestions.set(false);
		this.sharedLocationsService.locations.set([]);
	}

	ngOnDestroy(): void {
		this.startQuerySubject.complete();
		this.endQuerySubject.complete();
	}
}
