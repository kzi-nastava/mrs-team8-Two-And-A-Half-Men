import { inject, Injectable } from '@angular/core';
import {
	HttpErrorResponse,
	HttpEvent,
	HttpHandler,
	HttpInterceptor,
	HttpRequest,
	HttpStatusCode
} from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { AuthTokenService } from '@core/services/auth-token.service';
import { Router } from '@angular/router';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
	tokenService = inject(AuthTokenService);
	router = inject(Router);

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		const accessToken = this.tokenService.getToken();
		if (req.headers.get('skip')) return next.handle(req);

		const authReq = accessToken
			? req.clone({
					headers: req.headers.set('Authorization', `Bearer ${accessToken}`),
				})
			: req;

		return next.handle(authReq).pipe(
			catchError((error: HttpErrorResponse) => {
				if (error.status === HttpStatusCode.Unauthorized) {
					this.router.navigate(['error', 'unauthorized'], { queryParams: { msg: 'Your session has expired. Please log in again.' } }).then();
				}
				return throwError(() => error);
			}),
		);
	}
}
