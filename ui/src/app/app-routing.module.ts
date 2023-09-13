import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

import { CustomerRoutes } from './customer/customer.routes';
import { HomeRoutes } from './home/home.routes';
import { LoginRoutes } from './login/login.routes';
import { LtoRoutes } from './lto/lto.routes';
import { NewTabRoutes } from './new-tab/new-tab.routes';
import { PaytronixRoutes } from './paytronix/paytronix.routes';
import { SpoofRoutes } from './spoof/spoof.routes';
import { SubscriptionsRoutes } from "./subscriptions/subscriptions.routes";

const appRoutes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'cmt',
    redirectTo: '/home',
    pathMatch: 'prefix'
  },

  // Main routes
  ...CustomerRoutes,
  ...HomeRoutes,
  ...LoginRoutes,
  ...LtoRoutes,
  ...NewTabRoutes,
  ...PaytronixRoutes,
  ...SpoofRoutes,
  ...SubscriptionsRoutes,

  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: false, useHash: true, onSameUrlNavigation: 'reload'  } // <-- debugging purposes only
    )
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {}
