import { Routes } from '@angular/router';
import { PaytronixComponent } from './paytronix.component';

export const PaytronixRoutes: Routes = [
  { path: 'paytronix'
    , component: PaytronixComponent
    , data: { animation: 'paytronix' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'paytronix/balance'
    , component: PaytronixComponent
    , data: { animation: 'paytronix' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'paytronix/card-exchange'
    , component: PaytronixComponent
    , data: { animation: 'paytronix' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'paytronix/missed-visit'
    , component: PaytronixComponent
    , data: { animation: 'paytronix' }
    , runGuardsAndResolvers: 'always'
  },
  { path: 'paytronix/transactions'
    , component: PaytronixComponent
    , data: { animation: 'paytronix' }
    , runGuardsAndResolvers: 'always'
  },
];
