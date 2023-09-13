import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';

// Models
import { CustomerAddress } from '@models/customer-address.model';

// Services
import { HttpService } from '@services/http.service';

@Injectable()
export class CustomerAddressService {

  constructor(private _httpService: HttpService) {}

  /**
   * Adds a customer address
   *
   * @param {CustomerAddress} customerAddress The new CustomerAddress
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerAddress>} The response
   */
  public addCustomerAddress(customerId: number, customerAddress: CustomerAddress): Observable<CustomerAddress> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.ADDRESS.BASE, customerAddress,
      { customerId: customerId });
  }

  /**
   * Retrieves addresses from the server by customerId
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerAddress>} Observable with Address
   */
  public getAddressesByCustomerId(customerId: number): Observable<CustomerAddress[]> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.ADDRESS.BASE, {customerId: customerId});
  }

  /**
   * Saves an updated customer address
   *
   * @param {number} customerId The id of the customer
   * @param {number} addressId The updated id of the address
   * @param {CustomerAddress} customerAddress The updated customer address
   * @returns {Observable<CustomerAddress>} The saved customer
   */
  public updateCustomerAddress(customerId: number, addressId: number, customerAddress: CustomerAddress): Observable<CustomerAddress> {
    return this._httpService.doPut<CustomerAddress>(CONSTANTS.API_ROUTES.CUSTOMER.ADDRESS.BY_ID,
      customerAddress, {customerId: customerId, addressId: addressId});
  }

  /**
   * Retrieves customer address from the server by customer id and address id
   *
   * @param {number} customerId The id of the customer
   * @param {number} addressId The updated id of the address
   * @returns {Observable<CustomerAddress>} Observable with CustomerAddress
   */
  public getCustomerAddressById(customerId: number, addressId: number): Observable<CustomerAddress> {
    return this._httpService.doGet<CustomerAddress>(CONSTANTS.API_ROUTES.CUSTOMER.ADDRESS.BY_ID,
      {customerId: customerId, addressId: addressId});
  }

  /**
   * Delete customer address from the server by customer id and address id
   *
   * @param {number} customerId The id of the customer
   * @param {number} addressId The updated id of the address
   * @returns {Observable<{}>} Observable with CustomerAddress
   */
  public deleteCustomerAddressById(customerId: number, addressId: number): Observable<{}> {
    return this._httpService.doDelete(CONSTANTS.API_ROUTES.CUSTOMER.ADDRESS.BY_ID, {customerId: customerId, addressId: addressId});
  }

}
