import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../Services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (req.url.includes('/api/')) {
    const token = authService.getToken();

    console.log('🔍 Interceptor Debug:', {
      url: req.url,
      method: req.method,
      hasToken: !!token,
      token: token ? token : 'NO TOKEN',
      headers: req.headers.keys()
    });

    if (token) {
      const clonedRequest = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });

      console.log('✅ Token agregado al header Authorization');
      return next(clonedRequest);
    } else {
      console.warn('⚠️ No hay token disponible para agregar');
    }
  }

  return next(req);
};
