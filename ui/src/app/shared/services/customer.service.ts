import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';

// Models
import { Customer } from '@models/customer.model';
import { CustomerDetails } from '@models/customer-details.model';

// Services
import { HttpService } from '@services/http.service';

// Enums
import { AccountActionType } from '@enums/account-action.enum';
import { CustomerInfoType } from '@enums/customer-info-type.enum';

@Injectable()
export class CustomerService {

  constructor(private _httpService: HttpService ) {}

  /**
   * Retrieves customer from the server by id
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<Customer>} Observable with Customer
   */
  public getCustomer(customerId: number): Observable<Customer> {
    if (customerId != null) {
      return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.BY_ID, {customerId: customerId});
    } else {
      return null;
    }
  }

  /**
   * Retrieves customer details from the server by id
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerDetails>} Observable with Customer
   */
  public getCustomerDetails(customerId: number): Observable<CustomerDetails> {
    return this._httpService.doGet<CustomerDetails>(CONSTANTS.API_ROUTES.CUSTOMER.BY_TYPE,
      {customerId: customerId, type: CustomerInfoType[CustomerInfoType.DETAILS].toLowerCase()});
  }

  public updateAccountByCustomerIdAndActionType(customerId: number, action: AccountActionType): Observable<boolean> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.STATUS, null,
      {customerId: customerId}, {action: AccountActionType[action]});
  }

  /**
   * Saves an updated customer
   *
   * @param {number} customerId The id of the updated customer
   * @param {Customer} customer The updated customer
   * @returns {Observable<Customer>} The saved customer
   */
  public updateCustomer(customerId: number, customer: Customer): Observable<Customer> {
    return this._httpService.doPut<Customer>(CONSTANTS.API_ROUTES.CUSTOMER.BY_ID, customer,
      {customerId: customerId});
  }

  /**
   * Update customer status from the server by id
   *
   * @param {number} customerId The id of the customer
   * @param {StatusType} action The status type
   * @returns {Observable<Customer>} Observable with Customer
   */
  public updateCustomerStatus(customerId: number, action: AccountActionType): Observable<string> {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.BY_TYPE, action,
      {customerId: customerId, type: CustomerInfoType[CustomerInfoType.STATUS].toLowerCase()});
  }

}
