import {Directive, HostBinding} from '@angular/core';

@Directive({
	selector: '[appBox]',
	standalone: true
})
export class BoxDirective {
// Apply base classes
	@HostBinding('class.box') baseClass = true;

	// You can also apply specific styles directly
	@HostBinding('style.display') display = 'block';
	@HostBinding('style.background') background = '#3a3a3a';
	@HostBinding('style.border') border = '2px solid #1a5c3a';
	@HostBinding('style.border-radius') borderRadius = '12px';
	@HostBinding('style.padding') padding = '2rem';
  constructor() { }

}
