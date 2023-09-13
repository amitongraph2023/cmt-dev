import { EventEmitter, Injectable } from '@angular/core';

@Injectable()
export class TitlePropagatorService {

  public titleChange$: EventEmitter<string> = new EventEmitter(true);

  /**
   * Sets the new title
   *
   * @param {string} newTitle The new title
   */
  public setNewTitle(newTitle: string): void {
    this.titleChange$.emit(newTitle);
  };
}
