import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'app-unauthenticated-page',
	imports: [RouterLink],
	templateUrl: './unauthenticated-page.component.html',
	styleUrl: './unauthenticated-page.component.css',
})
export class UnauthenticatedPageComponent {}
