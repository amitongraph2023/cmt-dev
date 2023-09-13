import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CustomerAddress } from '@models/customer-address.model';

import * as _ from 'lodash';
import { AuthenticationService } from '@services/authentication.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { CustomerAddressService } from '@services/customer-address.service';
import { StaticType } from '@enums/static-type.enum';
import { StaticDataService } from '@services/static-data.service';
import { StateProvince } from '@models/state-province.model';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { Router } from '@angular/router';
import { CustomerDetails } from '@models/customer-details.model';
import { isNullOrUndefined } from 'util';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-address-card',
  templateUrl: './address-card.component.html',
  styleUrls: ['./address-card.component.scss']
})
export class AddressCardComponent implements OnInit {

  @Input() address: CustomerAddress;
  @Input() customerDetails: CustomerDetails;

  @Output() updatedAddress = new EventEmitter<boolean>();

  public addressTypes: string[];
  public states: StateProvince[];
  public provinces: StateProvince[];
  public countries: string[];
  public edit: boolean;
  public successMessage;
  public isValidFormSubmitted: boolean;

  public canDelete = false;
  public canUpdate = false;
  public canSetDefault = false;

  private editAddressBackup: CustomerAddress;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerAddressService: CustomerAddressService
    , private _router: Router
    , private _staticDataService: StaticDataService
    , private _titlePropagatorService: TitlePropagatorService
  ) {

    // Load address types
    this._staticDataService.getStaticDataByType(StaticType.addressType).subscribe(
      (addressTypes) => this.addressTypes = addressTypes
      , (error) => console.error(error)
    );

    // Load states / provinces
    this._staticDataService.getStaticDataByType(StaticType.statesProvinces).subscribe(
      (statesProvinces) => this.states = statesProvinces
      , (error) => console.error(error)
      , () => {
        this.provinces = this.states.filter( c => c.countryCode === 'CA')
          .sort( (a, b) => a.value.localeCompare(b.value));
        this.states = this.states.filter( c => c.countryCode === 'US')
          .sort( (a, b) => a.value.localeCompare(b.value));
      }
    );

    // Load countries
    this._staticDataService.getStaticDataByType(StaticType.countryType).subscribe(
      (countries) => this.countries = countries
      , (error) => console.error(error)
    );

  }

  ngOnInit() {

    this._authService.checkComponentPrivilege('cust.addr.edit.delete')
      .subscribe(canDelete => this.canDelete = canDelete);
    this._authService.checkComponentPrivilege('cust.addr.edit.update')
      .subscribe(canUpdate => this.canUpdate = canUpdate);
    this._authService.checkComponentPrivilege('cust.addr.set_default')
      .subscribe(canSetUpdate => this.canSetDefault = canSetUpdate);

    // Since this api doesn't have a set default, need to pass whole object to set default
    // Using editAddressBackup to make sure default is cleanly set
    this.editAddressBackup = _.cloneDeep(this.address);
    this.edit = false;

    // Set Default Name if empty for display purposes
    // If empty and not updated, this value will remain empty
    if (isNullOrUndefined(this.address.name)) {
      this.address.name = this.address.addressLine1;
    }
  }


  /**
   * Click event handler to cancel updating address and revert changes
   */
  public cancelUpdateCustomerAddress(): void {
    this.edit = false;
    this.address = _.cloneDeep(this.editAddressBackup);
  }

  /**
   * Click event handler to set the value of the editAddressBackup and set edit=true
   */
  public clickEditAddress(): void {
    this.edit = true;
    this.editAddressBackup = _.cloneDeep(this.address);
  }

  /**
   * Confirm deleting selected address with a modal
   */
  public confirmDelete(): void {
    this._titlePropagatorService.setNewTitle('Confirm delete address');
    const modalEl = $('#deleteConfirmModal' + this.address.id);
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Delete address after confirming in deleteConfirm modal
   */
  public deleteAddress(): void {
    $('#deleteConfirmModal' + this.address.id).modal('hide');
    this._customerAddressService.deleteCustomerAddressById(
      this.customerDetails.customerId
      , this.address.id
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.updatedAddress.emit(true);
    });
  }

  /**
   * Make the customer address the default address
   */
  public makeDefaultCustomerAddress(): void {
    // To assure no changes were made in edit, using backup
    this.editAddressBackup.isDefault = true;

    this._customerAddressService.updateCustomerAddress(
      this.customerDetails.customerId
      , this.address.id
      , this.editAddressBackup
    ).subscribe( () => {}, (error) => console.log(error), () => {
      this.updatedAddress.emit(true);
    });
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal' + this.address.id).modal('show');
  }

  /**
   * Update the Additional Info as it is typed
   *
   * @param {string} value The current value of the Additional Info textarea
   */
  public updateAdditionalInfo(value: string): void {
    this.address.additionalInfo = value;
  }

  /**
   * Update the customer address after editing in the editCustomerPhoneModal
   */
  public updateCustomerAddress(): void {
    this.edit = false;
    this.editAddressBackup = null;
    this.address.contactPhone = this.address.contactPhone.replace(/\D/g, '' );
    this._customerAddressService.updateCustomerAddress(
      this.customerDetails.customerId
      , this.address.id
      , this.address
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.isValidFormSubmitted = null;
      this.editAddressBackup = _.cloneDeep(this.address);
      this.openSuccessModal('Address updated successfully');
    });
  }


}
