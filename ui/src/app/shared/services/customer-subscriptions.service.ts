import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';

// Models
import { CustomerSubscriptions } from '@models/customer-subscriptions.model';

// Services
import { HttpService } from '@services/http.service';

@Injectable()
export class CustomerSubscriptionsService {

  constructor(private _httpService: HttpService) {}

  /**
   * Retrieves customer subscriptions from the server by customerId
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerSubscriptions>} Observable with CustomerSubscriptions
   */
  public getCustomerSubscriptions(customerId: number): Observable<CustomerSubscriptions> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.SUBSCRIPTIONS.BASE, {customerId: customerId});
  }

  /**
   * Update customer subscriptions
   *
   * @param {number} customerId The id of the customer
   * @param {CustomerSubscriptions} customerSubscriptions The updated customerSubscriptions object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateCustomerSubscriptions(customerId: number, customerSubscriptions: CustomerSubscriptions): Observable<{}> {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.SUBSCRIPTIONS.BASE, customerSubscriptions, {customerId: customerId});
  }

  /**
   * Unsubscribe by email token
   *
   * @param {string} emailToken The id of the customer
   * @returns {Observable<{}>} Observable of empty set
   */
  public unsubscribeByEmailToken(emailToken: string): Observable<CustomerSubscriptions> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.SUBSCRIPTIONS.UNSUBSCRIBE_BY_EMAIL_TOKEN, emailToken);
  }

}
