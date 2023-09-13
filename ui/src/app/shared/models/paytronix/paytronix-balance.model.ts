import { PaytronixPointBalance } from '@models/paytronix/paytronix-point-balance.model';
import { PaytronixRewardBalance } from '@models/paytronix/paytronix-reward-balance.model';

export class PaytronixBalance {
  public pointBalances: PaytronixPointBalance[];
  public rewardBalances: PaytronixRewardBalance[];
}
