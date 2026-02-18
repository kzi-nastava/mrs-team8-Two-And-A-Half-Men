import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { firstValueFrom } from 'rxjs';

export interface VehicleTypeDTO {
	id: number;
	typeName: string;
	description: string;
	price: number;
}

export interface PricingUpdateDTO {
	price: number;
}

@Component({
	selector: 'app-vehicle-pricing-settings',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './vehicle-pricing-settings.component.html',
	styleUrls: ['./vehicle-pricing-settings.component.css'],
})
export class VehiclePricingSettingsComponent implements OnInit {
	private http = inject(HttpClient);

	// Signals
	vehicleTypes = signal<VehicleTypeDTO[]>([]);
	loading = signal<boolean>(false);
	error = signal<string | null>(null);
	editingId = signal<number | null>(null);
	editingPrice = signal<number | null>(null);
	savingId = signal<number | null>(null);

	// Computed values
	hasVehicleTypes = computed(() => this.vehicleTypes().length > 0);
	isSaving = computed(() => this.savingId() !== null);

	ngOnInit(): void {
		this.loadVehicleTypes().then();
	}

	/**
	 * Load all vehicle types from the API
	 */
	async loadVehicleTypes(): Promise<void> {
		this.loading.set(true);
		this.error.set(null);

		try {
			const types = await firstValueFrom(
				this.http.get<VehicleTypeDTO[]>(`/api/${environment.apiVersion}/vehicle-types`),
			);

			this.vehicleTypes.set(types);
			console.log('[VehiclePricing] Loaded vehicle types:', types);
		} catch (error: any) {
			console.error('[VehiclePricing] Error loading vehicle types:', error);
			this.error.set('Failed to load vehicle types');
		} finally {
			this.loading.set(false);
		}
	}

	/**
	 * Start editing a vehicle type price
	 */
	startEdit(vehicleType: VehicleTypeDTO): void {
		this.editingId.set(vehicleType.id);
		this.editingPrice.set(vehicleType.price);
	}

	/**
	 * Cancel editing
	 */
	cancelEdit(): void {
		this.editingId.set(null);
		this.editingPrice.set(null);
	}

	/**
	 * Check if a vehicle type is being edited
	 */
	isEditing(id: number): boolean {
		return this.editingId() === id;
	}

	/**
	 * Update vehicle type price
	 */
	async updatePrice(vehicleType: VehicleTypeDTO): Promise<void> {
		const newPrice = this.editingPrice();

		// Validation
		if (newPrice === null || newPrice < 0) {
			this.error.set('Price must be a positive number');
			return;
		}

		if (newPrice === vehicleType.price) {
			this.cancelEdit();
			return;
		}

		this.savingId.set(vehicleType.id);
		this.error.set(null);

		try {
			const pricingUpdate: PricingUpdateDTO = {
				price: newPrice,
			};

			const updatedType = await firstValueFrom(
				this.http.patch<VehicleTypeDTO>(
					`/api/${environment.apiVersion}/vehicle-types/${vehicleType.id}/price`,
					pricingUpdate,
				),
			);

			// Update the vehicle type in the list
			this.vehicleTypes.update((types) =>
				types.map((t) => (t.id === updatedType.id ? updatedType : t)),
			);

			console.log('[VehiclePricing] Price updated successfully:', updatedType);
			this.cancelEdit();

			// Show success message (optional)
			this.showSuccessMessage(vehicleType.typeName);
		} catch (error: any) {
			console.error('[VehiclePricing] Error updating price:', error);

			if (error.status === 403) {
				this.error.set('You do not have permission to update prices');
			} else if (error.status === 404) {
				this.error.set('Vehicle type not found');
			} else {
				this.error.set('Failed to update price. Please try again.');
			}
		} finally {
			this.savingId.set(null);
		}
	}

	/**
	 * Show success message (you can replace with toast notification)
	 */
	private showSuccessMessage(typeName: string): void {
		// You can integrate with a toast service here
		console.log(`✅ ${typeName} price updated successfully`);
	}

	/**
	 * Handle Enter key press in input
	 */
	onEnterKey(vehicleType: VehicleTypeDTO): void {
		this.updatePrice(vehicleType);
	}

	/**
	 * Handle price input change
	 */
	onPriceChange(value: string): void {
		const numValue = parseFloat(value);
		this.editingPrice.set(isNaN(numValue) ? null : numValue);
	}

	/**
	 * Format price for display
	 */
	formatPrice(price: number): string {
		return price.toFixed(2);
	}

	/**
	 * Get icon for vehicle type
	 */
	getVehicleIcon(typeName: string): string {
		const name = typeName.toLowerCase();

		if (name.includes('standard')) return '🚗';
		if (name.includes('comfort')) return '🚙';
		if (name.includes('premium') || name.includes('luxury')) return '🚘';
		if (name.includes('van')) return '🚐';
		if (name.includes('electric')) return '⚡';

		return '🚗'; // Default
	}

	/**
	 * Retry loading vehicle types
	 */
	retry(): void {
		this.loadVehicleTypes().then();
	}
}
