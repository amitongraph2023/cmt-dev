export enum CustomerInfoType {
  DETAILS
  , STATUS

}

export namespace CustomerInfoType {
  export function key(): Array<string> {
    const keys = Object.keys(CustomerInfoType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const CustomerInfoTypeMap = new Map<CustomerInfoType, string>([
  [CustomerInfoType.DETAILS, 'details']
  , [CustomerInfoType.STATUS, 'status']
]);

export function CustomerInfoTypeDecorator(constructor: Function) {
  constructor.prototype.CustomerInfoType = CustomerInfoType;
}
