import { PaytronixExpiration } from '@models/paytronix/paytronix-expiration.model';


export class PaytronixRewardBalance {
  public balance: string;
  public name: string;
  public expirations: PaytronixExpiration[];
  public walletCode: number;
  public giftable: boolean;
}
