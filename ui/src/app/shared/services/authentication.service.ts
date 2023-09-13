import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { CookieService } from 'ngx-cookie';
import { Observable } from 'rxjs/Observable';
import { isNullOrUndefined } from 'util';

import { CONSTANTS } from '../../constants';
// Enums
import { AppPropertyType } from '@enums/app-property-type.enum';
import { PermissionType, PermissionTypeMap } from '@enums/permission-type.enum';
// Models
import { AuthRequest } from '@models/auth-request.model';
import { AuthenticationToken } from '@models/authentication-token.model';
import { Permissions } from '@models/permissions.model';
// Services
import { AppConfigService } from '@services/app-config.service';
import { CateringRedirectPropagatorService } from '@services/catering-redirect-propagator.service';
import { ErrorPropagatorService } from '@services/error-propagator.service';
import { HttpService } from './http.service';
import { SessionTimerService } from './session-timer.service';

@Injectable()
export class AuthenticationService {

  constructor(private _appPropertiesService: AppConfigService,
              private _cateringRedirectPropagationService: CateringRedirectPropagatorService,
              private _cookieService: CookieService,
              private _errorPropagatorService: ErrorPropagatorService,
              private _http: HttpClient,
              private _httpService: HttpService,
              private _router: Router,
              private _sessionTimer: SessionTimerService) {}

  /**
   * Checks if the user has the permissions to view the requested url
   */
  public checkCredentials(): Observable<boolean> {
    const permissions = this.loadPermissions();
    const appRoute = this._router.url;
    let isAllowed = false;

    if (isNullOrUndefined(permissions)) {
      this._errorPropagatorService.setError('You do not have the permissions to access this page. Redirecting you to home.');
      this._router.navigate(['/home']);
      return Observable.of(false);
    }

    if (appRoute === '/home') {
      return Observable.of(true);
    }

    if (permissions.admin) {
      return Observable.of(true);
    } else {
      return this._appPropertiesService.getPermissionWhitelist(AppPropertyType.PERMISSION_WHITELIST).map(whiteLists => {

        if (permissions.cbss) {
          isAllowed = isAllowed || this.canView(appRoute, whiteLists.get('cbss'));
        }
        if (permissions.cbssManager) {
          isAllowed = isAllowed || this.canView(appRoute, whiteLists.get('cbss_manager'));
        }
        if (permissions.coffee) {
          isAllowed = isAllowed || this.canView(appRoute, whiteLists.get('coffee'));
        }
        if (permissions.prodSupport) {
          isAllowed = isAllowed || this.canView(appRoute, whiteLists.get('prod_support'));
        }
        if (permissions.readOnly) {
          isAllowed = isAllowed || this.canView(appRoute, whiteLists.get('read_only'));
        }
        if (permissions.salesAdmin) {
          isAllowed = isAllowed || this.canView(appRoute, whiteLists.get('sales_admin'));
        }
        if (permissions.security) {
          isAllowed = isAllowed || this.canView(appRoute, whiteLists.get('security'));
        }

        if (!isAllowed) {
          this._errorPropagatorService.setError('You do not have the permissions to access this page. Redirecting you to home.');
          this._router.navigate(['/home']);
        }
        return isAllowed;
      });
    }
  }

  /**
   * Checks if the user has permission to access the requested component
   */
  public checkComponentPrivilege(checkMethod: string): Observable<boolean> {
    const permissions = this.loadPermissions();
    let isAllowed = false;

    if (permissions.admin) {
      return Observable.of(true);
    } else {
      return this._appPropertiesService.getPermissionWhitelist(AppPropertyType.PERMISSION_WHITELIST).map(whiteLists => {

        if (permissions.cbss) {
          isAllowed = whiteLists.get('cbss').includes(checkMethod);
        }
        if (permissions.cbssManager) {
          isAllowed = whiteLists.get('cbss_manager').includes(checkMethod);
        }
        if (permissions.coffee) {
          isAllowed = whiteLists.get('coffee').includes(checkMethod);
        }
        if (permissions.prodSupport) {
          isAllowed = whiteLists.get('prod_support').includes(checkMethod);
        }
        if (permissions.readOnly) {
          isAllowed = whiteLists.get('read_only').includes(checkMethod);
        }
        if (permissions.salesAdmin) {
          isAllowed = whiteLists.get('sales_admin').includes(checkMethod);
        }
        if (permissions.security) {
          isAllowed = whiteLists.get('security').includes(checkMethod);
        }

        return isAllowed;
      });
    }
  }

  /**
   * Gets session (which resets the expiration of the SSO session)
   *
   * @returns {Observable<boolean>} If session is active or not
   */
  public isSessionActive(): Observable<boolean> {
    return this._httpService.doGetResponse(CONSTANTS.API_ROUTES.AUTHENTICATION.BASE).map(response => {
      if (response.status === 200) {
        this._sessionTimer.resetTimer();
        return true;
      } else {
        this._sessionTimer.stopTimer();
        return false;
      }
    }, () => {
      this._sessionTimer.stopTimer();
      return false;
    });
  }

