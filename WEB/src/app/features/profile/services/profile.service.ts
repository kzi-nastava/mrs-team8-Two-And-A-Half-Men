// src/app/services/profile.service.ts

import { Injectable, signal, computed } from '@angular/core';
import { UserProfile, VehicleInfo, VehicleType, AdditionalService } from '../models/user-profile.model';

@Injectable({
	providedIn: 'root'
})
export class ProfileService {
	// User profile signal
	private userProfileSignal = signal<UserProfile>({
		id: 'user-123',
		firstName: 'John',
		lastName: 'Doe',
		phoneNumber: '+381641234567',
		address: 'Random Street 67, Novi Sad',
		email: 'example@gmail.com',
		photoUrl: 'https://images.pexels.com/photos/3785079/pexels-photo-3785079.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500'
	});

	// Vehicle info signal
	private vehicleInfoSignal = signal<VehicleInfo>({
		type: 'car',
		numberOfSeats: 5,
		model: 'Fiat Punto',
		plateNumber: 'AB-123-CD',
		additionalServices: ['pet-friendly', 'baby-seat', 'smoking-allowed']
	});

	// Vehicle types - readonly signal
	readonly vehicleTypes = signal<VehicleType[]>([
		{ id: 'car', name: 'Car' },
		{ id: 'van', name: 'Van' },
		{ id: 'suv', name: 'SUV' },
		{ id: 'minibus', name: 'Minibus' }
	]);

	// Available additional services - readonly signal
	readonly availableServices = signal<AdditionalService[]>([
		{ id: 'pet-friendly', name: 'Pet friendly', enabled: true },
		{ id: 'baby-seat', name: 'Baby seat', enabled: true },
		{ id: 'smoking-allowed', name: 'Smoking allowed', enabled: true },
		{ id: 'wheelchair-accessible', name: 'Wheelchair accessible', enabled: false },
		{ id: 'wifi', name: 'WiFi', enabled: false }
	]);

	// Public readonly signals
	readonly userProfile = this.userProfileSignal.asReadonly();
	readonly vehicleInfo = this.vehicleInfoSignal.asReadonly();

	// Computed signal for enabled services
	readonly enabledServices = computed(() => {
		const vehicle = this.vehicleInfoSignal();
		return this.availableServices().filter(service =>
			vehicle.additionalServices.includes(service.id)
		);
	});

	// Update user profile
	updateUserProfile(profile: Partial<UserProfile>): void {
		this.userProfileSignal.update(current => ({
			...current,
			...profile
		}));
	}

	// Update vehicle info
	updateVehicleInfo(vehicle: Partial<VehicleInfo>): void {
		this.vehicleInfoSignal.update(current => ({
			...current,
			...vehicle
		}));
	}

	// Change password (simulate API call)
	changePassword(newPassword: string, confirmPassword: string): Promise<boolean> {
		return new Promise((resolve) => {
			setTimeout(() => {
				if (newPassword === confirmPassword && newPassword.length >= 8) {
					resolve(true);
				} else {
					resolve(false);
				}
			}, 500);
		});
	}

	// Toggle additional service
	toggleAdditionalService(serviceId: string): void {
		this.vehicleInfoSignal.update(current => {
			const services = current.additionalServices.includes(serviceId)
				? current.additionalServices.filter(id => id !== serviceId)
				: [...current.additionalServices, serviceId];

			return {
				...current,
				additionalServices: services
			};
		});
	}
}
