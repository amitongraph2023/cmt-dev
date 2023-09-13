import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';
import { CONSTANTS } from '../../constants';
import { GiftCoffeeEmail } from '@models/gift-coffee-email.model';

@Injectable()
export class PngService {

  constructor(private _httpService: HttpService) {}

  public resendGiftCoffeeSubscription(giftCode: string,
                                      giftCoffeeEmail: GiftCoffeeEmail
  ): Observable<boolean> {
    return this._httpService.doPost(
      CONSTANTS.API_ROUTES.PNG.RESEND_GIFT_COFFEE_SUBSCRIPTION, giftCoffeeEmail, {giftCode: giftCode});
  }
}
