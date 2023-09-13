import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CampusCard } from '@models/campus-card.model';
import { AuthenticationService } from '@services/authentication.service';
import { CustomerService } from '@services/customer.service';
import { CustomerPaymentOptionsService } from '@services/customer-payment-options.service';
import { Router } from '@angular/router';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { PaymentOptionType } from '@enums/payment-option-type.enum';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerDetails } from '@models/customer-details.model';

// Enums
import { isNullOrUndefined } from 'util';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-campus-card',
  templateUrl: './campus-card-card.component.html',
  styleUrls: ['./campus-card-card.component.scss']
})
export class CampusCardCardComponent implements OnInit {

  @Input() campusCard: CampusCard;
  @Input() customerDetails: CustomerDetails;

  @Output() updatedCampusCard = new EventEmitter<boolean>();

  public edit = false;
  public successMessage: string;

  public canDelete = false;
  
  public validExpiration: boolean;

   constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerPaymentOptionsService: CustomerPaymentOptionsService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit(): void {
    this._authService.checkComponentPrivilege('cust.pmt.campus_card.delete')
      .subscribe(canDelete => this.canDelete = canDelete);

    if (!isNullOrUndefined(this.campusCard.expirationDate)) {
      this.expirationValidator(this.campusCard.expirationDate);
    }
  }

  /**
   * Confirm deleting selected credit card with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete campus card');
    const modalEl = $('#deleteConfirmModal' + this.campusCard.campusCardToken);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete Campus Card after confirming in deleteConfirm modal
   */
  public deleteCampusCard(): void {
    $('#deleteConfirmModal' + this.campusCard.campusCardToken).modal('hide');

    this._customerPaymentOptionsService.deletePaymentOptionByCustomerIdAndPaymentOptionType(
      this.customerDetails.customerId
      , PaymentOptionType.CAMPUSCARD
      , this.campusCard.campusCardToken
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.updatedCampusCard.emit(true);
    });
  }


  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.campusCard.campusCardToken).modal('show');
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