  /**
   * Checks if any of the permissions in the array is the current user's permissions
   *
   * @param permissions The array of permissions to check
   * @returns {boolean} The permissible status
   */
  public isPermissible(permissions: PermissionType[]): boolean {
    const currentPermissions = this.loadPermissions();

    // If user has admin privilege, show nav link
    if (currentPermissions.admin) {
      return true;
    }

    // Loop through the permissions array
    for (const permission of permissions) {
      // Check if the permission is all
      if (permission === PermissionType.ALL) {
        // Return as we don't need to continue checking
        return true;
      }

      // Check if the permission is true
      if (currentPermissions[PermissionTypeMap.get(permission)]) {
        return true;
      }
    }

    return false;
  }

  /**
   * Loads the permissions from the session storage
   *
   * @returns {Permissions} The permissions object for the user
   */
  public loadPermissions(): Permissions {
    // Get the permissions
    const permissions = <Permissions> JSON.parse(sessionStorage.getItem(CONSTANTS.STUBS.PERMISSIONS_STUB));

    // Ensure that there is a permissions object
    if (permissions !== null) {
      return permissions;
    } else {
      // Only re-route if not at login page already
      if (this._router.url !== '/login') {
        // Reroute to the login page in order to reload the permissions.
        this._router.navigate(['/login']);
      }

      // Return a new permissions object in order to eliminate the possibility of an error message
      return new Permissions();
    }
  }

  /**
   * Handles the logging in attempt and setting of appropriate settings
   *
   * @param {AuthRequest} authRequest The username and password object to send
   * @param {string} r The usr to redirect to
   */
  public login(authRequest: AuthRequest, r: string): void {
    const permissions = new Permissions();

    this.authenticate(authRequest)
      .subscribe(authResponse => {
        this._sessionTimer.startTimer(this);

        sessionStorage.setItem(CONSTANTS.STUBS.USER_STUB, JSON.stringify(authResponse));

        const role = PermissionType[authResponse.role];
        switch (role) {
          case PermissionType.ADMIN:
            permissions.admin = true;
            break;
          case PermissionType.CBSS:
            permissions.cbss = true;
            break;
          case PermissionType.CBSS_MANAGER:
            permissions.cbssManager = true;
            break;
          case PermissionType.COFFEE:
            permissions.coffee = true;
            break;
          case PermissionType.PROD_SUPPORT:
            permissions.prodSupport = true;
            break;
          case PermissionType.READ_ONLY:
            permissions.readOnly = true;
            break;
          case PermissionType.SALES_ADMIN:
            permissions.salesAdmin = true;
            // This is to catch the catering user at login and redirect if set in appConfig
            this._cateringRedirectPropagationService.setHasSalesAdminPermission(true);
            break;
          case PermissionType.SECURITY:
            permissions.security = true;
            break;
        }

        sessionStorage.setItem(CONSTANTS.STUBS.PERMISSIONS_STUB, JSON.stringify(permissions));

        this._cookieService.putObject('proxyUser', authResponse, {
          path: '/',
          domain: '.panerabread.com',
          expires: '',
          secure: true
        });

        if (!isNullOrUndefined(r)) {
          const split = r.split(';');
          const route = split.splice(0, 1)[0];
          if (split.length > 0) {
            const params = {};
            split.forEach((param) => {
              const splitParam = param.split('=');
              if (splitParam.length > 1) {
                params[splitParam[0]] = splitParam[1];
              }
            });
            this._router.navigate([route, params]);
          } else {
            this._router.navigate([route]);
          }
        } else {
          this._router.navigate(['/']);
        }
      });
  }

  /**
   * Handles the logging out and the request to logout to the api
   */
  public logout(): void {
    sessionStorage.clear();

    this._sessionTimer.stopTimer();

    this._httpService.doDelete(CONSTANTS.API_ROUTES.AUTHENTICATION.BASE).subscribe(() => {
      this._router.navigate(['/login']);
    }, () => {
      this._router.navigate(['/login']);
    });
  }

  /**
   * Makes the call to the api in order to authenticate the user
   *
   * @param {AuthRequest} authRequest The username and password object to send
   * @returns {Observable<AuthenticationToken>} The response from the server
   */
  private authenticate(authRequest: AuthRequest): Observable<AuthenticationToken> {
    return this._httpService.doPost<AuthenticationToken>(CONSTANTS.API_ROUTES.AUTHENTICATION.BASE, authRequest);
  }

  /**
   * Loops through the provided (wildcard) whitelist and checks if the current resource
   *
   * @param {string} currResource The current resource
   * @param {string[]} whiteList The string array of wildcard available routes
   * @returns {boolean} Can view route
   */
  private canView(currResource: string, whiteList: string[]): boolean {
    let canView = false;

    if (isNullOrUndefined(whiteList) || whiteList.length === 0) {
      return false;
    }

    canView = canView || whiteList.some((route) => {
      if (!isNullOrUndefined(currResource.match(route))) {
        return true;
      }
    });

    return canView;
  }
}
