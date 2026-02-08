import { Component, input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NavbarSettings, NavbarButton } from '../models/navbar-models';
import { ButtonDirective } from "@shared/directives/button/button.directive";
import { SafeHtml, DomSanitizer } from '@angular/platform-browser';


@Component({
  selector: 'app-navbar-renderer',
  standalone: true,
  imports: [CommonModule, ButtonDirective],
  templateUrl: './navbar-renderer.html',
  styleUrls: ['./navbar-renderer.css']
})
export class NavbarRenderer {
  config = input.required<NavbarSettings>()
  @Output() buttonClick = new EventEmitter<string>();
  private router = inject(Router);
  private sanitizer = inject(DomSanitizer);
  handleButtonClick(button: NavbarButton, event: Event): void {
    event.preventDefault();
    console.log(`Button clicked: ${button.id}`);
    console.log(button.route)
    this.buttonClick.emit(button.id);
    if (button.onClick) {
      console.log('Executing onClick for button:', button.id);
      button.onClick();
    }
    if (button.route) {
      console.log('Navigating to route for button:', button.id, button.route);
      this.router.navigate([button.route]);
    }
  }
  getLeftButtons(): NavbarButton[] {
    return this.config().buttons.filter(btn => btn.position === 'left' || !btn.position);
  }
  getRightButtons(): NavbarButton[] {
    return this.config().buttons.filter(btn => btn.position === 'right');
  }
  getStyles() {
    return {
      'background-color': this.config().backgroundColor || '#2d7a4f',
      'color': this.config().textColor || '#ffffff'
    };
  }

  getSafeLogo(): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(this.config().logoUrl || '');
  }
  LogoClick(): void {
    console.log('Logo clicked', this.config().logoRoute);
    if (this.config().logoRoute) {
      this.router.navigate([this.config().logoRoute]);
    }
  }
}
