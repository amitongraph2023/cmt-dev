import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { ResponseHandlingInterceptor } from './response-handling-interceptor';

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: ResponseHandlingInterceptor, multi: true}
];
