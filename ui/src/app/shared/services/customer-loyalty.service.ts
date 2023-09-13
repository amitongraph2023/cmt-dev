import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';
import { CONSTANTS } from '../../constants';
import { CustomerCardExchange } from '@models/customer-card-exchange.model';
import { LoyaltyRewardsEnabled } from '@models/customer-loyalty-rewards-enabled.model';
import { Loyalty } from '@models/customer-loyalty.model';

@Injectable()
export class CustomerLoyaltyService {

  constructor(private _httpService: HttpService) {
  }

  /**
   * Exchange Loyalty Card for new Card number
   *
   * @param {number} customerId The id of the customer
   * @param {string} existingLoyaltyCard The existing Loyalty Card number
   * @param {CustomerCardExchange} dto with the new card number and regCode
   * @param {boolean} existingLoyaltyCard If true, only does card exchange in CHUB but not in Paytronix
   * @returns {Observable<{}>} Observable of empty set
   */
  public cardExchange(customerId: number, existingLoyaltyCard: string, dto: CustomerCardExchange, excludePX: boolean): Observable<{}> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.LOYALTY.CARD_EXCHANGE, dto,
      {customerId: customerId, existingLoyaltyCard: existingLoyaltyCard, excludePX: excludePX});
  }

  public updateLoyaltyAccount(customerId: number) {
    return this._httpService.doPut(CONSTANTS.API_ROUTES.CUSTOMER.LOYALTY.BASE, {}, {customerId: customerId});
  }

  public getLoyaltyRewardsEnabled(customerId: number) : Observable<Loyalty>{
	return this._httpService.doGet<Loyalty>(CONSTANTS.API_ROUTES.CUSTOMER.LOYALTY.REWARDS_ENABLED, {customerId: customerId});
  }

 public updateRewardsEnabledByCustomerId(customerId: number, rewardsEnabled: LoyaltyRewardsEnabled): Observable<LoyaltyRewardsEnabled> {
    return this._httpService.doPut<LoyaltyRewardsEnabled>(CONSTANTS.API_ROUTES.CUSTOMER.LOYALTY.REWARDS_ENABLED_UPDATE, rewardsEnabled, {customerId: customerId});
  }
}
