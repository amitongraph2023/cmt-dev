export enum PaymentOptionType {
  CREDITCARD
  , GIFTCARD
  , PAYPAL
  , CAMPUSCARD
  , APPLEPAY
  , BONUSCARD
}

export namespace PaymentOptionType {
  export function key(): Array<string> {
    const keys = Object.keys(PaymentOptionType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const PaymentOptionTypeMap = new Map<PaymentOptionType, string>([
  [PaymentOptionType.CREDITCARD, 'creditcard']
  , [PaymentOptionType.GIFTCARD, 'giftcard']
  , [PaymentOptionType.PAYPAL, 'paypal']
  , [PaymentOptionType.CAMPUSCARD, 'campuscard']
  , [PaymentOptionType.APPLEPAY, 'applepay']
  , [PaymentOptionType.BONUSCARD, 'bonuscard']
]);

export function PaymentOptionTypeDecorator(constructor: Function) {
  constructor.prototype.PaymentOptionType = PaymentOptionType;
}
