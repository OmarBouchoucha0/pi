import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
) => {
  const authReq = req.clone({
    withCredentials: true,
  });
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/refresh')) {
        return inject(AuthService)
          .refreshToken()
          .pipe(
            switchMap(() => next(req)),
            catchError((refreshError) => {
              inject(AuthService).logout();
              return throwError(() => refreshError);
            }),
          );
      }
      return throwError(() => error);
    }),
  );
};
