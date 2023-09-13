import { isNullOrUndefined } from 'util';

export class CustomerSubscription {
  displayName:	string;
  isSubscribed:	boolean;
  subscriptionCode:	number;

  constructor(subscriptionCode: number
    , displayName: string
    , isSubscribed?: boolean) {
    this.subscriptionCode = subscriptionCode;
    this.displayName = displayName;
    if (!isNullOrUndefined(isSubscribed)) {
      this.isSubscribed = isSubscribed;
    }
  }
}


