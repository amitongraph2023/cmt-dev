import { AbstractControl, ValidatorFn } from '@angular/forms';

export function maxValidator(max: number): ValidatorFn {
  return (c: AbstractControl): {[key: string]: any} => {
    if (c.value === null) {
      return null;
    }

    return (c.value > max) ? {max: true} : null;
  };
}
