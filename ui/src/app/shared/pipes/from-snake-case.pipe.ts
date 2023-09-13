import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'fromsnakecase', pure: false})
export class FromSnakeCase implements PipeTransform {
  transform(input: string, length: number): string {
    return input.length > 0 ? this.replaceAll('_', ' ', input).replace(/.*/, (txt => txt[0].toUpperCase() + txt.substr(1).toLowerCase() )) : '';
  }

  private replaceAll(search: string, replacement: string, input: string): string {
    return input.replace(new RegExp(search, 'g'), replacement);
  };

}
