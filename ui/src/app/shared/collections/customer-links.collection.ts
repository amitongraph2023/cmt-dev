import { PermissionType } from '@enums/permission-type.enum';
import { CustNavLink } from '@components/customer-navbar/customer-navbar.component';

export const CUST_NAV_LINKS: CustNavLink[] = [
  {
    display: 'Customer Info'
    , route: ['/customer/customer-info']
    , isActive: false
    , isEnabled: true
    , permissions: [
      PermissionType.ALL
    ]
  }
  , {
    display: 'Phones'
    , route: ['/customer/phones']
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
    display: 'Addresses'
    , route: ['/customer/addresses']
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
    display: 'Emails'
    , route: ['/customer/emails']
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
    display: 'Payment Options'
    , route: ['/customer/payment-options']
    , isActive: false
    , isEnabled: true
    , permissions: [
      PermissionType.ALL
    ]
  }
  , {
    display: 'Preferences'
    , route: ['/customer/preferences']
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
    display: 'Communication Preferences'
    , route: ['/customer/subscriptions']
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
    display: 'MFA'
    , route: ['/customer/mfa']
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
    display: 'Cof Sub Use'
    , route: ['/customer/coffee-subscription-usage']
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
