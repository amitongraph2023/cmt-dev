import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';


import { CONSTANTS } from '../../constants';

// Models
import { CustomerEmail } from '@models/customer-email.model';

// Services
import { HttpService } from '@services/http.service';
import {EmailVerification} from "@models/email-verification.model";

@Injectable()
export class CustomerEmailService {

  constructor(private _httpService: HttpService) {}

  /**
   *
   */
  public addEmailAddressByCustomerId(customerId: number, newEmail: CustomerEmail): Observable<CustomerEmail> {
    return this._httpService.doPost<CustomerEmail>(CONSTANTS.API_ROUTES.CUSTOMER.EMAIL.BASE,
      newEmail, {customerId: customerId});
  }

  /**
   * Retrieves customer email from the server by customerId
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerEmail>} Observable with Address
   */
  public getEmailsByCustomerId(customerId: number): Observable<CustomerEmail[]> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.EMAIL.BASE, {customerId: customerId});
  }

  /**
   * Retrieves customer email from the server by customer id and email id
   *
   * @param {number} customerId The id of the customer
   * @param {number} emailId The updated id of the address
   * @returns {Observable<CustomerEmail>} Observable with CustomerEmail
   */
  public getCustomerEmailById(customerId: number, emailId: number): Observable<CustomerEmail> {
    return this._httpService.doGet<CustomerEmail>(CONSTANTS.API_ROUTES.CUSTOMER.EMAIL.BY_ID,
      {customerId: customerId, emailId: emailId});
  }

  /**
   * Set default flag on email by customer id and address id
   *
   * @param {number} customerId The id of the customer
   * @param {number} addressId The updated id of the address
   * @returns {Observable<{}>} Observable with CustomerAddress
   */
  public setDefaultEmailById(customerId: number, addressId: number): Observable<{}> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.EMAIL.SET_DEFAULT_BY_ID, {customerId: customerId, addressId: addressId});
  }

  /**
   * Saves an updated customer email
   *
   * @param {number} customerId The id of the customer
   * @param {number} emailId The updated id of the address
   * @param {CustomerEmail} customerEmail The updated customer email
   * @returns {Observable<CustomerEmail>} The saved CustomerEmail
   */
  public updateCustomerAddress(customerId: number, emailId: number, customerEmail: CustomerEmail): Observable<CustomerEmail> {
    return this._httpService.doPut<CustomerEmail>(CONSTANTS.API_ROUTES.CUSTOMER.EMAIL.BY_ID,
      customerEmail, {customerId: customerId, emailId: emailId});
  }

  public resendVerficationEmail(customerId: number, emailAddress: string): Observable<EmailVerification> {
    let emailVerification: EmailVerification;
    emailVerification.emailAddress = emailAddress;
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.EMAIL.RESEND_VERIFICATION, emailVerification, {customerId: customerId});
  }

}
