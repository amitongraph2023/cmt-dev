import { Component, EventEmitter, Input, OnChanges, Output, SimpleChange } from '@angular/core';
import { isNullOrUndefined } from 'util';

export class Page {
  public content: any[];
  public last: boolean;
  public totalPages: number;
  public totalElements: number;
  public size: number;
  public number: number;
  public first: boolean;
  public sortCol: string;
  public sortDir: string;
  public numberOfElements: number;
}

@Component({
  selector: 'app-pagination',
  styleUrls: ['./pagination.component.scss'],
  templateUrl: './pagination.component.html'
})
export class PaginationComponent implements OnChanges {
  @Input('isSmall') isSmall = false;
  @Input('response') response: Page;
  @Output() newPage = new EventEmitter<number>();

  public page = {
    isFirst: false,
    isLast: false,
    totalPages: 0,
    numbers: [0],
    number: 0
  };

  ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
    if (changes.hasOwnProperty('response')) {
      if (!isNullOrUndefined(this.response)) {
        this.buildPagination();
      }
    }
  }

  /**
   * Handles the clicking of the pagination strip and calls the service to get the new page
   *
   * @param {number} pageNum The page to go to
   */
  public goToPage(pageNum: number): void {
    if (pageNum !== this.page.number) {
      this.newPage.emit(pageNum);
    }
  }

  /**
   * Sets the page variables and builds the pagination numbers strip
   */
  private buildPagination(): void {
    const pageNum = this.response.number + 1;

    let numbers = [];
    if (this.isSmall) {
      if (pageNum >= 3 && pageNum <= this.response.totalPages - 2) {
        numbers = [pageNum - 1, pageNum, pageNum + 1];
      } else if (pageNum <= 3) {
        numbers = (this.response.totalPages > 2) ? [1, 2, 3]
          : Array(this.response.totalPages).fill(0, 0, this.response.totalPages).map((x, i) => i + 1);
      } else {
        numbers = Array(3).fill(0, 0, 3).map((x, i) => this.response.totalPages - 2 + i);
      }
    } else {
      if (pageNum >= 5 && pageNum <= this.response.totalPages - 4) {
        numbers = [1, -1, pageNum - 2, pageNum - 1, pageNum, pageNum + 1, pageNum + 2, -1, this.response.totalPages];
      } else if (pageNum <= 5) {
        numbers = (this.response.totalPages > 5) ? [1, 2, 3, 4, 5, -1, this.response.totalPages]
          : Array(this.response.totalPages).fill(0, 0, this.response.totalPages).map((x, i) => i + 1);
      } else {
        numbers = [
          1
          , -1
          , this.response.totalPages - 4
          , this.response.totalPages - 3
          , this.response.totalPages - 2
          , this.response.totalPages - 1
          , this.response.totalPages];
      }
    }

    this.page = {
      isFirst: this.response.first,
      isLast: this.response.last,
      totalPages: this.response.totalPages,
      numbers: numbers,
      number: this.response.number + 1
    };
  }
}
