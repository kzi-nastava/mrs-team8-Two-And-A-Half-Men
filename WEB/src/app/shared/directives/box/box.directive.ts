import { Directive, HostBinding } from '@angular/core';

@Directive({
	selector: '[appBox]',
	standalone: true,
})
export class BoxDirective {
	// Apply the base class
	@HostBinding('class.app-box')
	baseClass = true;

}
