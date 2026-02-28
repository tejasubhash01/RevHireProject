import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (isPlatformBrowser(this.platformId)) {
      const expectedRole = route.data['role'];
      const userRole = localStorage.getItem('role');
      if (userRole === expectedRole) {
        return true;
      }
    }
    this.router.navigate(['/']);
    return false;
  }
}
