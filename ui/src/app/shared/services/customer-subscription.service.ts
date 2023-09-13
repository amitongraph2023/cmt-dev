import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';
import { CONSTANTS } from '../../constants';
import { CoffeeSubscriptionUsage } from '@models/coffee-subscription-usage.model';

@Injectable()
export class CustomerSubscriptionService {

  constructor(private _httpService: HttpService) {
  }

  public searchCoffeeSubscriptionUsage(customerId: number): Observable<CoffeeSubscriptionUsage[]> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.CUSTOMER.COFFEE_SUBSCRIPTION_USAGE.BASE, { customerId: customerId });
  }

  public cancelCoffeeSubscription(customerId: number, programId: number): Observable<any> {
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.COFFEE_SUBSCRIPTION_USAGE.CANCEL, null,{customerId: customerId, programId: programId})
  }

}
