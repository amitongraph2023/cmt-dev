import { EventEmitter, Injectable } from '@angular/core';

@Injectable()
export class LoadingPropagatorService {

  public loadingChange$: EventEmitter<boolean> = new EventEmitter(true);

  /**
   * Sets the loading status
   *
   * @param {boolean} isLoading The loading status
   */
  public setIsLoading(isLoading: boolean): void {
    this.loadingChange$.emit(isLoading);
  };
}
