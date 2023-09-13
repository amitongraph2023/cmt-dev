import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'phonemask' })
export class PhoneMask implements PipeTransform {

  transform(tel, args) {
    const value = tel.toString().trim().replace(/^\+/, '');

    if (value.match(/[^0-9]/)) {
      return tel;
    }

    let country: string;
    let areaCode: string;
    let phone: string;

    switch (value.length) {
      case 10: // +1PPP####### -> C (PPP) ###-####
        country = '1';
        areaCode = value.slice(0, 3);
        phone = value.slice(3);
        break;

      case 11: // +CPPP####### -> CCC (PP) ###-####
        country = value[0];
        areaCode = value.slice(1, 4);
        phone = value.slice(4);
        break;

      case 12: // +CCCPP####### -> CCC (PP) ###-####
        country = value.slice(0, 3);
        areaCode = value.slice(3, 5);
        phone = value.slice(5);
        break;

      default:
        return tel;
    }

    if (country === '1') {
      country = '';
    }

    phone = phone.slice(0, 3) + '-' + phone.slice(3);

    // Modified code to only return area code and phone, but no cpuntry code.
    return (' (' + areaCode + ') ' + phone).trim();
  }
}

