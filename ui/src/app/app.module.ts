import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule, Title } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { CookieModule } from 'ngx-cookie';

import './string.prototype';

import { httpInterceptorProviders } from './shared/http-interceptors';
// Components
import { AppComponent } from './app.component';
import { NavbarComponent } from '@components/navbar/navbar.component';

import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

import { AddressCardComponent } from './customer/customer-addresses/address-card/address-card.component';
import { CampusCardCardComponent } from './customer/customer-payment-options/campus-card-card/campus-card-card.component';
import { CateringRedirectComponent } from './catering-redirect/catering-redirect.component';
import { CorpCateringCardComponent } from './customer/customer-payment-options/corp-catering-card/corp-catering-card.component';
import { CreditCardCardComponent } from './customer/customer-payment-options/credit-card-card/credit-card-card.component';
import { CustomerAddressesComponent } from './customer/customer-addresses/customer-addresses.component';
import { CustomerComponent } from './customer/customer.component';
import { CustomerEmailsComponent } from './customer/customer-emails/customer-emails.component';
import { CustomerInfoComponent } from './customer/customer-info/customer-info.component';
import { CustomerNavbarComponent } from '@components/customer-navbar/customer-navbar.component';
import { CustomerPhonesComponent } from './customer/customer-phones/customer-phones.component';
import { CustomerPaymentOptionsComponent } from './customer/customer-payment-options/customer-payment-options.component';
import { CustomerPreferencesComponent } from './customer/customer-preferences/customer-preferences.component';
import { CustomerSearchComponent } from '@components/customer-search/customer-search.component';
import { CustomerSubscriptionsComponent } from './customer/customer-subscriptions/customer-subscriptions.component';
import { EmailCardComponent } from './customer/customer-emails/email-card/email-card.component';
import { GiftCardCardComponent } from './customer/customer-payment-options/gift-card-card/gift-card-card.component';
import { LtoComponent } from './lto/lto.component';
import { LtoCodeComponent } from './lto/lto-code/lto-code.component';
import { LtoNavbarComponent } from '@components/lto-navbar/lto-navbar.component';
import { NewTabComponent } from './new-tab/new-tab.component';
import { PaypalCardComponent } from './customer/customer-payment-options/paypal-card/paypal-card.component';
import { PaytronixComponent } from './paytronix/paytronix.component';
import { PaytronixBalanceComponent } from './paytronix/paytronix-balance/paytronix-balance.component';
import { PaytronixCardExchangeComponent } from './paytronix/paytronix-card-exchange/paytronix-card-exchange.component';
import { PaytronixMissedVisitComponent } from './paytronix/paytronix-missed-visit/paytronix-missed-visit.component';
import { PaytronixNavbarComponent } from './shared/components/paytronix-navbar/paytronix-navbar.component';
import { PaytronixTransactionsComponent } from './paytronix/paytronix-transactions/paytronix-transactions.component';
import { PhoneCardComponent } from './customer/customer-phones/phone-card/phone-card.component';
import { SpoofComponent } from './spoof/spoof.component';
import { SpoofInfoBarComponent } from './spoof/spoof-info-bar/spoof-info-bar.component';
import { SpoofInfoModalComponent } from './spoof/spoof-info-modal/spoof-info-modal.component';
import { BonusCardCardComponent } from './customer/customer-payment-options/bonus-card-card/bonus-card-card.component';
// Modules
import { AdminModule } from './admin/admin.module';
import { AppRoutingModule } from './app-routing.module';
import { PipesModule } from './shared/pipes/pipes.module';
import { ServicesModule } from '@services/services.module';
import { NgxPaginationModule } from 'ngx-pagination';
import { ApplepayCardComponent } from './customer/customer-payment-options/applepay-card/applepay-card.component';
// tslint:disable-next-line:max-line-length
import { CustomerGiftCoffeeSubscriptionsComponent } from './subscriptions/customer-gift-coffee-subscriptions/customer-gift-coffee-subscriptions.component';
import { GiftCoffeeComponent } from './subscriptions/gift-coffee/gift-coffee.component';
import { GiftCoffeeSubscriptionComponent } from './shared/components/gift-coffee-subscription-card/gift-coffee-subscription.component';
import { GcsCodeCardComponent } from './shared/components/gift-coffee-subscription-card/gcs-code-card/gcs-code-card.component';
// tslint:disable-next-line:max-line-length
import { CustomerCoffeeSubscriptionUsageComponent } from './subscriptions/customer-coffee-subscription-usage/customer-coffee-subscription-usage.component';
import { SubscriptionsComponent } from './subscriptions/subscriptions.component';
import { SubscriptionNavbarComponent } from './shared/components/subscription-navbar/subscription-navbar.component';
import { MfaComponent } from './customer/mfa/mfa.component';


@NgModule({
  declarations: [
    AppComponent,

    NavbarComponent,

    HomeComponent,
    LoginComponent,
    PageNotFoundComponent,

    AddressCardComponent,
    CampusCardCardComponent,
    CateringRedirectComponent,
    CorpCateringCardComponent,
    CreditCardCardComponent,
    CustomerAddressesComponent,
    CustomerComponent,
    CustomerEmailsComponent,
    CustomerInfoComponent,
    CustomerNavbarComponent,
    CustomerPaymentOptionsComponent,
    CustomerPhonesComponent,
    CustomerPreferencesComponent,
    CustomerSearchComponent,
    CustomerSubscriptionsComponent,
    EmailCardComponent,
    GiftCardCardComponent,
    MfaComponent,
    NewTabComponent,
    PaypalCardComponent,
    PhoneCardComponent,
    BonusCardCardComponent,

    LtoComponent,
    LtoCodeComponent,
    LtoNavbarComponent,

    PaytronixComponent,
    PaytronixBalanceComponent,
    PaytronixCardExchangeComponent,
    PaytronixMissedVisitComponent,
    PaytronixNavbarComponent,
    PaytronixTransactionsComponent,

    SpoofComponent,
    SpoofInfoBarComponent,
    SpoofInfoModalComponent,
    ApplepayCardComponent,
    CustomerGiftCoffeeSubscriptionsComponent,
    GiftCoffeeComponent,
    GiftCoffeeSubscriptionComponent,
    GcsCodeCardComponent,
    CustomerCoffeeSubscriptionUsageComponent,
    SubscriptionsComponent,
    SubscriptionNavbarComponent,
    MfaComponent,

  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ReactiveFormsModule,

    CookieModule.forRoot(),

    PipesModule,
    ServicesModule,

    AdminModule,

    AppRoutingModule,
    NgxPaginationModule
  ],
  providers: [
    Title,

    httpInterceptorProviders
  ],
  bootstrap: [AppComponent],
  exports: [],
})
export class AppModule { }
