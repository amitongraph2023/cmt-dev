import { Routes } from '@angular/router';

import { SubscriptionsComponent } from "./subscriptions.component";

export const SubscriptionsRoutes: Routes = [
  { path: 'subscriptions/gift-coffee'
    , component: SubscriptionsComponent
    , data: { animation: 'subscriptions' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'subscriptions/gift-coffee-subscriptions'
    , component: SubscriptionsComponent
    , data: { animation: 'subscriptions' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'subscriptions/coffee-subscription-usage'
    , component: SubscriptionsComponent
    , data: { animation: 'subscriptions' }
    , runGuardsAndResolvers: 'always'
  }
];
