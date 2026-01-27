import { Directive, TemplateRef, input } from '@angular/core';

@Directive({
	selector: '[appTabIcon]',
	standalone: true
})
export class TabIconDirective {
	// The tab ID this icon belongs to
	appTabIcon = input.required<string>({ alias: 'appTabIcon' });

	// Convenience getter
	get forTab(): string {
		return this.appTabIcon();
	}

	constructor(public templateRef: TemplateRef<any>) {}
}
