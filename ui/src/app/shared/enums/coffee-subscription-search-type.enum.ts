export enum CoffeeSubscriptionSearchType {
  GIFT_CODE,
  ORDER_ID,
  CUSTOMER_ID,
  CUSTOMER_EMAIL
}

export namespace CoffeeSubscriptionSearchType {
  export function key(): Array<string> {
    const keys = Object.keys(CoffeeSubscriptionSearchType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const CoffeeSubscriptionSearchMap = new Map<CoffeeSubscriptionSearchType, string>([
  [CoffeeSubscriptionSearchType.CUSTOMER_EMAIL, 'customerEmail']
  , [CoffeeSubscriptionSearchType.GIFT_CODE, 'giftCode']
  , [CoffeeSubscriptionSearchType.ORDER_ID, 'orderId']
  , [CoffeeSubscriptionSearchType.CUSTOMER_ID, 'customerId']
]);

export function CoffeeSubscriptionSearchTypeDecorator(constructor: Function) {
  constructor.prototype.CoffeeSubscriptionSearchType = CoffeeSubscriptionSearchType;
}
