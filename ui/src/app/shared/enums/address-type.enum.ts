export enum AddressType {
  BUSINESS
  , RESIDENTIAL
  , OTHER
  , COLLEGE_CAMPUS
}


export namespace AddressType {
  export function key(): Array<string> {
    const keys = Object.keys(AddressType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const AddressTypeMap = new Map<string, any>([
  [AddressType[AddressType.BUSINESS], {type: AddressType.BUSINESS, display: 'Business'}]
  , [AddressType[AddressType.COLLEGE_CAMPUS], {type: AddressType.COLLEGE_CAMPUS, display: 'College Campus'}]
  , [AddressType[AddressType.OTHER], {type: AddressType.OTHER, display: 'Other'}]
  , [AddressType[AddressType.RESIDENTIAL], {type: AddressType.RESIDENTIAL, display: 'Residential'}]
]);

export function AddressTypeDecorator(constructor: Function) {
  constructor.prototype.AddressType = AddressType;
}
