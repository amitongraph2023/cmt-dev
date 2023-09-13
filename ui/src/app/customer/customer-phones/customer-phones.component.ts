import { Component, Input, OnInit } from '@angular/core';

import { Router } from '@angular/router';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPhoneService } from '@services/customer-phone.service';
import { CustomerService } from '@services/customer.service';
import { StaticDataService } from '@services/static-data.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Models
import { CustomerDetails } from '@models/customer-details.model';
import { CustomerPhone } from '@models/customer-phone.model';

// Enums
import { StaticType } from '@enums/static-type.enum';
import { CustomerPropagatorService } from '@services/customer-propagator.service';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-phones',
  templateUrl: './customer-phones.component.html',
  styleUrls: ['./customer-phones.component.scss']
})
export class CustomerPhonesComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public customerPhones1: CustomerPhone[];
  public customerPhones2: CustomerPhone[];
  public newCustomerPhone: CustomerPhone;
  public phoneTypes: string[];
  public successMessage: string;

  public canCreate = false;

  private pageTitle = 'Customer Phone Numbers';

  constructor(private _authService: AuthenticationService
    , private _customerService: CustomerService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerPhoneService: CustomerPhoneService
    , private _router: Router
    , private _staticDataService: StaticDataService
    , private _titlePropagatorService: TitlePropagatorService
  ) {
    this._staticDataService.getStaticDataByType(StaticType.phoneType).subscribe(
      (phoneTypes) => this.phoneTypes = phoneTypes
      , (error) => console.error(error)
    );
  }

  ngOnInit() {
    this._authService.checkCredentials().subscribe((canView) => {
      if (canView) {

        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this._authService.checkComponentPrivilege('cust.phn.create')
          .subscribe(canCreate => this.canCreate = canCreate);

      } else {
        this._customerPropagatorService.setCustomerId(null);
      }
    });

    this.loadCustomerPhones();
  }

  /**
   *  Add a new phone by API call and refresh the phone page
   */
  public addCustomerPhone() {
    $('#addCustomerPhoneModal').modal('hide');
    this.newCustomerPhone.phoneNumber = this.newCustomerPhone.phoneNumber.replace(/\D/g, '' );
    this._customerPhoneService.addCustomerPhone(
      this.customerDetails.customerId
      , this.newCustomerPhone
    ).subscribe( () => {}, (error) => console.error(error), () => {
      this.newCustomerPhone = null;
      this.loadCustomerPhones();
    });
  }

  /**
   * Click event handler to set newCustomerPhone to new instance, then call to open the addCustomerPhone modal
   */
  public createNewPhone(): void {
    this.newCustomerPhone = new CustomerPhone();
    this.newCustomerPhone.countryCode = '1';
    this._titlePropagatorService.setNewTitle('Add Customer Phone');
    const modalEl = $('#addCustomerPhoneModal');
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Load customer phones from an API call
   */
  public loadCustomerPhones(): void {
    this._customerPhoneService.getCustomerPhonesByCustomerId(this.customerDetails.customerId).subscribe(
      (allCustomerPhones) => {
        // this.customerPhones = customerPhones
                  const odd = !(allCustomerPhones.length % 2 === 0);
                  this.customerPhones1 = allCustomerPhones.slice(
                    0,
                    odd ?
                      (allCustomerPhones.length / 2) + 1 :
                      (allCustomerPhones.length / 2));
                  if (allCustomerPhones.length > 1) {
                    this.customerPhones2 = allCustomerPhones.slice(
                      odd ?
                        (allCustomerPhones.length / 2) + 1 :
                        (allCustomerPhones.length / 2),
                      allCustomerPhones.length);
                  }
      },
      (error) => console.error(error)
    );
  }
}
