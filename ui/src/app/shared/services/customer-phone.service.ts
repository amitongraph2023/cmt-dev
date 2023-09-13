import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';

// Models
import { CustomerPhone } from '@models/customer-phone.model';

// Services
import { HttpService } from '@services/http.service';

@Injectable()
export class CustomerPhoneService {

  constructor(private _httpService: HttpService) {}

  /**
   * Add a customer phone number
   *
   * @param {number} customerId The id of the customer
   * @param {CustomerPhone} customerPhone  The new CustomerPhone
   * @returns {Observable<{}>} Observable of empty set
   */
  public addCustomerPhone(customerId: number, customerPhone: CustomerPhone): Observable<CustomerPhone> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.PHONE.BASE, customerPhone, {customerId: customerId});
  }

  /**
   * Delete customer phone from the server by customer id and phone id
   *
   * @param {number} customerId The id of the customer
   * @param {number} phoneId The id of the customer phone
   * @returns {Observable<{}>} Observable of empty set
   */
  public deleteCustomerPhoneById(customerId: number, phoneId: number): Observable<{}> {
    return this._httpService.doDelete(CONSTANTS.API_ROUTES.CUSTOMER.PHONE.BY_ID, {customerId: customerId, phoneId: phoneId});
  }

  /**
   * Get customer phone by Id
   *
   * @param {number} customerId The id of the customer
   * @param {number} phoneId The id of the gift card
   * @returns {Observable<CustomerPhone>} The CustomerPhone of that Id
   */
  public getCustomerPhoneById(customerId: number, phoneId: number): Observable<CustomerPhone> {
    return this._httpService.doGet<CustomerPhone>(CONSTANTS.API_ROUTES.CUSTOMER.PHONE.BY_ID,
      {customerId: customerId, phoneId: phoneId});
  }

  /**
   * Retrieves customer phone numbers from the server by customerId
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerPhone>} Observable with CustomerPhone
   */
  public getCustomerPhonesByCustomerId(customerId: number): Observable<CustomerPhone[]> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.PHONE.BASE, {customerId: customerId});
  }

  /**
   * Update customer phone by id
   *
   * @param {number} customerId The id of the customer
   * @param {number} phoneId The id of the customer phone
   * @param {CustomerPhone} customerPhone The updated giftCard object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateCustomerPhoneById(customerId: number, phoneId: number, customerPhone: CustomerPhone): Observable<{}> {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.PHONE.BY_ID, customerPhone,
      {customerId: customerId, phoneId: phoneId});
  }

  /**
   * Set default flag on phone by customer id and phone id
   *
   * @param {number} customerId The id of the customer
   * @param {number} phoneId The id of the customer phone
   * @returns {Observable<{}>} Observable of empty set
   */
  public setDefaultCustomerPhoneById(customerId: number, phoneId: number): Observable<{}> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.PHONE.SET_DEFAULT_BY_ID, {}, {customerId: customerId, phoneId: phoneId});
  }
}
