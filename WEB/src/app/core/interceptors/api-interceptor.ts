import { Injectable } from '@angular/core';
import {
	HttpRequest,
	HttpHandler,
	HttpEvent,
	HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
	intercept(
		req: HttpRequest<any>,
		next: HttpHandler
	): Observable<HttpEvent<any>> {
		const accessToken: any = sessionStorage.getItem('authTokenUser') || localStorage.getItem('authTokenUser');
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
