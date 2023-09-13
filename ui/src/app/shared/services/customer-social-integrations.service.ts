import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { HttpService } from '@services/http.service';
import { SocialIntegrationsModel } from '@models/social-integrations.model';
import { CONSTANTS } from '../../constants';

@Injectable()
export class CustomerSocialIntegrationsService {

  constructor(private _httpService: HttpService) {}

   /**
   * Retrieves customer social integrations from the server by customerId
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<SocialIntegrationsModel>} Observable with Social Integrations
   */
  public getCustomerSocialInteractions(customerId: number): Observable<SocialIntegrationsModel> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.SOCIAL_INTEGRATIONS.BASE, {customerId: customerId});
  }
}
