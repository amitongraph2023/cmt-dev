import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

import * as _ from 'lodash';

// Models
import { PayPal } from '@models/paypal.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerPaymentOptionsService } from '@services/customer-payment-options.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enums
import { PaymentOptionType } from '@enums/payment-option-type.enum';
import { CustomerDetails } from '@models/customer-details.model';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-paypal-card',
  templateUrl: './paypal-card.component.html',
  styleUrls: ['./paypal-card.component.scss']
})
export class PaypalCardComponent implements OnInit {

  @Input() customerDetails: CustomerDetails;
  @Input() payPal: PayPal;

  @Output() updatedPayPal = new EventEmitter<boolean>();

  public edit: boolean;
  public payPalBackup: PayPal;
  public successMessage: string;

  public canDelete = false;
  public canUpdate = false;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerPaymentOptionsService: CustomerPaymentOptionsService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit() {
    this._authService.checkComponentPrivilege('cust.pmt.pp.edit.delete')
      .subscribe(canDelete => this.canDelete = canDelete);
    this._authService.checkComponentPrivilege('cust.pmt.pp.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
  }

  /**
   * Click event handler to cancel updating PayPal and revert changes
   */
  public cancelUpdatePayPal(): void {
    this.edit = false;
    this.payPal = _.cloneDeep(this.payPalBackup);
  }

  /**
   * Click event handler to set the value of the payPalBackup and set edit=true
   */
  public clickEditPayPal(): void {
    this.edit = true;
    this.payPalBackup = _.cloneDeep(this.payPal);
  }

  /**
   * Confirm deleting selected PayPal with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete PayPal account');
    const modalEl = $('#deleteConfirmModal' + this.payPal.accountNumber);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete payPal after confirming in deleteConfirm modal
   */
  public deletePayPal(): void {
    $('#deleteConfirmModal' + this.payPal.accountNumber).modal('hide');

    this._customerPaymentOptionsService.deletePaymentOptionByCustomerIdAndPaymentOptionType(
      this.customerDetails.customerId
      , PaymentOptionType.PAYPAL
      , this.payPal.accountNumber
    ).subscribe(() => {
    }, (error) => console.error(error), () => {
      this.updatedPayPal.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.payPal.accountNumber).modal('show');
  }

  /**
   * Update PayPal account after editing inline
   */
  public updatePayPal(): void {
    this.edit = false;
    this.payPalBackup = null;
    this._customerPaymentOptionsService.updatePayPalByAccountNumber(
      this.customerDetails.customerId
      , this.payPal.accountNumber
      , this.payPal
    ).subscribe(() => {
    }, (error) => console.error(error), () => {
      this.openSuccessModal('PayPal account updated successfully');
    });
  }
}
