export enum CustomerEmailType {
  Personal
  , Business
  , Unknown
}

export namespace CustomerEmailType {
  export function key(): Array<string> {
    const keys = Object.keys(CustomerEmailType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const CustomerEmailTypeMap = new Map<CustomerEmailType, string>([
  [CustomerEmailType.Business, 'business']
  , [CustomerEmailType.Personal, 'personal']
  , [CustomerEmailType.Unknown, 'unknown']
]);

export function CustomerEmailTypeDecorator(constructor: Function) {
  constructor.prototype.CustomerEmailType = CustomerEmailType;
}
