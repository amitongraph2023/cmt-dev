import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';
import { CONSTANTS } from '../../constants';
import { StaticType } from '@enums/static-type.enum';

@Injectable()
export class StaticDataService {

  constructor(private _httpService: HttpService) {
  }

  /**
   * Retrieves customer phone numbers from the server by customerId
   *
   * @param {string} type The type of the data wanted
   * @returns {Observable<String[]>} Observable with list of strings
   */
  public getStaticDataByType(type: StaticType): Observable<any[]> {
    return this._httpService.doGet(CONSTANTS.API_ROUTES.STATIC_DATA, {type: StaticType[type]});
  }
}
