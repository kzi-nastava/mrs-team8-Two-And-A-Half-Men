import { Directive, HostBinding, HostListener } from '@angular/core';

@Directive({
	selector: '[appButton]',
})
export class ButtonDirective {
	// Base class (always on)
	@HostBinding('class.app-button')
	baseClass = true;

	// Hover class (toggled)
	@HostBinding('class.app-button--hover')
	isHovering = false;

	@HostListener('mouseenter')
	onEnter() {
		this.isHovering = true;
	}

	@HostListener('mouseleave')
	onLeave() {
		this.isHovering = false;
	}
}
