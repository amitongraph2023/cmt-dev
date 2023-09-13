import { PermissionType } from '@enums/permission-type.enum';
import { SubNavLink } from "@components/subscription-navbar/subscription-navbar.component";

export const SUBS_NAV_LINKS: SubNavLink[] = [
  {
    display: 'Cof Sub Use'
    , route: ['/subscriptions/coffee-subscription-usage']
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
  }
  , {
    display: 'Customer Gift Coffee'
    , route: ['/subscriptions/gift-coffee-subscriptions']
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
  }
  , {
    display: 'Find Gift Coffee'
    , route: ['/subscriptions/gift-coffee']
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
  }
];
