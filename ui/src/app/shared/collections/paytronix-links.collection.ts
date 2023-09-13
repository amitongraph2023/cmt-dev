import { PermissionType } from '@enums/permission-type.enum';
import { PaytronixNavLink } from '@components/paytronix-navbar/paytronix-navbar.component';

export const PAYTRONIX_NAV_LINKS: PaytronixNavLink[] = [
  {
    display: 'Paytronix Balance'
    , route: ['/paytronix/balance']
    , isActive: false
    , isEnabled: true
    , permissions: [
      PermissionType.ADMIN
      , PermissionType.CBSS
      , PermissionType.CBSS_MANAGER
      , PermissionType.COFFEE
      , PermissionType.PROD_SUPPORT
      , PermissionType.READ_ONLY
      , PermissionType.SECURITY
    ]
  },
  {
    display: 'Transaction History'
    , route: ['/paytronix/transactions']
    , isActive: false
    , isEnabled: true
    , permissions: [
      PermissionType.ADMIN
      , PermissionType.CBSS
      , PermissionType.CBSS_MANAGER
      , PermissionType.COFFEE
      , PermissionType.PROD_SUPPORT
      , PermissionType.READ_ONLY
      , PermissionType.SECURITY
    ]
  },
  {
    display: 'Card Exchange'
    , route: ['/paytronix/card-exchange']
    , isActive: false
    , isEnabled: true
    , permissions: [
      PermissionType.ADMIN
      , PermissionType.CBSS
      , PermissionType.CBSS_MANAGER
      , PermissionType.COFFEE
      , PermissionType.PROD_SUPPORT
      , PermissionType.SECURITY
    ]
  },
  {
    display: 'Missed Visit'
    , route: ['/paytronix/missed-visit']
    , isActive: false
    , isEnabled: true
    , permissions: [
      PermissionType.ADMIN
      , PermissionType.CBSS
      , PermissionType.CBSS_MANAGER
      , PermissionType.COFFEE
      , PermissionType.PROD_SUPPORT
      , PermissionType.SECURITY
    ]
  }
];
