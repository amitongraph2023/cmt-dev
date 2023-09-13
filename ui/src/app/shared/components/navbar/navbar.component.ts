import { Component, Input, OnChanges, OnInit, SimpleChange } from '@angular/core';
import { Router } from '@angular/router';
import { isNullOrUndefined } from 'util';

import * as _ from 'lodash';

import { CONSTANTS } from '../../../constants';
// Collections
import { NAV_LINKS } from '@collections/nav-links.collection';
// Enums
import { PermissionType, PermissionTypeMap } from '@enums/permission-type.enum';
// Models
import { Permissions } from '@models/permissions.model';
import { SpoofButton } from '@models/spoof-button.model';
// Services
import { AuthenticationService } from '@services/authentication.service';
import { AuthenticationToken } from '@models/authentication-token.model';
import { CustomerService } from '@services/customer.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';
import { SpoofService } from '@services/spoof.service';
import { NavbarService } from '@services/navbar.service';
import { CustomerDetails } from '@models/customer-details.model';
import { AccountStatusType } from '@enums/account-status-type.enum';

// Declare $ as jQuery
declare var $: any;

// Declare the build version
declare var buildVersion: string;

export class NavLink {
  public display: string;
  public icon: string;
  public route: string[];

  public isActive: boolean;
  public isEnabled: boolean;
  public isSoftEnabled: boolean;

  public permissions: PermissionType[];

