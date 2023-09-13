import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PaginationComponent } from '@components/pagination/pagination.component';

@NgModule({
  exports: [
    PaginationComponent
  ],
  imports: [
    CommonModule
  ],
  declarations: [
    PaginationComponent
  ]
})
export class PaginationModule {}
