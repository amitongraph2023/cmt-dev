import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AdminAppConfigComponent } from './app-config/app-config.component';

export const viewRoutes: Routes = [
  { path: 'admin/app-config',  component: AdminAppConfigComponent, data: { animation: 'adminAppConfig' }, runGuardsAndResolvers: 'always' },
];

@NgModule({
  imports: [
    RouterModule.forChild(viewRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class AdminRoutingModule { }
