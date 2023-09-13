import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { GiftCoffeeSubscriptionService } from '@services/gift-coffee-subscription.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enums
import { CustomerDetails } from '@models/customer-details.model';
import { CoffeeSubscriptionSearchType } from '@enums/coffee-subscription-search-type.enum';
import { SubscriptionServiceResults } from '@models/subscription-service-results.model';
import { CustomerEmail } from '@models/customer-email.model';
import { CustomerEmailService } from '@services/customer-email.service';


// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-customer-gift-coffee-subscriptions',
  templateUrl: './customer-gift-coffee-subscriptions.component.html',
  styleUrls: ['./customer-gift-coffee-subscriptions.component.scss']
})
export class CustomerGiftCoffeeSubscriptionsComponent implements OnInit {
  @Input() customerDetails: CustomerDetails;

  private pageTitle = 'Gift Coffee Subscriptions';

  private searchType = CoffeeSubscriptionSearchType[CoffeeSubscriptionSearchType.CUSTOMER_EMAIL];

  public results: SubscriptionServiceResults;
  public customerEmail: CustomerEmail;

  constructor(private _authService: AuthenticationService
    , private _customerEmailService: CustomerEmailService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _giftCoffeeSubscriptionService: GiftCoffeeSubscriptionService
    , private _router: Router
    , private _titlePropagatorService: TitlePropagatorService
  ) {

  }

  ngOnInit() {
    this._customerEmailService.getEmailsByCustomerId(this.customerDetails.customerId).subscribe(
      (customerEmails) => {
        this.customerEmail = customerEmails.filter(ce => ce.isDefault)[0];
      },
      (error) => console.error(error),
      () => {
        this._giftCoffeeSubscriptionService.searchGiftCoffeeSubscriptions(this.searchType, this.customerEmail.emailAddress).subscribe(
          (results) => this.results = results,
          (error) => console.error()
        );
      }
    );
  }

}
