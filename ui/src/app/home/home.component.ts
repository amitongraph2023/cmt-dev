import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CONSTANTS } from '../constants';
import { isNullOrUndefined } from 'util';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {

  constructor(
    private _customerPropagatorService: CustomerPropagatorService
    , private _router: Router
  ) {
    if (!isNullOrUndefined(this._customerPropagatorService.getCustomerId())) {
      this.loadCustomerPage();
    }
  }

  /**
   * Redirects / Routes the browser to the customer module
   */
  public loadCustomerPage(): void {
    this._router.navigate([CONSTANTS.SETTINGS.CUSTOMER_LANDING_PAGE]);
  }
}
