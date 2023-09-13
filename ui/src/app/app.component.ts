import { Component, ElementRef, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { isNullOrUndefined } from 'util';

import { CONSTANTS } from './constants';

import { routerTransition } from './shared/animations/router-transition.animation';
// Services
import { AuthenticationService } from '@services/authentication.service';
import { ErrorPropagatorService } from '@services/error-propagator.service';
import { LoadingPropagatorService } from '@services/loading-propagator.service';
import { SessionTimerService } from '@services/session-timer.service';
import { TitlePropagatorService } from '@services/title-propagator.service';
import { SpoofService } from '@services/spoof.service';
import { NavbarService } from '@services/navbar.service';
import { CateringRedirectPropagatorService } from '@services/catering-redirect-propagator.service';

// Declare $ as jQuery
declare var $: any;

@Component({
  animations: [routerTransition],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnChanges, OnDestroy, OnInit {
  @ViewChild('timeoutModal') timeoutModal: ElementRef;
  @ViewChild('timedoutModal') timedoutModal: ElementRef;

  public isSpoofing = false;

  public collapsed = false;
  public error: any;
  public isLoading = false;
  public minutes = 0;
  public seconds = 0;
  public showBackdrop = false;
  public ticks: number;

  public appRoute = this._router.url;

  public showCateringRedirect: boolean;

  constructor(private _authService: AuthenticationService,
              private _cateringRedirectPropagationService: CateringRedirectPropagatorService,
              private _errorPropagatorService: ErrorPropagatorService,
              private _loadingPropagatorService: LoadingPropagatorService,
              private _navbarService: NavbarService,
              private _router: Router,
              private _sessionTimer: SessionTimerService,
              private _spoofService: SpoofService,
              private _titlePropagatorService: TitlePropagatorService,
              private _titleService: Title) {

    this._cateringRedirectPropagationService.showCateringRedirect$.subscribe(
      showCateringRedirect => {
        this.showCateringRedirect = showCateringRedirect;
      },
      error => console.error(error)
    );

    // Listen for changes from the error propagator
    this._errorPropagatorService.errorChange$.subscribe(error => {
      this.showBackdrop = typeof error !== 'undefined' && error !== null;
      if (this.isLoading) {
        this.isLoading = false;
      }
      this.error = error;
    });

    // Listen for changes from the loading propagator
    this._loadingPropagatorService.loadingChange$.subscribe(isLoading => {
      this.showBackdrop = isLoading;
      this.isLoading = isLoading;
    });

    this._titlePropagatorService.titleChange$.subscribe(newTitle => {
      this._titleService.setTitle('Customer Management Tool - ' + newTitle);
    });

    // Listen for changes from the router
    _router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.appRoute = event.url;
      }
    });

    // Watch the session countdown time
    _sessionTimer.seconds$.subscribe((tick) => {
      // Update the ui (if modal is shown)
      this.ticks = tick;

      // Check if there is more than 59 seconds left
      if (tick > 59) {
        this.minutes = Math.floor(tick / 60);
        this.seconds = tick % 60;
      } else {
        this.minutes = 0;
      }

      // Check if there are n seconds left, if so, show the modal
      if (tick === (CONSTANTS.SETTINGS.SESSION_TIMEOUT_MODAL_SHOWN_AT_MINUTES * 60)) {
        // Show the countdown modal that can only be closed by clicking the continue or logout buttons
        $(this.timeoutModal.nativeElement).modal({
          backdrop: 'static',
          keyboard: false,
          show: true
        });
      } else if (tick === 0) {
        $(this.timeoutModal.nativeElement).modal('hide');

        setTimeout(() => {
          $(this.timedoutModal.nativeElement).modal('show');
        }, 500);
      }
    });

    if (sessionStorage.getItem(CONSTANTS.STUBS.USER_STUB) !== null) {
      _sessionTimer.startTimer(this._authService);
    }

    const _self = this;
    window.addEventListener('storage', function(e) {
      const permStub = CONSTANTS.STUBS.PERMISSIONS_STUB;

      if (e.storageArea === sessionStorage) {
        if (e.key === permStub) {
          alert('Bad, bad, bad! Hacking of the permissions is not allowed!');
          _self._authService.logout();
        }
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

  ngOnDestroy(): void {
    $('.modal').remove();
  }

  ngOnInit(): void {

    if (location.protocol === 'http:' && location.href.indexOf('localhost') === -1) {
      location.href = 'https://' + location.href.split('/')[2];
    }

    this._navbarService.collapsed.subscribe(
      (collapsed) => this.collapsed = collapsed,
      (error) => console.error(error)
    );

    this._spoofService.spoofStatus.subscribe(
      (spoofStatus) => this.isSpoofing = spoofStatus,
      (error) => console.error(error)
    );

    $('#cateringRedirectModal').modal('show');

  }

  /**
   * Handles the clicking of the backdrop
   */
  public backdropClick(): void {
    // Only continue if backdrop is shown and is not the loading screen
    if (this.showBackdrop && !this.isLoading) {
      // Check if there is an error
      if (!isNullOrUndefined(this.error)) {
        // Reset the error
        this.error = null;
      }

      // Hide the backdrop
      this.showBackdrop = false;
    }
  }

  /**
   * Handles the error card's ok button click
   */
  public closeErrorCard(): void {
    // Reset the error
    this.error = null;

// Hide the backdrop
    this.showBackdrop = false;
  }

  /**
   * Get the animation state
   *
   * @param outlet The router's outlet
   * @returns {any} The animation
   */
  public getRouteAnimation(outlet): any {
    return outlet.activatedRouteData.animation;
  }

  /**
   * Handles the continue button click of the timeoutModal
   */
  public onContinueClick(): void {
    $(this.timeoutModal.nativeElement).modal('hide');

    this._authService.isSessionActive().subscribe(() => {
      // do nothing
    }, status => {
      if (!status) {
        this._authService.logout();
      }
    });
  }

  /**
   * Handles the logout button click of the timeoutModal
   */
  public onLogoutClick(): void {
    $(this.timeoutModal.nativeElement).modal('hide');
    this._authService.logout();
  }

}
