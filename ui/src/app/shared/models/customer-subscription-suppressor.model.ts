import { isNullOrUndefined } from 'util';

export class CustomerSubscriptionSuppressor {
  public displayName: string;
  public isSuppressed: boolean;
  public suppressionCode: number;

  constructor(suppressionCode: number
    , displayName: string
    , isSuppressed?: boolean) {
    this.suppressionCode = suppressionCode;
    this.displayName = displayName;
    if (!isNullOrUndefined(isSuppressed)) {
      this.isSuppressed = isSuppressed;
    }
  }
}
