import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, Validator } from '@angular/forms';

import { maxValidator } from './max.validator';

@Directive({
  selector: '[max]',
  providers: [{provide: NG_VALIDATORS, useExisting: MaxValidatorDirective, multi: true}]
})
export class MaxValidatorDirective implements Validator {
  @Input() max: number;

  validate(control: AbstractControl): {[key: string]: any} {
    return this.max ? maxValidator(this.max)(control) : null;
  }
}
