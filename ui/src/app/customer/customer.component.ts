import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { isNullOrUndefined } from 'util';

// Models
import { CustomerDetails } from '@models/customer-details.model';

// Services
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { AuthenticationService } from '@services/authentication.service';
import { CustomerLoyaltyService } from '@services/customer-loyalty.service';
import { AccountStatusType } from '@enums/account-status-type.enum';

// Declare $ as jQuery
declare var $: any;


@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {

  public customerDetails: CustomerDetails;
  public updateLoyalty = false;

  private navigationSubscription;

  public statusActive = AccountStatusType[AccountStatusType.ACTIVE];
  public statusProtected = AccountStatusType[AccountStatusType.FORCE_RESET];
  public statusTerminated = AccountStatusType[AccountStatusType.TERMINATED];
  public statusLocked = AccountStatusType[AccountStatusType.LOCKED];
  public statusSuspended = AccountStatusType[AccountStatusType.SUSPENDED];
  public statusPending = AccountStatusType[AccountStatusType.PENDING];

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

  /**
   * verifies that the route is the current route
   * @param route
   */
  public isRoute(route: string): boolean {
    return this._router.url === '/customer/' + route;
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
