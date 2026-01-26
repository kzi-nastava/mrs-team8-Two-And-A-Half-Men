import {inject, Injectable} from '@angular/core';
import {
	HttpErrorResponse,
	HttpEvent,
	HttpHandler,
	HttpInterceptor,
	HttpRequest,
	HttpStatusCode,
} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {AuthService} from '@core/services/auth-service.service';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
	intercept(
		req: HttpRequest<any>,
		next: HttpHandler
	): Observable<HttpEvent<any>> {
		const authService = inject(AuthService)
		const accessToken = authService.getToken()
		if (req.headers.get('skip')) return next.handle(req);

		const authReq = accessToken
			? req.clone({
				headers: req.headers.set(
					'Authorization',
					`Bearer ${accessToken}`
				)
			})
			: req;

		return next.handle(authReq).pipe(
			catchError((error: HttpErrorResponse) => {
				if (error.status === HttpStatusCode.Unauthorized) {
					authService.handleUnauthorized(error);
				}
				return throwError(() => error);
			})
		);
	}
}
