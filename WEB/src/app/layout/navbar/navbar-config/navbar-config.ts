import { Component, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar-compnent';
import  { NavbarButton , NavbarSettings } from '../models/navbar-models';
import { AuthService } from 'src/app/core/services/auth.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

@Component({
  selector: 'app-navbar-config',
  imports: [NavbarComponent, CommonModule],
  templateUrl: './navbar-config.html',
  styleUrl: './navbar-config.css',
})
export class NavbarConfig {
  private authService = inject(AuthService);
  navbarConfig: NavbarSettings = {
    isSvgLogo: true,
    logoUrl: `  <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
        <path d="M18 16.5L20 14M20 14L22 11.5M20 14L18 11.5M20 14L22 16.5"
              stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <rect x="2" y="6" width="16" height="12" rx="2" stroke="white" stroke-width="2"/>
        <path d="M2 10H18M6 14H6.01M10 14H10.01"
              stroke="white" stroke-width="2" stroke-linecap="round"/>
      </svg>`,
    logoText: 'Taxi Taxi',
    backgroundColor: '#2d7a4f',
    textColor: '#ffffff',
    logoRoute: '/',
    buttons: []
  };

  private GetUnregistredButtons(): NavbarButton[] {
    return [
      {
        id: 'login',
        type: 'text',
        label: 'Login',
        position: 'right',
        route: '/login'
      },
      {
        id: 'register',
        type: 'text',
        label: 'Register',
        position: 'right',
        route: '/register'
      }
    ];
  } 
  private GetCustomerButtons(): NavbarButton[] {
    return [
      { 
        id: 'logout',
        type: 'text',
        label: 'Logout',
        position: 'right',
        onClick: () => { this.authService.logout(); },
        route: '/home'
      },
      {
        id: 'rides',
        type: 'text',
        label: 'Active Rides',
        position: 'right',
        route: '/rides/booked'
      },
      {
        id: 'profile',
        type: 'icon',
        icon: this.authService.user()?.imgSrc || 'assets/default-profile.png',
        label: 'Profile',
        position: 'right',
        route: '/profile'
      }
    ];  
  }
  private GetDriverButtons(): NavbarButton[] {
    return [
      { 
        id: 'logout',
        type: 'text',
        label: 'Logout',
        position: 'right',
        onClick: () => { this.authService.logout(); },
        route: '/home'
      },
      { 
        id: 'history',
        type: 'text',
        label: 'Ride History',
        position: 'right',
        route: '/rides/history'
      },
      {
        id: 'profile',
        type: 'icon',
        icon: this.authService.user()?.imgSrc || 'assets/default-profile.png',
        label: 'Profile',
        position: 'right',
        route: '/profile'
      }
    ];
  }
  private GetAdminButtons(): NavbarButton[] {
    return [
      {
        id: 'logout',
        type: 'text',
        label: 'Logout',
        position: 'right',
        onClick: () => { this.authService.logout(); },
        route: '/home'
      },
      {
        id: 'profile',
        type: 'icon',
        icon: this.authService.user()?.imgSrc || 'assets/default-profile.png',
        label: 'Profile',
        position: 'right',
        route: '/profile'
      }
    ];
  }
  constructor() {
    effect(() => {
      if(this.authService.user() == null) {
        this.navbarConfig.buttons = this.GetUnregistredButtons();
      } else if(this.authService.user()?.role === LoggedInUserRole.CUSTOMER) {
        this.navbarConfig.buttons = this.GetCustomerButtons();
      }else if(this.authService.user()?.role === LoggedInUserRole.DRIVER) {
        this.navbarConfig.buttons = this.GetDriverButtons();
      } else if(this.authService.user()?.role === LoggedInUserRole.ADMIN) {
        this.navbarConfig.buttons = this.GetAdminButtons();
      }
      
    });
  }
  onNavbarButtonClick(buttonId: string): void {
    console.log(`Navbar button clicked: ${buttonId}`);
  }
}
