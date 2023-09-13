import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import * as _ from 'lodash';

// Models
import { CreditCard } from '@models/credit-card.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { CustomerPaymentOptionsService } from '@services/customer-payment-options.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enums
import { PaymentOptionType } from '@enums/payment-option-type.enum';
import { CustomerDetails } from '@models/customer-details.model';
import { isNullOrUndefined } from 'util';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-credit-card-card',
  templateUrl: './credit-card-card.component.html',
  styleUrls: ['./credit-card-card.component.scss']
})
export class CreditCardCardComponent implements OnInit {
  @Input() creditCard: CreditCard;
  @Input() customerDetails: CustomerDetails;

  @Output() updatedCreditCard = new EventEmitter<boolean>();

  public edit: boolean;
  public creditCardBackup: CreditCard;
  public successMessage: string;

  public canDelete = false;
  public canUpdate = false;
  public canSetDefault = false;

  public validExpiration: boolean;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerPaymentOptionsService: CustomerPaymentOptionsService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit() {
    this._authService.checkComponentPrivilege('cust.pmt.cc.edit.delete')
      .subscribe(canDelete => this.canDelete = canDelete);
    this._authService.checkComponentPrivilege('cust.pmt.cc.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
    this._authService.checkComponentPrivilege('cust.pmt.cc.set_default')
      .subscribe(canSetUpdate => this.canSetDefault = canSetUpdate);

    if (isNullOrUndefined(this.creditCard.paymentLabel)) {
      this.creditCard.paymentLabel = this.creditCard.creditCardType + '*' + this.creditCard.lastFour;
    }

    if (!isNullOrUndefined(this.creditCard.expirationDate)) {
      this.expirationValidator(this.creditCard.expirationDate);
    }


  }

  /**
   * Click event handler to cancel updating credit card and revert changes
   */
  public cancelUpdateCreditCard(): void {
    this.edit = false;
    this.creditCard = _.cloneDeep(this.creditCardBackup);
    this.expirationValidator(this.creditCard.expirationDate);
  }

  /**
   * Click event handler to set the value of the editCreditCardBackup and set edit=true
   */
  public clickEditCreditCard(): void {
    this.edit = true;
    this.creditCardBackup = _.cloneDeep(this.creditCard);
  }

  /**
   * Confirm deleting selected credit card with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete credit card');
    const modalEl = $('#deleteConfirmModal' + this.creditCard.token);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete creditCard after confirming in deleteConfirm modal
   */
  public deleteCreditCard(): void {
    $('#deleteConfirmModal' + this.creditCard.token).modal('hide');

    this._customerPaymentOptionsService.deletePaymentOptionByCustomerIdAndPaymentOptionType(
      this.customerDetails.customerId
      , PaymentOptionType.CREDITCARD
      , this.creditCard.token
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.updatedCreditCard.emit(true);
    });
  }

  /**
   * Make the credit card the default credit card
   */
  public makeDefaultCreditCard(): void {
    let defaultCreditCard: CreditCard;

    this.edit ? defaultCreditCard = this.creditCardBackup : defaultCreditCard = this.creditCard;

    defaultCreditCard.isDefault = true;
    this._customerPaymentOptionsService.updateCreditCardByToken(
      this.customerDetails.customerId
      , this.creditCard.token
      , defaultCreditCard
    ).subscribe( () => {}, (error) => console.log(error), () => {
      this.updatedCreditCard.emit(true);
    });
  }

  /**
   * Make the credit card the default subscription credit card
   */
  public makeDefaultSubscriptionCreditCard(): void {
    let defaultCreditCard: CreditCard;

    this.edit ? defaultCreditCard = this.creditCardBackup : defaultCreditCard = this.creditCard;

    defaultCreditCard.isDefaultSubscription = true;
    this._customerPaymentOptionsService.updateCreditCardByToken(
      this.customerDetails.customerId
      , this.creditCard.token
      , defaultCreditCard
    ).subscribe( () => {}, (error) => console.log(error), () => {
      this.updatedCreditCard.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.creditCard.token).modal('show');
  }

  /**
   * Update the credit card after editing in the editCreditCardModal
   */
  public updateCreditCard(): void {
    this.edit = false;
    this.creditCardBackup = null;
    this._customerPaymentOptionsService.updateCreditCardByToken(
      this.customerDetails.customerId
      , this.creditCard.token
      , this.creditCard
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.openSuccessModal('Credit card updated successfully');
    });
  }

  public expirationValidator(value: string) : void {
    let valid = false;
    const curM = new Date().getMonth() + 1;
    const curY = new Date().getFullYear()%100;

    if(!isNullOrUndefined(value)) {
      let testM = parseInt(value.substring(0, 2));
      let testY = parseInt(value.substring(2));
      if (testY > curY || (testY == curY && testM >= curM)) {
        valid = true;
      }
    }

    this.validExpiration = valid;
  }

}
