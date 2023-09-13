import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { isNullOrUndefined } from 'util';
// Enums
import { CustomerSearchType } from '@enums/customer-search-type.enum';
// Models
import { Customer } from '@models/customer.model';
import { SearchCustomer } from '@models/search-customer.model';
// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerSearchService } from '@services/customer-search.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { SpoofService } from '@services/spoof.service';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-search',
  templateUrl: './customer-search.component.html',
  styleUrls: ['./customer-search.component.scss']
})
export class CustomerSearchComponent {
  public customer: Customer;
  public searchTypes = CustomerSearchType;
  public foundUsers: SearchCustomer[] = [];
  public searchType = 'EMAIL';
  public searchValue = '';

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerSearchService: CustomerSearchService
    , private _customerService: CustomerService
    , private _router: Router
    , private _spoofService: SpoofService
    , private _titlePropagatorService: TitlePropagatorService
  ) { }

  /**
   * Closes Customer Not Found Modal, which is displayed when search returned no customers
   */
  public closeCustomerNotFoundModal(): void {
    $('#customerNotFoundModal').modal('hide');
    this.redirectToHome();
  }

  /**
   * Opens Select Customer Modal, which is displayed when search returned multiple customers
   */
  public openSelectCustomerModal(): void {

    this._titlePropagatorService.setNewTitle('Choose Customer');

    const modalEl = $('#selectCustomerModal');

    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Redirects / Navigate browser to Customer module
   */
  public redirectToCustomerPage(): void {
    this._router.navigate(['customer/customer-info']);
  }

  /**
   * Redirects / Navigate browser to Home module
   */
  public redirectToHome(): void {
    this._router.navigate([ 'home' ]);
  }

  /**
   * Uses form submission to search for customer
   *
   * @param {NgForm} form The form data
   */
  public search(): void {
    const type = this.searchType.toLowerCase();
    let value = this.searchValue.trim();
    if (type === CustomerSearchType[CustomerSearchType.PHONE].toLowerCase()) {
      value = value.replace(/\D/g, '' );
    }
    this._customerSearchService.searchCustomers(type, value)
      .subscribe(
        (foundUsers) => this.foundUsers = foundUsers,
        (error) => console.error(error),
        () => {
          let customerId: number;
          if (this.foundUsers.length === 1) {
            customerId = this.foundUsers[0].customerId;
            this._customerPropagatorService.setCustomerId(customerId);
            this.redirectToCustomerPage();
          } else if (this.foundUsers.length === 0 ) {
            customerId = null;
            this._customerPropagatorService.setCustomerId(customerId);
            this.openCustomerNotFoundModal();
          } else {
            this.openSelectCustomerModal();
          }
        }
      );
  }

  /**
   * Uses form submission from SelectCustomerModal to select customer from multiple results
   *
   * @param {NgForm} form The form data
   */
  public selectedUser(form: NgForm): void {
    $('#selectCustomerModal').modal('hide');
    if (!isNullOrUndefined(form.value.customerId)) {
      this._customerPropagatorService.setCustomerId(form.value.customerId);
      this.redirectToCustomerPage();
    } else {
      this._customerPropagatorService.setCustomerId(null);
      this.redirectToHome();
    }
  }

  /**
   * Opens Customer Not Found Modal, which is displayed when search returned multiple customers
   */
  public openCustomerNotFoundModal(): void {
    this._titlePropagatorService.setNewTitle('Customer Not Found');
    const modalEl = $('#customerNotFoundModal');
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

}


