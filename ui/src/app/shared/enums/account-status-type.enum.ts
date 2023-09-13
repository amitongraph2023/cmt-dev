export enum AccountStatusType {
  ACTIVE
  , TERMINATED
  , SUSPENDED
  , LOCKED
  , FORCE_RESET
  , PENDING
}

export namespace AccountStatusType {
  export function key(): Array<string> {
    const keys = Object.keys(AccountStatusType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const AccountStatusTypeMap = new Map<string, any>([
  [AccountStatusType[AccountStatusType.ACTIVE], {type: AccountStatusType.ACTIVE, display: 'Active'}]
  , [AccountStatusType[AccountStatusType.TERMINATED], {type: AccountStatusType.TERMINATED, display: 'Terminated'}]
  , [AccountStatusType[AccountStatusType.SUSPENDED], {type: AccountStatusType.SUSPENDED, display: 'Suspended'}]
  , [AccountStatusType[AccountStatusType.LOCKED], {type: AccountStatusType.LOCKED, display: 'Locked'}]
  , [AccountStatusType[AccountStatusType.FORCE_RESET], {type: AccountStatusType.FORCE_RESET, display: 'Protected'}]
  , [AccountStatusType[AccountStatusType.PENDING], {type: AccountStatusType.PENDING, display: 'Pending'}]

]);

export function AccountStatusTypeDecorator(constructor: Function) {
  constructor.prototype.AccountStatusType = AccountStatusType;
}
