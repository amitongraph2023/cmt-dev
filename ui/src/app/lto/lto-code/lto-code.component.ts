import { Component, OnInit } from '@angular/core';
import { LTO } from '@models/lto.model';
import { LtoService } from '@services/lto.service';
import * as moment from 'moment';

@Component({
  selector: 'app-lto-code',
  templateUrl: './lto-code.component.html',
  styleUrls: ['./lto-code.component.scss']
})
export class LtoCodeComponent implements OnInit {

  public loading = false;
  public searchCode = '';

  public result: LTO;

  constructor(
    private _ltoService: LtoService
  ) { }

  ngOnInit() {
  }

  public onClickResetButton() {
    this.searchCode = '';
  }
  public onClickSearchButton() {
    this._ltoService.getLtoByCode(this.searchCode).subscribe(
      (lto) => this.result = lto,
      (error) => console.error(error),
      () => {
        this.result.startDateTime = moment(this.result.startDateTime).format('MMMM Do YYYY, h:mm a');
        this.result.endDateTime = moment(this.result.endDateTime).format('MMMM Do YYYY, h:mm a');
      }
    );
  }

}
