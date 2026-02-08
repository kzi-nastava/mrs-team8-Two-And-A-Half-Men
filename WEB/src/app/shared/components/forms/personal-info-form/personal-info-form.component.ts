// src/app/shared/renderer/personal-info-form/personal-info-form.component.ts

import { Component, input, output, model } from '@angular/core';
import {CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PersonalInfo } from '@shared/models/personal-info.model';

@Component({
	selector: 'app-personal-info-form',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './personal-info-form.component.html',
	styleUrls: ['./personal-info-form.component.css']
})
export class PersonalInfoFormComponent {
	// Two-way binding for form data
	personalInfo = model.required<PersonalInfo>();

	// Configuration
	showPhotoUpload = input<boolean>(true);
	readonly = input<boolean>(false);

	// Events
	photoChange = output<File>();

	// Internal state for file input
	selectedFile: File | null = null;

	onPhotoUpload(event: Event): void {
		const input = event.target as HTMLInputElement;
		if (input.files && input.files[0]) {
			this.selectedFile = input.files[0];

			// Create a preview URL
			const reader = new FileReader();
			reader.onload = (e) => {
				const result = e.target?.result as string;
				this.personalInfo.update(info => ({
					...info,
					imgSrc: result
				}));
			};
			reader.readAsDataURL(input.files[0]);

			// Emit the file for parent component to handle upload
			this.photoChange.emit(input.files[0]);
		}
	}

	getPhotoUrl(): string {
		return this.personalInfo().imgSrc || 'assets/default-profile.png';
	}
}
