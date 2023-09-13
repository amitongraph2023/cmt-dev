import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';
import { isNullOrUndefined } from 'util';

@Injectable()
export class HttpService {

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type':  'application/json'
    })
  };

  constructor(private _http: HttpClient) {}

  /**
   * Makes delete call to the api
   *
   * @param {string} uri The api uri
   * @param paramsMap The params to replace in the format of {varName: value}
   * @param queryStringMap The map of params to build the query string from
   * @returns {Observable<{}>} Empty observable
   */
  public doDelete(uri: string, paramsMap: any = null, queryStringMap: any = null): Observable<{}> {
    return this._http.delete(this.buildUrl(uri, paramsMap, queryStringMap));
  }

  /**
   * Makes get call to the api
   *
   * @param {string} uri The api uri
   * @param paramsMap The params to replace in the format of {varName: value}
   * @param queryStringMap The map of params to build the query string from
   * @returns {Observable<T>} Observable of the response from the api
   */
  public doGet<T>(uri: string, paramsMap: any = null, queryStringMap: any = null): Observable<T> {

    return this._http.get<T>(this.buildUrl(uri, paramsMap, queryStringMap));
  }

  /**
   * Makes get call to the api
   *
   * @param {string} uri The api uri
   * @param paramsMap The params to replace in the format of {varName: value}
   * @param queryStringMap The map of params to build the query string from
   * @returns {Observable<HttpResponse<T>>} Observable of the response from the api
   */
  public doGetResponse<T>(uri: string, paramsMap: any = null, queryStringMap: any = null): Observable<HttpResponse<T>> {
    return this._http.get<T>(this.buildUrl(uri, paramsMap, queryStringMap), { observe: 'response' });
  }

  /**
   * Makes post call to the api
   *
   * @param {string} uri The api uri
   * @param data The object to send to the api
   * @param paramsMap The params to replace in the format of {varName: value}
   * @param queryStringMap The map of params to build the query string from
   * @returns {Observable<T>} Observable of the response from the api
   */
  public doPost<T>(uri: string, data: any, paramsMap: any = null, queryStringMap: any = null): Observable<T> {
    return this._http.post<T>(this.buildUrl(uri, paramsMap, queryStringMap), data, this.httpOptions);
  }

  /**
   * Makes put call to the api
   *
   * @param {string} uri The api uri
   * @param data The object to send to the api
   * @param paramsMap The params to replace in the format of {varName: value}
   * @param queryStringMap The map of params to build the query string from
   * @returns {Observable<T>} Observable of the response from the api
   */
  public doPut<T>(uri: string, data: any, paramsMap: any = null,  queryStringMap: any = null): Observable<T> {
    return this._http.put<T>(this.buildUrl(uri, paramsMap, queryStringMap), data, this.httpOptions);
  }

  /**
   * Creates a query string from the passed in map and appends it to the uri
   *
   * @param {string} uri The URI of the request
   * @param queryStringMap The map of params to build the query string from
   * @returns {string} The finished URI
   */
  protected buildQueryString(uri: string, queryStringMap: any): string {
    if (!isNullOrUndefined(uri) && !isNullOrUndefined(queryStringMap)) {
      const params = [];

      // Loop through the params
      for (const key of Object.keys(queryStringMap)) {
        params.push(key + '=' + queryStringMap[key]);
      }

      uri += '?' + params.join('&');
    }

    return uri;
  }

  /**
   * Builds the url string and replaces the path variables with the
   * Provided params if any are provided
   *
   * @param {string} uri The uri to prepare
   * @param paramsMap The params map to use to replace the path variables
   * @param queryStringMap The map of params to build the query string from
   * @returns {string} The prepared url
   */
  private buildUrl(uri: string, paramsMap: any = null, queryStringMap: any = null): string {
    if (!isNullOrUndefined(queryStringMap)) {
      uri = this.buildQueryString(uri, queryStringMap);
    }

    let urlString = CONSTANTS.API_ROUTES.API_BASE + uri.replace(/\+/gi, '%2B');

    // Pre-pend the server if on localhost:3000
    if (location.href.indexOf('localhost:3000') !== -1) {
      urlString = 'http://localhost:8080' + urlString;
    }
    // Pre-pend the server if on localhost.panerabread.com:3000
    if (location.href.indexOf('localhost.panerabread.com:3000') !== -1) {
      urlString = 'http://localhost.panerabread.com:8080' + urlString;
    }
    // Check if there were params to replace
    if (!isNullOrUndefined(paramsMap)) {
      // Loop through the params
      for (const key of Object.keys(paramsMap)) {
        // Replace the placeholder with the value
        urlString = urlString.replace('{' + key + '}', paramsMap[key]);
      }
    }

    return urlString;
  }
}
