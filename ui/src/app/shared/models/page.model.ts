export class Page {
  public content: Array<any>;
  public totalItems: number;
  public currentPage: number;
  public pageSize: number;
  public totalPages: number;
  public startPage: number;
  public endPage: number;
  public startIndex: number;
  public endIndex: number;
  public pages: Array<number>;

  constructor(currentPage: number = 1, pageSize: number = 5) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
  }
}

