export enum AppConfigServiceType {
  CMT
  , CHUB
  , EPS
}

export namespace AppConfigServiceType {
  export function key(): Array<string> {
    const keys = Object.keys(AppConfigServiceType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const AppConfigServiceMap = new Map<string, any>([
  [AppConfigServiceType[AppConfigServiceType.CMT], {type: AppConfigServiceType.CMT, display: 'CMT'}]
  , [AppConfigServiceType[AppConfigServiceType.CHUB], {type: AppConfigServiceType.CHUB, display: 'CustHub'}]
  , [AppConfigServiceType[AppConfigServiceType.EPS], {type: AppConfigServiceType.EPS, display: 'EPS'}]
]);

export function AppConfigDomainTypeDecorator(constructor: Function) {
  constructor.prototype.AppConfigServiceType = AppConfigServiceType;
}
