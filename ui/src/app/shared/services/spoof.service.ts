import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { CONSTANTS } from '../../constants';
// Models
import { SpoofButton } from '@models/spoof-button.model';
import { SpoofResponse } from '@models/spoof-response.model';
// Services
import { HttpService } from '@services/http.service';

@Injectable()
export class SpoofService {

  public spoofButtons: SpoofButton[];

  private spoofStatusState = new BehaviorSubject(null);
  private spoofNameSource = new BehaviorSubject(null);

  constructor(private _httpService: HttpService) {}

  /**
   * Observable spoofStatus to allow various components to track whether actively spoofing
   */
  public spoofStatus = this.spoofStatusState.asObservable();

  /**
   * Holds the ssoToken for the current spoofing session
   * stored to logout of session once done
   * Because this is only present during spoof, use for redirecting until exit chosen
   */
  public ssoToken: string;

  /**
   * Observable spoofUnit to allow various components to track the current spoof unit selected
   */
  public spoofName = this.spoofNameSource.asObservable();



  /**
   * Change the spoof button, used to pass from NavLink to the Spoof Component
   *
   * @param {string} newUnit The new unit to spoof, based on the button clicked in the NavLinks
   */
  public changeSpoofName(newName: string) {
    this.spoofNameSource.next(newName);
  }

  /**
   * change the spoofStatus state based on value passed
   * @param {boolean} state
   */
  public changeSpoofStatus(state: boolean) {
    this.spoofStatusState.next(state);
  }

  /**
   * Get the spoof button values for all dynamically typed values
   *
   * @returns {Observable<SpoofButton[]>} An observable array of the spoof buttons
   */
  public getSpoofButtons (nonMyPanera: boolean): Observable<SpoofButton[]> {
    let queryStringMap: any = null;
    if (nonMyPanera) {
      queryStringMap = { 'nonMyPanera': true };
    }
    return this._httpService.doGet(CONSTANTS.API_ROUTES.SPOOF.BUTTONS, null, queryStringMap);
  }

  /**
   * Sends the user and unit to spoof, sets the cookie
   *
   * @param {number} customerId The customerId to spoof
   * @param {string} unit The unit who's site is being spoofed (i.e. catering)
   * @returns {Observable<SpoofResponse>} The response
   */
  public getSpoofSession ( customerId: number, unit: string ): Observable<SpoofResponse> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.SPOOF.LOGIN, null, { customerId: customerId, unit: unit });
  }

  /**
   * spoofLogout to clear the session of the current spoofed session
   *
   * @param {string} ssoToken The existing authentication token
   */
  public spoofLogout ( ssoToken: string, customerId: number, unit: string ): Observable<boolean> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.SPOOF.LOGOUT, null, {ssoToken: ssoToken, customerId: customerId, unit: unit});
  }

  public getSpoofUnitByName (name: string): string {
    return this.spoofButtons.filter(u => u.name === name)[0].unit;
  }

}

