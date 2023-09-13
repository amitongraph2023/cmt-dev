import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { isNullOrUndefined } from 'util';

// Services
import { ErrorPropagatorService } from '@services/error-propagator.service';
import { LoadingPropagatorService } from '@services/loading-propagator.service';

@Injectable()
export class ResponseHandlingInterceptor implements HttpInterceptor {

  constructor(private _errorPropagatorService: ErrorPropagatorService,
              private _loadingPropagatorService: LoadingPropagatorService,
              private _router: Router) {}

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    this._loadingPropagatorService.setIsLoading(true);
    let method = req.method;

    // Intercept the communication
    return next.handle(req)
      .map((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          this._loadingPropagatorService.setIsLoading(false);
        }
        return event;
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          if (error.status === 200 && !isNullOrUndefined(error.error) && error.error.error.message === 'Unexpected token < in JSON at position 0') {
            if (error.url.split('/').length >= 4 && error.url.split('/')[3] === '' && (req.url.indexOf('logout') > -1 || req.url.indexOf('status') > -1)) {
              sessionStorage.clear();
              this._router.navigate(['/login']);

              this._loadingPropagatorService.setIsLoading(false);
              return Observable.of('');
            } else {
              this._errorPropagatorService.setError('No valid session! Redirecting to login.');
              this.redirectToLogin();
              return Observable.throw('No valid session! Redirecting to login.');
            }
          } else {
            if (error.status === 401 && error.url.indexOf('/authentication') > -1 && method === 'DELETE') {
              return Observable.of('');
            } else if (error.status === 401 && error.url.indexOf('/authentication') > -1) {
              this._errorPropagatorService.setError('Username/password is incorrect or user does not have permission(s).');
            } else if (error.status === 401) {
              this._errorPropagatorService.setError('No valid session! Redirecting to login.');
              this.redirectToLogin();
              return Observable.throw('No valid session! Redirecting to login.');
            } else if (error.status === 500 || isNullOrUndefined(error.error)) {
              this._errorPropagatorService.setError('Unknown error');
            } else {
              this._errorPropagatorService.setError(error.error);
            }
            return Observable.throw(error);
          }
        }
      });
  }

  private redirectToLogin(): void {
    if (this._router.url.indexOf('/login') === -1) {
      sessionStorage.clear();
      this._router.navigate(['/login', {r: this._router.url}]);
    }
  }
}
