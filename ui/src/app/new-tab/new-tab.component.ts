import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';

import { isNullOrUndefined } from 'util';

import { TitlePropagatorService } from '@services/title-propagator.service';
import { SpoofService } from '@services/spoof.service';
import { SpoofButton } from '@models/spoof-button.model';

@Component({
  selector: 'app-login',
  templateUrl: './new-tab.component.html',
  styleUrls: ['./new-tab.component.scss']
})
export class NewTabComponent implements OnInit {

  public iFrameHTML = 'Redirecting...';

  private pageTitle = 'Redirecting...';

  private nonMyPaneraSpoofButtons: SpoofButton[] = [];

  private selectedNonMyPaneraButton: SpoofButton = null;

  private params: Params;

  constructor(private _route: ActivatedRoute,
              private _spoofService: SpoofService,
              private _titlePropagatorService: TitlePropagatorService) {}

  ngOnInit() {
    this._titlePropagatorService.setNewTitle(this.pageTitle);
    this._route.params.subscribe(
      params => {
        this.params = params;
        if ( !isNullOrUndefined(this.params.type) && !isNullOrUndefined(this.params.id) ) {
          if ( this.params.type === 'ANONYMOUS_ORDER' ) {
            this._spoofService.getSpoofButtons(true).subscribe(
              (nonMyPaneraSpoofButtons) => {
                this.nonMyPaneraSpoofButtons = nonMyPaneraSpoofButtons;
                this.selectedNonMyPaneraButton = this.nonMyPaneraSpoofButtons.filter(u => u.name === this.params.id)[0];
                this.pageTitle = 'Order Panera Bread';
                this._titlePropagatorService.setNewTitle(this.pageTitle);
                this.iFrameHTML = `<iframe style="width: 100%; height: 100vh;" src="${this.selectedNonMyPaneraButton.url}" frameborder="0" class="iframe"></iframe>`;
              },
              (error) => console.error(error)
            );
          }
        }
      },
      (error) => console.error(error)
    );
  }
}

