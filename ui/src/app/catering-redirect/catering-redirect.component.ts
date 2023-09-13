import { Component, OnDestroy, OnInit } from '@angular/core';
import { CateringRedirect } from '@models/catering-redirect.model';
import { CateringRedirectService } from '@services/catering-redirect.service';
import { AuthenticationService } from '@services/authentication.service';
import { CateringRedirectPropagatorService } from '@services/catering-redirect-propagator.service';


// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-catering-redirect',
  templateUrl: './catering-redirect.component.html',
  styleUrls: ['./catering-redirect.component.scss']
})
export class CateringRedirectComponent implements OnDestroy, OnInit {

  public cateringRedirectUrl: string;

  constructor(private _authService: AuthenticationService
    , private _cateringRedirectPropagatorService: CateringRedirectPropagatorService
    , private _cateringRedirectService: CateringRedirectService) {}

  ngOnDestroy(): void {
    $('.modal').remove();
  }
  ngOnInit() {
    this._cateringRedirectService.getRedirectStatus().subscribe(
      (cateringRedirect: CateringRedirect) => {
        this.cateringRedirectUrl = cateringRedirect.redirectUrl;
      }
    );
  }

  /**
   * Handles the logout button click
   */
  public onLogoutClick(): void {
    this._authService.logout();
    this._cateringRedirectPropagatorService.setHasSalesAdminPermission(false);
  }

}
