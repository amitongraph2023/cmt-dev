import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';


import { CONSTANTS } from '../../constants';

// Models
import { CustomerMfa } from '@models/customer-mfa.model';

// Services
import { HttpService } from '@services/http.service';

@Injectable()
export class CustomerMfaService {

  constructor(private _httpService: HttpService) {}


  /**
   * Retrieves customer mfas from the server by customerId
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerEmail>} Observable with Mfas
   */
  public getMfasByCustomerId(customerId: number): Observable<CustomerMfa[]> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.MFA.BASE, {customerId: customerId});
  }


  /**
   * Remove customer Sms Mfa from the server by customer id
   *
   * @param {number} customerId The id of the customer
   */
  public removeCustomerSmsMfaByCustomerId(customerId: number): Observable<{}> {
    return this._httpService.doDelete(CONSTANTS.API_ROUTES.CUSTOMER.MFA.SMS, {customerId: customerId});
  }




}
