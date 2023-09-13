export enum CustomerSearchType {
  CUSTOMERID
  , EMAIL
  , LOYALTYCARD
  , PHONE
  , USERNAME
}

export namespace CustomerSearchType {
  export function key(): Array<string> {
    const keys = Object.keys(CustomerSearchType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const CustomerSearchMap = new Map<CustomerSearchType, string>([
  [CustomerSearchType.CUSTOMERID, 'customerid']
  , [CustomerSearchType.EMAIL, 'email']
  , [CustomerSearchType.LOYALTYCARD, 'loyaltycard']
  , [CustomerSearchType.PHONE, 'phone']
  , [CustomerSearchType.USERNAME, 'username']
]);

export function CustomerSearchTypeDecorator(constructor: Function) {
  constructor.prototype.CustomerSearchType = CustomerSearchType;
}
