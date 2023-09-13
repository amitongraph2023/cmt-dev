import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { isNullOrUndefined } from 'util';
import * as _ from 'lodash';
import { PhoneMask } from '../../../shared/pipes/phone-mask.pipe';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { CustomerPhoneService } from '@services/customer-phone.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Models
import { CustomerPhone } from '@models/customer-phone.model';
import { Router } from '@angular/router';
import { CustomerDetails } from '@models/customer-details.model';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-phone-card',
  templateUrl: './phone-card.component.html',
  styleUrls: ['./phone-card.component.scss']
})
export class PhoneCardComponent implements OnInit {

  @Input() customerDetails: CustomerDetails;
  @Input() phone: CustomerPhone;
  @Input() phoneTypes: string[];

  @Output() updatedPhone = new EventEmitter<boolean>();

  public edit: boolean;
  public editPhoneBackup: CustomerPhone;
  public successMessage: string;

  public canDelete = false;
  public canUpdate = false;
  public canSetDefault = false;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerPhoneService: CustomerPhoneService
    , private _phoneMask: PhoneMask
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit(): void {
    this._authService.checkComponentPrivilege('cust.phn.edit.delete')
      .subscribe(canDelete => this.canDelete = canDelete);
    this._authService.checkComponentPrivilege('cust.phn.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
    this._authService.checkComponentPrivilege('cust.phn.set_default')
      .subscribe(canSetDefault => this.canSetDefault = canSetDefault);

    if (this.phone.countryCode !== '1') {
      this.phone.countryCode = '1';
    }

    // Set Default Name if empty for display purposes
    // If empty and not updated, this value will remain empty
    if (isNullOrUndefined(this.phone.name)) {
      this.phone.name = this._phoneMask.transform(this.phone.phoneNumber, null);
    }
  }

  /**
   * Click event handler to cancel updating phone and revert changes
   */
  public cancelUpdateCustomerPhone(): void {
    this.edit = false;
    this.phone = _.cloneDeep(this.editPhoneBackup);
  }

  /**
   * Click event handler to set the value of the editPhoneBackup and set edit=true
   */
  public clickEditPhone(): void {
    this.edit = true;
    this.editPhoneBackup = _.cloneDeep(this.phone);
  }

  /**
   * Confirm deleting selected phone number with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete phone number');
    const modalEl = $('#deleteConfirmModal' + this.phone.id);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete phone after confirming in deleteConfirm modal
   */
  public deletePhone(): void {
    $('#deleteConfirmModal' + this.phone.id).modal('hide');

    this._customerPhoneService.deleteCustomerPhoneById(
      this.customerDetails.customerId
      , this.phone.id
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.updatedPhone.emit(true);
    });
  }

  /**
   * Make the customer phone the default phone number
   */
  public makeDefaultCustomerPhone(): void {
    this._customerPhoneService.setDefaultCustomerPhoneById(
      this.customerDetails.customerId
      , this.phone.id
    ).subscribe( () => {}, (error) => console.log(error), () => {
      this.updatedPhone.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.phone.id).modal('show');
  }

  /**
   * Update the customer phone after editing in the editCustomerPhoneModal
   */
  public updateCustomerPhone(): void {
    this.edit = false;
    this.editPhoneBackup = null;
    this.phone.phoneNumber = this.phone.phoneNumber.replace(/\D/g, '' );
    this._customerPhoneService.updateCustomerPhoneById(
      this.customerDetails.customerId
      , this.phone.id
      , this.phone
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.openSuccessModal('Phone number updated successfully');
    });
  }
}
