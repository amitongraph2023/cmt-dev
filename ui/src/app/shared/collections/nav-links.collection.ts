// Components
import { NavLink } from '@components/navbar/navbar.component';
// Enums
import { PermissionType } from '@enums/permission-type.enum';

export const NAV_LINKS: NavLink[] = [
  {
    display: 'Home',
    icon: 'home',
    route: ['home'],
    isActive: false,
    isEnabled: true,
    isSoftEnabled: false,
    permissions: [PermissionType.ALL],
    showChildren: false,
    children: null
  },
  {
    display: 'Customer',
    icon: 'bullet',
    route: ['/customer/customer-info'],
    isActive: false,
    isEnabled: true,
    isSoftEnabled: false,
    permissions: [PermissionType.ALL],
    showChildren: false,
    children: null
  },
  {
    display: 'Paytronix',
    icon: 'bullet',
    route: ['/paytronix/balance'],
    isActive: false,
    isEnabled: true,
    isSoftEnabled: false,
    permissions: [PermissionType.ALL],
    showChildren: false,
    children: null
  },
  {
    display: 'Subscriptions',
    icon: 'bullet',
      route: ['subscriptions/coffee-subscription-usage'],
    isActive: false,
    isEnabled: true,
    isSoftEnabled: false,
    permissions: [PermissionType.ALL],
    showChildren: false,
    children: null
  },
  {
    display: '---',
    icon: null,
    route: null,
    isActive: false,
    isEnabled: true,
    isSoftEnabled: false,
    permissions: [PermissionType.ADMIN],
    children: null,
    showChildren: false
  },
  {
    display: 'Admin',
    icon: 'cogs',
    route: ['/admin'],
    isActive: false,
    isEnabled: true,
    isSoftEnabled: false,
    permissions: [PermissionType.ADMIN],
    showChildren: false,
    children: [
      {
        display: 'App Config',
        icon: 'cog',
        route: ['/admin/app-config'],
        isActive: false,
        isEnabled: true,
        isSoftEnabled: false,
        permissions: [PermissionType.ADMIN],
        showChildren: false,
        children: null
      }
    ]
  }
];
