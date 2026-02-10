import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

export interface SimplePopupOptions {
	buttonText?: string;
	onConfirm?: () => void;
}

@Injectable({
	providedIn: 'root',
})
export class PopupsService {
	success(title: string, text: string, options?: SimplePopupOptions) {
		Swal.fire({
			title,
			html: text,
			icon: 'success',
			confirmButtonText: options?.buttonText || 'OK',
		}).then(result => {
			if (result.isConfirmed) {
				options?.onConfirm?.();
			}
		});
	}

	error(title: string, text: string, options?: SimplePopupOptions) {
		Swal.fire({
			title,
			html: text,
			icon: 'error',
			confirmButtonText: options?.buttonText || 'OK',
		}).then((result) => {
			if (result.isConfirmed) {
				options?.onConfirm?.();
			}
		});
	}

	confirm(title: string, text: string, onYes?: () => void, onNo?: () => void) {
		Swal.fire({
			title,
			text,
			icon: 'warning',
			showCancelButton: true,
			confirmButtonText: 'Yes',
			cancelButtonText: 'No',
		}).then(result => {
			if (result.isConfirmed) {
				onYes?.();
			} else {
				onNo?.();
			}
		});
	}

	prompt(title: string, text: string, placeholder: string, onConfirm: (value: string) => void, onCancel?: () => void) {
		Swal.fire({
			title,
			text,
			input: 'text',
			inputPlaceholder: placeholder,
			showCancelButton: true,
			confirmButtonText: 'Submit',
			cancelButtonText: 'Cancel',
		}).then((result) => {
			if (result.isConfirmed && result.value) {
				onConfirm?.(result.value);
			} else {
				onCancel?.();
			}
		});
	}
}
