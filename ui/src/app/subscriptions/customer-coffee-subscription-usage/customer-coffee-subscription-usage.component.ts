import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

// Services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerService } from '@services/customer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Enums
import { CustomerDetails } from '@models/customer-details.model';
import { CustomerSubscriptionService } from '@services/customer-subscription.service';
import { CoffeeSubscriptionUsage } from '@models/coffee-subscription-usage.model';

// Declare $ as jQuery
 declare var $: any;

 @Component({
   selector: 'app-customer-coffee-subscription-usage',
   templateUrl: './customer-coffee-subscription-usage.component.html',
   styleUrls: ['./customer-coffee-subscription-usage.component.scss']
 })
 export class CustomerCoffeeSubscriptionUsageComponent implements OnInit {
   @Input() customerDetails: CustomerDetails;

   private pageTitle = 'Coffee Subscriptions Usage';

   public results: CoffeeSubscriptionUsage[] = [];

   public cancelCoffeeSubscription = false;

   constructor(private _authService: AuthenticationService
     , private _customerPropagatorService: CustomerPropagatorService
     , private _customerService: CustomerService
     , private _coffeeSubscriptionUsageService: CustomerSubscriptionService
     , private _router: Router
     , private _titlePropagatorService: TitlePropagatorService
   ) {

   }

   ngOnInit() {
     this._coffeeSubscriptionUsageService.searchCoffeeSubscriptionUsage(this.customerDetails.customerId).subscribe(
       (results) => this.results = results,
       (error) => console.error()
     );

     this._authService.checkCredentials().subscribe((canView) => {
       if ( canView ) {

         this._titlePropagatorService.setNewTitle(this.pageTitle);

         this._authService.checkComponentPrivilege('cof.subc.cancel')
           .subscribe(cancelCoffeeSubscription => this.cancelCoffeeSubscription = cancelCoffeeSubscription);
       }
     });
   }

   public onClickCancelSubscriptionButton(): void {
     this._coffeeSubscriptionUsageService.cancelCoffeeSubscription(this.customerDetails.customerId, this.results[0].programId).subscribe(
       () => {},
       error => console.error(error)
     );
   }


 }
