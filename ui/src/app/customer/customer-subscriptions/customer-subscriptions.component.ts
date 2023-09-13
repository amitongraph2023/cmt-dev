import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { isNullOrUndefined } from 'util';
// Models
import { CustomerSubscriptions } from '@models/customer-subscriptions.model';
// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { CustomerSubscriptionsService } from '@services/customer-subscriptions.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
// Enums
import { StaticDataService } from '@services/static-data.service';
import { StaticType } from '@enums/static-type.enum';
import { CustomerSubscription } from '@models/customer-subscription.model';
import { CustomerSubscriptionSuppressor } from '@models/customer-subscription-suppressor.model';
import { CustomerDetails } from '@models/customer-details.model';


@Component({
  selector: 'app-customer-subscriptions',
  templateUrl: './customer-subscriptions.component.html',
  styleUrls: ['./customer-subscriptions.component.scss']
})
export class CustomerSubscriptionsComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  public customerSubscriptions: CustomerSubscriptions;
  public isEmailGlobalOpt: boolean;
  public showSuppressors: boolean;

  public canUpdateSubscription = false;

  private pageTitle = 'Customer Preferences';


  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _customerSubscriptionsService: CustomerSubscriptionsService
    , private _router: Router
    , private _staticDataService: StaticDataService
    , private _titlePropagatorService: TitlePropagatorService
  ) {}

  ngOnInit() {
    this._authService.checkCredentials().subscribe((canView) => {
      if ( canView ) {

        this._titlePropagatorService.setNewTitle(this.pageTitle);

        this._authService.checkComponentPrivilege('cust.subscr.update')
          .subscribe(canUpdateSubscription => this.canUpdateSubscription = canUpdateSubscription);
      } else {
        this._customerPropagatorService.setCustomerId(null);
      }
    });

    this.isEmailGlobalOpt = this.customerDetails.isEmailGlobalOpt;

    // Get existing subscriptions and suppressors
    this._customerSubscriptionsService.getCustomerSubscriptions(this.customerDetails.customerId).subscribe(
      (customerSubscriptions) => this.customerSubscriptions = customerSubscriptions,
      (error) => console.error(error),
      () => {
        // If none exist on the user, get the static versions that are out there
        if (this.customerSubscriptions.subscriptions.length === 0) {
          this._staticDataService.getStaticDataByType(StaticType.subscriptionType).subscribe(
            (staticSubscriptions) => {
              const subscriptions: CustomerSubscription[] = [];
              staticSubscriptions.forEach(s => subscriptions.push(new CustomerSubscription(s.code, s.displayName)));
              this.customerSubscriptions.subscriptions = subscriptions;
            });
          this._staticDataService.getStaticDataByType(StaticType.subscriptionSuppressionType).subscribe(
            (staticSuppessors) => {
              const suppressors: CustomerSubscriptionSuppressor[] = [];
              staticSuppessors.forEach(s => suppressors.push(new CustomerSubscriptionSuppressor(s.suppressionCode, s.displayName)));
              this.customerSubscriptions.suppressors = suppressors;
            });
        } else {
          this.showSuppressors = this.customerSubscriptions.subscriptions.filter( s => s.subscriptionCode === 2)[0].isSubscribed;
        }
      });
  }

  /**
   * Event handler for the subscription checkboxes
   *
   * @param {any} $event The event passed by the the handler
   */
  public checkboxChangeSubscriptionHandler($event: any): void {
    const index =
      Number(this.customerSubscriptions.subscriptions.indexOf(
        this.customerSubscriptions.subscriptions.filter( subscription => subscription.subscriptionCode === Number($event.target.value))[0]
      ));

    this.customerSubscriptions.subscriptions[index].isSubscribed = !this.customerSubscriptions.subscriptions[index].isSubscribed;

    if (this.customerSubscriptions.subscriptions[index].subscriptionCode === 2
      && this.customerSubscriptions.subscriptions[index].isSubscribed === false ) {
      this.setSuppressorsTrue();
    }

    if (this.customerSubscriptions.subscriptions[index].subscriptionCode === 2) {
      this.showSuppressors = !this.showSuppressors;
    }
  }

  /**
   * Event handler for the suppressor checkboxes
   *
   * @param {any} $event The event passed to the handler
   */
  public checkboxChangeSuppressorHandler($event: any): void {
    this.customerSubscriptions.suppressors[
      this.customerSubscriptions.suppressors.indexOf(
        this.customerSubscriptions.suppressors.filter( suppressor => suppressor.suppressionCode === Number($event.target.value))[0]
      )].isSuppressed = !(this.customerSubscriptions.suppressors[
      this.customerSubscriptions.suppressors.indexOf(
        this.customerSubscriptions.suppressors.filter( suppressor => suppressor.suppressionCode === Number($event.target.value))[0]
      )].isSuppressed);
  }

  /**
   * For the HTML, verifies that the customerSubscription object and the isEmailGlobalOpt are set
   */
  public pageCheck(): boolean {
    return (!isNullOrUndefined(this.customerSubscriptions) && !isNullOrUndefined(this.isEmailGlobalOpt));
  }

  /**
   * Change handler for the radio button on the email option
   *
   * @param {any} $event The event from the form
   */
  public radioChangeGlobalEmailOptionHandler($event: any): void {
    if (this.canUpdateSubscription) {
      this.isEmailGlobalOpt = !this.isEmailGlobalOpt;
      this.customerDetails.isEmailGlobalOpt = this.isEmailGlobalOpt;

      if ( this.customerDetails.isEmailGlobalOpt === false ) {
        // set subscriptions to none
        for ( let i = 0; i < this.customerSubscriptions.subscriptions.length; i++ ) {
          this.customerSubscriptions.subscriptions[ i ].isSubscribed = false;
        }
        // set suppressors to none
        this.setSuppressorsTrue();
        this.showSuppressors = false;
      }
    }
  }

  /**
   * Sets the suppressors to true (suppressed) when not subscribed
   */
  private setSuppressorsTrue() {
    for ( let i = 0; i < this.customerSubscriptions.suppressors.length; i++ ) {
      this.customerSubscriptions.suppressors[i].isSuppressed = true;
    }
  }

  /**
   * Update the customer's subscriptions with an api call
   */
  public updateSubscriptions(): void {

    // If setting global to off, then clear subscriptions first
    if (!this.customerDetails.isEmailGlobalOpt) {
      this._customerSubscriptionsService.updateCustomerSubscriptions(
        this.customerDetails.customerId
        , this.customerSubscriptions)
        .subscribe( () => {} , (error) => console.log(error));
    }

    this._customerService.updateCustomer(this.customerDetails.customerId, this.customerDetails)
      .subscribe(
        () => {}
        , (error) => console.log(error)
        , () => {
          if (this.customerDetails.isEmailGlobalOpt) {
            this._customerSubscriptionsService.updateCustomerSubscriptions(
              this.customerDetails.customerId
              , this.customerSubscriptions)
              .subscribe( () => {} , (error) => console.log(error));
          }
        });
  }

}
