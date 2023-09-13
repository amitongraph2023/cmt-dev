import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AdminRoutingModule } from './admin-routing.module';
import { PaginationModule } from '@components/pagination/pagination.module';

import { AdminAppConfigComponent } from './app-config/app-config.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,

    AdminRoutingModule,
    PaginationModule
  ],
  declarations: [
    AdminAppConfigComponent
  ]
})
export class AdminModule {}
