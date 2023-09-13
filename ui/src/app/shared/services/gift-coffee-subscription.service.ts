import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { CONSTANTS } from '../../constants';

// Models


// Services
import { HttpService } from '@services/http.service';
import { CoffeeSubscriptionSearchType } from '@enums/coffee-subscription-search-type.enum';
import { SubscriptionServiceResults } from '@models/subscription-service-results.model';

@Injectable()
export class GiftCoffeeSubscriptionService {

  constructor(private _httpService: HttpService) {}

  /**
   * Add a customer phone number
   *
   * @param {number} customerId The id of the customer
   * @param {CustomerPhone} customerPhone  The new CustomerPhone
   * @returns {Observable<{}>} Observable of empty set
   */
  public searchGiftCoffeeSubscriptions(searchType: String, searchTerm: String):
    Observable<SubscriptionServiceResults> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.GIFT_COFFEE_SUBSCRIPTIONS.BASE, {searchType: searchType, searchTerm: searchTerm});
  }

}
