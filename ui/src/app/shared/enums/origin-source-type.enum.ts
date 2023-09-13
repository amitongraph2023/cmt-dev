export enum OrigonSourceType {
  CATERING
  , RETAIL
}

export namespace OrigonSourceType {
  export function key(): Array<string> {
    const keys = Object.keys(OrigonSourceType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const OrigonSourceTypeMap = new Map<OrigonSourceType, string>([
  [OrigonSourceType.CATERING, 'catering']
  , [OrigonSourceType.RETAIL, 'retail']
]);

export function OrigonSourceTypeDecorator(constructor: Function) {
  constructor.prototype.OrigonSourceType = OrigonSourceType;
}
