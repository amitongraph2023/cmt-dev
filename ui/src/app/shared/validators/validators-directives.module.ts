import { NgModule } from '@angular/core';

import { MaxValidatorDirective } from './max-validator.directive';
import { MinValidatorDirective } from './min-validator.directive';

@NgModule({
  declarations: [
    MaxValidatorDirective,
    MinValidatorDirective
  ],
  exports: [
    MaxValidatorDirective,
    MinValidatorDirective
  ]
})
export class ValidatorsDirectivesModule {}
