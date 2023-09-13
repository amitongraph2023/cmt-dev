import { Component, OnDestroy, OnInit } from '@angular/core';
import { TitlePropagatorService } from '@services/title-propagator.service';

// Declare $ as jQuery
declare var $: any;

@Component({
  selector: 'app-spoof-info-modal',
  templateUrl: './spoof-info-modal.component.html',
  styleUrls: ['./spoof-info-modal.component.scss']
})
export class SpoofInfoModalComponent implements OnInit, OnDestroy {

  constructor(private _titlePropagatorService: TitlePropagatorService) {
  }

  ngOnInit() {
    this.openStartSpoofingModal();
  }

  ngOnDestroy(): void {
    $('#startCustomerSessionModal').remove();
  }

  public openStartSpoofingModal(): void {
    this._titlePropagatorService.setNewTitle('Start Customer Session');
    const modalEl = $('#startCustomerSessionModal');
    modalEl.appendTo('body')
      .modal({
        focus: true
      })
      .css('transform', 'translateX(125px)');
  }

  /**
   * Close modal window
   * @param $event passed in event
   */
  public onClickCloseModal($event): void {
    $('#startCustomerSessionModal').modal('hide');
  }

}
