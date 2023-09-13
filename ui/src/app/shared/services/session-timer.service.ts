import { EventEmitter, Injectable } from '@angular/core';
import 'rxjs/add/observable/interval';
import 'rxjs/add/operator/map';

import { CONSTANTS } from '../../constants';

// Services
import { AuthenticationService } from '@services/authentication.service';

// Declare $ as jQuery
declare var $: any;

@Injectable()
export class SessionTimerService {
  seconds$: EventEmitter<number> = new EventEmitter();

  public secondsLeft: number;

  private timer: any;
  private maxTime = CONSTANTS.SETTINGS.SESSION_TIMEOUT_MINUTES * 60;

  public resetTimer(): void {
    this.secondsLeft = this.maxTime;
  }

  public startTimer(authService: AuthenticationService): void {
    // if (!CONSTANTS.SETTINGS.IS_DEV) {
    //   // Set the seconds left
    //   this.secondsLeft = this.maxTime;
    //
    //   // Start the timer
    //   this.timer = Observable.interval(1000)
    //     .map((x) => x + 1)
    //     .subscribe(() => {
    //       this.secondsLeft--;
    //
    //       this.seconds$.emit(this.secondsLeft);
    //
    //       // Check if the session has expired
    //       if (this.secondsLeft === 0) {
    //         // Stop the timer
    //         this.timer.unsubscribe();
    //
    //         // Send the logout
    //         authService.logout(true);
    //       }
    //     });
    // }
  };

  public stopTimer(): void {
    this.secondsLeft = 0;
    if (typeof this.timer !== 'undefined') {
      this.timer.unsubscribe();
    }
  }
}