  public children: NavLink[];
  public showChildren: boolean;
}

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnChanges {
  @Input('appRoute') appRoute: string;

  public version: string;
  public menuToggleEnabled = false;
  public showMenu = true;
  public shownNavLinks: NavLink[] = [];
  public untouchedNavLinks: NavLink[] = [];
  public user = '';
  public customerDetails: CustomerDetails;

  public isSelectedCustomerActive: boolean;
  public isSelectedCustomerSuspended: boolean;
  public isSelectedCustomerPending: boolean;

  public isCustomerSelected: boolean;
  public spoofButtons: SpoofButton[] = [];
  public nonMyPaneraSpoofButtons: SpoofButton[] = [];

  public canViewSpoofButtons = false;

  private permissions: Permissions;

  private selectedNonMyPaneraButton: SpoofButton = null;

  constructor(
    private _authService: AuthenticationService,
    private _customerPropagatorService: CustomerPropagatorService,
    private _customerService: CustomerService,
    private _navbarService: NavbarService,
    private _router: Router,
    private _spoofService: SpoofService) {
    this.permissions = this._authService.loadPermissions();

    this._customerPropagatorService.customer$.subscribe(
      customerDetails => {
        this.customerDetails = customerDetails;
        this.isCustomerSelected = !(isNullOrUndefined(this.customerDetails));
        this.isSelectedCustomerActive = !(isNullOrUndefined(this.customerDetails))
          && this.customerDetails.status.toLocaleString() === AccountStatusType[AccountStatusType.ACTIVE];
        this.isSelectedCustomerSuspended = !(isNullOrUndefined(this.customerDetails))
          && this.customerDetails.status.toLocaleString() === AccountStatusType[AccountStatusType.SUSPENDED];
        this.isSelectedCustomerPending = !(isNullOrUndefined(this.customerDetails))
          && this.customerDetails.status.toLocaleString() === AccountStatusType[AccountStatusType.PENDING];
      }
    );

    this.setShownNavLinks();
    this.version = buildVersion;
  }

  ngOnChanges(changes: {[propertyName: string]: SimpleChange}): void {
    if (changes.hasOwnProperty('appRoute')) {
      if (!isNullOrUndefined(this.appRoute)) {
        this.updateActiveNavLink();
      }
    }

    this.isCustomerSelected = !(isNullOrUndefined(this.customerDetails));
    this.isSelectedCustomerActive = !(isNullOrUndefined(this.customerDetails))
      && this.customerDetails.status.toLocaleString() === AccountStatusType[AccountStatusType.ACTIVE];
    this.isSelectedCustomerSuspended = !(isNullOrUndefined(this.customerDetails))
      && this.customerDetails.status.toLocaleString() === AccountStatusType[AccountStatusType.SUSPENDED];
    this.isSelectedCustomerPending = !(isNullOrUndefined(this.customerDetails))
      && this.customerDetails.status.toLocaleString() === AccountStatusType[AccountStatusType.PENDING];
  }

  ngOnInit(): void {
		const userObject = <AuthenticationToken>JSON.parse(sessionStorage.getItem(CONSTANTS.STUBS.USER_STUB));
		if (!isNullOrUndefined(userObject)) {
			if (!isNullOrUndefined(userObject.displayName)) {
				this.user = userObject.displayName;
			} else if (!isNullOrUndefined(userObject.knownAs) && !isNullOrUndefined(userObject.lastName)) {
				this.user = userObject.knownAs + ' ' + userObject.lastName;
			} else if (!isNullOrUndefined(userObject.firstName) && !isNullOrUndefined(userObject.lastName)) {
				this.user = userObject.firstName + ' ' + userObject.lastName;
			} else if (!isNullOrUndefined(userObject.firstName) && isNullOrUndefined(userObject.lastName)) {
				this.user = userObject.firstName;
			} else if (isNullOrUndefined(userObject.firstName) && !isNullOrUndefined(userObject.lastName)) {
				this.user = userObject.lastName;
			} else if (isNullOrUndefined(userObject.firstName) && isNullOrUndefined(userObject.lastName) &&
				!isNullOrUndefined(userObject.username)) {
				this.user = userObject.username;
			} else {
				this.user = 'unknown';
			}

      this._authService.checkComponentPrivilege('spoof_buttons')
        .subscribe(canViewSpoofButtons => this.canViewSpoofButtons = canViewSpoofButtons);

      if ( isNullOrUndefined(this._spoofService.spoofButtons) ) {
        this._spoofService.getSpoofButtons(false).subscribe(
          (spoofButtons) => this.spoofButtons = spoofButtons,
          (error) => console.error(error),
          () => {
            this._spoofService.spoofButtons = this.spoofButtons;
          }
        );
      } else {
        this.spoofButtons = this._spoofService.spoofButtons;
      }

      this._spoofService.getSpoofButtons(true).subscribe(
        (nonMyPaneraSpoofButtons) => this.nonMyPaneraSpoofButtons = nonMyPaneraSpoofButtons,
        (error) => console.error(error)
      );

    }

    this._spoofService.changeSpoofStatus(false);

    this._navbarService.changeCollapsedState(false);

    this._navbarService.collapsed.subscribe(
      (collapsed) => this.showMenu = !collapsed,
      (error) => console.error(error)
    );

  }

  /**
   * Logs out and clears any set variables
   */
  public doLogout(): void {
    this._customerPropagatorService.setCustomerId(null);
    this._authService.logout();
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
      let hideCurrent = false;
      const navLink = this.shownNavLinks[i];

      // Check if there are children, if not check if there is a route
      if (navLink.children !== null && navLink.children.length > 0) {

        // Collapse other parent(s)
        for (let j = 0; j < this.shownNavLinks.length; j++) {
          if (this.shownNavLinks[j].showChildren) {
            if (i === j) {
              hideCurrent = true;
            }
            this.shownNavLinks[j].showChildren = false;
          }
        }

        // Toggle the showing of the children
        if (!hideCurrent) {
          navLink.showChildren = !navLink.showChildren;
        }
      } else if (navLink.route !== null && navLink.route.length > 0) {
        // Go to the route
        this._router.navigate(navLink.route);
      }
    }
  }

  /**
   * Handles the clicking of the sub menu items
   *
   * @param {number} i The parent nav link index
   * @param {number} j The child nav link index
   */
  public navSubLinkClick(i: number, j: number): void {
    // Error check the indices values
    if (!isNullOrUndefined(i) && i < this.shownNavLinks.length && typeof this.shownNavLinks[i] !== 'undefined'
      && !isNullOrUndefined(j) && j < this.shownNavLinks[i].children.length && typeof this.shownNavLinks[i].children[j] !== 'undefined') {
      // Get the nav link
      const navLink = this.shownNavLinks[i].children[j];

      // Check if there is a route
      if (navLink.route !== null && navLink.route.length > 0) {
        // Go to the route
        this._router.navigate(navLink.route);
      }
    }
  }

  /**
   * On continue button click, opens new tab that will set up the environment and redirect to the page
   */
  public onContinueButtonClick() {
    $('#leavingCMTConfirmationModal').modal('hide');
    window.open(window.location.origin + '#/new-tab;type=ANONYMOUS_ORDER;id='
      + this.selectedNonMyPaneraButton.name.toUpperCase() , '_blank' );
    this.selectedNonMyPaneraButton = null;
  }


  /**
   * On button click for NON-MYPANERA ORDER, confirms that they are leaving CMT
   */
  public onNonMyPaneraSpoofButtonClick(name: string) {
    this.selectedNonMyPaneraButton = this.nonMyPaneraSpoofButtons.filter(u => u.name === name)[0];
    const modalEl = $('#leavingCMTConfirmationModal');
    const _self = this;
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');

  }

  /**
   * On button click sets the spoofUnit in the spoofService and redirect to spoof component
   *
   * @param {string} unit The name of the unit to spoof (i.e. catering)
   */
  public onSpoofButtonClick(name: string) {
    this._spoofService.changeSpoofStatus(true);
    this.toggleMenu();
    this._spoofService.changeSpoofName(name);
    this._router.navigate(['spoof']);
  }

  /**
   * Toggles the Collapse/Show of the menu
   */
  public toggleMenu(): void {
    this._navbarService.changeCollapsedState(this.showMenu);
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
    NAV_LINKS.forEach((navLink) => {
      // Check if this one is enabled and has the current permission
      if (this.isNavLinkShown(navLink)) {
        // Check if there are children links
        if (navLink.children !== null && navLink.children.length > 0) {
          // Loop through the child nav links
          const shownChildren = [];
          navLink.children.forEach((navChildLink) => {
            // Check if this one is enabled and has the current permission
            if (this.isNavLinkShown(navChildLink)) {
              // Add the navLink to the shown array
              shownChildren.push(navChildLink);
            }
          });
          // Update the children links
          navLink.children = shownChildren;
        }

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

            // Enable the link if it is a soft enable (only shown if at the route)
            if (navLinks[i].isSoftEnabled) {
              navLinks[i].isEnabled = true;
            }
          } else {
            // Disable the link if it is a soft enable (only shown if at the route)
            if (navLinks[i].isSoftEnabled) {
              navLinks[i].isEnabled = false;
            }
          }
        }

        // Check if there are children links
        if (navLink.children !== null && navLink.children.length > 0) {
          // Loop through the child nav links
          for (let j = 0; j < navLink.children.length; j++) {
            const navChildLink = navLinks[i].children[j];

            // Check if at this route
            if (navChildLink.route !== null && navChildLink.route.length > 0) {
              if ((appRoute !== '' && navChildLink.route[0] !== '') && (appRoute.indexOf(navChildLink.route[0]) > -1
                || navChildLink.route[0].indexOf(appRoute) > -1)) {
                navLinks[i].isActive = false;
                navLinks[i].showChildren = true;
                navLinks[i].children[j].isActive = true;

                // Enable the link if it is a soft enable (only shown if at the route)
                if (navLinks[i].children[j].isSoftEnabled) {
                  navLinks[i].children[j].isEnabled = true;
                }
              } else {
                // Disable the link if it is a soft enable (only shown if at the route)
                if (navLinks[i].children[j].isSoftEnabled) {
                  navLinks[i].children[j].isEnabled = false;
                }
              }
            }
          }
        }
      }
    }

    this.shownNavLinks = navLinks;
  }
}
