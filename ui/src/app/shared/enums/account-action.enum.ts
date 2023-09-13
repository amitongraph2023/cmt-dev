export enum AccountActionType {
  REINSTATE,
  SUSPEND,
  TERMINATE,
  PROTECT
}

export namespace AccountActionType {
  export function key(): Array<string> {
    const keys = Object.keys(AccountActionType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const AccountActionTypeMap = new Map<AccountActionType, string>([
  [AccountActionType.REINSTATE, 'REINSTATE']
  , [AccountActionType.SUSPEND, 'SUSPEND']
  , [AccountActionType.TERMINATE, 'TERMINATE']
  , [AccountActionType.PROTECT, 'PROTECT']
]);

export function AccountActionTypeDecorator(constructor: Function) {
  constructor.prototype.AccountActionType = AccountActionType;
}
