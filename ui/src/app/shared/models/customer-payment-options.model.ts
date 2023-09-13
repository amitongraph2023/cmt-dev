import { ApplePay } from '@models/apple-pay.model';
import { CampusCard } from '@models/campus-card.model';
import { CorporateCateringAccount } from '@models/corporate-catering-account.model';
import { CreditCard } from '@models/credit-card.model';
import { GiftCard } from '@models/gift-card.model';
import { PayPal } from '@models/paypal.model';
import { BonusCard } from '@models/bonus-card.model';

export class CustomerPaymentOptions {
  public applePays: ApplePay[];
  public corporateCateringAccounts:	CorporateCateringAccount[];
  public creditCards: CreditCard[];
  public giftCards: GiftCard[];
  public payPals: PayPal[];
  public campusCards: CampusCard[];
  public bonusCards: BonusCard[];
}
