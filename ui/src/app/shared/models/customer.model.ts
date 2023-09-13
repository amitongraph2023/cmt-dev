import { CustomerDetails } from '@models/customer-details.model';

export class Customer {

  public customerId: number;
  public username: string;
  public firstName: string;
  public lastName: string;
  public isEmailGlobalOpt: boolean;
  public isSmsGlobalOpt: boolean;
  public isMobilePushOpt: boolean;

  public static fromCustomerDetails(customerDetails: CustomerDetails): Customer {
    return new Customer(
      customerDetails.customerId
      , customerDetails.username
      , customerDetails.firstName
      , customerDetails.lastName
      , customerDetails.isEmailGlobalOpt
      , customerDetails.isSmsGlobalOpt
      , customerDetails.isMobilePushOpt
    );
  }

  constructor (customerId, username, firstName, lastName, isEmailGlobalOpt, isSmsGlobalOpt, isMobilePushOpt) {
    this.customerId = customerId;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.isEmailGlobalOpt = isEmailGlobalOpt;
    this.isSmsGlobalOpt = isSmsGlobalOpt;
    this.isMobilePushOpt = isMobilePushOpt;
  }

}
