import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { isNullOrUndefined } from 'util';

// Models
import { CustomerAddress } from '@models/customer-address.model';
import { StateProvince } from '@models/state-province.model';

// Enums
import { StaticType } from '@enums/static-type.enum';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerAddressService } from '@services/customer-address.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { StaticDataService } from '@services/static-data.service';

// Enums
import { CustomerDetails } from '@models/customer-details.model';


// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-addresses',
  templateUrl: './customer-addresses.component.html',
  styleUrls: ['./customer-addresses.component.scss']
})
export class CustomerAddressesComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;


  public addressTypes: string[];
  public countries: string[];
  public customerAddresses1: CustomerAddress[];
  public customerAddresses2: CustomerAddress[];
  public newCustomerAddress: CustomerAddress;
  public provinces: StateProvince[];
  public states: StateProvince[];
  public successMessage;

  public canCreate = false;

  private pageTitle = 'Customer Addresses';

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

    this._authService.checkCredentials().subscribe((canView) => {
      if ( canView ) {

        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this._authService.checkComponentPrivilege('cust.addr.create')
          .subscribe(canCreate => this.canCreate = canCreate);

      } else {
        this._customerPropagatorService.setCustomerId(null);
      }
    });

    if (!isNullOrUndefined(this.customerDetails.customerId)) {
      this.loadCustomerAddresses();
    } else {
      this._customerPropagatorService.setCustomerId(null);
    }
  }

    /**
   *  Add a new address by API call and refresh the address page
   */
  public addCustomerAddress(): void {
    $('#addCustomerAddressModal').modal('hide');
    this.newCustomerAddress.contactPhone = this.newCustomerAddress.contactPhone.replace(/\D/g, '' );
    this._customerAddressService.addCustomerAddress(
      this.customerDetails.customerId
      , this.newCustomerAddress
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.newCustomerAddress = null;
      this.loadCustomerAddresses();
    });
  }

  /**
   * Click event handler to set editCustomerAddress to new instance, set isAdd to true, and then call to open the editCustomerAddress modal
   */
  public createNewAddress(): void {
    this.newCustomerAddress = new CustomerAddress();
    this.newCustomerAddress.country = this.countries.filter(c => c === 'United States')[0];
    this._titlePropagatorService.setNewTitle('Add Customer Address');
    const modalEl = $('#addCustomerAddressModal');
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Load customer addresses from an API call
   */
  public loadCustomerAddresses(): void {
    if (!isNullOrUndefined(this.customerDetails.customerId)) {
      this._customerAddressService.getAddressesByCustomerId(this.customerDetails.customerId).subscribe(
        (allCustomerAddresses) => {
          const odd = !(allCustomerAddresses.length % 2 === 0);
          this.customerAddresses1 = allCustomerAddresses.slice(
            0,
            odd ?
              (allCustomerAddresses.length / 2) + 1 :
              (allCustomerAddresses.length / 2));
          if (allCustomerAddresses.length > 1) {
            this.customerAddresses2 = allCustomerAddresses.slice(
              odd ?
                (allCustomerAddresses.length / 2) + 1 :
                (allCustomerAddresses.length / 2),
              allCustomerAddresses.length);
          }
        },
        (error) => console.error(error)
      );
      this.newCustomerAddress = null;
      this.successMessage = null;
    } else {
      this._customerPropagatorService.setCustomerId(null);
    }
  }

  /**
   * Open success modal
   */
  public openSuccessModal(successMessage: string): void {
    this.successMessage = successMessage;
    $('#successModal').modal('show');
  }

  /**
   * Update the Additional Info as it is typed
   *
   * @param {string} value The current value of the Additional Info textarea
   */
  public updateAdditionalInfo(value: string): void {
    this.newCustomerAddress.additionalInfo = value;
  }

}
