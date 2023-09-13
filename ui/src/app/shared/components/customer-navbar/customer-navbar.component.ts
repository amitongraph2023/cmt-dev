import { Component, Input, OnChanges, SimpleChange } from '@angular/core';
import { PermissionType, PermissionTypeMap } from '@enums/permission-type.enum';
import { Permissions } from '@models/permissions.model';
import { AuthenticationService } from '@services/authentication.service';
import { Router } from '@angular/router';
import { isNullOrUndefined } from 'util';
import * as _ from 'lodash';
import { CUST_NAV_LINKS } from '@collections/customer-links.collection';

export class CustNavLink {
  public display: string;
  public route: string[];

  public isActive: boolean;
  public isEnabled: boolean;
  public permissions: PermissionType[];

}

@Component({
  selector: 'app-customer-navbar',
  templateUrl: './customer-navbar.component.html',
  styleUrls: ['./customer-navbar.component.scss']
})
export class CustomerNavbarComponent implements OnChanges {

  @Input('appRoute') appRoute: string;

  public shownNavLinks: CustNavLink[] = [];
  public untouchedNavLinks: CustNavLink[] = [];
  public user = '';

  private permissions: Permissions;

  constructor(
    private _authService: AuthenticationService,
    private _router: Router) {
    this.permissions = this._authService.loadPermissions();

    this.setShownNavLinks();
  }

  ngOnChanges(changes: {[propertyName: string]: SimpleChange}): void {
    if (changes.hasOwnProperty('appRoute')) {
      if (!isNullOrUndefined(this.appRoute)) {
        this.updateActiveNavLink();
      }
    }
  }

  /**
   * Handles the clicking of the menu items
   *
   * @param {number} i The nav link index
   */
  public navLinkClick(i: number): void {
    // Error check the index value
    if (!isNullOrUndefined(i) && i < this.shownNavLinks.length && typeof this.shownNavLinks[i] !== 'undefined') {
      // Get the nav link
      const navLink = this.shownNavLinks[i];

      if (navLink.route !== null && navLink.route.length > 0) {
        // Go to the route
        this._router.navigate(navLink.route);
      }
    }
  }

  /**
   * Checks if any of the permissions in the array for the given nav link is the current user's permissions
   *
   * @param navLink The link to shown status of
   * @returns {boolean} The shown status
   */
  private isNavLinkShown(navLink): boolean {
    // If user has admin privilege, show nav link
    if (this.permissions.admin) {
      return true;
    }
    // Loop through the permissions array
    if (!isNullOrUndefined(navLink.permissions)) {
      for (const permission of navLink.permissions) {
        // Check if the permission is all
        if (permission === PermissionType.ALL) {
          // Return as we don't need to continue checking
          return true;
        }
        // Check if the permission is true
        if (this.permissions[PermissionTypeMap.get(permission)]) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Loops through available nav links (and their children) and filters out links
   * That are either disabled or not allowed for the user's current permissions level
   */
  private setShownNavLinks(): void {
    // Loop through the nav links
    CUST_NAV_LINKS.forEach((navLink) => {
      // Check if this one is enabled and has the current permission
      if (this.isNavLinkShown(navLink)) {
        // Add the navLink to the shown array
        this.shownNavLinks.push(navLink);
      }
    });
    this.untouchedNavLinks = this.shownNavLinks;
  }

  /**
   * Updates the shown nav links with the new active route
   */
  private updateActiveNavLink(): void {
    const appRoute = (this.appRoute === '/') ? '' : this.appRoute;
    const navLinks = _.cloneDeep(this.untouchedNavLinks);

    // Loop through the nav links
    for (let i = 0; i < navLinks.length; i++) {
      const navLink = navLinks[i];

      // Check if this one is enabled and has the current permission
      if (this.isNavLinkShown(navLink)) {
        // Check if at this route
        if (navLink.route !== null && navLink.route.length > 0) {
          if ((appRoute === '' && navLink.route[0] === '') || ((appRoute !== '' && navLink.route[0] !== '')
            && (appRoute.indexOf(navLink.route[0]) > -1 || navLink.route[0].indexOf(appRoute) > -1))) {
            navLinks[i].isActive = true;
          }
        }
      }
    }
    this.shownNavLinks = navLinks;
  }
}
