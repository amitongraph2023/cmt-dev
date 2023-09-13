import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

import * as _ from 'lodash';

// Models
import { BonusCard } from '@models/bonus-card.model';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerPaymentOptionsService } from '@services/customer-payment-options.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enums
import { PaymentOptionType } from '@enums/payment-option-type.enum';
import { CustomerDetails } from '@models/customer-details.model';

import {isNullOrUndefined} from 'util';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-bonus-card-card',
  templateUrl: './bonus-card-card.component.html',
  styleUrls: ['./bonus-card-card.component.scss']
})
export class BonusCardCardComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;
  @Input() bonusCard: BonusCard;

  @Output() updatedBonusCard = new EventEmitter<boolean>();

  public edit: boolean;
  public bonusCardBackup: BonusCard;
  public successMessage: string;

  public canDelete = false;
  public canUpdate = false;
  public validExpiration: boolean;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerPaymentOptionsService: CustomerPaymentOptionsService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {
  }

  ngOnInit() {
    this._authService.checkComponentPrivilege('cust.pmt.bc.edit.delete')
      .subscribe(canDelete => this.canDelete = canDelete);
    this._authService.checkComponentPrivilege('cust.pmt.bc.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
  }

  /**
   * Click event handler to cancel updating bonus card and revert changes
   */
  public cancelUpdateBonusCard(): void {
    this.edit = false;
    this.bonusCard = _.cloneDeep(this.bonusCardBackup);
  }

  /**
   * Click event handler to set the value of the bonusCardBackup and set edit=true
   */
  public clickEditBonusCard(): void {
    this.edit = true;
    this.bonusCardBackup = _.cloneDeep(this.bonusCard);
  }

  /**
   * Confirm deleting selected bonus card with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete bonus card');
    const modalEl = $('#deleteConfirmModal' + this.bonusCard.cardNumber);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete bonusCard after confirming in deleteConfirm modal
   */
  public deleteBonusCard(): void {
    $('#deleteConfirmModal' + this.bonusCard.cardNumber).modal('hide');

    this._customerPaymentOptionsService.deletePaymentOptionByCustomerIdAndPaymentOptionType(
      this.customerDetails.customerId
 	  , PaymentOptionType.BONUSCARD
      , this.bonusCard.cardNumber
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.updatedBonusCard.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.bonusCard.cardNumber).modal('show');
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
