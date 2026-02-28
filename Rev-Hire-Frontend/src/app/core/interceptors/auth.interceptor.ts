import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  console.log('Interceptor running for URL:', req.url);
  const platformId = inject(PLATFORM_ID);
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('token');
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next(cloned);
    }
  }
  return next(req);
};
