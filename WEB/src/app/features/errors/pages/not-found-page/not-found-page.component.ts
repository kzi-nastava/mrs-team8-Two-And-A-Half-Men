import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
	selector: 'app-not-found-page',
	imports: [RouterLink],
	templateUrl: './not-found-page.component.html',
	styleUrl: './not-found-page.component.css',
})
export class NotFoundPageComponent implements OnInit {
	private activeRoute: ActivatedRoute = inject(ActivatedRoute);
	message = signal<string>('The page you are looking for does not exist.');
	ngOnInit() {
		this.activeRoute.queryParams.subscribe((params) => {
			const msg: string | null = params['msg'];
			if (msg) {
				this.message.set(msg);
			}
		});
	}
}
