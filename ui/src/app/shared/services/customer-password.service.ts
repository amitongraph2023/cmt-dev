import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { CONSTANTS } from '../../constants';
import { Observable } from 'rxjs/Observable';
import { NewPassword } from '@models/new-password.model';
import { CustomerPasswordWotd } from '@models/customer-password-wotd.model';
import { CustomerPassword } from '@models/customer-password.model';

@Injectable()
export class CustomerPasswordService {

  constructor(private _httpService: HttpService) {
  }

  /**
   * Admin set password
   *
   * @param {number} customerId The id of the customer
   * @param {NewPassword} newPassword The new password
   * @returns {Observable<{String}>} Observable of string with new generated password
   */
  public adminSetPassword(customerId: number, newPassword: NewPassword): Observable<{}> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.PASSWORD.ADMIN.SET, newPassword, { customerId: customerId });
  }

  /**
   * Reset password and return temporary password
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<{String}>} Observable of string with new generated password
   */
  public resetPassword(customerId: number): Observable<CustomerPassword> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.PASSWORD.RESET, null, {customerId: customerId});
  }

  /**
   * Reset password and email customer new generated password
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<{}>} Observable of empty set
   */
  public sendResetPassword(customerId: number): Observable<CustomerPassword> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.PASSWORD.SEND_RESET, null, {customerId: customerId});
  }

}
