import { Routes } from '@angular/router';

import { CustomerComponent } from './customer.component';

export const CustomerRoutes: Routes = [
  { path: 'customer'
    , component: CustomerComponent
    , data: { animation: 'customer' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/customer-info'
    , component: CustomerComponent
    , data: { animation: 'customerInfo' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/phones'
    , component: CustomerComponent
    , data: { animation: 'phones' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/addresses'
    , component: CustomerComponent
    , data: { animation: 'addresses' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/emails'
    , component: CustomerComponent
    , data: { animation: 'emails' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/payment-options'
    , component: CustomerComponent
    , data: { animation: 'paymentOptions' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/preferences'
    , component: CustomerComponent
    , data: { animation: 'preferences' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/subscriptions'
    , component: CustomerComponent
    , data: { animation: 'mfa' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'customer/mfa'
    , component: CustomerComponent
    , data: { animation: 'mfa' }
    , runGuardsAndResolvers: 'always'
 },
  { path: 'customer/coffee-subscription-usage'
    , component: CustomerComponent
    , data: { animation: 'subscriptions' }
    , runGuardsAndResolvers: 'always'
  }
];
