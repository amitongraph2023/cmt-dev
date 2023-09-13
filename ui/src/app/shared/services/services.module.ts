import { NgModule } from '@angular/core';

import { AppConfigService } from './app-config.service';
import { AuthenticationService } from './authentication.service';
import { CateringRedirectPropagatorService } from '@services/catering-redirect-propagator.service';
import { CateringRedirectService } from '@services/catering-redirect.service';
import { CustomerAddressService } from '@services/customer-address.service';
import { CustomerEmailService } from '@services/customer-email.service';
import { CustomerLoyaltyService } from '@services/customer-loyalty.service';
import { CustomerMfaService } from "@services/customer-mfa.service";
import { CustomerMissedVisitService } from '@services/customer-missed-visit.service';
import { CustomerPasswordService } from '@services/customer-password.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { CustomerPaymentOptionsService } from '@services/customer-payment-options.service';
import { CustomerPhoneService } from '@services/customer-phone.service';
import { CustomerPreferencesService } from '@services/customer-preferences.service';
import { CustomerSearchService } from '@services/customer-search.service';
import { CustomerService } from '@services/customer.service';
import { CustomerSocialIntegrationsService } from '@services/customer-social-integrations.service';
import { CustomerSubscriptionsService } from '@services/customer-subscriptions.service';
import { ErrorPropagatorService } from './error-propagator.service';
import { GiftCoffeeSubscriptionService } from '@services/gift-coffee-subscription.service';
import { HttpService } from './http.service';
import { LoadingPropagatorService } from './loading-propagator.service';
import { LtoService } from '@services/lto.service';
import { NavbarService } from '@services/navbar.service';
import { PaytronixService } from '@services/paytronix.service';
import { PngService } from '@services/png.service';
import { SessionTimerService } from './session-timer.service';
import { SpoofService } from '@services/spoof.service';
import { StaticDataService } from '@services/static-data.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { CustomerSubscriptionService } from '@services/customer-subscription.service';

@NgModule({
  imports: [
  ],
  exports: [
  ],
  providers: [
    AppConfigService
    , AuthenticationService
    , AppConfigService
    , CateringRedirectService
    , CateringRedirectPropagatorService
    , CustomerService
    , CustomerAddressService
    , CustomerSubscriptionService
    , CustomerEmailService
    , CustomerLoyaltyService
    , CustomerMfaService
    , CustomerMissedVisitService
    , CustomerPasswordService
    , CustomerPropagatorService
    , CustomerPaymentOptionsService
    , CustomerPhoneService
    , CustomerPreferencesService
    , CustomerSearchService
    , CustomerSocialIntegrationsService
    , CustomerSubscriptionsService
    , ErrorPropagatorService
    , GiftCoffeeSubscriptionService
    , HttpService
    , LoadingPropagatorService
    , LtoService
    , NavbarService
    , PaytronixService
    , PngService
    , SessionTimerService
    , SpoofService
    , StaticDataService
    , TitlePropagatorService
  ]
})
export class ServicesModule {}
