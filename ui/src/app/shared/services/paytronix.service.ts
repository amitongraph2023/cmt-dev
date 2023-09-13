import { Injectable } from '@angular/core';
import { CONSTANTS } from '../../constants';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';

import { PaytronixBalance } from '@models/paytronix/paytronix-balance.model';
import { PaytronixTransactions } from '@models/paytronix/paytronix-transactions.model';
import { PaytronixWallets } from '@models/paytronix/paytronix-wallets';

@Injectable()
export class PaytronixService {
  constructor(private _httpService: HttpService) {}

  public getBalance(cardNumber: string): Observable<PaytronixBalance> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.PAYTRONIX.BALANCE, {cardNumber: cardNumber});
  }

  public getTransactionHistory(cardNumber: string, startDate: string): Observable<PaytronixTransactions> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.PAYTRONIX.TRANSACTION_HISTORY, {cardNumber: cardNumber, startDate: startDate});
  }

  public getWalletCodes(): Observable<PaytronixWallets> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.PAYTRONIX.WALLET_CODES);
  }

}
