import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

// Pipes
import { FromSnakeCase } from './from-snake-case.pipe';
import { PhoneMask } from './phone-mask.pipe';
import { NameSort } from './name-sort.pipe';
import { SafePipe } from './safe-pipe.pipe';
import { SentenceCase } from './sentence-case.pipe';

@NgModule({
  declarations: [
    FromSnakeCase,
    NameSort,
    PhoneMask,
    SafePipe,
    SentenceCase
  ],
  imports: [CommonModule],
  exports: [
    FromSnakeCase,
    NameSort,
    PhoneMask,
    SafePipe,
    SentenceCase
  ],
  providers: [
    FromSnakeCase,
    NameSort,
    PhoneMask,
    SafePipe,
    SentenceCase
  ]
})
export class PipesModule {}
