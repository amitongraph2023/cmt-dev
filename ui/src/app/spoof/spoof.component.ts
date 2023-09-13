import { Component, OnDestroy, OnInit } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { ActivatedRoute, Router } from '@angular/router';
// models
import { SpoofButton } from '@models/spoof-button.model';
// services
import { AuthenticationService } from '@services/authentication.service';
import { CustomerService } from '@services/customer.service';
import { NavbarService } from '@services/navbar.service';
import { SpoofService } from '@services/spoof.service';
import { CustomerPropagatorService } from '@services/customer-propagator.service';

declare var document: any;

@Component({
  selector: 'app-spoof',
  templateUrl: './spoof.component.html',
  styleUrls: ['./spoof.component.scss']
})
export class SpoofComponent implements OnDestroy, OnInit {
  public customerId: number;
  public spoofHTML = '';

  private spoofName: string;
  private $spoofLoginSubscription;
  private $spoofLogoutSubscription;
  private $spoofUnitSubscription;

  constructor(private _authService: AuthenticationService
    , private _customerPropagatorService: CustomerPropagatorService
    , private _customerService: CustomerService
    , private _navbarService: NavbarService
    , private _route: ActivatedRoute
    , private _router: Router
    , private _spoofService: SpoofService
  ) {
  }

  ngOnDestroy(): void {
    if (typeof this.$spoofUnitSubscription !== 'undefined') {
      this.$spoofUnitSubscription.unsubscribe();
    }
  }

  ngOnInit() {
    this.customerId = this._customerPropagatorService.getCustomerId();
    this.$spoofUnitSubscription = this._spoofService.spoofName.subscribe(
      (spoofName) => {
        this.spoofName = spoofName;
        if (!isNullOrUndefined(this.spoofName)) {
          this.spoofSession();
        }
      },
      (error) => {
        console.error(error);
      },
    );
  }

  /**
   * do spoof cleanup, which calls spoofLogout and sets values back to default
   */
  public  doCleanup(): void {
    if (!isNullOrUndefined(this.spoofName)
      && !isNullOrUndefined(this.customerId)
    ) {
      const selectedButton =  this.getSelectedButton();
      this.$spoofLogoutSubscription = this._spoofService.spoofLogout(
        this._spoofService.ssoToken
        , this.customerId
        , selectedButton.unit).subscribe(
        () => {
        },
        (error) => console.error(error),
        () => {
          if (typeof this.$spoofLogoutSubscription !== 'undefined') {
            this.$spoofLogoutSubscription.unsubscribe();
          }
        }
      );
    }
    this._spoofService.changeSpoofStatus(false);
    this._spoofService.changeSpoofName(null);
    this._navbarService.changeCollapsedState(false);
    this.redirectToHome();
  }

  /**
   * return to CMT button clicked
   *   inject reset message info iframe
   *   after time out, doCleanup()
   * @param $event the click event
   */
  public returnToCmtClicked($event): void {
    const f = document.querySelector('.iframe');
    f.contentWindow.postMessage('reset', this.getSelectedButton().url);

    setTimeout( () => { this.doCleanup();  }, 1000);
  }

  /**
   * Redirects / Navigate browser to Customer module
   */
  public redirectToHome(): void {
    this._router.navigate(['home']);
  }

  /**
   * Get the spoof session and set the html in spoofUrl
   */
  public spoofSession(): void {
    if (
      isNullOrUndefined(this.spoofName)
      || isNullOrUndefined(this.customerId)
    ) {
      this.doCleanup();
    }

    const selectedButton = this.getSelectedButton();
    this.$spoofLoginSubscription = this._spoofService.getSpoofSession(this.customerId, selectedButton.unit)
      .subscribe(
        (spoofResponse) => {
          this._spoofService.ssoToken = spoofResponse.accessToken;
        },
        (error) => console.error(error),
        () => {
          this.spoofHTML = '<iframe style="width: 100%; height: 100vh;" src="'
            + selectedButton.url
            + '" frameborder="0" class="iframe"></iframe>';
          if (typeof this.$spoofLoginSubscription !== 'undefined') {
            this.$spoofLoginSubscription.unsubscribe();
          }
        });
  }

  /**
   * get the selected button based on the spoofUnit
   *
   * @returns {SpoofButton} the currently selected spoof button based on the spoofUnit selected
   */
  private getSelectedButton(): SpoofButton {
    return this._spoofService.spoofButtons.filter(u => u.name === this.spoofName)[0];
  }

}
