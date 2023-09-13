import { EventEmitter, Injectable } from '@angular/core';

@Injectable()
export class ErrorPropagatorService {

  public errorChange$: EventEmitter<boolean> = new EventEmitter(true);

  /**
   * Sets the error
   *
   * @param {boolean} error The error
   */
  public setError(error: any): void {
    this.errorChange$.emit(error);
  };
}
