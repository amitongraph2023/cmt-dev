import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';


@Injectable()
export class NavbarService {

  private collapsedState = new BehaviorSubject(null);

  /**
   * Observable to keep make the collapsed state available to other components
   */
  public collapsed = this.collapsedState.asObservable();

  /**
   * Method to change the state of the collapsed state
   * @param newState
   */
  public changeCollapsedState(newState: boolean) {
    this.collapsedState.next(newState);
  }
}
