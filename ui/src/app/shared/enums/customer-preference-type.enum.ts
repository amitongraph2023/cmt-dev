export enum CustomerPreferenceType {
  DIETARY
  , GATHER
}

export namespace PaymentOptionType {
  export function key(): Array<string> {
    const keys = Object.keys(PaymentOptionType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const CustomerPreferenceTypeMap = new Map<CustomerPreferenceType, string>([
  [CustomerPreferenceType.DIETARY, 'DIETARY']
  , [CustomerPreferenceType.GATHER, 'GATHER']

]);

export function CustomerPreferenceTypeDecorator(constructor: Function) {
  constructor.prototype.CustomerPreferenceType = CustomerPreferenceType;
}
