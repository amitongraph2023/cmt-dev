export enum PhoneType {
  Business
  , Mobile
  , Other
  , Residential
  , Unknown
}

export namespace PhoneType {
  export function key(): Array<string> {
    const keys = Object.keys(PhoneType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const PhoneTypeMap = new Map<PhoneType, string>([
  [PhoneType.Business, 'business']
  , [PhoneType.Mobile, 'mobile']
  , [PhoneType.Other, 'other']
  , [PhoneType.Residential, 'residential']
  , [PhoneType.Unknown, 'unknown']
]);

export function PhoneTypeDecorator(constructor: Function) {
  constructor.prototype.PhoneType = PhoneType;
}
