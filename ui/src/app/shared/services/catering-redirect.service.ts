import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';
import { CONSTANTS } from '../../constants';
import { CateringRedirect } from '@models/catering-redirect.model';

@Injectable()
export class CateringRedirectService {

  constructor(private _httpService: HttpService) {}

  /**
   * Retrieves the CateringRedirect status and url
   *
   * @returns {Observable<CateringRedirect>} Observable with Redirect
   */
  public getRedirectStatus(): Observable<CateringRedirect> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CATERING_REDIRECT.BASE);
  }
}
