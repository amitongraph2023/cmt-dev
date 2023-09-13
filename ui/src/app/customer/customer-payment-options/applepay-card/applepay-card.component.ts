import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

import * as _ from 'lodash';

// Models
import { CustomerDetails } from '@models/customer-details.model';
import { ApplePay } from '@models/apple-pay.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerPaymentOptionsService } from '@services/customer-payment-options.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enums
import { PaymentOptionType } from '@enums/payment-option-type.enum';


// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-applepay-card',
  templateUrl: './applepay-card.component.html',
  styleUrls: ['./applepay-card.component.scss']
})
export class ApplepayCardComponent implements OnInit {

  @Input() customerDetails: CustomerDetails;
  @Input() applePay: ApplePay;

  @Output() updatedApplePay = new EventEmitter<boolean>();

  public edit: boolean;
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
    this._authService.checkComponentPrivilege('cust.pmt.ap.edit.delete')
      .subscribe(canDelete => this.canDelete = canDelete);
    this._authService.checkComponentPrivilege('cust.pmt.ap.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
  }

  /**
   * Click event handler to cancel updating ApplePay and revert changes
   */
  public cancelUpdateApplePay(): void {
    this.edit = false;
  }

  /**
   * Click event handler to set the value of the applePayBackup and set edit=true
   */
  public clickEditApplePay(): void {
    this.edit = true;
  }

  /**
   * Confirm deleting selected ApplePay with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete Apple Pay account');
    const modalEl = $('#deleteConfirmModal' + this.applePay.accountNumber);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete applePay after confirming in deleteConfirm modal
   */
  public deleteApplePay(): void {
    $('#deleteConfirmModal' + this.applePay.accountNumber).modal('hide');

    this._customerPaymentOptionsService.deletePaymentOptionByCustomerIdAndPaymentOptionType(
      this.customerDetails.customerId
      , PaymentOptionType.APPLEPAY
      , this.applePay.accountNumber
    ).subscribe(() => {
    }, (error) => console.error(error), () => {
      this.updatedApplePay.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.applePay.accountNumber).modal('show');
  }

}
