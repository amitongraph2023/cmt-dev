import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

import * as _ from 'lodash';

// Models
import { GiftCard } from '@models/gift-card.model';

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
  selector: 'app-gift-card-card',
  templateUrl: './gift-card-card.component.html',
  styleUrls: ['./gift-card-card.component.scss']
})
export class GiftCardCardComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;
  @Input() giftCard: GiftCard;

  @Output() updatedGiftCard = new EventEmitter<boolean>();

  public edit: boolean;
  public giftCardBackup: GiftCard;
  public successMessage: string;

  public canDelete = false;
  public canUpdate = false;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerPaymentOptionsService: CustomerPaymentOptionsService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {
  }

  ngOnInit() {
    this._authService.checkComponentPrivilege('cust.pmt.gc.edit.delete')
      .subscribe(canDelete => this.canDelete = canDelete);
    this._authService.checkComponentPrivilege('cust.pmt.gc.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
  }

  /**
   * Click event handler to cancel updating gift card and revert changes
   */
  public cancelUpdateGiftCard(): void {
    this.edit = false;
    this.giftCard = _.cloneDeep(this.giftCardBackup);
  }

  /**
   * Click event handler to set the value of the giftCardBackup and set edit=true
   */
  public clickEditGiftCard(): void {
    this.edit = true;
    this.giftCardBackup = _.cloneDeep(this.giftCard);
  }

  /**
   * Confirm deleting selected gift card with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete gift card');
    const modalEl = $('#deleteConfirmModal' + this.giftCard.cardNumber);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete giftCard after confirming in deleteConfirm modal
   */
  public deleteGiftCard(): void {
    $('#deleteConfirmModal' + this.giftCard.cardNumber).modal('hide');

    this._customerPaymentOptionsService.deletePaymentOptionByCustomerIdAndPaymentOptionType(
      this.customerDetails.customerId
      , PaymentOptionType.GIFTCARD
      , this.giftCard.cardNumber
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.updatedGiftCard.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.giftCard.cardNumber).modal('show');
  }

  /**
   * Update the gift card after editing inline
   */
  public updateGiftCard(): void {
    this._customerPaymentOptionsService.updateGiftCardById(
      this.customerDetails.customerId
      , this.giftCardBackup.cardNumber
      , this.giftCard
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.edit = false;
      this.giftCardBackup = null;
      this.openSuccessModal('Gift card updated successfully');
    });
  }

}
