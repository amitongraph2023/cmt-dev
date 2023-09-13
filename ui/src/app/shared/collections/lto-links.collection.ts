// Components
import { LtoNavLink } from '@components/lto-navbar/lto-navbar.component';

// Enums
import { PermissionType } from '@enums/permission-type.enum';

export const LTO_NAV_LINKS: LtoNavLink[] = [
  {
    display: 'Find By Code'
    , route: ['/lto/code']
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
