import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'sentencecase', pure: false})
export class SentenceCase implements PipeTransform {
  transform(input: string, length: number): string {
    return input.length > 0 ? input.replace(/.*/, (txt => txt[0].toUpperCase() + txt.substr(1).toLowerCase() )) : '';
  }
}
