export enum PermissionType {
  ADMIN
  , ALL
  , CBSS
  , CBSS_MANAGER
  , COFFEE
  , PROD_SUPPORT
  , READ_ONLY
  , SALES_ADMIN
  , SECURITY

}

export namespace PermissionType {
  export function key(): Array<string> {
    const keys = Object.keys(PermissionType);
    return keys.slice(keys.length / 2, keys.length - 1);
  }
}

export const PermissionTypeMap = new Map<PermissionType, string>([
  [PermissionType.ADMIN, 'admin']
  , [PermissionType.ALL, 'all']
  , [PermissionType.CBSS, 'cbss']
  , [PermissionType.CBSS_MANAGER, 'cbssManager']
  , [PermissionType.COFFEE, 'coffee']
  , [PermissionType.PROD_SUPPORT, 'prodSupport']
  , [PermissionType.READ_ONLY, 'readOnly']
  , [PermissionType.SALES_ADMIN, 'salesAdmin']
  , [PermissionType.SECURITY, 'security']
]);

export function PermissionTypeDecorator(constructor: Function) {
  constructor.prototype.PermissionType = PermissionType;
}
