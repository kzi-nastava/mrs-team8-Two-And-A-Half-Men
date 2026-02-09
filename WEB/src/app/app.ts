import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { routeAnimations } from '@core/animations/route-animation';
import { NavbarComponent } from "./layout/navbar/component/navbar.component";

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
