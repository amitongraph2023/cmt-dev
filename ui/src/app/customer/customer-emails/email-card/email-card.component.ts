import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import * as _ from 'lodash';

// Models
import { CustomerEmail } from '@models/customer-email.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerEmailService } from '@services/customer-email.service';
import { CustomerService } from '@services/customer.service';

// Enums
import { CustomerEmailType } from '@enums/cusotmer-email-type.enum';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerDetails } from '@models/customer-details.model';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-email-card',
  templateUrl: './email-card.component.html',
  styleUrls: ['./email-card.component.scss']
})
export class EmailCardComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;
  @Input() email: CustomerEmail;

  @Output() updatedEmail = new EventEmitter<boolean>();

  public edit: boolean;
  public editEmailBackup: CustomerEmail;
  public emailTypes = CustomerEmailType;
  public successMessage: string;

  public canUpdate = false;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerEmailService: CustomerEmailService
  ) {}

  ngOnInit(): void {
    this._authService.checkComponentPrivilege('cust.email.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
  }

  /**
   * Click event handler to cancel updating email and revert changes
   */
  public cancelUpdateCustomerEmail(): void {
    this.edit = false;
    this.email = _.cloneDeep(this.editEmailBackup);
  }

  /**
   * Click event handler to set the value of the editEmailBackup and set edit=true
   */
  public clickEditEmail(): void {
    this.edit = true;
    this.editEmailBackup = _.cloneDeep(this.email);
  }

  /**
   * Make the customer email the default email
   */
  public makeDefaultCustomerEmail(): void {
    this._customerEmailService.setDefaultEmailById(
      this.customerDetails.customerId
      , this.email.id
    ).subscribe( () => {}, (error) => console.log(error), () => {
      this.updatedEmail.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.email.id).modal('show');
  }

  /**
   * Update the customer email after inline editing
   */
  public updateCustomerEmail(): void {
    this.edit = false;
    this.editEmailBackup = null;
    this._customerEmailService.updateCustomerAddress(
      this.customerDetails.customerId
      , this.email.id
      , this.email
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.openSuccessModal('Email address updated successfully');
    });
  }

}
