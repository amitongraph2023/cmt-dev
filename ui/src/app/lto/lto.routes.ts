import { Routes } from '@angular/router';
import { LtoComponent } from './lto.component';

export const LtoRoutes: Routes = [
  { path: 'lto'
    , component: LtoComponent
    , data: { animation: 'lto' }
    , runGuardsAndResolvers: 'always'
  }
  , { path: 'lto/code'
    , component: LtoComponent
    , data: { animation: 'code' }
    , runGuardsAndResolvers: 'always'
  }
];
