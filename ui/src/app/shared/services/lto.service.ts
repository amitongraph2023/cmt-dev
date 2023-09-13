import { Injectable } from '@angular/core';
import { HttpService } from '@services/http.service';
import { Observable } from 'rxjs/Observable';
import { CONSTANTS } from '../../constants';
import { LTO } from '@models/lto.model';


@Injectable()
export class LtoService {

  constructor(private _httpService: HttpService) {}

  public getLtoByCode(specialCode: string): Observable<LTO> {
    return this._httpService.doGet<LTO>(CONSTANTS.API_ROUTES.LTO.BY_CODE,
      {code: specialCode});
  }
}
