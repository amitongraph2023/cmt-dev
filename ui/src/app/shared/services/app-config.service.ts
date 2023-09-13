import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { isNullOrUndefined } from 'util';

import { CONSTANTS } from '../../constants';
// Enums
import { AppPropertyType } from '@enums/app-property-type.enum';
import { AppConfigSortColumn } from '@enums/sort/app-config-sort-column.enum';
import { SortDirection } from '@enums/sort/sort-direction.enum';
// Models
import { AppConfig } from '@models/app-config.model';
// Services
import { HttpService } from '@services/http.service';

const PAGE_SIZE = 20;

@Injectable()
export class AppConfigService {

  private anonymousOrderURL: Observable<string>;
  private routeWhiteLists: Observable<Map<string, string[]>>;

  constructor(private _httpService: HttpService) {}

  /**
   * Adds a property
   *
   * @param {AppConfig} appConfig The new AppConfig
   * @param {string} service: The service run against
   * @returns {Observable<AppConfig>} The response
   */
  public addProperty(appConfig: AppConfig, service: string): Observable<AppConfig> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CONFIG.BASE, appConfig, null, {service: service});
  }

  /**
   * Deletes a property
   *
   * @param {number} id The id of the property to delete
   * @param {string} service: The service run against
   * @returns {Observable<{}>} The response
   */
  public deleteProperty(id: number, service: string): Observable<{}> {
    return this._httpService.doDelete(CONSTANTS.API_ROUTES.CONFIG.BY_ID, {id: id},  {service: service});
  }

  /**
   * Retrieves app config properties from the server
   *
   * @param query The string to search for
   * @param {number} pageNumber The page number to get
   * @param {number} pageSize The number of elements in the page
   * @param {SortDirection} dir The direction to sort
   * @param {AppConfigSortColumn} col The column to sort
   * @param {string} service The service to perform query against
   * @returns {Observable<AppConfig[]>} Observable with array of AppConfig
   */
  public getPropertiesPaged(
    query: string
    , pageNumber = 1
    , pageSize = PAGE_SIZE
    , dir: SortDirection
    , col: AppConfigSortColumn
    , service: string = 'CMT'
  ): Observable<AppConfig[]> {
    const queryString = {page: pageNumber, size: pageSize};

    if (!isNullOrUndefined(query)) {
      queryString['query'] = query;
    }
    if (!isNullOrUndefined(dir)) {
      queryString['dir'] = SortDirection[dir];
    }
    if (!isNullOrUndefined(col)) {
      queryString['col'] = AppConfigSortColumn[col];
    }
    if (!isNullOrUndefined(service)) {
      queryString['service'] = service;
    }

    return this._httpService.doGet(CONSTANTS.API_ROUTES.CONFIG.BASE, null, queryString);
  }

  /**
   * Gets the permissions for the user role
   *
   * @param {AppPropertyType} propertyType The property type to get
   * @returns {Observable<any>} The property values
   */
  public getPermissionWhitelist(propertyType: AppPropertyType): Observable<any> {
    switch (propertyType) {
      case AppPropertyType.PERMISSION_WHITELIST:
        return this.getPermissionWhiteLists();
    }
  }

  /**
   * Updates an AppConfig property
   *
   * @param {number} id The id of the property to update
   * @param {AppConfig} appConfig The updated AppConfig
   * @param {string} service The subdomain/server to perform update against
   * @returns {Observable<AppConfig>} The saved AppConfig
   */
  public updateAppConfig(id: number, appConfig: AppConfig, service: string): Observable<AppConfig> {
    return this._httpService.doPut<AppConfig>(CONSTANTS.API_ROUTES.CONFIG.BY_ID, appConfig,
      {id: id}, {service: service});
  }

  /**
   * Gets the local stored copy of the whitelists or retrieves it from the server.
   *
   * @returns {Observable<Map<string, string[]>>}
   */
  private getPermissionWhiteLists(): Observable<Map<string, string[]>> {
    if (isNullOrUndefined(this.routeWhiteLists)) {
      this.routeWhiteLists = this._httpService.doGet(CONSTANTS.API_ROUTES.CONFIG.PERMISSION_WHITELISTS).map(response => {
        const routeWhiteLists = new Map<string, string[]>();
        for (const key in response) {
          if (response.hasOwnProperty(key)) {
            routeWhiteLists.set(this.stripQualifiers(key), response[key]);
          }
        }

        return routeWhiteLists;
      });

      return this.routeWhiteLists;
    } else {
      return this.routeWhiteLists;
    }
  }

  /**
   * Strips off the leading qualifiers of the app code
   *
   * @param {string} s The string to strip
   * @param {number} numberToKeep The number of qualifiers to strip
   * @returns {string} The stripped string
   */
  private stripQualifiers(s: string, numberToKeep = 1): string {
    if (numberToKeep < 1) {
      numberToKeep = 1;
    }

    const a = s.split('.');
    return a.slice(a.length - numberToKeep).join('.');
  }
}
