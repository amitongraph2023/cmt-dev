import { EventEmitter, Injectable } from '@angular/core';

// Enums
import { AccountStatusType } from '@enums/account-status-type.enum';

// Services
import { CustomerService } from '@services/customer.service';

// Models
import { CustomerDetails } from '@models/customer-details.model';

import { isNullOrUndefined } from 'util';
import { ActivatedRoute, Router } from '@angular/router';


@Injectable()
export class CustomerPropagatorService {

  public customerId$: EventEmitter<number> = new EventEmitter(true);
  public customer$: EventEmitter<CustomerDetails> = new EventEmitter(true);
  private customerId: number;
  private customer: CustomerDetails;

  public getCustomerId(): number {
    return this.customerId;
  }

  public getCustomer(): CustomerDetails {
    return this.customer;
  }

  constructor(private _customerService: CustomerService
    ,  private _route: ActivatedRoute
    , private _router: Router) { }

  /**
   * Sets the customerId
   *
   * @param {number} customerId The customer's id
   */
  public setCustomerId(customerId: number): void {
    this.customerId$.emit(customerId);
    this.customerId = customerId;

    this.setCustomer();

    this.redirectToHome();
  }

  /**
   * Gets and sets the customer
   */
  public setCustomer(): void {
    if (isNullOrUndefined(this.customerId)) {
      this.customer = null;
      this.customer$.emit(null);
    } else {
      this._customerService.getCustomerDetails(this.customerId).subscribe(
        (customerDetails: CustomerDetails) => {
          this.customer = customerDetails;
          if (isNullOrUndefined(this.customer.status)) {
            this.customer.status = AccountStatusType.ACTIVE;
          }
          this.customer$.emit(customerDetails);
        },
        (error) => console.log(error));
    }

  }

  public redirectToHome(): void {
    this._router.navigate(['home']);
  }
}
