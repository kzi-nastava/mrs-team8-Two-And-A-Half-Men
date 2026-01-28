import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonDirective } from '@shared/directives/button/button.directive';

@Component({
	selector: 'app-all-users-page',
	imports: [RouterLink, ButtonDirective],
	templateUrl: './all-users-page.component.html',
	styleUrl: './all-users-page.component.css',
})
export class AllUsersPageComponent {}
