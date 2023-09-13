import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';
// Models
import { CustomerPreferences } from '@models/customer-preferences.model';
import { GeneralPreferencesValue } from '@models/general-preference-value.model';
import { PersonGeneralPreference } from '@models/person-general-preference.model';
// Services
import { CustomerPreferenceType } from '@enums/customer-preference-type.enum';
import { HttpService } from '@services/http.service';
import { StaticDataService } from '@services/static-data.service';
// Enums
import { StaticType } from '@enums/static-type.enum';



@Injectable()
export class CustomerPreferencesService {

  private allFoodPreferences: GeneralPreferencesValue[] = [];
  private allGatherPreferences: GeneralPreferencesValue[] = [];

  constructor(
    private _httpService: HttpService
    , private _staticDataService: StaticDataService
  ) {
    let generalPreferenceValues: GeneralPreferencesValue[] = [];
    this._staticDataService.getStaticDataByType(StaticType.generalPreferenceValue).subscribe(
        gpv => generalPreferenceValues = gpv,
        error => console.error(error),
        () => {
          generalPreferenceValues.forEach(gpv => {
            if (gpv.type.toString() === CustomerPreferenceType[CustomerPreferenceType.DIETARY]) {
              this.allFoodPreferences.push(gpv);
            } else if (gpv.type.toString() === CustomerPreferenceType[CustomerPreferenceType.GATHER]) {
              this.allGatherPreferences.push(gpv);
            }
          });
        }
      );
  }

  /**
   * Return all gather preferences loaded as part of constructor from the chub static end point
   */
  public getAllGatherPreferences(): PersonGeneralPreference[] {
    return this.allGatherPreferences.sort( (a, b) => a.sortKey > b.sortKey ? 1 : a.sortKey === b.sortKey ? 0 : -1);
  }

  /**
   * Returns all food preferences with current checkbox values
   * Uses the values loaded as part of the constructor from the chub static end point
   *
   * @param foodPreferences
   * @return allFoodPreferneces
   */
  public getAllFoodPreferences(foodPreferences: PersonGeneralPreference[]): PersonGeneralPreference[] {
    const returnAllFoodPreferences: PersonGeneralPreference[] = [];
    this.allFoodPreferences.sort( (a, b) => a.sortKey > b.sortKey ? 1 : a.sortKey === b.sortKey ? 0 : -1).forEach(fp => {
      returnAllFoodPreferences.push(new PersonGeneralPreference(
        fp.code, fp.displayName, ( JSON.stringify(foodPreferences).indexOf(JSON.stringify(
          new PersonGeneralPreference(fp.code, fp.displayName))) >= 0 )));
    });

    return returnAllFoodPreferences;
  }

  /**
   * Retrieves User Preferences from the server by customerId
   *  getUserPreferences, Required Authority: ADMIN, CBSS, CBSS_MANAGER, PROD_SUPPORT
   *
   * @param {number} customerId The id of the customer
   * @returns {Observable<CustomerPreferences>} Observable with CustomerPreferences
   */
  public getCustomerPreferences(customerId: number): Observable < CustomerPreferences > {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.PREFERENCES.BASE, {customerId: customerId});
  }

  /**
   * Update customer preferences
   *
   * @param {number} customerId The id of the customer
   * @param {CustomerPreferences} customerPreferences The updated CustomerPreferences object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateCustomerPreferences(customerId: number, customerPreferences: CustomerPreferences ): Observable < {} > {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.PREFERENCES.BASE, customerPreferences, {customerId: customerId});
  }

  /**
   * Update customer food preferences
   *
   * @param {number} customerId The id of the customer
   * @param {PersonGeneralPreference} foodPreference The updated food preferences in a PersonalGeneralPreference object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateCustomerFoodPreferences(customerId: number, foodPreference: PersonGeneralPreference[]): Observable < {} > {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.PREFERENCES.BY_TYPE, foodPreference,
      {customerId: customerId, type: CustomerPreferenceType[CustomerPreferenceType.DIETARY].toLowerCase()});
  }

  /**
   * Update customer gather preferences
   *
   * @param {number} customerId The id of the customer
   * @param {PersonGeneralPreference} gatherPreference The updated gather preferences in a PersonalGeneralPreference object
   * @returns {Observable<{}>} Observable of empty set
   */
  public updateCustomerGatherPreferences(customerId: number, gatherPreference: PersonGeneralPreference): Observable < {} > {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.PREFERENCES.BY_TYPE, gatherPreference,
      {customerId: customerId, type: CustomerPreferenceType[CustomerPreferenceType.GATHER].toLowerCase()});
  }

}
