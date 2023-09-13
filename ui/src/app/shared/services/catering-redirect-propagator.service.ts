import { EventEmitter, Injectable } from '@angular/core';
import { CateringRedirectService } from '@services/catering-redirect.service';
import { CateringRedirect } from '@models/catering-redirect.model';

@Injectable()
export class CateringRedirectPropagatorService {

  public showCateringRedirect$: EventEmitter<Boolean> = new EventEmitter(true);

  private cateringRedirectEnabled: boolean;
  private hasSalesAdminPermission: boolean;

  constructor(private _cateringRedirectService: CateringRedirectService) { }

  public getShowCateringRedirect(): boolean {
    return this.cateringRedirectEnabled && this.hasSalesAdminPermission;
  }

  /**
   * Checks the value of catering.redirect.enabled in appConfig and sets redirectEnabled
   */
  public setCateringRedirect(): void {
    this._cateringRedirectService.getRedirectStatus().subscribe(
      (cateringRedirect: CateringRedirect) => {
        this.cateringRedirectEnabled = cateringRedirect.enabled;
        this.showCateringRedirect$.emit(this.getShowCateringRedirect());
      },
      (error) => console.log(error));
  }

  /**
   * Sets the hasSalesAdminPermissions and then checks for redirect and emits new value
   * @param value boolean value to set hasSalesAdminPermissions to
   */
  public setHasSalesAdminPermission(value: boolean): void {
    this.setCateringRedirect();
    this.hasSalesAdminPermission = value;
    this.showCateringRedirect$.emit(this.getShowCateringRedirect());
  }
}
