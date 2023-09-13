import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'namesort', pure: false})
export class NameSort implements PipeTransform {
  transform(array: Array<string>, args: string): Array<string> {
    array.sort((a: any, b: any) => {
      if (a.name.toLowerCase() < b.name.toLowerCase()) {
        return -1;
      } else if (a.name.toLowerCase() > b.name.toLowerCase()) {
        return 1;
      } else {
        return 0;
      }
    });
    return array;
  }
}
