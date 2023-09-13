import { AbstractControl, ValidatorFn } from '@angular/forms';

export function minValidator(min: number): ValidatorFn {
  return (c: AbstractControl): {[key: string]: any} => {
    if (c.value === null) {
      return null;
    }

    return (c.value < min) ? {min: true} : null;
  };
}
