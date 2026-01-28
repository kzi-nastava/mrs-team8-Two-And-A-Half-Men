import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { trigger, transition, style, animate, query, group } from '@angular/animations';
import { NavbarComponent } from './layout/navbar/navbar';
import { routeAnimations } from '@core/animations/route-animation';
import { RatingFormComponent } from '@shared/components/forms/rating-form/rating-form.component';

@Component({
	selector: 'app-root',
	imports: [RouterOutlet, NavbarComponent],
	templateUrl: './app.html',
	styleUrl: './app.css',
	animations: [routeAnimations],
})
export class App {
	protected readonly title = signal('WEB');

	prepareRoute(outlet: RouterOutlet) {
		return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
	}
}
