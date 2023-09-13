import { GiftCode } from '@models/gift-code.model';

export class GiftCoffeeSubscription {
  customerId: number;
  purchaseOrderId: number;
  purchaseCafeId: number;
  purchaseDateTime: Date;
  purchaserEmail: string;
  giftCodes: GiftCode[];
}
