import {inject, Injectable} from '@angular/core';
import {
	HttpRequest,
	HttpHandler,
	HttpEvent,
	HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
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

		if (accessToken) {
			const cloned = req.clone({
				headers: req.headers.set('Authorization', "Bearer " + accessToken)
			});
			return next.handle(cloned);
		} else {
			return next.handle(req);
		}
	}
}
