import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';

// Models
import { SearchCustomer } from '@models/search-customer.model';

// Services
import { HttpService } from '@services/http.service';

// Enums
import { CustomerSearchType } from '@enums/customer-search-type.enum';

@Injectable()
export class CustomerSearchService {

  constructor(private _httpService: HttpService) {}

  /**
   * Search for customer by:
   *  customer-search-type.enum (EMAIL, LOYALTYCARD, PHONE, USERNAME)
   *
   * @param {CustomerSearchType} searchType The customer search type
   * @param {string} value The search query string
   * @returns {Observable<SearchCustomer>} Observable with CustomerPhone
   */
  public searchCustomers ( searchType: String, value: string ): Observable<SearchCustomer[]> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.SEARCH.BASE, null, { type: searchType, value: value });
  }

}

