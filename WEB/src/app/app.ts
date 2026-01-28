import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { routeAnimations } from '@core/animations/route-animation';
import { NavbarConfig } from "./layout/navbar/navbar-config/navbar-config";

@Component({
	selector: 'app-root',
	imports: [RouterOutlet, NavbarConfig],
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
