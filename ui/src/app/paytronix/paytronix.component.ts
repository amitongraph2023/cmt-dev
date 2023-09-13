import { Component, OnInit } from '@angular/core';
import { CustomerDetails } from '@models/customer-details.model';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { NavigationEnd, Router } from '@angular/router';
import { isNullOrUndefined } from 'util';
import { CustomerLoyaltyService } from '@services/customer-loyalty.service';
import { AuthenticationService } from '@services/authentication.service';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-paytronix',
  templateUrl: './paytronix.component.html',
  styleUrls: ['./paytronix.component.scss']
})
export class PaytronixComponent implements OnInit {

  public customerDetails: CustomerDetails;
  public updateLoyalty = false;

  private navigationSubscription;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerLoyaltyService: CustomerLoyaltyService
    , private _router: Router
  ) {
    if (isNullOrUndefined(this._customerPropagatorService.getCustomerId())) {
      this.redirectToHome();
    }
    this._customerPropagatorService.customer$.subscribe(
      customerDetails => {
        this.customerDetails = customerDetails;
      }
    );
    this.navigationSubscription = this._router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
        this.customerDetails = this._customerPropagatorService.getCustomer();
      }
    });
  }

  ngOnInit() {
    this._authService.checkComponentPrivilege('update_loyalty')
      .subscribe(update_loyalty => this.updateLoyalty = update_loyalty);
  }

  public isRoute(route: string): boolean {
    return this._router.url === '/paytronix/' + route;
  }

  public onClickConfirmUpdateLoyaltyButton(): void {
    $('#updateLoyaltyConfirmModal').modal('hide');
    this._customerLoyaltyService.updateLoyaltyAccount(this.customerDetails.customerId).subscribe(
      response => {
        if (response) {
          this.openUpdateLoyaltySuccessModal();
        } else {
          this.openUpdateLoyaltyFailedModal();
        }
      }
    );
  }

  public onClickUpdateLoyaltyButton(): void {
    $('#updateLoyaltyConfirmModal').modal('show');
  }

  public openUpdateLoyaltyFailedModal(): void {
    $('#updateLoyaltyFailedModal').modal('show');
  }

  public openUpdateLoyaltySuccessModal(): void {
    $('#updateLoyaltySuccessModal').modal('show');
  }

  /**
   * Redirects / Navigate browser to Customer module
   */
  public redirectToHome(): void {
    this._router.navigate(['home']);
  }

}
