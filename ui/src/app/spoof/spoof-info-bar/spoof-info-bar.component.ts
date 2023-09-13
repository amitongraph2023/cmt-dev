import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-spoof-info-bar',
  templateUrl: './spoof-info-bar.component.html',
  styleUrls: ['./spoof-info-bar.component.scss']
})
export class SpoofInfoBarComponent {
  @Output() returnToCmtClicked = new EventEmitter<boolean>();

  constructor() { }

  /**
   * Close spoofing session, emits call used by spoof component to clean up session
   *
   * @param $event
   */
  public onCloseSpoofSubmit($event): void {
    this.returnToCmtClicked.emit(true);
  }

}
