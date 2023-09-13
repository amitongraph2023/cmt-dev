export enum SpoofStatusType {
  SPOOFING
  , CLEANUP
  , NOTSPOOFING
}

export namespace SpoofStatusType {
  export function key(): Array<string> {
    const keys = Object.keys(SpoofStatusType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const SpoofStatusTypeMap = new Map<SpoofStatusType, string>([
  [SpoofStatusType.CLEANUP, 'cleanup']
  , [SpoofStatusType.NOTSPOOFING, 'not spoofing']
  , [SpoofStatusType.SPOOFING, 'spoofing']

]);

export function SpoofStatusTypeDecorator(constructor: Function) {
  constructor.prototype.SpoofStatusType = SpoofStatusType;
}
