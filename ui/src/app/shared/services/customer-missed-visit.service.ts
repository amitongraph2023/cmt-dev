import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';
import { CONSTANTS } from '../../constants';

@Injectable()
export class CustomerMissedVisitService {

  constructor(private _httpService: HttpService) {
  }

  /**
   * Missed Visit
   *
   * @param {number} customerId The id of the customer
   * @param {string} missedVisitCode The missed visit code
   * @param {boolean} validateOnly If true, only validates that the code is good.
   * @returns {Observable<{}>} Observable of empty set
   */
  public missedVisit(customerId: number, missedVisitCode: string): Observable<{}> {
    console.log(customerId + ' ' + missedVisitCode);
    return this._httpService.doPost(CONSTANTS.API_ROUTES.CUSTOMER.LOYALTY.MISSED_VISIT, null,
      { customerId: customerId, missedVisitCode: missedVisitCode});
  }
}
