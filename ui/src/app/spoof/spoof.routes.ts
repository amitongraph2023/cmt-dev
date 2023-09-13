import { Routes } from '@angular/router';

import { SpoofComponent } from './spoof.component';

export const SpoofRoutes: Routes = [
  {
    path: 'spoof'
    , component: SpoofComponent
    , data: { animation: 'spoof' }
    , runGuardsAndResolvers: 'always'
  }
];
